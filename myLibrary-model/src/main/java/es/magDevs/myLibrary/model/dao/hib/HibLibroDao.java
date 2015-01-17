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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.beans.Ubicacion;
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
			if (filter != null) {
				query.addOrder(Property.forName("titulo").asc());
			} else {
				query.addOrder(Property.forName("id").desc());
			}
			// Fijamos los datos que queremos obtener
			query.setProjection(Projections.projectionList()
					.add(Projections.property("id"))
					.add(Projections.property("titulo"))
					.add(Projections.property("editorial"))
					.add(Projections.property("tipo"))
					.add(Projections.property("ubicacion"))
					.add(Projections.property("tomo")));
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
				// Obtenemos los autores asociados
				Query queryAutores = s
						.createSQLQuery("SELECT a.id, a.nombre, a.apellidos"
								+ " FROM autores a JOIN libros_autores la ON"
								+ " a.id=la.autor where la.libro=:id").setParameter("id", book.getId());
				List<Object[]> autoresData = queryAutores.list();
				Set<Autor> autores = new HashSet<Autor>();
				for (Object[] autorData : autoresData) {
					autores.add(new Autor((Integer) autorData[0],
							(String) autorData[1], (String) autorData[2], null,
							null, null, null, null));
				}
				book.setAutores(autores);
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
			c.createCriteria("ubicacion").add(
					Restrictions
							.eq("codigo", filter.getUbicacion().getCodigo()));
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
}
