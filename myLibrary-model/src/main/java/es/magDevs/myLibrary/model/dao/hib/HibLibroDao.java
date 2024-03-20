/**
 * Copyright (c) 2014-2015, Javier Vaquero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * required by applicable law or agreed to in writing, software
 * under the License is distributed on an "AS IS" BASIS,
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * the License for the specific language governing permissions and
 * under the License.
 */
package es.magDevs.myLibrary.model.dao.hib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.beans.Usuario;
import es.magDevs.myLibrary.model.commons.SqlOrder;
import es.magDevs.myLibrary.model.dao.LibroDao;

/**
 * Acceso a los datos de libros, usando hibernate
 * 
 * @author javier.vaquero
 * 
 */
@SuppressWarnings("unchecked")
public class HibLibroDao extends HibAbstractDao implements LibroDao {

	public HibLibroDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.BOOKS_TABLE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Boolean> getOrders() {
		Map<String, Boolean> orders = new LinkedHashMap<String, Boolean>();
		orders.put("id", true);
		return orders;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Libro> getWithPag(Bean filter, int page, int pageSize)
			throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			// Obtenemos el filtro
			Criteria query = getFilters(s, filter);
			// Fijamos las opciones de paginacion
			query.setMaxResults(pageSize);
			query.setFirstResult(page * pageSize);
			// Ordenamos por titulo de libro
			Libro libro = (Libro) filter;
			boolean authorFiltered = libro != null && libro.getAutores() != null
					&& libro.getAutores().size() > 0
					&& (!StringUtils.isBlank(libro.getAutores().iterator().next().getNombre())
							|| !StringUtils.isBlank(libro.getAutores().iterator().next().getApellidos())
							|| !StringUtils.isBlank(libro.getAutores().iterator().next().getPais())
							|| libro.getAutores().iterator().next().getAnnoNacimiento() != null);
			if (filter != null && StringUtils.isNotEmpty(filter.getSortedColumn())) {
				query.addOrder(new SqlOrder(filter.getSortedColumn(), filter.getSortedDirection()));
			} else if (filter == null
					|| (StringUtils.isBlank(libro.getTitulo())
					&& !authorFiltered
					&& (libro.getEditorial() == null || StringUtils.isBlank(libro.getEditorial().getNombre()))
					&& (libro.getUbicacion() == null || StringUtils.isBlank(libro.getUbicacion().getCodigo()))
					&& (libro.getColeccion() == null || StringUtils.isBlank(libro.getColeccion().getNombre()))
					&& (libro.getTipo() == null || StringUtils.isBlank(libro.getTipo().getDescripcion()))
					&& StringUtils.isBlank(libro.getNotas())
					&& StringUtils.isBlank(libro.getIsbn())
					&& libro.getAnnoCompra() == null
					&& libro.getNumPaginas() == null)) {
				query.addOrder(Property.forName("id").desc());
			} else {
				query.addOrder(Property.forName("titulo").asc());
			}
			// Fijamos los datos que queremos obtener
			ProjectionList projection = Projections.projectionList()
					.add(Projections.property("id"))
					.add(Projections.property("titulo"))
					.add(Projections.property("editorial"))
					.add(Projections.property("tipo"))
					.add(Projections.property("ubicacion"))
					.add(Projections.property("tomo"))
					.add(Projections.sqlProjection("(SELECT GROUP_CONCAT(concat(ifnull(concat(a.nombre,' '), ''),a.apellidos) SEPARATOR ', ') "
							+ "FROM libros_autores la JOIN autores a ON la.autor=a.id WHERE la.libro=this_.id) AS autores_txt", new String[]{"autores_txt"}, new Type[]{StandardBasicTypes.STRING}))
					.add(Projections.sqlProjection("(SELECT u.nombre "
							+ "FROM prestamos p JOIN usuarios u ON u.id=p.usuario WHERE p.libro=this_.id) AS usr_prestamo", new String[]{"usr_prestamo"}, new Type[]{StandardBasicTypes.STRING}));
			boolean hayUsuarioRegistrado = libro != null && libro.getUsuarioRegistrado() != null;
			if (hayUsuarioRegistrado) {
				projection.add(Projections.sqlProjection("(SELECT p.fecha FROM pendientes p WHERE p.libro=this_.id AND p.usuario='"
							+ libro.getUsuarioRegistrado().getId()+"') AS fecha_pendiente", new String[]{"fecha_pendiente"}, new Type[]{StandardBasicTypes.STRING}))
					.add(Projections.sqlProjection("(SELECT GROUP_CONCAT(l.fecha SEPARATOR '|') FROM leidos l WHERE l.libro=this_.id AND l.usuario='"
							+ libro.getUsuarioRegistrado().getId()+"') AS fechas_leido", new String[]{"fechas_leido"}, new Type[]{StandardBasicTypes.STRING}));
			}
			query.setProjection(projection);
			List<Object[]> l = query.list();
			List<Libro> books = new ArrayList<Libro>();
			// Recorremos los datos obtenidos para convertirlos en objetos de la
			// clase Libro y obtener los autores correspondientes
			for (Object[] objects : l) {
				// Creamos el objeto Libro con los datos obtenidos
				Libro book = new Libro();
				book.setId((Integer) objects[0]);
				book.setTitulo((String) objects[1]);
				book.setEditorial((Editorial) objects[2]);
				book.setTipo((Tipo) objects[3]);
				book.setUbicacion((Ubicacion) objects[4]);
				book.setTomo((Integer) objects[5]);
				book.setAutoresTxt((String) objects[6]);
				String usrPrestamo = (String) objects[7];
				if (StringUtils.isNotBlank(usrPrestamo)) {
					book.setPrestamo(new Usuario(null, null, null, null, usrPrestamo, null, null));
				}
				if (hayUsuarioRegistrado) {
					book.setPendiente(string2Presentation((String) objects[8]));
					String fechasLeido = (String) objects[9];
					if (StringUtils.isNotEmpty(fechasLeido)) {
						book.setLeido(Arrays.asList(fechasLeido.split("\\|")).stream().map(f->int2Presentation(f)).collect(Collectors.toList()));
					}
				}
				books.add(book);
			}

			s.getTransaction().commit();
			return books;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Criteria getFilters(Session session, Bean f) {
		Libro filter = (Libro) f;
		Criteria c = session.createCriteria(Libro.class);
		if (filter == null) {
			return c;
		}
		Criteria authorsFilter = null;

		// Recorremos todos los posibles criterios para filtrar:
		// Titulo
		if (!StringUtils.isBlank(filter.getTitulo())) {
			c.add(Restrictions.like("titulo", "%" + filter.getTitulo() + "%"));
		}
		// Editorial
		if (filter.getEditorial() != null
				&& !StringUtils.isBlank(filter.getEditorial().getNombre())) {
			c.createCriteria("editorial").add(
					Restrictions.like("nombre", "%"
							+ filter.getEditorial().getNombre() + "%"));
		}
		// Ubicacion
		if (filter.getUbicacion() != null
				&& !StringUtils.isBlank(filter.getUbicacion().getCodigo())) {
			if (filter.getUbicacion().getCodigo().equals("-1")) {
				c.add(Restrictions.isNull("ubicacion"));
			} else {
				c.createCriteria("ubicacion").add(Restrictions.eq("codigo", filter.getUbicacion().getCodigo()));
			}
		}
		// Coleccion
		if (filter.getColeccion() != null
				&& !StringUtils.isBlank(filter.getColeccion().getNombre())) {
			c.createCriteria("coleccion").add(
					Restrictions.like("nombre", "%"
							+ filter.getColeccion().getNombre() + "%"));
		}
		// Tipo
		if (filter.getTipo() != null
				&& !StringUtils.isBlank(filter.getTipo().getDescripcion())) {
			c.createCriteria("tipo").add(
					Restrictions.eq("descripcion", filter.getTipo()
							.getDescripcion()));
		}
		// Notas
		if (!StringUtils.isBlank(filter.getNotas())) {
			c.add(Restrictions.like("notas", "%" + filter.getNotas() + "%"));
		}
		// ISBN
		if (!StringUtils.isBlank(filter.getIsbn())) {
			c.add(Restrictions.like("isbn", "%" + filter.getIsbn() + "%"));
		}
		// Codigo de barras
		if (!StringUtils.isBlank(filter.getCb())) {
			if (filter.getCb().equals("-1")) {
				c.add(Restrictions.isNull("cb"));
			} else {
				c.add(Restrictions.eq("cb", filter.getCb()));
			}
		}
		// Año de compra
		if (filter.getAnnoCompra() != null) {
			c.add(Restrictions.eq("annoCompra", filter.getAnnoCompra()));
		}
		// Nº paginas
		if (filter.getNumPaginas() != null) {
			c.add(Restrictions.eq("numPaginas", filter.getNumPaginas()));
		}

		// Datos del autor
		if (filter.getAutores() != null && filter.getAutores().size() > 0) {
			Autor authorFilter = (Autor) filter.getAutores().toArray()[0];
			// Nombre
			if (!StringUtils.isBlank(authorFilter.getNombre())) {
				authorsFilter = c.createCriteria("autores");
				authorsFilter.add(Restrictions.like("nombre", "%"
						+ authorFilter.getNombre() + "%"));
			}
			// Apellidos
			if (!StringUtils.isBlank(authorFilter.getApellidos())) {
				if (authorsFilter == null) {
					authorsFilter = c.createCriteria("autores");
				}
				authorsFilter.add(Restrictions.like("apellidos", "%"
						+ authorFilter.getApellidos() + "%"));
			}
			// Pais
			if (!StringUtils.isBlank(authorFilter.getPais())) {
				if (authorsFilter == null) {
					authorsFilter = c.createCriteria("autores");
				}
				authorsFilter.add(Restrictions.like("pais",
						"%" + authorFilter.getPais() + "%"));
			}
			// Año de nacimiento
			if (authorFilter.getAnnoNacimiento() != null) {
				if (authorsFilter == null) {
					authorsFilter = c.createCriteria("autores");
				}
				authorsFilter.add(Restrictions.eq("annoNacimiento",
						authorFilter.getAnnoNacimiento()));
			}
		}
		return c;
	}
	
	@Override
	protected Map<String, String> getCambios(Bean viejo, Bean nuevo) {
		Libro vie = (Libro) viejo, nue = (Libro) nuevo;
		Map<String, String> cambios = new HashMap<>();
		
		if (StringUtils.isNotEmpty(nue.getTitulo()) && !vie.getTitulo().equals(nue.getTitulo())) {
			cambios.put("titulo", vie.getTitulo());
			vie.setTitulo(nue.getTitulo());
		}
		if (StringUtils.isNotEmpty(nue.getIsbn()) && !vie.getIsbn().equals(nue.getIsbn())) {
			cambios.put("isbn", vie.getIsbn());
			vie.setIsbn(nue.getIsbn());
		}
		if (StringUtils.isNotEmpty(nue.getCb()) && !vie.getCb().equals(nue.getCb())) {
			cambios.put("cb", vie.getCb());
			vie.setCb(nue.getCb());
		}
		if (nue.getAnnoCompra() != null && !vie.getAnnoCompra().equals(nue.getAnnoCompra())) {
			cambios.put("anno_compra", vie.getAnnoCompra()==null?null:""+vie.getAnnoCompra());
			vie.setAnnoCompra(nue.getAnnoCompra());
		}
		if (nue.getAnnoPublicacion() != null && !vie.getAnnoPublicacion().equals(nue.getAnnoPublicacion())) {
			cambios.put("anno_publicacion", vie.getAnnoPublicacion()==null?null:""+vie.getAnnoPublicacion());
			vie.setAnnoPublicacion(nue.getAnnoPublicacion());
		}
		if (nue.getAnnoCopyright() != null && !vie.getAnnoCopyright().equals(nue.getAnnoCopyright())) {
			cambios.put("anno_copyright", vie.getAnnoCopyright()==null?null:""+vie.getAnnoCopyright());
			vie.setAnnoCopyright(nue.getAnnoCopyright());
		}
		if (nue.getNumEdicion() != null && !vie.getNumEdicion().equals(nue.getNumEdicion())) {
			cambios.put("num_edicion", vie.getNumEdicion()==null?null:""+vie.getNumEdicion());
			vie.setNumEdicion(nue.getNumEdicion());
		}
		if (nue.getNumPaginas() != null && !vie.getNumPaginas().equals(nue.getNumPaginas())) {
			cambios.put("num_paginas", vie.getNumPaginas()==null?null:""+vie.getNumPaginas());
			vie.setNumPaginas(nue.getNumPaginas());
		}
		if (nue.getTomo() != null && !vie.getTomo().equals(nue.getTomo())) {
			cambios.put("tomo", vie.getTomo()==null?null:""+vie.getTomo());
			vie.setTomo(nue.getTomo());
		}
		if (nue.getPrecio() != null && !vie.getPrecio().equals(nue.getPrecio())) {
			cambios.put("precio", vie.getPrecio()==null?null:""+vie.getPrecio());
			vie.setPrecio(nue.getPrecio());
		}
		if (StringUtils.isNotEmpty(nue.getNotas()) && !vie.getNotas().equals(nue.getNotas())) {
			cambios.put("notas", vie.getNotas());
			vie.setNotas(nue.getNotas());
		}
		if (nue.getEditorial() != null && nue.getEditorial().getId() != null &&
				(vie.getEditorial() == null || !nue.getEditorial().getId().equals(vie.getEditorial().getId()))) {
			if (vie.getEditorial() == null) {
				vie.setEditorial(new Editorial());
			}
			cambios.put("editorial", vie.getEditorial().getId() != null ? ""+vie.getEditorial().getId() : null);
			vie.setEditorial(nue.getEditorial().clone());
		}
		if (nue.getColeccion() != null && nue.getColeccion().getId() != null &&
				(vie.getColeccion() == null || !nue.getColeccion().getId().equals(vie.getColeccion().getId()))) {
			if (vie.getColeccion() == null) {
				vie.setColeccion(new Coleccion());
			}
			cambios.put("coleccion", vie.getColeccion().getId() != null ? ""+vie.getColeccion().getId() : null);
			vie.setColeccion(nue.getColeccion().clone());
		}
		if (nue.getTipo() != null && nue.getTipo().getId() != null &&
				(vie.getTipo() == null || !nue.getTipo().getId().equals(vie.getTipo().getId()))) {
			if (vie.getTipo() == null) {
				vie.setTipo(new Tipo());
			}
			cambios.put("tipo", vie.getTipo().getId() != null ? ""+vie.getTipo().getId() : null);
			vie.setTipo(nue.getTipo().clone());
		}
		if (nue.getUbicacion() != null && nue.getUbicacion().getId() != null &&
				(vie.getUbicacion() == null || !nue.getUbicacion().getId().equals(vie.getUbicacion().getId()))) {
			if (vie.getUbicacion() == null) {
				vie.setUbicacion(new Ubicacion());
			}
			cambios.put("ubicacion", vie.getUbicacion().getId() != null ? ""+vie.getUbicacion().getId() : null);
			vie.setUbicacion(nue.getUbicacion().clone());
		}
		
		return cambios;
	}
	
	@Override
	public void updateCB(Integer id, String cb) {
		Session session = getSession();
		if (!session.isOpen() || session.getTransaction() == null) {
			return;
		}
		session.createQuery("UPDATE Libro SET cb = :cb where id = :id")
		 	.setParameter( "cb", cb )
	        .setParameter( "id", id )
	        .executeUpdate();
	}
}
