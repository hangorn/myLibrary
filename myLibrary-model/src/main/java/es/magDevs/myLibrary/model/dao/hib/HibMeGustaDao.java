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
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.MeGusta;
import es.magDevs.myLibrary.model.dao.MeGustaDao;

/**
 * Acceso a los datos de libros marcados como me gusta, usando hibernate
 * 
 * @author javier.vaquero
 * 
 */
public class HibMeGustaDao extends HibAbstractDao implements MeGustaDao {

	public HibMeGustaDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.MEGUSTA_TABLE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Boolean> getOrders() {
		Map<String, Boolean> orders = new LinkedHashMap<String, Boolean>();
		orders.put("id", false);
		return orders;
	}

	/** 
	 * {@inheritDoc}
	 */
	protected Criteria getFilters(Session session, Bean f) {
		MeGusta filter = (MeGusta)f;
		Criteria c = session.createCriteria(MeGusta.class, "megusta");
		c.createAlias("usuario", "usuario");
		c.createCriteria("libro", "lib");
		if (filter == null) {
			return c;
		}
		// Recorremos todos los posibles criterios para filtrar:
		// Libro
		if (filter.getLibro() != null && filter.getLibro().getId() != null) {
			c.add(Restrictions.eq("libro", filter.getLibro()));
		}
		// Usuario
		if (filter.getUsuario() != null && filter.getUsuario().getId() != null) {
			c.add(Restrictions.eq("usuario", filter.getUsuario()));
		}
		// MeGusta
		if (filter.getMegusta() != null) {
			c.add(Restrictions.eq("megusta", filter.getMegusta()));
		}
		
		return c;
	}

	@Override
	protected Map<String, String> getCambios(Bean viejo, Bean nuevo) {
		return new HashMap<>();
	}
}
