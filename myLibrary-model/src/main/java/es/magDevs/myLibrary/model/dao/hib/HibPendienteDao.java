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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Pendiente;
import es.magDevs.myLibrary.model.dao.PendienteDao;

/**
 * Acceso a los datos de Prestamos, usando hibernate
 * 
 * @author javier.vaquero
 * 
 */
public class HibPendienteDao extends HibAbstractDao implements PendienteDao {

	public HibPendienteDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.PENDIENTE_TABLE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Boolean> getOrders() {
		Map<String, Boolean> orders = new LinkedHashMap<String, Boolean>();
		orders.put("libro.annoCompra", false);
		return orders;
	}

	/** 
	 * {@inheritDoc}
	 */
	protected Criteria getFilters(Session session, Bean f) {
		Pendiente filter = (Pendiente)f;
		Criteria c = session.createCriteria(Pendiente.class, "pendiente");
		if (filter == null) {
			return c;
		}

		// Recorremos todos los posibles criterios para filtrar:
		// Libro
		if (filter.getLibro() != null && filter.getLibro().getId() != null) {
			c.add(Restrictions.eq("libro", filter.getLibro()));
		}
		// Titulo del libro
		if (filter.getLibro() != null && StringUtils.isNotEmpty(filter.getLibro().getTitulo())) {
			c.createCriteria("libro").add(Restrictions.like("titulo", "%"+ filter.getLibro().getTitulo() + "%"));
		}
		// Usuario
		if (filter.getUsuario() != null && StringUtils.isNotEmpty(filter.getUsuario().getUsername())) {
			c.add(Restrictions.eq("usuario", filter.getUsuario()));
		}
		// Nombre de usuario
		if (filter.getUsuario() != null && StringUtils.isNotEmpty(filter.getUsuario().getNombre())) {
			c.createCriteria("usuario").add(Restrictions.like("nombre", "%"+ filter.getUsuario().getNombre() + "%"));
		}
		
		c.createAlias("pendiente.libro", "libro");
		return c;
	}

	@Override
	protected Map<String, String> getCambios(Bean viejo, Bean nuevo) {
		return new HashMap<>();
	}
}
