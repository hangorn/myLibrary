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
import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.dao.UbicacionDao;

/**
 * Acceso a los datos de ubicaciones, usando hibernate
 * 
 * @author javier.vaquero
 * 
 */
public class HibUbicacionDao extends HibAbstractDao implements UbicacionDao {

	public HibUbicacionDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.PLACES_TABLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Boolean> getOrders() {
		Map<String, Boolean> orders = new LinkedHashMap<String, Boolean>();
		orders.put("codigo", true);
		return orders;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Criteria getFilters(Session session, Bean f) {
		Ubicacion filter = (Ubicacion) f;
		Criteria c = session.createCriteria(Ubicacion.class);
		if (filter == null) {
			return c;
		}

		// Recorremos todos los posibles criterios para filtrar:
		// Codigo
		if (!StringUtils.isBlank(filter.getCodigo())) {
			c.add(Restrictions.like("codigo", "%" + filter.getCodigo() + "%"));
		}
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
	public List<Libro> getLibrosUbicacion(Integer id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Libro> l = s.createCriteria(Libro.class)
					.addOrder(Property.forName("titulo").asc())
					.createCriteria("ubicacion").add(Restrictions.idEq(id)).list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
	
	@Override
	protected Map<String, String> getCambios(Bean viejo, Bean nuevo) {
		Ubicacion vie = (Ubicacion) viejo, nue = (Ubicacion) nuevo;
		Map<String, String> cambios = new HashMap<>();
		if (StringUtils.isNotEmpty(nue.getCodigo()) && !vie.getCodigo().equals(nue.getCodigo())) {
			cambios.put("codigo", vie.getCodigo());
			vie.setCodigo(nue.getCodigo());
		}
		if (StringUtils.isNotEmpty(nue.getDescripcion()) && !vie.getDescripcion().equals(nue.getDescripcion())) {
			cambios.put("descripcion", vie.getDescripcion());
			vie.setDescripcion(nue.getDescripcion());
		}
		return cambios;
	}
}
