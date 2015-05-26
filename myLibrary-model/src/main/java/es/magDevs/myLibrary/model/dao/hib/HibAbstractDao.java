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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;

import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.dao.AbstractDao;

/**
 * Clase abstracta con los metodos comunes para los DAO de Hibernate
 * 
 * @author javier.vaquero
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class HibAbstractDao extends HibBasicDao implements AbstractDao {

	private String table;

	/**
	 * Metodo abstracto con el que se obtendra el filtro general para cualquier
	 * dato
	 * 
	 * @param s
	 * @param filter
	 *            bean con los campos a filtrar
	 * @return
	 */
	protected abstract Criteria getFilters(Session s, Bean filter);

	/**
	 * Metodo abstracto para obtener los ordenes con los que se ordenaran los
	 * resultados de las consultas, ordenados por prioridad. Se debe utilizar la
	 * implementacion {@link LinkedHashMap} para preservar el orden de
	 * inserccion
	 * 
	 * @return mapa con los nombres de los campos por los cuales se han de
	 *         ordenar como clave, y un boleano para saber si el orden es
	 *         ascendente o no
	 */
	protected abstract Map<String, Boolean> getOrders();

	public HibAbstractDao(SessionFactory sessionFactory, String table) {
		this.sessionFactory = sessionFactory;
		this.table = table;
	}

	/**
	 * {@inheritDoc}
	 */
	public int insert(Object data) {
		Session session = getSession();
		if (!session.isOpen() || session.getTransaction() == null) {
			return 0;
		}
		return (Integer) session.save(data);
	}

	/**
	 * {@inheritDoc}
	 */
	public void delete(Object data) {
		Session session = getSession();
		if (!session.isOpen() || session.getTransaction() == null) {
			return;
		}
		session.delete(data);
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(Object data) {
		Session session = getSession();
		if (!session.isOpen() || session.getTransaction() == null) {
			return;
		}
		session.update(data);
	}

	/**
	 * {@inheritDoc}
	 */
	public Bean get(int id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Bean bean = (Bean) s.createQuery("FROM " + table + " WHERE id=:id")
					.setParameter("id", id).uniqueResult();
			s.getTransaction().commit();
			return bean;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List getWithPag(int page, int pageSize) throws Exception {
		return getWithPag(null, page, pageSize);
	}

	/**
	 * {@inheritDoc}
	 */
	public List getWithPag(Bean filter, int page, int pageSize)
			throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			// Obtenemos el filtro
			Criteria query = getFilters(s, filter);
			// Fijamos las opciones de paginacion
			if(pageSize > 0)  {
				query.setMaxResults(pageSize);
			}
			query.setFirstResult(page * pageSize);
			// Ordenamos por los ordenes indicados
			for (Entry<String, Boolean>  orderData : getOrders().entrySet()) {
				Property field = Property.forName(orderData.getKey());
				Order order = orderData.getValue() ? field.asc() : field.desc();
				query.addOrder(order);
			}
			List l = query.list();
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
	public List getAll() throws Exception {
		return getWithPag(0, 0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getCount() throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Long count = (Long) s.createQuery("SELECT count(*) FROM "+table)
					.uniqueResult();
			s.getTransaction().commit();
			return count.intValue();
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
	public int getCount(Bean filter) throws Exception {
		if (filter == null) {
			return getCount();
		}
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Criteria query = getFilters(s, filter);
			query.setProjection(Projections.rowCount());
			Long count = (Long) query.uniqueResult();
			s.getTransaction().commit();
			return count.intValue();
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
