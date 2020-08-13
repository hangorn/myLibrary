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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
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
import es.magDevs.myLibrary.model.beans.Usuario;
import es.magDevs.myLibrary.model.dao.UsuarioDao;

/**
 * Acceso a los datos de los usuarios, usando hibernate
 * 
 * @author javier.vaquero
 *
 */
@SuppressWarnings("unchecked")
public class HibUsuarioDao extends HibAbstractDao implements UsuarioDao {
	public HibUsuarioDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.USERS_TABLE);
	}

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
	@Override
	protected Map<String, Boolean> getOrders() {
		LinkedHashMap<String, Boolean> orders = new LinkedHashMap<String, Boolean>();
		orders.put("username", true);
		return orders;
	}

	/**
	 * Metodo con el que se obtendra el filtro de usuarios
	 * 
	 * @param s
	 * @param filter
	 *            bean con los campos a filtrar
	 * @return
	 */
	@Override
	protected Criteria getFilters(Session session, Bean f) {
		Usuario filter = (Usuario) f;
		Criteria c = session.createCriteria(Usuario.class);
		if (filter == null) {
			return c;
		}
		// Nombre de usuario
		if (!StringUtils.isBlank(filter.getUsername())) {
			c.add(Restrictions.like("username", "%" + filter.getUsername() + "%"));
		}
		// Email
		if (!StringUtils.isBlank(filter.getEmail())) {
			c.add(Restrictions.like("email", "%" + filter.getEmail() + "%"));
		}
		// Nombre
		if (!StringUtils.isBlank(filter.getNombre())) {
			c.add(Restrictions.like("nombre", "%" + filter.getNombre() + "%"));
		}
		// Activo
		if (filter.getEnabled() != null) {
			c.add(Restrictions.eq("enabled", filter.getEnabled()));
		}
		// Administrador
		if (filter.getAdmin() != null) {
			c.add(Restrictions.eq("admin", filter.getAdmin()));
		}
		return c;
	}
	
	@SuppressWarnings({ "rawtypes" })
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
					.add(Projections.property("username"))
					.add(Projections.property("password"))
					.add(Projections.property("email"))
					.add(Projections.property("nombre"))
					.add(Projections.property("enabled"))
					.add(Projections.property("admin"))
					.add(Projections.sqlProjection("(SELECT count(1) FROM prestamos WHERE this_.id=prestamos.usuario) count_prestamos", new String[]{"count_prestamos"}, new Type[]{StandardBasicTypes.INTEGER}))
					.add(Projections.sqlProjection("(SELECT count(1) FROM pendientes WHERE this_.id=pendientes.usuario) count_pendientes", new String[]{"count_pendientes"}, new Type[]{StandardBasicTypes.INTEGER}))
					.add(Projections.sqlProjection("(SELECT count(1) FROM leidos WHERE this_.id=leidos.usuario) count_leidos", new String[]{"count_leidos"}, new Type[]{StandardBasicTypes.INTEGER}));
			query.setProjection(projection);
			List<Object[]> l = query.list();
			List<Usuario> data = new ArrayList<>();
			// Recorremos los datos obtenidos para convertirlos en objetos
			for (Object[] objects : l) {
				Usuario usuario = new Usuario();
				usuario.setId((Integer) objects[0]);
				usuario.setUsername((String) objects[1]);
				usuario.setPassword((String) objects[2]);
				usuario.setEmail((String) objects[3]);
				usuario.setNombre((String) objects[4]);
				usuario.setEnabled((Boolean) objects[5]);
				usuario.setAdmin((Boolean) objects[6]);

				usuario.setPrestamos((Integer) objects[7]);
				usuario.setPendientes((Integer) objects[8]);
				usuario.setLeidos((Integer) objects[9]);
				
				data.add(usuario);
			}
			s.getTransaction().commit();
			return data;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int updatePassword(Usuario usuario, String newPassword)
			throws Exception {
		Session session = getSession();
		if (!(session.isOpen() && session.getTransaction() != null)) {
			return 0;
		}
		return session.createQuery("UPDATE Usuario SET password=:newPassword"
				+ " WHERE username=:username AND password=:oldPassword")
				.setParameter("newPassword", newPassword)
				.setParameter("username", usuario.getUsername())
				.setParameter("oldPassword", usuario.getPassword())
				.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	public int updatePermission(Usuario usuario) throws Exception {
		Session session = getSession();
		if (!(session.isOpen() && session.getTransaction() != null)) {
			return 0;
		}
		String params = "";
		if (usuario.getEnabled() != null) {
			params = "enabled=:enabled ";
		}
		if (usuario.getAdmin() != null) {
			if (!params.isEmpty()) {
				params = params + "AND ";
			}
			params = "admin=:admin ";
		}
		if (params.isEmpty()) {
			return 0;
		}
		Query query = session.createQuery(
				"UPDATE Usuario SET " + params + "WHERE username=:username")
				.setParameter("username", usuario.getUsername());
		if (usuario.getEnabled() != null) {
			query.setParameter("enabled", usuario.getEnabled());
		}
		if (usuario.getAdmin() != null) {
			query.setParameter("admin", usuario.getAdmin());
		}
		return query.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	public Usuario getUser(String username) throws Exception {
		if (StringUtils.isBlank(username)) {
			return null;
		}
		Criteria c = getSession().createCriteria(Usuario.class);
		c.add(Restrictions.eq("username",username));
		return (Usuario) c.uniqueResult();
	}

	/**
	 * {@inheritDoc}
	 */
	public String insert(Usuario data) {
		Session session = getSession();
		if (!(session.isOpen() && session.getTransaction() != null)) {
			return null;
		}
		if (data.getAdmin() == null) {
			data.setAdmin(new Boolean(false));
		}
		if (data.getEnabled() == null) {
			data.setEnabled(new Boolean(true));
		}
		return (String) session.save(data);
	}

	@Override
	public List<Usuario> getUsers(String start) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Object[]> l;
			if (StringUtils.isNotBlank(start)) {
				l = s.createQuery("SELECT id, username, nombre FROM Usuario WHERE nombre LIKE :nombre ORDER BY nombre").setParameter("nombre", start + "%").list();
			} else {
				l = s.createQuery("SELECT id, username, nombre FROM Usuario ORDER BY nombre").list();
			}
			List<Usuario> list = new ArrayList<>(l.size());
			for (Object[] result : l) {
				list.add(new Usuario((Integer)result[0], (String)result[1], null, null, (String)result[2], null, null));
			}
			s.getTransaction().commit();
			return list;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}