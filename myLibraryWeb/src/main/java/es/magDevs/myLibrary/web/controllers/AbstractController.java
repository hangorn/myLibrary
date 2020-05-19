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
package es.magDevs.myLibrary.web.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;

import es.magDevs.myLibrary.model.Constants.ACTION;
import es.magDevs.myLibrary.model.Constants.RELATED_ACTION;
import es.magDevs.myLibrary.model.Constants.SECTION;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.web.controllers.main.Controller;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.PaginationManager;

/**
 * Controlador con las acciones comunes para todos los controladores
 * 
 * @author javier.vaquero
 * 
 */
public abstract class AbstractController implements Controller {
	public AbstractController(MessageSource messageSource) {
		this.messageSource = messageSource;
		this.pagManager = new PaginationManager(messageSource, 0);
	}

	/* *****************************************
	 * ********** METODOS COMUNES **************
	 * *****************************************
	 */

	/**
	 * Guarda en el modelo de datos de Spring los datos a listar, los datos de
	 * paginacion, y los datos del filtro
	 * 
	 * @param model
	 */
	protected void setModelData(Model model) {
		model.addAttribute("data", data);
		model.addAttribute("page", pagManager.getPageLabel());
		model.addAttribute("pageCount", pagManager.getPageCountLabel());
		model.addAttribute("filter", filter == null ? getNewFilter() : filter);
		model.addAttribute("currentURL", getSection().get());
		model.addAttribute("multiUpdateDisabled", isMultiUpdateDisabled());
	}

	/**
	 * Metodo para realizar las operaciones oportunas cuando se produzca una
	 * excepcion (registrarla en el log, iniciar variables, ...)
	 * 
	 * @param param
	 *            accion que realizaba el controlador cuando se produjo la
	 *            excepcion, mas alguna aclaracion que se quiera añadir a la
	 *            traza del log
	 * @return mensaje a mostrar al usuario
	 */
	protected String manageException(String param, Exception e) {
		getLog().error(
				"Error en el controlador '" + getSection().get()
						+ "' para la ruta /" + getSection().get() + "?" + param,
				e);
		return messageSource.getMessage("error", null,
				LocaleContextHolder.getLocale());
	}

	/* *****************************************
	 * ********* METODOS ABSTRACTOS ************
	 * *****************************************
	 */

	/**
	 * Obtiene la seccion que gestiona el controlador
	 * 
	 * @return enumeracion de tipo {@link SECTION}
	 */
	protected abstract SECTION getSection();

	/**
	 * Obtiene el log correspondiente al controlador
	 * 
	 * @return log del controlador
	 */
	protected abstract Logger getLog();

	/**
	 * Obtiene el DAO correspodiente de la seccion para el acceso a datos
	 * 
	 * @return DAO de la seccion correspondiente
	 */
	protected abstract AbstractDao getDao();

	/**
	 * Obtiene el filtro para la seccion correspondiente
	 * 
	 * @return filtro para la seccion correspondiente
	 */
	protected abstract Bean getNewFilter();

	/**
	 * Debe procesar el filtro verificar que realmente se este filtrando la
	 * informacion, y si no devuelva <code>null</code>
	 * 
	 * @param filter
	 *            filtro a procesar
	 * @return el mismo filtro ya procesado
	 */
	protected abstract Bean processFilter(Bean filter);

	/**
	 * Debe procesar el nuevo dato suministrado para que no ocurran errores,
	 * rellenar valores por defecto, y comprobar si los valores obligatorios
	 * estan rellenados
	 * 
	 * @param filter
	 *            filtro a procesar
	 * @return el mismo filtro ya procesado
	 */
	protected abstract boolean processNewData(Bean newData);

	/* *****************************************
	 * ********* ATRIBUTOS COMUNES *************
	 * *****************************************
	 */
	// Clase para obtener los mensajes
	protected MessageSource messageSource;
	// Gestor de paginacion
	protected PaginationManager pagManager;
	// Datos obtenidos que se mostraran en el listado
	@SuppressWarnings("rawtypes")
	protected List data;
	// Filtro
	protected Bean filter;
	// ID del elemento que se esta modificando
	protected Integer modifiedElementId;
	// IDs de los elementos que se estan modificando a la vez
	protected List<Integer> modifiedElementsIds;

	/* *****************************************
	 * ************* ACCIONES ******************
	 * *****************************************
	 */
	public String list(Model model) {
		String msg = "";
		try {
			// Iniciamos paginacion
			AbstractDao dao = getDao();
			pagManager.reset(dao.getCount(filter));
			// Reiniciamos el filtro de busqueda si no hay un filtro valido
			filter = processFilter(filter);
			// Buscamos los datos
			data = dao.getWithPag(filter, pagManager.getPage() - 1,
					pagManager.getPageSize());
		} catch (Exception e) {
			msg = manageException("", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}

	public String listNext(Model model) {
		String msg = "";
		try {
			// Realizamos la paginacion
			AbstractDao dao = getDao();
			if (pagManager.getElementsCount() <= 0) {
				pagManager.reset(dao.getCount(filter));
			}
			if (pagManager.next()) {
				data = dao.getWithPag(filter, pagManager.getPage() - 1,
						pagManager.getPageSize());
			}
		} catch (Exception e) {
			msg = manageException("next", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}

	public String listPrevious(Model model) {
		String msg = "";
		try {
			// Realizamos la paginacion
			AbstractDao dao = getDao();
			if (pagManager.getElementsCount() <= 0) {
				pagManager.reset(dao.getCount(filter));
			}
			if (pagManager.previous()) {
				data = dao.getWithPag(filter, pagManager.getPage() - 1,
						pagManager.getPageSize());
			}
		} catch (Exception e) {
			msg = manageException("previous", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}

	public String listStart(Model model) {
		String msg = "";
		try {
			// Realizamos la paginacion
			AbstractDao dao = getDao();
			if (pagManager.getElementsCount() <= 0) {
				pagManager.reset(dao.getCount(filter));
			}
			if (pagManager.start()) {
				data = dao.getWithPag(filter, pagManager.getPage() - 1,
						pagManager.getPageSize());
			}
		} catch (Exception e) {
			msg = manageException("start", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}

	public String listEnd(Model model) {
		String msg = "";
		try {
			// Realizamos la paginacion
			AbstractDao dao = getDao();
			if (pagManager.getElementsCount() <= 0) {
				pagManager.reset(dao.getCount(filter));
			}
			if (pagManager.end()) {
				data = dao.getWithPag(filter, pagManager.getPage() - 1,
						pagManager.getPageSize());
			}
		} catch (Exception e) {
			msg = manageException("end", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}

	public String listPageSize(String pageSize, Model model) {
		String msg = "";
		try {
			// Realizamos la paginacion
			AbstractDao dao = getDao();
			if (pagManager.getElementsCount() <= 0) {
				pagManager.reset(dao.getCount(filter));
			}
			if (pagManager.setPageSize(pageSize)) {
				data = dao.getWithPag(filter, pagManager.getPage() - 1,
						pagManager.getPageSize());
				model.addAttribute("selectedPageSize", pageSize);
			} else {
				model.addAttribute("selectedPageSize",
						"" + pagManager.getPageSize());
			}
		} catch (Exception e) {
			msg = manageException("pageSize", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		model.addAttribute("selectedPageSize", pageSize);
		setModelData(model);
		return "commons/body";
	}

	public String filter(Bean f, Model model) {
		String msg = "";
		try {
			// Guardamos el filtro
			filter = processFilter(f);
			// Iniciamos paginacion
			AbstractDao dao = getDao();
			pagManager.reset(dao.getCount(filter));
			data = dao.getWithPag(filter, pagManager.getPage() - 1,
					pagManager.getPageSize());
		} catch (Exception e) {
			msg = manageException("search", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}
	
	public String multiupdate(Model model) {
		String msg = "";
		try {
			AbstractDao dao = getDao();
			data = dao.getWithPag(filter, 0, Integer.MAX_VALUE);
		} catch (Exception e) {
			msg = manageException("end", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		model.addAttribute("multiselect", true);
		return "commons/body";
	}
	
	public String multiupdate(Bean f, Model model) {
		String msg = "";
		try {
			// Guardamos el filtro
			filter = processFilter(f);
			AbstractDao dao = getDao();
			data = dao.getWithPag(filter, 0, Integer.MAX_VALUE);
		} catch (Exception e) {
			msg = manageException("end", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		model.addAttribute("multiselect", true);
		return "commons/body";
	}

	public String create(Integer index, Model model) {
		Bean elementData = null;
		// Mensaje a mostrar en caso de error
		String msg = "";
		// Si tenemos un indice valido
		if (index >= 0 && data != null) {
			try {
				// Obtenemos todos los datos del elemento seleccionado
				elementData = getDao().get(((Bean) data.get(index)).getId());
			} catch (Exception e) {
				elementData = getNewFilter();
				msg = manageException("create", e);
			}
		} else {
			// Creamos un elemento vacio, para que no de fallos al intentar
			// acceder a algunos campos
			elementData = getNewFilter();
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.CREATE,
				getSection()));
		// Fijamos variables para la vista
		model.addAttribute("elementData", elementData);
		model.addAttribute("currentURL", getSection().get());
		return "commons/body";
	}

	public String delete(Integer index, Model model) {
		// Mensaje a mostrar en caso de error
		String msg = "";
		AbstractDao dao = getDao();
		// Borramos el elemento indicado por el indice recibido
		try {
			dao.beginTransaction();
			dao.delete(data.get(index));
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			msg = manageException("delete, al borrar un dato: " + index + " "
					+ data.get(index), e);
		}
		// Volvemos a listar
		try {
			// Iniciamos paginacion
			pagManager.reset(dao.getCount());
			// Buscamos los datos
			data = dao.getWithPag(pagManager.getPage() - 1,
					pagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			//filter = null;
		} catch (Exception e) {
			msg = manageException("delete", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}

	public String update(Integer index, Model model) {
		Integer id = null;
		if (index != null && index >= 0 && data != null) {
			id = ((Bean) data.get(index)).getId();
		}
		return updateFromId(id, model);
	}

	public String updateFromId(Integer id, Model model) {
		Bean elementData = null;
		String msg = "";
		// Si tenemos un indice valido
		if (id != null && id >= 0 && data != null) {
			try {
				// Obtenemos todos los datos del elemento seleccionado
				elementData = getDao().get(id);
			} catch (Exception e) {
				elementData = getNewFilter();
				msg = manageException("update", e);
			}
		} else {
			// Creamos un elemento vacio, para que no de fallos al intentar
			// acceder a algunos campos
			elementData = getNewFilter();
			msg = messageSource.getMessage(getSection().get()
					+ ".menu.update.noIndexMsg", null,
					LocaleContextHolder.getLocale());
		}
		modifiedElementId = elementData.getId();
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.UPDATE,
				getSection()));
		model.addAttribute("elementData", elementData);
		model.addAttribute("currentURL", getSection().get());
		return "commons/body";
	}

	public String read(Integer index, Model model) {
		Bean elementData = null;
		String msg = "";
		// Si tenemos un indice valido
		if (index >= 0 && data != null) {
			// Obtenemos todos los datos del elemento seleccionado
			try {
				elementData = getDao().get(((Bean) data.get(index)).getId());
			} catch (Exception e) {
				elementData = getNewFilter();
				msg = manageException("read", e);
			}
		} else {
			// Creamos un elemento vacio, para que no de fallos al intentar
			// acceder a algunos campos
			elementData = getNewFilter();
			msg = messageSource.getMessage(getSection().get()
					+ ".menu.read.noIndexMsg", null,
					LocaleContextHolder.getLocale());
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.READ,
				getSection()));
		model.addAttribute("elementData", elementData);
		model.addAttribute("currentURL", getSection().get());
		return "commons/body";
	}
	
	public String readFromId(Integer id, Model model) {
		Bean elementData = null;
		String msg = "";
		// Obtenemos todos los datos del elemento seleccionado
		try {
			elementData = getDao().get(id);
		} catch (Exception e) {
			elementData = getNewFilter();
			msg = manageException("read", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.READ,
				getSection()));
		model.addAttribute("elementData", elementData);
		model.addAttribute("currentURL", getSection().get());
		return "commons/body";
	}

	public String acceptCreation(Bean newElement, Model model) {
		String msg = "";
		AbstractDao dao = getDao();
		try {
			dao.beginTransaction();
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!processNewData(newElement)) {
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager.get(
						messageSource.getMessage("error." + getSection().get()
								+ ".fieldsNotRight", null,
								LocaleContextHolder.getLocale()),
						ACTION.CREATE, getSection()));
				model.addAttribute("elementData", newElement);
				dao.rollbackTransaction();
				return "commons/body";
			}
			// Guardamos el dato
			dao.insert(newElement);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			msg = manageException("acceptCreation, creando el nuevo dato", e);
		}
		try {
			// Iniciamos paginacion
			pagManager.reset(dao.getCount(filter));
			// Buscamos los datos
			data = dao.getWithPag(filter, pagManager.getPage() - 1,
					pagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			//filter = null;
		} catch (Exception e) {
			msg = manageException("acceptCreation, cargando los datos", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}

	public String acceptUpdate(Bean newElement, Model model) {
		newElement.setId(modifiedElementId);
		String msg = "";
		AbstractDao dao = getDao();
		try {
			dao.beginTransaction();
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!processNewData(newElement)) {
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager.get(
						messageSource.getMessage("error." + getSection().get()
								+ ".fieldsNotRight", null,
								LocaleContextHolder.getLocale()),
						ACTION.UPDATE, getSection()));
				model.addAttribute("elementData", newElement);
				dao.rollbackTransaction();
				return "commons/body";
			}
			// Guardamos el dato
			dao.update(newElement);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			msg = manageException("acceptUpdate, modificando el dato", e);
		}

		try {
			// Iniciamos paginacion
			pagManager.reset(dao.getCount(filter));
			// Buscamos los datos
			data = dao.getWithPag(filter, pagManager.getPage() - 1,
					pagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			//filter = null;
		} catch (Exception e) {
			msg = manageException("acceptUpdate, cargando los datos", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}
	
	public String acceptMultiupdateSelection(Collection<Integer> index, Model model) {
		Bean elementData = null;
		String msg = "";
		// Si tenemos un indice valido
		if (index != null && !index.isEmpty() && data != null) {
			try {
				// Obtenemos todos los datos seleccionados
				modifiedElementsIds = new ArrayList<>(index.size());
				for (Integer i : index) {
					Bean seleccionado = (Bean) data.get(i);
					modifiedElementsIds.add(seleccionado.getId());
				}
				elementData = getNewFilter();
			} catch (Exception e) {
				elementData = getNewFilter();
				msg = manageException("acceptMultiupdateSelection", e);
			}
		} else {
			// Creamos un elemento vacio, para que no de fallos al intentar
			// acceder a algunos campos
			elementData = getNewFilter();
			msg = messageSource.getMessage(getSection().get()
					+ ".menu.multiupdate.noIndexMsg", null,
					LocaleContextHolder.getLocale());
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.MULTIUPDATE,
				getSection()));

		model.addAttribute("confirmMsg", messageSource.getMessage("multiUpdateConfirmMessage", null, LocaleContextHolder.getLocale())+index.size()+" "+
				messageSource.getMessage("menu."+getSection().get()+".text", null, LocaleContextHolder.getLocale())+"?");
		model.addAttribute("elementData", elementData);
		model.addAttribute("currentURL", getSection().get());
		return "commons/body";
	}

	protected boolean isMultiUpdateDisabled() {
		return false;
	}
	
	public String manageRelatedData(RELATED_ACTION action, String dataType, String data) {
		return "FAIL";
	}
	
	public String acceptMultiupdate(Bean newElement, Model model) {
		AbstractDao dao = getDao();
		String msg = "";
		try {
			dao.beginTransaction();
			processNewData(newElement);
			// Guardamos el dato
			dao.multiupdate(newElement, modifiedElementsIds);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			msg = manageException("acceptUpdate, modificando el dato", e);
		}

		try {
			// Iniciamos paginacion
			pagManager.reset(dao.getCount(filter));
			// Buscamos los datos
			data = dao.getWithPag(filter, pagManager.getPage() - 1,
					pagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			//filter = null;
		} catch (Exception e) {
			msg = manageException("acceptUpdate, cargando los datos", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}

	@SuppressWarnings("rawtypes")
	public List getData(String hint) {
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public List getData(String hint, Integer id) {
		return getData(hint);
	}
}
