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
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.dao.TipoDao;

/**
 * Acceso a los datos de tipos, usando hibernate
 * 
 * @author javier.vaquero
 * 
 */
public class HibTipoDao extends HibAbstractDao implements TipoDao {

	public HibTipoDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.TYPES_TABLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Boolean> getOrders() {
		Map<String, Boolean> orders = new LinkedHashMap<String, Boolean>();
		return orders;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Criteria getFilters(Session session, Bean f) {
		Tipo filter = (Tipo) f;
		Criteria c = session.createCriteria(Tipo.class);
		if (filter == null) {
			return c;
		}

		// Recorremos todos los posibles criterios para filtrar:
		// Descripcion
		if (!StringUtils.isBlank(filter.getDescripcion())) {
			c.add(Restrictions.like("descripcion",
					"%" + filter.getDescripcion() + "%"));
		}
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<Libro> getLibrosTipo(Integer id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Libro> l = s.createCriteria(Libro.class)
					.addOrder(Property.forName("titulo").asc())
					.createCriteria("tipo").add(Restrictions.idEq(id)).list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
	
	@Override
	protected Map<String, String> getCambios(Bean viejo, Bean nuevo) {
		Tipo vie = (Tipo) viejo, nue = (Tipo) nuevo;
		Map<String, String> cambios = new HashMap<>();
		if (StringUtils.isNotEmpty(nue.getDescripcion()) && !vie.getDescripcion().equals(nue.getDescripcion())) {
			cambios.put("descripcion", vie.getDescripcion());
			vie.setDescripcion(nue.getDescripcion());
		}
		return cambios;
	}
}
