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
package es.magDevs.myLibrary.web.conf;

import org.apache.log4j.Logger;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.dao.UsuarioDao;
import es.magDevs.myLibrary.web.gui.beans.UserBean;

/**
 * Clase encargada de obtener los datos del usuario a autenticar
 * 
 * @author javier.vaquero
 *
 */
@Component(value = "userDetailsService")
public class ConfUserDetailsService implements UserDetailsService {
	private final Logger log = Logger.getLogger(ConfUserDetailsService.class);

	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		UsuarioDao dao = DaoFactory.getUsuarioDao();
		try {
			dao.beginTransaction();
			// Creamos los datos del usuario a partir de los obtenidos de la
			// BBDD, si no se obtiene ningun se lanzara un excepcion
			UserBean user = new UserBean(dao.getUser(username));
			// Creamos los detalles del usuario para Spring
			User userDetails = new User(user.getUsername(), user.getPassword(),
					user.isEnabled(), true, true, true, user.getAuths());
			dao.commitTransaction();
			return userDetails;
		} catch (Exception e) {
			dao.rollbackTransaction();
			// Si ocurre una excepcion, inidicamos a Spring que no existe el
			// usuario lanzando la excepcion correspondiente 
			log.error("Error al obtener los datos de un usuario al autenticarse", e);
			throw new UsernameNotFoundException("");
		}
	}
}