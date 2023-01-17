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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.commons.SqlOrder;
import es.magDevs.myLibrary.model.dao.AutorDao;

/**
 * Acceso a los datos de autores, usando hibernate
 * 
 * @author javier.vaquero
 * 
 */
@SuppressWarnings("unchecked")
public class HibAutorDao extends HibAbstractDao implements AutorDao {

	public HibAutorDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.AUTHORS_TABLE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Boolean> getOrders() {
		Map<String, Boolean> orders = new LinkedHashMap<String, Boolean>();
		orders.put("apellidos", true);
		orders.put("nombre", true);
		return orders;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Autor> getWithPag(Bean filter, int page, int pageSize)
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
			if (filter != null && StringUtils.isNotEmpty(filter.getSortedColumn())) {
				query.addOrder(new SqlOrder(filter.getSortedColumn(), filter.getSortedDirection()));
			} else  {
				query.addOrder(Property.forName("apellidos").asc()).addOrder(Property.forName("nombre").asc());
			}
			// Fijamos los datos que queremos obtener
			query.setProjection(Projections.projectionList()
					.add(Projections.property("id"))
					.add(Projections.property("nombre"))
					.add(Projections.property("apellidos"))
					.add(Projections.property("pais"))
					.add(Projections.property("annoNacimiento")));
			List<Object[]> l = query.list();
			List<Autor> authors = new ArrayList<Autor>();
			// Recorremos los datos obtenidos para convertirlos en objetos de la
			// clase Autor
			for (Object[] objects : l) {
				// Creamos el objeto Autor con los datos obtenidos
				Autor author = new Autor();
				author.setId((Integer) objects[0]);
				author.setNombre((String) objects[1]);
				author.setApellidos((String) objects[2]);
				author.setPais((String) objects[3]);
				author.setAnnoNacimiento((Integer) objects[4]);
				authors.add(author);
			}
			s.getTransaction().commit();
			return authors;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	/** 
	 * {@inheritDoc}
	 */
	protected Criteria getFilters(Session session, Bean f) {
		Autor filter = (Autor)f;
		Criteria c = session.createCriteria(Autor.class);
		if (filter == null) {
			return c;
		}

		// Recorremos todos los posibles criterios para filtrar:
		// Nombre
		if (!StringUtils.isBlank(filter.getNombre())) {
			c.add(Restrictions.like("nombre", "%" + filter.getNombre() + "%"));
		}
		// Nombre exacto
		if (!StringUtils.isBlank(filter.getNombreExacto())) {
			c.add(Restrictions.eq("nombre", filter.getNombreExacto()));
		}
		// Apellidos
		if (!StringUtils.isBlank(filter.getApellidos())) {
			c.add(Restrictions.like("apellidos", "%" + filter.getApellidos()
					+ "%"));
		}
		// Apellidos exacto
		if (!StringUtils.isBlank(filter.getApellidosExacto())) {
			c.add(Restrictions.eq("apellidos", filter.getApellidosExacto()));
		}
		// Pais
		if (!StringUtils.isBlank(filter.getPais())) {
			c.add(Restrictions.like("pais", "%" + filter.getPais() + "%"));
		}
		// Ciudad
		if (!StringUtils.isBlank(filter.getCiudad())) {
			c.add(Restrictions.like("ciudad", "%" + filter.getCiudad() + "%"));
		}
		// Año de nacimiento
		if (filter.getAnnoNacimiento() != null) {
			c.add(Restrictions.eq("annoNacimiento", filter.getAnnoNacimiento()));
		}
		// Año de fallecimiento
		if (filter.getAnnoFallecimiento() != null) {
			c.add(Restrictions.eq("annoFallecimiento",
					filter.getAnnoFallecimiento()));
		}
		// Notas
		if (!StringUtils.isBlank(filter.getNotas())) {
			c.add(Restrictions.like("notas", "%" + filter.getNotas() + "%"));
		}
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Autor> getAutores(String start) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Autor> l = s
					.createQuery(
							"FROM Autor WHERE nombre LIKE :nombre OR apellidos LIKE :apellidos ORDER BY apellidos,nombre")
					.setParameter("nombre", "%"+start+"%")
					.setParameter("apellidos", "%"+start+"%").list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Libro> getLibrosAutor(Integer id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Libro> l = s.createCriteria(Libro.class).addOrder(Property.forName("titulo").asc())
					.createCriteria("autores").add(Restrictions.idEq(id))
					.list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
	
	@Override
	protected Map<String, String> getCambios(Bean viejo, Bean nuevo) {
		Autor vie = (Autor) viejo, nue = (Autor) nuevo;
		Map<String, String> cambios = new HashMap<>();
		
		if (StringUtils.isNotEmpty(nue.getNombre()) && !vie.getNombre().equals(nue.getNombre())) {
			cambios.put("nombre", vie.getNombre());
			vie.setNombre(nue.getNombre());
		}
		if (StringUtils.isNotEmpty(nue.getApellidos()) && !vie.getApellidos().equals(nue.getApellidos())) {
			cambios.put("apellidos", vie.getApellidos());
			vie.setApellidos(nue.getApellidos());
		}
		if (StringUtils.isNotEmpty(nue.getPais()) && !vie.getPais().equals(nue.getPais())) {
			cambios.put("pais", vie.getPais());
			vie.setPais(nue.getPais());
		}
		if (StringUtils.isNotEmpty(nue.getCiudad()) && !vie.getCiudad().equals(nue.getCiudad())) {
			cambios.put("ciudad", vie.getCiudad());
			vie.setCiudad(nue.getCiudad());
		}
		if (nue.getAnnoNacimiento() != null && !vie.getAnnoNacimiento().equals(nue.getAnnoNacimiento())) {
			cambios.put("anno_nacimiento", vie.getAnnoNacimiento()==null?null:""+vie.getAnnoNacimiento());
			vie.setAnnoNacimiento(nue.getAnnoNacimiento());
		}
		if (nue.getAnnoFallecimiento() != null && !vie.getAnnoFallecimiento().equals(nue.getAnnoFallecimiento())) {
			cambios.put("anno_fallecimiento", vie.getAnnoFallecimiento()==null?null:""+vie.getAnnoFallecimiento());
			vie.setAnnoFallecimiento(nue.getAnnoFallecimiento());
		}
		if (StringUtils.isNotEmpty(nue.getNotas()) && !vie.getNotas().equals(nue.getNotas())) {
			cambios.put("notas", vie.getNotas());
			vie.setNotas(nue.getNotas());
		}
		return cambios;
	}
}
