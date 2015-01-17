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

import es.magDevs.myLibrary.model.beans.Bean;

/**
 * Interfaz para la clase abstracta de los DAOs
 * 
 * @author javier.vaquero
 * 
 */
@SuppressWarnings("rawtypes")
public interface AbstractDao {
	/**
	 * Inicia una transaccion
	 */
	public void beginTransaction();

	/**
	 * Finaliza una transaccion
	 */
	public void commitTransaction();

	/**
	 * Cancela una transaccion
	 */
	public void rollbackTransaction();

	/**
	 * Inserta un nuevo dato en la base de datos
	 * 
	 * @param libro
	 *            datos a insertar
	 * @return id del dato introducido
	 */
	public int insert(Object data);

	/**
	 * Elimina un dato de la base de datos
	 * 
	 * @param data
	 *            objeto de la clase que se quiera borrar y que ha de contener
	 *            su ID
	 */
	public void delete(Object data);

	/**
	 * Modifica un dato de la base de datos
	 * 
	 * @param data
	 *            objeto de la clase que se quiera modificar
	 */
	public void update(Object data);
	
	/**
	 * Obtiene el dato referenciado por el ID suministrado
	 * 
	 * @param id
	 *            del dato a obtener
	 * @return dato
	 * @throws Exception 
	 */
	public Bean get(int id) throws Exception;
	

	/**
	 * * Obtiene una lista de datos paginada con las opiones indicadas
	 * 
	 * @param page
	 *            numero de pagina
	 * @param pageSize
	 *            tamaño de la pagina
	 * @return
	 * @throws Exception 
	 */
	public List getWithPag(int page, int pageSize) throws Exception;
	
	/**
	 * Obtiene una lista de datos, filtrados por los criterios indicados y
	 * paginada
	 * 
	 * @param filter
	 *            criterios de busqueda
	 * @param page
	 *            numero de pagina
	 * @param pageSize
	 *            tamaño de pagina
	 * @return
	 * @throws Exception 
	 */
	public List getWithPag(Bean filter, int page, int pageSize) throws Exception;
	
	/**
	 * Obtiene una lista de todos los datos
	 * @return
	 * @throws Exception
	 */
	public List getAll() throws Exception;
	
	/**
	 * Obtiene el numero total de datos
	 * 
	 * @return
	 * @throws Exception 
	 */
	public int getCount() throws Exception;

	/**
	 * Obtiene el numero total de datos filtrados por los criterios indicados
	 * 
	 * @param filter
	 *            criterios de busqueda
	 * @return
	 * @throws Exception 
	 */
	public int getCount(Bean filter) throws Exception;
}
