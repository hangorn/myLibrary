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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.ColeccionDao;

/**
 * Acceso a los datos de colecciones, usando hibernate
 * 
 * @author javier.vaquero
 * 
 */
public class HibColeccionDao extends HibAbstractDao implements ColeccionDao {

	public HibColeccionDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.COLLECTIONS_TABLE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Boolean> getOrders() {
		Map<String, Boolean> orders = new LinkedHashMap<String, Boolean>();
		orders.put("nombre", true);
		return orders;
	}

	/** 
	 * {@inheritDoc}
	 */
	protected Criteria getFilters(Session session, Bean f) {
		Coleccion filter = (Coleccion)f;
		Criteria c = session.createCriteria(Coleccion.class);
		if (filter == null) {
			return c;
		}

		// Recorremos todos los posibles criterios para filtrar:
		// Nombre
		if (!StringUtils.isBlank(filter.getNombre())) {
			c.add(Restrictions.like("nombre", "%" + filter.getNombre() + "%"));
		}
		// Editorial
		if (filter.getEditorial() != null
				&& !StringUtils.isBlank(filter.getEditorial().getNombre())) {
			c.createCriteria("editorial").add(
					Restrictions.like("nombre", "%"
							+ filter.getEditorial().getNombre() + "%"));
		}
		return c;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Coleccion> getColecciones(String start) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Coleccion> l = s
					.createQuery("FROM Coleccion WHERE nombre LIKE :nombre ORDER BY nombre")
					.setParameter("nombre", start + "%").list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Coleccion> getColecciones(String start, Integer idEditorial)
			throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Coleccion> l = s
					.createQuery(
							"FROM Coleccion WHERE nombre LIKE :nombre AND editorial.id = :id ORDER BY nombre")
					.setParameter("nombre", start + "%")
					.setParameter("id", idEditorial).list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Libro> getLibrosColeccion(Integer id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Libro> l = s.createCriteria(Libro.class)
					.addOrder(Property.forName("titulo").asc())
					.createCriteria("coleccion").add(Restrictions.idEq(id)).list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
	
	@Override
	protected Map<String, String> getCambios(Bean viejo, Bean nuevo) {
		Coleccion vie = (Coleccion) viejo, nue = (Coleccion) nuevo;
		Map<String, String> cambios = new HashMap<>();

		if (StringUtils.isNotEmpty(nue.getNombre()) && !vie.getNombre().equals(nue.getNombre())) {
			cambios.put("nombre", vie.getNombre());
			vie.setNombre(nue.getNombre());
		}
		if (nue.getEditorial() != null && nue.getEditorial().getId() != null &&
				(vie.getEditorial() == null || !nue.getEditorial().getId().equals(vie.getEditorial().getId()))) {
			if (vie.getEditorial() == null) {
				vie.setEditorial(new Editorial());
			}
			cambios.put("editorial", vie.getEditorial().getId() != null ? ""+vie.getEditorial().getId() : null);
			vie.setEditorial(nue.getEditorial().clone());
		}
		return cambios;
	}
}
