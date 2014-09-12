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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.dao.TraductorDao;
import es.magDevs.myLibrary.web.gui.beans.MenuItem;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;
import es.magDevs.myLibrary.web.gui.utils.PaginationManager;

/**
 * Controlador para la seccion de traductores
 * 
 * @translator javi
 * 
 */
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class TranslatorsController {
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
	private static final Logger log = Logger
			.getLogger(TranslatorsController.class);

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
	private PaginationManager translatorsPagManager;
	private List<Traductor> translatorsData;
	private Traductor translatorsFilter;
	private Integer modifiedTranslatorId;

	@RequestMapping(value = "/translators")
	public String translators(Model model) {
		try {
			// Iniciamos paginacion
			TraductorDao dao = DaoFactory.getTraductorDao();
			translatorsPagManager = new PaginationManager(messageSource,
					dao.getCountTraductores());
			// Buscamos los datos
			translatorsData = dao.getTraductoresWithPag(
					translatorsPagManager.getPage() - 1,
					translatorsPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			translatorsFilter = null;
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(""));
		} catch (Exception e) {
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Translators'"
					+ " para la ruta '/transaltors'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("translatorsData", translatorsData);
		model.addAttribute("page", translatorsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				translatorsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				translatorsFilter == null ? new Traductor() : translatorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/translators", params = { "next" })
	public String translatorsNext(Model model) {
		try {
			// Realizamos la paginacion
			TraductorDao dao = DaoFactory.getTraductorDao();
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource,
						dao.getCountTraductores(translatorsFilter));
			}
			if (translatorsPagManager.next()) {
				translatorsData = dao.getTraductoresWithPag(translatorsFilter,
						translatorsPagManager.getPage() - 1,
						translatorsPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(""));
		} catch (Exception e) {
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Translators'"
					+ " para la ruta '/transaltors?next'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("translatorsData", translatorsData);
		model.addAttribute("page", translatorsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				translatorsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				translatorsFilter == null ? new Traductor() : translatorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/translators", params = { "previous" })
	public String translatorsPrevious(Model model) {
		try {
			// Realizamos la paginacion
			TraductorDao dao = DaoFactory.getTraductorDao();
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource,
						dao.getCountTraductores(translatorsFilter));
			}
			if (translatorsPagManager.previous()) {
				translatorsData = dao.getTraductoresWithPag(translatorsFilter,
						translatorsPagManager.getPage() - 1,
						translatorsPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(""));
		} catch (Exception e) {
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Translators'"
					+ " para la ruta '/transaltors?previous'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("translatorsData", translatorsData);
		model.addAttribute("page", translatorsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				translatorsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				translatorsFilter == null ? new Traductor() : translatorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/translators", params = { "start" })
	public String translatorsStart(Model model) {
		try {
			// Realizamos la paginacion
			TraductorDao dao = DaoFactory.getTraductorDao();
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource,
						dao.getCountTraductores(translatorsFilter));
			}
			if (translatorsPagManager.start()) {
				translatorsData = dao.getTraductoresWithPag(translatorsFilter,
						translatorsPagManager.getPage() - 1,
						translatorsPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(""));
		} catch (Exception e) {
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Translators'"
					+ " para la ruta '/transaltors?start'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("translatorsData", translatorsData);
		model.addAttribute("page", translatorsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				translatorsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				translatorsFilter == null ? new Traductor() : translatorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/translators", params = { "end" })
	public String translatorsEnd(Model model) {
		try {
			// Realizamos la paginacion
			TraductorDao dao = DaoFactory.getTraductorDao();
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource,
						dao.getCountTraductores(translatorsFilter));
			}
			if (translatorsPagManager.end()) {
				translatorsData = dao.getTraductoresWithPag(translatorsFilter,
						translatorsPagManager.getPage() - 1,
						translatorsPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(""));
		} catch (Exception e) {
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Translators'"
					+ " para la ruta '/transaltors?end'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("translatorsData", translatorsData);
		model.addAttribute("page", translatorsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				translatorsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				translatorsFilter == null ? new Traductor() : translatorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/translators", params = { "pageSize" })
	public String translatorsPagesSize(
			@RequestParam("pageSize") String pageSize, Model model) {
		try {
			// Realizamos la paginacion
			TraductorDao dao = DaoFactory.getTraductorDao();
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource,
						dao.getCountTraductores(translatorsFilter));
			}
			if (translatorsPagManager.setPageSize(pageSize)) {
				translatorsData = dao.getTraductoresWithPag(translatorsFilter,
						translatorsPagManager.getPage() - 1,
						translatorsPagManager.getPageSize());
				model.addAttribute("selectedPageSize", pageSize);
			} else {
				model.addAttribute("selectedPageSize", ""
						+ translatorsPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(""));
		} catch (Exception e) {
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Translators'"
					+ " para la ruta '/transaltors?pageSize'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("selectedPageSize", pageSize);
		model.addAttribute("translatorsData", translatorsData);
		model.addAttribute("page", translatorsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				translatorsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				translatorsFilter == null ? new Traductor() : translatorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/translators", params = { "search" })
	public String translatorsFilter(Traductor filter, Model model) {
		try {
			// Guardamos el filtro
			translatorsFilter = FilterManager.processTranslatorsFilter(filter);
			// Iniciamos paginacion
			TraductorDao dao = DaoFactory.getTraductorDao();
			translatorsPagManager = new PaginationManager(messageSource,
					dao.getCountTraductores(translatorsFilter));
			translatorsData = dao.getTraductoresWithPag(translatorsFilter,
					translatorsPagManager.getPage() - 1,
					translatorsPagManager.getPageSize());

			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(""));
		} catch (Exception e) {
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Translators'"
					+ " para la ruta '/transaltors?search'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getTranslatorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("translatorsData", translatorsData);
		model.addAttribute("page", translatorsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				translatorsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				translatorsFilter == null ? new Traductor() : translatorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/translators", params = { "create" })
	public String translatorsCreate(@RequestParam("create") Integer index,
			Model model) {
		Traductor translatorData = null;
		// Mensaje a mostrar en caso de error
		String msg = "";
		// Si tenemos un indice valido
		if (index >= 0 && translatorsData != null) {
			try {
				// Obtenemos todos los datos del traductor seleccionado
				TraductorDao dao = DaoFactory.getTraductorDao();
				translatorData = dao.getTraductor(translatorsData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un traductor, creamos uno vacio para que no de
				// error
				if (translatorData == null) {
					translatorData = new Traductor();
				}
				log.error("Error en el controlador 'Translators'"
						+ " para la ruta '/translators?create'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un traductor vacio, para que no de fallos al intentar acceder
			// a algunos campos
			translatorData = new Traductor();
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getTranslatorsCreateForm(msg));

		// Fijamos variables para la vista
		model.addAttribute("translatorData", translatorData);
		return "commons/body";
	}

	@RequestMapping(value = "/translators", params = { "delete" })
	public String translatorsDelete(@RequestParam("delete") Integer index,
			Model model) {
		// Mensaje a mostrar en caso de error
		String msg = "";
		TraductorDao dao = DaoFactory.getTraductorDao();

		try {
			// Borramos el traductor indicado por el indice recibido
			dao.beginTransaction();
			dao.delete(translatorsData.get(index));
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error("Error en el controlador 'Translators' al borrar un traductor: "
					+ index + " " + translatorsData.get(index), e);
			msg = messageSource.getMessage("error.delete", null, null);
		}

		try {
			// Iniciamos paginacion
			translatorsPagManager = new PaginationManager(messageSource,
					dao.getCountTraductores());
			// Buscamos los datos
			translatorsData = dao.getTraductoresWithPag(
					translatorsPagManager.getPage() - 1,
					translatorsPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			translatorsFilter = null;
		} catch (Exception e) {
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Translators'"
					+ " para la ruta '/delete' al mostrar los datos", e);
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getTranslatorsList(msg));
		// Fijamos variables para la vista
		model.addAttribute("translatorsData", translatorsData);
		model.addAttribute("page", translatorsPagManager.getPageLabel());
		model.addAttribute("pageCount", translatorsPagManager.getPageCountLabel());
		model.addAttribute("filter", translatorsFilter == null ? new Traductor()
				: translatorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/translators", params = { "update" })
	public String translatorsUpdate(@RequestParam("update") Integer index, Model model) {
		Traductor translatorData = null;

		// Si tenemos un indice valido
		String msg = "";
		if (index >= 0 && translatorsData != null) {
			try {
				// Obtenemos todos los datos del traductor seleccionado
				TraductorDao dao = DaoFactory.getTraductorDao();
				translatorData = dao.getTraductor(translatorsData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un traductor, creamos uno vacio para que no de
				// error
				if (translatorData == null) {
					translatorData = new Traductor();
				}
				log.error("Error en el controlador 'Translators'"
						+ " para la ruta '/translators?update'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un traductor vacio, para que no de fallos al intentar acceder
			// a algunos campos
			translatorData = new Traductor();
			msg = messageSource.getMessage("translators.menu.update.noIndexMsg",
					null, null);
		}
		modifiedTranslatorId = translatorData.getId();

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getTranslatorsUpdateForm(msg));

		model.addAttribute("translatorData", translatorData);
		return "commons/body";
	}

	@RequestMapping(value = "/translators", params = { "read" })
	public String translatorsRead(@RequestParam("read") Integer index, Model model) {
		Traductor translatorData = null;
		List<Libro> translatorBooks = null;
		String msg = "";

		// Si tenemos un indice valido
		if (index >= 0 && translatorsData != null) {
			// Obtenemos todos los datos del libro seleccionado
			TraductorDao dao = DaoFactory.getTraductorDao();
			try {
				translatorData = dao.getTraductor(translatorsData.get(index).getId());
				translatorBooks =  dao.getLibrosTraductor(translatorsData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un libro, creamos uno vacio para que no de
				// error
				if (translatorData == null) {
					translatorData = new Traductor();
				}
				if (translatorBooks == null) {
					translatorBooks = new ArrayList<Libro>();
				}
				log.error("Error en el controlador 'Translators'"
						+ " para la ruta '/translators?read'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un traductor vacio, para que no de fallos al intentar acceder
			// a algunos campos
			translatorData = new Traductor();
			translatorBooks = new ArrayList<Libro>();
			msg = messageSource.getMessage("translators.menu.read.noIndexMsg", null,
					null);
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getTranslatorsReadForm(msg));

		model.addAttribute("translatorData", translatorData);

		model.addAttribute("translatorBooks", translatorBooks);
		return "commons/body";
	}

	@RequestMapping(value = "/translators", params = { "acceptCreation" })
	public String translatorsAcceptCreation(Traductor newTranslator, Model model) {
		String msg = "";
		// Iniciamos la transaccion para guardar el traductor
		TraductorDao dao = DaoFactory.getTraductorDao();
		try {
			dao.beginTransaction();
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processTranslator(newTranslator, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getTranslatorsCreateForm(messageSource.getMessage(
								"error.translator.fieldsNotRight", null, null)
								+ " " + newTranslator.getNombre()));
				model.addAttribute("translatorData", newTranslator);
				return "commons/body";
			}
			// Guardamos el traductor
			dao.insert(newTranslator);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Translators'"
							+ " para la ruta '/translators?acceptCreation' al crear el traductor",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			translatorsPagManager = new PaginationManager(messageSource,
					dao.getCountTraductores());
			// Buscamos los datos
			translatorsData = dao.getTraductoresWithPag(
					translatorsPagManager.getPage() - 1,
					translatorsPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			translatorsFilter = null;
		} catch (Exception e) {
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Translators'"
					+ " para la ruta '/translators?acceptCreation'"
					+ " al cargar los datos", e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getTranslatorsList(msg));
		// Fijamos variables para la vista
		model.addAttribute("translatorsData", translatorsData);
		model.addAttribute("page", translatorsPagManager.getPageLabel());
		model.addAttribute("pageCount", translatorsPagManager.getPageCountLabel());
		model.addAttribute("filter", translatorsFilter == null ? new Traductor()
				: translatorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/translators", params = { "acceptUpdate" })
	public String translatorsAcceptUpdate(Traductor newTranslator, Model model) {
		newTranslator.setId(modifiedTranslatorId);
		String msg = "";
		// Iniciamos la transaccion para guardar el traductor
		TraductorDao dao = DaoFactory.getTraductorDao();
		try {
			dao.beginTransaction();
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processTranslator(newTranslator, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getTranslatorsCreateForm(messageSource.getMessage(
								"error.translator.fieldsNotRight", null, null)
								+ " " + newTranslator.getNombre()));
				model.addAttribute("translatorData", newTranslator);
				return "commons/body";
			}
			// Guardamos el libro
			dao.update(newTranslator);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Translators'"
							+ " para la ruta '/translators?acceptUpdate' al modificar el traductor",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			translatorsPagManager = new PaginationManager(messageSource,
					dao.getCountTraductores());
			// Buscamos los datos
			translatorsData = dao.getTraductoresWithPag(
					translatorsPagManager.getPage() - 1,
					translatorsPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			translatorsFilter = null;
		} catch (Exception e) {
			if (translatorsPagManager == null) {
				translatorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Translators'"
					+ " para la ruta '/translators?acceptCreation'"
					+ " al cargar los datos", e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getTranslatorsList(msg));
		// Fijamos variables para la vista
		model.addAttribute("translatorsData", translatorsData);
		model.addAttribute("page", translatorsPagManager.getPageLabel());
		model.addAttribute("pageCount", translatorsPagManager.getPageCountLabel());
		model.addAttribute("filter", translatorsFilter == null ? new Traductor()
				: translatorsFilter);
		return "commons/body";
	}

	/**
	 * Metodo para gestionar peticiones AJAX para obtener datos de traductores
	 * 
	 * @param hint
	 * @return
	 */
	@RequestMapping(value = "/translators", params = { "getdata" }, method = RequestMethod.POST)
	public @ResponseBody
	List<Traductor> getData(@RequestParam("getdata") String hint) {
		TraductorDao dao = DaoFactory.getTraductorDao();
		try {
			return dao.getTraductores(hint);
		} catch (Exception e) {
			return new ArrayList<Traductor>();
		}
	}
}
