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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Modificacion;
import es.magDevs.myLibrary.model.beans.ModificacionCampo;
import es.magDevs.myLibrary.model.commons.SqlOrder;
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

	private SimpleDateFormat formatModelString;
	private SimpleDateFormat formatModelInt;
	private SimpleDateFormat formatPresentation;

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
	public Bean getWithTransaction(int id) throws Exception {
		return (Bean) getSession().createQuery("FROM " + table + " WHERE id=:id").setParameter("id", id).uniqueResult();
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
			if (filter != null && StringUtils.isNotEmpty(filter.getSortedColumn())) {
				query.addOrder(new SqlOrder(filter.getSortedColumn(), filter.getSortedDirection()));
			} else  {
				for (Entry<String, Boolean>  orderData : getOrders().entrySet()) {
					Property field = Property.forName(orderData.getKey());
					Order order = orderData.getValue() ? field.asc() : field.desc();
					query.addOrder(order);
				}
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
	public List getWithTransaction(Bean filter, int page, int pageSize) throws Exception {
		// Obtenemos el filtro
		Criteria query = getFilters(getSession(), filter);
		// Fijamos las opciones de paginacion
		if(pageSize > 0)  {
			query.setMaxResults(pageSize);
		}
		query.setFirstResult(page * pageSize);
		// Ordenamos por los ordenes indicados
		if (filter != null && StringUtils.isNotEmpty(filter.getSortedColumn())) {
			query.addOrder(new SqlOrder(filter.getSortedColumn(), filter.getSortedDirection()));
		} else  {
			for (Entry<String, Boolean>  orderData : getOrders().entrySet()) {
				Property field = Property.forName(orderData.getKey());
				Order order = orderData.getValue() ? field.asc() : field.desc();
				query.addOrder(order);
			}
		}
		return query.list();
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
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public void multiupdate(Bean data, List<Integer> ids) throws Exception {
		Session session = getSession();
		if (!session.isOpen() || session.getTransaction() == null) {
			return;
		}
		String fecha = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
		for (Integer id : ids) {
			Bean originalBean = (Bean) session.createQuery("FROM " + table + " WHERE id=:id").setParameter("id", id).uniqueResult();
			Map<String, String> cambios = getCambios(originalBean, data);
			if (!cambios.isEmpty()) {
				session.update(originalBean);
				Modificacion modificacion = new Modificacion(null, id, table, fecha);
				session.save(modificacion);
				for (Entry<String, String> cambio : cambios.entrySet()) {
					session.save(new ModificacionCampo(modificacion.getId(), cambio.getKey(), cambio.getValue()));
				}
			}
		}
	}
	
	/**
	 * Metodo que debe calcular los cambios realizados entre dos datos, para
	 * guardar antiguos en un historico. Ademas debe rellenar el bean viejo con
	 * los datos nuevos para actualizarlo
	 * 
	 * @param viejo
	 *            datos originales antes del cambio
	 * @param nuevo
	 *            datos nuevo que se guardaran
	 * @return mapa con los datos antiguos que se han modificado, como clave la
	 *         columna y como valor el dato
	 */
	protected Map<String, String> getCambios(Bean viejo, Bean nuevo) {
		return new HashMap<>();
	}
	
	protected String string2Presentation(String date) {
		if (date == null) {
			return null;
		}
		if (formatModelString == null) {
			formatModelString = new SimpleDateFormat(Constants.STRING_FORMAT);
		}
		if (formatPresentation == null) {
			formatPresentation = new SimpleDateFormat(Constants.PRESENTATION_FORMAT);
		}
		try {
			return formatPresentation.format(formatModelString.parse(date));
		} catch (ParseException e) {
			return date;
		}
	}
	
	protected String int2Presentation(String date) {
		if (date == null) {
			return null;
		}
		if (formatModelInt == null) {
			formatModelInt = new SimpleDateFormat(Constants.INT_FORMAT);
		}
		if (formatPresentation == null) {
			formatPresentation = new SimpleDateFormat(Constants.PRESENTATION_FORMAT);
		}
		try {
			return formatPresentation.format(formatModelInt.parse(date));
		} catch (ParseException e) {
			return date;
		}
	}
	
	protected String int2Presentation(Integer date) {
		if (date == null) {
			return null;
		}
		return int2Presentation(""+date);
	}
}
