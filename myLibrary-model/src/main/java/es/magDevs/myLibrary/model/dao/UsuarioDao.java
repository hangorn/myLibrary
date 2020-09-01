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
package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Usuario;

/**
 * Interfaz para el acceso a los datos de usuarios
 * 
 * @author javier.vaquero
 * 
 */
public interface UsuarioDao extends AbstractDao {

	/**
	 * Actualiza la contraseña del usuario indicado con la nueva contraseña
	 * suministrada. Los datos del usuario deben contener tanto el nombre de
	 * usuario, como su antigua contraseña.
	 * 
	 * @param usuario datos del usuario, necesarios nombre y contraseña antigua
	 * @param newPassword nueva contraseña
	 * @return
	 * @throws Exception
	 */
	public int updatePassword(Usuario usuario, String newPassword) throws Exception;

	/**
	 * Fija la contraseña indicada para el usuario si el usuario no tiene una contraseña previa
	 * @param userId
	 * @param password
	 * @return numero de cambios
	 */
	public int createPassword(Integer userId, String password);

	/**
	 * Actualiza los permisos del usuario indicado
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public int updatePermission(Usuario usuario) throws Exception;

	/**
	 * Obtiene los datos de un usuario a partir de su nombre
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public Usuario getUser(String username) throws Exception;

	/**
	 * Crea un nuevo usurio con los datos suministrados
	 * @param data
	 * @return
	 */
	public String insert(Usuario data);
	
	/**
	 * Obtiene una lista de las usuarios cuyo nombre empieza por la
	 * sugenrencia indicada
	 * 
	 * @param start
	 * @return
	 * @throws Exception 
	 */
	public List<Usuario> getUsers(String start) throws Exception;
}