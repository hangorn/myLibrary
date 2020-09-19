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
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Prestamo;
import es.magDevs.myLibrary.model.beans.Usuario;
import es.magDevs.myLibrary.model.commons.SqlOrder;
import es.magDevs.myLibrary.model.dao.PrestamoDao;

/**
 * Acceso a los datos de Prestamos, usando hibernate
 * 
 * @author javier.vaquero
 * 
 */
public class HibPrestamoDao extends HibAbstractDao implements PrestamoDao {

	public HibPrestamoDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.PRESTAMOS_TABLE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Boolean> getOrders() {
		Map<String, Boolean> orders = new LinkedHashMap<String, Boolean>();
		orders.put("fecha", false);
		return orders;
	}

	/** 
	 * {@inheritDoc}
	 */
	protected Criteria getFilters(Session session, Bean f) {
		Prestamo filter = (Prestamo)f;
		Criteria c = session.createCriteria(Prestamo.class);
		c.createAlias("usuario", "usr");
		c.createAlias("libro", "lib");
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
			c.add(Restrictions.like("lib.titulo", "%"+ filter.getLibro().getTitulo() + "%"));
		}
		// Usuario
		if (filter.getUsuario() != null && filter.getUsuario().getId() != null) {
			c.add(Restrictions.eq("usuario", filter.getUsuario()));
		}
		// Nombre de usuario
		if (filter.getUsuario() != null && StringUtils.isNotEmpty(filter.getUsuario().getNombre())) {
			c.add(Restrictions.like("usr.nombre", "%"+ filter.getUsuario().getNombre() + "%"));
		}
		return c;
	}

	@Override
	protected Map<String, String> getCambios(Bean viejo, Bean nuevo) {
		return new HashMap<>();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List getWithPag(Bean filter, int page, int pageSize) throws Exception {
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
			
			ProjectionList projection = Projections.projectionList()
					.add(Projections.property("id"))
					.add(Projections.property("fecha"))
					.add(Projections.property("lib.id"))
					.add(Projections.property("lib.titulo"))
					.add(Projections.property("usr.id"))
					.add(Projections.property("usr.nombre"))
					.add(Projections.sqlProjection("(SELECT GROUP_CONCAT(concat(ifnull(concat(a.nombre,' '), ''),a.apellidos) SEPARATOR ', ') "
							+ "FROM libros_autores la JOIN autores a ON la.autor=a.id WHERE la.libro=this_.libro) AS autores_txt", new String[]{"autores_txt"}, new Type[]{StandardBasicTypes.STRING}));
			query.setProjection(projection);
			List<Object[]> l = query.list();
			List<Prestamo> data = new ArrayList<>();
			// Recorremos los datos obtenidos para convertirlos en objetos
			for (Object[] objects : l) {
				Prestamo prestamo = new Prestamo();
				int i = 0;
				prestamo.setId((Integer) objects[i++]);
				prestamo.setFecha(string2Presentation((String) objects[i++]));
				prestamo.setLibro(new Libro());
				prestamo.getLibro().setId((Integer) objects[i++]);
				prestamo.getLibro().setTitulo((String) objects[i++]);
				prestamo.setUsuario(new Usuario());
				prestamo.getUsuario().setId((Integer) objects[i++]);
				prestamo.getUsuario().setNombre((String) objects[i++]);
				prestamo.setAutoresTxt((String) objects[i++]);
				data.add(prestamo);
			}
			s.getTransaction().commit();
			return data;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
