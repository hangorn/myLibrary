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
			query.addOrder(Property.forName("apellidos").asc()).addOrder(
					Property.forName("nombre").asc());
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
		// Apellidos
		if (!StringUtils.isBlank(filter.getApellidos())) {
			c.add(Restrictions.like("apellidos", "%" + filter.getApellidos()
					+ "%"));
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
					.setParameter("nombre", start+"%")
					.setParameter("apellidos", start+"%").list();
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
			List<Libro> l = s.createCriteria(Libro.class)
					.createCriteria("autores").add(Restrictions.idEq(id))
					.addOrder(Property.forName("titulo").asc()).list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
