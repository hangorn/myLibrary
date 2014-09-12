package es.magDevs.myLibrary.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.dao.TipoDao;
import es.magDevs.myLibrary.web.gui.beans.MenuItem;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;
import es.magDevs.myLibrary.web.gui.utils.PaginationManager;

/**
 * Controlador para la seccion de tipos
 * 
 * @type javi
 * 
 */
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class TypesController {
	/* *****************************************
	 * **************** COMUN ******************
	 * *****************************************
	 */
	/**
	 * Ejecuta las inicializaciones necesarias
	 */
	public void afterPropertiesSet() throws Exception {
		DaoFactory.init();
	}

	// Spring Message Source
	@Autowired
	private MessageSource messageSource;

	// LOG
	private static final Logger log = Logger.getLogger(TypesController.class);

	/**
	 * Lista con los datos de los elementos del menu
	 * 
	 * @return
	 */
	@ModelAttribute("menuItems")
	List<MenuItem> getMenuItems() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		// Obtenemos los elementos del menu
		String[] items = messageSource.getMessage("menu.items", null, null)
				.split(" ");
		for (int i = 0; i < items.length; i++) {
			MenuItem item = new MenuItem();
			item.setText(messageSource.getMessage("menu." + items[i] + ".text",
					null, null));
			item.setImg(messageSource.getMessage("menu." + items[i] + ".img",
					null, null));
			item.setLink(messageSource.getMessage("menu." + items[i] + ".link",
					null, null));
			menuItems.add(item);
		}
		return menuItems;
	}

	/**
	 * Lista con los tamaños de paginas disponibles
	 * 
	 * @return
	 */
	@ModelAttribute("pageSizes")
	String[] getPageSizes() {
		// Obtenemos los elementos del menu
		return messageSource.getMessage("menu.pag.sizes", null, null)
				.split(" ");
	}

	/* *****************************************
	 * **************** TIPOS ******************
	 * *****************************************
	 */
	private PaginationManager typesPagManager;
	private List<Tipo> typesData;
	private Tipo typesFilter;
	private Integer modifiedTypeId;

	@RequestMapping(value = "/types")
	public String types(Model model) {
		try {
			// Iniciamos paginacion
			TipoDao dao = DaoFactory.getTipoDao();
			typesPagManager = new PaginationManager(messageSource,
					dao.getCountTipos());
			// Buscamos los datos
			typesData = dao.getTiposWithPag(typesPagManager.getPage() - 1,
					typesPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			typesFilter = null;
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(""));
		} catch (Exception e) {
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Types'"
					+ " para la ruta '/types'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("typesData", typesData);
		model.addAttribute("page", typesPagManager.getPageLabel());
		model.addAttribute("pageCount", typesPagManager.getPageCountLabel());
		model.addAttribute("filter", typesFilter == null ? new Tipo()
				: typesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/types", params = { "next" })
	public String typesNext(Model model) {
		try {
			// Realizamos la paginacion
			TipoDao dao = DaoFactory.getTipoDao();
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource,
						dao.getCountTipos(typesFilter));
			}
			if (typesPagManager.next()) {
				typesData = dao.getTiposWithPag(typesFilter,
						typesPagManager.getPage() - 1,
						typesPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(""));
		} catch (Exception e) {
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Types'"
					+ " para la ruta '/types?next'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("typesData", typesData);
		model.addAttribute("page", typesPagManager.getPageLabel());
		model.addAttribute("pageCount", typesPagManager.getPageCountLabel());
		model.addAttribute("filter", typesFilter == null ? new Tipo()
				: typesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/types", params = { "previous" })
	public String typesPrevious(Model model) {
		try {
			// Realizamos la paginacion
			TipoDao dao = DaoFactory.getTipoDao();
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource,
						dao.getCountTipos(typesFilter));
			}
			if (typesPagManager.previous()) {
				typesData = dao.getTiposWithPag(typesFilter,
						typesPagManager.getPage() - 1,
						typesPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(""));
		} catch (Exception e) {
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Types'"
					+ " para la ruta '/types?previous'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("typesData", typesData);
		model.addAttribute("page", typesPagManager.getPageLabel());
		model.addAttribute("pageCount", typesPagManager.getPageCountLabel());
		model.addAttribute("filter", typesFilter == null ? new Tipo()
				: typesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/types", params = { "start" })
	public String typesStart(Model model) {
		try {
			// Realizamos la paginacion
			TipoDao dao = DaoFactory.getTipoDao();
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource,
						dao.getCountTipos(typesFilter));
			}
			if (typesPagManager.start()) {
				typesData = dao.getTiposWithPag(typesFilter,
						typesPagManager.getPage() - 1,
						typesPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(""));
		} catch (Exception e) {
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Types'"
					+ " para la ruta '/types?start'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("typesData", typesData);
		model.addAttribute("page", typesPagManager.getPageLabel());
		model.addAttribute("pageCount", typesPagManager.getPageCountLabel());
		model.addAttribute("filter", typesFilter == null ? new Tipo()
				: typesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/types", params = { "end" })
	public String typesEnd(Model model) {
		try {
			// Realizamos la paginacion
			TipoDao dao = DaoFactory.getTipoDao();
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource,
						dao.getCountTipos(typesFilter));
			}
			if (typesPagManager.end()) {
				typesData = dao.getTiposWithPag(typesFilter,
						typesPagManager.getPage() - 1,
						typesPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(""));
		} catch (Exception e) {
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Types'"
					+ " para la ruta '/types?end'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("typesData", typesData);
		model.addAttribute("page", typesPagManager.getPageLabel());
		model.addAttribute("pageCount", typesPagManager.getPageCountLabel());
		model.addAttribute("filter", typesFilter == null ? new Tipo()
				: typesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/types", params = { "pageSize" })
	public String typesPagesSize(@RequestParam("pageSize") String pageSize,
			Model model) {
		try {
			// Realizamos la paginacion
			TipoDao dao = DaoFactory.getTipoDao();
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource,
						dao.getCountTipos(typesFilter));
			}
			if (typesPagManager.setPageSize(pageSize)) {
				typesData = dao.getTiposWithPag(typesFilter,
						typesPagManager.getPage() - 1,
						typesPagManager.getPageSize());
				model.addAttribute("selectedPageSize", pageSize);
			} else {
				model.addAttribute("selectedPageSize",
						"" + typesPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(""));
		} catch (Exception e) {
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Types'"
					+ " para la ruta '/types?pageSize'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("selectedPageSize", pageSize);
		model.addAttribute("typesData", typesData);
		model.addAttribute("page", typesPagManager.getPageLabel());
		model.addAttribute("pageCount", typesPagManager.getPageCountLabel());
		model.addAttribute("filter", typesFilter == null ? new Tipo()
				: typesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/types", params = { "search" })
	public String typesFilter(Tipo filter, Model model) {
		try {// Guardamos el filtro
			typesFilter = FilterManager.processTypesFilter(filter);
			// Iniciamos paginacion
			TipoDao dao = DaoFactory.getTipoDao();
			typesPagManager = new PaginationManager(messageSource,
					dao.getCountTipos(typesFilter));
			typesData = dao.getTiposWithPag(typesFilter,
					typesPagManager.getPage() - 1,
					typesPagManager.getPageSize());
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(""));
		} catch (Exception e) {
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Types'"
					+ " para la ruta '/types?search'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTypesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("typesData", typesData);
		model.addAttribute("page", typesPagManager.getPageLabel());
		model.addAttribute("pageCount", typesPagManager.getPageCountLabel());
		model.addAttribute("filter", typesFilter == null ? new Tipo()
				: typesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/types", params = { "create" })
	public String typesCreate(@RequestParam("create") Integer index,
			Model model) {
		Tipo typeData = null;
		// Mensaje a mostrar en caso de error
		String msg = "";
		// Si tenemos un indice valido
		if (index >= 0 && typesData != null) {
			try {
				// Obtenemos todos los datos del tipo seleccionado
				TipoDao dao = DaoFactory.getTipoDao();
				typeData = dao.getTipo(typesData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un tipo, creamos uno vacio para que no de
				// error
				if (typeData == null) {
					typeData = new Tipo();
				}
				log.error("Error en el controlador 'Types'"
						+ " para la ruta '/types?create'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un tipo vacio, para que no de fallos al intentar acceder
			// a algunos campos
			typeData = new Tipo();
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getTypesCreateForm(msg));

		// Fijamos variables para la vista
		model.addAttribute("typeData", typeData);
		return "commons/body";
	}

	@RequestMapping(value = "/types", params = { "delete" })
	public String typesDelete(@RequestParam("delete") Integer index,
			Model model) {
		// Mensaje a mostrar en caso de error
		String msg = "";
		TipoDao dao = DaoFactory.getTipoDao();

		try {
			// Borramos el tipo indicado por el indice recibido
			dao.beginTransaction();
			dao.delete(typesData.get(index));
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error("Error en el controlador 'Types' al borrar un tipo: "
					+ index + " " + typesData.get(index), e);
			msg = messageSource.getMessage("error.delete", null, null);
		}

		try {
			// Iniciamos paginacion
			typesPagManager = new PaginationManager(messageSource,
					dao.getCountTipos());
			// Buscamos los datos
			typesData = dao.getTiposWithPag(
					typesPagManager.getPage() - 1,
					typesPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			typesFilter = null;
		} catch (Exception e) {
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Types'"
					+ " para la ruta '/delete' al mostrar los datos", e);
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getTypesList(msg));
		// Fijamos variables para la vista
		model.addAttribute("typesData", typesData);
		model.addAttribute("page", typesPagManager.getPageLabel());
		model.addAttribute("pageCount", typesPagManager.getPageCountLabel());
		model.addAttribute("filter", typesFilter == null ? new Tipo()
				: typesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/types", params = { "update" })
	public String typesUpdate(@RequestParam("update") Integer index, Model model) {
		Tipo typeData = null;

		// Si tenemos un indice valido
		String msg = "";
		if (index >= 0 && typesData != null) {
			try {
				// Obtenemos todos los datos del tipo seleccionado
				TipoDao dao = DaoFactory.getTipoDao();
				typeData = dao.getTipo(typesData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un tipo, creamos uno vacio para que no de
				// error
				if (typeData == null) {
					typeData = new Tipo();
				}
				log.error("Error en el controlador 'Types'"
						+ " para la ruta '/types?update'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un tipo vacio, para que no de fallos al intentar acceder
			// a algunos campos
			typeData = new Tipo();
			msg = messageSource.getMessage("types.menu.update.noIndexMsg",
					null, null);
		}
		modifiedTypeId = typeData.getId();

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getTypesUpdateForm(msg));

		model.addAttribute("typeData", typeData);
		return "commons/body";
	}

	@RequestMapping(value = "/types", params = { "read" })
	public String typesRead(@RequestParam("read") Integer index, Model model) {
		Tipo typeData = null;
		List<Libro> typeBooks = null;
		String msg = "";

		// Si tenemos un indice valido
		if (index >= 0 && typesData != null) {
			// Obtenemos todos los datos del libro seleccionado
			TipoDao dao = DaoFactory.getTipoDao();
			try {
				typeData = dao.getTipo(typesData.get(index).getId());
				typeBooks =  dao.getLibrosTipo(typesData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un libro, creamos uno vacio para que no de
				// error
				if (typeData == null) {
					typeData = new Tipo();
				}
				if (typeBooks == null) {
					typeBooks = new ArrayList<Libro>();
				}
				log.error("Error en el controlador 'Types'"
						+ " para la ruta '/types?read'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un tipo vacio, para que no de fallos al intentar acceder
			// a algunos campos
			typeData = new Tipo();
			typeBooks = new ArrayList<Libro>();
			msg = messageSource.getMessage("types.menu.read.noIndexMsg", null,
					null);
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getTypesReadForm(msg));

		model.addAttribute("typeData", typeData);

		model.addAttribute("typeBooks", typeBooks);
		return "commons/body";
	}

	@RequestMapping(value = "/types", params = { "acceptCreation" })
	public String typesAcceptCreation(Tipo newType, Model model) {
		String msg = "";
		// Iniciamos la transaccion para guardar el tipo
		TipoDao dao = DaoFactory.getTipoDao();
		try {
			dao.beginTransaction();
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processType(newType, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getTypesCreateForm(messageSource.getMessage(
								"error.type.fieldsNotRight", null, null)
								+ " " + newType.getDescripcion()));
				model.addAttribute("typeData", newType);
				return "commons/body";
			}
			// Guardamos el tipo
			dao.insert(newType);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Types'"
							+ " para la ruta '/types?acceptCreation' al crear el tipo",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			typesPagManager = new PaginationManager(messageSource,
					dao.getCountTipos());
			// Buscamos los datos
			typesData = dao.getTiposWithPag(
					typesPagManager.getPage() - 1,
					typesPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			typesFilter = null;
		} catch (Exception e) {
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Types'"
					+ " para la ruta '/types?acceptCreation'"
					+ " al cargar los datos", e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getTypesList(msg));
		// Fijamos variables para la vista
		model.addAttribute("typesData", typesData);
		model.addAttribute("page", typesPagManager.getPageLabel());
		model.addAttribute("pageCount", typesPagManager.getPageCountLabel());
		model.addAttribute("filter", typesFilter == null ? new Tipo()
				: typesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/types", params = { "acceptUpdate" })
	public String typesAcceptUpdate(Tipo newType, Model model) {
		newType.setId(modifiedTypeId);
		String msg = "";
		// Iniciamos la transaccion para guardar el tipo
		TipoDao dao = DaoFactory.getTipoDao();
		try {
			dao.beginTransaction();
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processType(newType, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getTypesCreateForm(messageSource.getMessage(
								"error.type.fieldsNotRight", null, null)
								+ " " + newType.getDescripcion()));
				model.addAttribute("typeData", newType);
				return "commons/body";
			}
			// Guardamos el libro
			dao.update(newType);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Types'"
							+ " para la ruta '/types?acceptUpdate' al modificar el tipo",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			typesPagManager = new PaginationManager(messageSource,
					dao.getCountTipos());
			// Buscamos los datos
			typesData = dao.getTiposWithPag(
					typesPagManager.getPage() - 1,
					typesPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			typesFilter = null;
		} catch (Exception e) {
			if (typesPagManager == null) {
				typesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Types'"
					+ " para la ruta '/types?acceptCreation'"
					+ " al cargar los datos", e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getTypesList(msg));
		// Fijamos variables para la vista
		model.addAttribute("typesData", typesData);
		model.addAttribute("page", typesPagManager.getPageLabel());
		model.addAttribute("pageCount", typesPagManager.getPageCountLabel());
		model.addAttribute("filter", typesFilter == null ? new Tipo()
				: typesFilter);
		return "commons/body";
	}
}
