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
import es.magDevs.myLibrary.model.beans.Leido;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Usuario;
import es.magDevs.myLibrary.model.dao.LeidoDao;

/**
 * Acceso a los datos de libros leidos, usando hibernate
 * 
 * @author javier.vaquero
 * 
 */
public class HibLeidoDao extends HibAbstractDao implements LeidoDao {

	public HibLeidoDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.LEIDO_TABLE);
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
		Leido filter = (Leido)f;
		Criteria c = session.createCriteria(Leido.class, "leido");
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
		if (filter.getUsuario() != null && filter.getUsuario().getId() != null) {
			c.add(Restrictions.eq("usuario", filter.getUsuario()));
		}
		// Nombre de usuario
		if (filter.getUsuario() != null && StringUtils.isNotEmpty(filter.getUsuario().getNombre())) {
			c.createCriteria("usuario").add(Restrictions.like("nombre", "%"+ filter.getUsuario().getNombre() + "%"));
		}
		
		// Fechas
		if (filter.getFechaMin() != null) {
			c.add(Restrictions.ge("fecha", filter.getFechaMin()));
		}
		if (filter.getFechaMax() != null) {
			c.add(Restrictions.le("fecha", filter.getFechaMax()));
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
			for (Entry<String, Boolean>  orderData : getOrders().entrySet()) {
				Property field = Property.forName(orderData.getKey());
				Order order = orderData.getValue() ? field.asc() : field.desc();
				query.addOrder(order);
			}
			
			ProjectionList projection = Projections.projectionList()
					.add(Projections.property("id"))
					.add(Projections.property("fecha"))
					.add(Projections.property("libro"))
					.add(Projections.property("usuario"))
					.add(Projections.sqlProjection("(SELECT GROUP_CONCAT(concat(ifnull(concat(a.nombre,' '), ''),a.apellidos) SEPARATOR ', ') "
							+ "FROM libros_autores la JOIN autores a ON la.autor=a.id WHERE la.libro=this_.id) AS autores_txt", new String[]{"autores_txt"}, new Type[]{StandardBasicTypes.STRING}));
			query.setProjection(projection);
			List<Object[]> l = query.list();
			List<Leido> data = new ArrayList<>();
			// Recorremos los datos obtenidos para convertirlos en objetos
			for (Object[] objects : l) {
				Leido leido = new Leido();
				leido.setId((Integer) objects[0]);
				leido.setFecha((Integer) objects[1]);
				leido.setFechaTxt(int2Presentation((Integer) objects[1]));
				leido.setLibro((Libro) objects[2]);
				leido.setUsuario((Usuario) objects[3]);
				leido.setAutoresTxt((String) objects[4]);
				data.add(leido);
			}
			s.getTransaction().commit();
			return data;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
