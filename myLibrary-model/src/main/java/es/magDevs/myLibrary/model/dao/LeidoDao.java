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

import es.magDevs.myLibrary.model.beans.Leido;

/**
 * Interfaz para el acceso a los datos de libros leidos
 * 
 * @author javier.vaquero
 * 
 */
public interface LeidoDao extends AbstractDao {

	/**
	 * Obtiene el historial de prestamos de un libro
	 * @param nombreUsuario texto para buscar usuarios
	 * @param idLibro del libro para el que se buscaran los prestamos
	 * @return datos de prestamos
	 * @throws Exception 
	 */
	List<Leido> getHistorialPrestamos(String nombreUsuario, Integer idLibro) throws Exception;

}
