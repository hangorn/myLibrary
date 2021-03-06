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
import org.hibernate.sql.JoinType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Pendiente;
import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.beans.Usuario;
import es.magDevs.myLibrary.model.commons.SqlOrder;
import es.magDevs.myLibrary.model.dao.PendienteDao;

/**
 * Acceso a los datos de libros pendientes, usando hibernate
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
		orders.put("pendiente.fecha", true);
		return orders;
	}

	/** 
	 * {@inheritDoc}
	 */
	protected Criteria getFilters(Session session, Bean f) {
		Pendiente filter = (Pendiente)f;
		Criteria c = session.createCriteria(Pendiente.class, "pendiente");
		c.createAlias("usuario", "usuario");
		Criteria critLibros = c.createCriteria("pendiente.libro", "libro");
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
			c.add(Restrictions.like("libro.titulo", "%"+ filter.getLibro().getTitulo() + "%"));
		}
		// Autor del libro
		if (StringUtils.isNotEmpty(filter.getAutoresTxt())) {
			critLibros.add(Restrictions.sqlRestriction("{alias}.id IN (SELECT libro FROM libros_autores libA JOIN autores aut ON libA.autor=aut.id AND CONCAT(aut.nombre,aut.apellidos) like '%"+filter.getAutoresTxt()+"%')"));
		}
		// Usuario
		if (filter.getUsuario() != null && filter.getUsuario().getId() != null) {
			c.add(Restrictions.eq("usuario", filter.getUsuario()));
		}
		// Nombre de usuario
		if (filter.getUsuario() != null && StringUtils.isNotEmpty(filter.getUsuario().getNombre())) {
			c.add(Restrictions.like("usuario.nombre", "%"+ filter.getUsuario().getNombre() + "%"));
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

			query.createAlias("libro.ubicacion", "ubicacion", JoinType.LEFT_OUTER_JOIN);
			ProjectionList projection = Projections.projectionList()
					.add(Projections.property("id"))
					.add(Projections.property("fecha"))
					.add(Projections.property("libro.id"))
					.add(Projections.property("libro.titulo"))
					.add(Projections.property("usuario.id"))
					.add(Projections.property("usuario.nombre"))
					.add(Projections.sqlProjection("(SELECT GROUP_CONCAT(concat(ifnull(concat(a.nombre,' '), ''),a.apellidos) SEPARATOR ', ') "
							+ "FROM libros_autores la JOIN autores a ON la.autor=a.id WHERE la.libro=this_.libro) AS autores_txt", new String[]{"autores_txt"}, new Type[]{StandardBasicTypes.STRING}))
					.add(Projections.property("ubicacion.codigo"));
			query.setProjection(projection);
			List<Object[]> l = query.list();
			List<Pendiente> data = new ArrayList<>();
			// Recorremos los datos obtenidos para convertirlos en objetos
			for (Object[] objects : l) {
				Pendiente pendiente = new Pendiente();
				int i = 0;
				pendiente.setId((Integer) objects[i++]);
				pendiente.setFecha(string2Presentation((String) objects[i++]));
				pendiente.setLibro(new Libro());
				pendiente.getLibro().setId((Integer) objects[i++]);
				pendiente.getLibro().setTitulo((String) objects[i++]);
				pendiente.setUsuario(new Usuario());
				pendiente.getUsuario().setId((Integer) objects[i++]);
				pendiente.getUsuario().setNombre((String) objects[i++]);
				pendiente.setAutoresTxt((String) objects[i++]);
				pendiente.getLibro().setUbicacion(new Ubicacion());
				pendiente.getLibro().getUbicacion().setCodigo((String) objects[i++]);
				data.add(pendiente);
			}
			s.getTransaction().commit();
			return data;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
