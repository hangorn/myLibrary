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
package es.magDevs.myLibrary.web.controllers.main;

import java.util.Collection;
import java.util.List;

import org.springframework.ui.Model;

import es.magDevs.myLibrary.model.Constants.RELATED_ACTION;
import es.magDevs.myLibrary.model.beans.Bean;

/**
 * Interfaz que define las acciones que pueden realizar todos los controladores
 * 
 * @author javier.vaquero
 *
 */
public interface Controller {
	/**
	 * Accion para mostrar el listado de datos
	 * 
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String list(Model model);

	/**
	 * Accion para mostrar la siguiente pagina en el listado de datos
	 * 
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String listNext(Model model);

	/**
	 * Accion para mostrar la pagina anterior en el listado de datos
	 * 
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String listPrevious(Model model);

	/**
	 * Accion para mostrar la primera pagina en el listado de datos
	 * 
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String listStart(Model model);

	/**
	 * Accion para mostrar la ultima pagina en el listado de datos
	 * 
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String listEnd(Model model);

	/**
	 * Accion para cambiar el tamaño de pagina en el listado de datos
	 * 
	 * @param model
	 *            modelo de datos de SPRING
	 * @param pageSize
	 *            tamaño de pagina a mostrar
	 * @return
	 */
	public String listPageSize(String pageSize, Model model);

	/**
	 * Accion para mostrar el listado de datos aplicando el filtro indicado
	 * 
	 * @param f
	 *            filtro a aplicar
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String filter(Bean f, Model model);

	/**
	 * Accion para mostrar el dialogo de creacion
	 * 
	 * @param index
	 *            indice del elemento del listado del que se reaprovecharan los
	 *            datos
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String create(Integer index, Model model);

	/**
	 * Accion para borrar el elemento indicado
	 * 
	 * @param index
	 *            indice del elemento del listado a borrar
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String delete(Integer index, Model model);

	/**
	 * Accion para mostrar el dialogo de modificacion del elemento indicado
	 * 
	 * @param index
	 *            indice del elemento del listado a modificar
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String update(Integer index, Model model);

	/**
	 * Accion para mostrar el dialogo de modificacion del elemento indicado
	 * 
	 * @param id
	 *            id de base de datos del elemento a modificar
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String updateFromId(Integer id, Model model);

	/**
	 * Accion para mostrar el listado para seleccionar los elementos a modificar
	 * 
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String multiupdate(Model model);

	/**
	 * Accion para mostrar el listado para seleccionar los elementos a modificar
	 * 
	 * @param model
	 *            modelo de datos de SPRING
	 * @param filter
	 *            datos por lo que filtrar el listado
	 * @return
	 */
	public String multiupdate(Bean filter, Model model);

	/**
	 * Accion para mostrar el dialogo de modificacion una vez seleccionados
	 * varios elementos
	 * 
	 * @param index
	 *            lista de indices de los elementos a modificar
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String acceptMultiupdateSelection(Collection<Integer> index, Model model);

	/**
	 * Accion para confirmar la modificacion varios elementos simultaneamente
	 * 
	 * @param newData
	 *            datos del elemento a modificar
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String acceptMultiupdate(Bean newData, Model model);

	/**
	 * Accion para mostrar el dialgo de informacion del elemento indicado
	 * 
	 * @param index
	 *            indice del elemento del listado a mostrar
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String read(Integer index, Model model);

	/**
	 * Accion para mostrar el dialgo de informacion del elemento indicado por id
	 * de base de datos
	 * 
	 * @param index
	 *            id de base de datos del elemento a mostrar
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String readFromId(Integer id, Model model);

	/**
	 * Accion para confirmar la creacion de un nuevo elemento
	 * 
	 * @param newElement
	 *            datos del nuevo elemento a crear
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String acceptCreation(Bean newElement, Model model);

	/**
	 * Accion para confirmar la modificacion de un elemento
	 * 
	 * @param newElement
	 *            datos del elemento a modificar
	 * @param model
	 *            modelo de datos de SPRING
	 * @return
	 */
	public String acceptUpdate(Bean newElement, Model model);

	/**
	 * Accion sobre los datos asociados al elemento actual
	 * 
	 * @param action
	 *            tipo de accion: añadir, crear o borrar,...
	 * @param dataType
	 *            tipo de dato asociado
	 * @param data
	 *            datos
	 * @return
	 */
	public String manageRelatedData(RELATED_ACTION action, String dataType, String data);

	/**
	 * Obtiene datos filtrados a partir de un indicio
	 * 
	 * @param hint
	 *            indicio por el que se filtraran los datos
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List getData(String hint);

	/**
	 * Obtiene datos filtrados a partir de un indicio
	 * 
	 * @param hint
	 *            indicio por el que se filtraran los datos
	 * 
	 * @param id
	 *            id por el que se filtraran los datos
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List getData(String hint, Integer id);

	/**
	 * Obtiene la clase del bean que gestiona este controlador
	 * 
	 * @return
	 */
	public Class<? extends Bean> getBeanClass();
}
