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

import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Libro;

/**
 * Interfaz para el acceso a los datos de colecciones
 * 
 * @author javier.vaquero
 * 
 */
public interface ColeccionDao extends AbstractDao {

	/**
	 * Obtiene una lista de las colecciones cuyo nombre empieza por la
	 * sugerencia indicada
	 * 
	 * @param start
	 * @return
	 * @throws Exception 
	 */
	public List<Coleccion> getColecciones(String start) throws Exception;

	/**
	 * Obtiene una lista de las colecciones cuyo nombre empieza por la
	 * sugerencia indicada y pertenecen a la editorial seleccionada
	 * 
	 * @param start
	 * @return
	 * @throws Exception 
	 */
	public List<Coleccion> getColecciones(String start, Integer idEditorial) throws Exception;

	/**
	 * Obtiene una lista de los libros que tiene una coleccion
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<Libro> getLibrosColeccion(Integer id) throws Exception;
}
