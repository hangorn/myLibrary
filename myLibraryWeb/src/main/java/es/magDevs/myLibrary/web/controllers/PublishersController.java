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
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.EditorialDao;
import es.magDevs.myLibrary.web.gui.beans.MenuItem;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;
import es.magDevs.myLibrary.web.gui.utils.PaginationManager;

/**
 * Controlador para la seccion de editoriales
 * 
 * @publisher javi
 * 
 */
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class PublishersController {
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
	private static final Logger log = Logger.getLogger(PublishersController.class);

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
	 * ************* EDITORIALES ***************
	 * *****************************************
	 */
	private PaginationManager publishersPagManager;
	private List<Editorial> publishersData;
	private Editorial publishersFilter;
	private Integer modifiedPublisherId;

	@RequestMapping(value = "/publishers")
	public String publishers(Model model) {
		try {
			// Iniciamos paginacion
			EditorialDao dao = DaoFactory.getEditorialDao();
			publishersPagManager = new PaginationManager(messageSource,
					dao.getCountEditoriales());
			// Buscamos los datos
			publishersData = dao.getEditorialesWithPag(
					publishersPagManager.getPage() - 1,
					publishersPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			publishersFilter = null;
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(""));
		} catch (Exception e) {
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Publishers'"
					+ " para la ruta '/publishers'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("publishersData", publishersData);
		model.addAttribute("page", publishersPagManager.getPageLabel());
		model.addAttribute("pageCount",
				publishersPagManager.getPageCountLabel());
		model.addAttribute("filter", publishersFilter == null ? new Editorial()
				: publishersFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/publishers", params = { "next" })
	public String publishersNext(Model model) {
		try {
			// Realizamos la paginacion
			EditorialDao dao = DaoFactory.getEditorialDao();
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource,
						dao.getCountEditoriales(publishersFilter));
			}
			if (publishersPagManager.next()) {
				publishersData = dao.getEditorialesWithPag(publishersFilter,
						publishersPagManager.getPage() - 1,
						publishersPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(""));
		} catch (Exception e) {
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Publishers'"
					+ " para la ruta '/publishers?next'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("publishersData", publishersData);
		model.addAttribute("page", publishersPagManager.getPageLabel());
		model.addAttribute("pageCount",
				publishersPagManager.getPageCountLabel());
		model.addAttribute("filter", publishersFilter == null ? new Editorial()
				: publishersFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/publishers", params = { "previous" })
	public String publishersPrevious(Model model) {
		try {
			// Realizamos la paginacion
			EditorialDao dao = DaoFactory.getEditorialDao();
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource,
						dao.getCountEditoriales(publishersFilter));
			}
			if (publishersPagManager.previous()) {
				publishersData = dao.getEditorialesWithPag(publishersFilter,
						publishersPagManager.getPage() - 1,
						publishersPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(""));
		} catch (Exception e) {
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Publishers'"
					+ " para la ruta '/publishers?next'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("publishersData", publishersData);
		model.addAttribute("page", publishersPagManager.getPageLabel());
		model.addAttribute("pageCount",
				publishersPagManager.getPageCountLabel());
		model.addAttribute("filter", publishersFilter == null ? new Editorial()
				: publishersFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/publishers", params = { "start" })
	public String publishersStart(Model model) {
		try {
			// Realizamos la paginacion
			EditorialDao dao = DaoFactory.getEditorialDao();
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource,
						dao.getCountEditoriales(publishersFilter));
			}
			if (publishersPagManager.start()) {
				publishersData = dao.getEditorialesWithPag(publishersFilter,
						publishersPagManager.getPage() - 1,
						publishersPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(""));
		} catch (Exception e) {
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Publishers'"
					+ " para la ruta '/publishers?start'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("publishersData", publishersData);
		model.addAttribute("page", publishersPagManager.getPageLabel());
		model.addAttribute("pageCount",
				publishersPagManager.getPageCountLabel());
		model.addAttribute("filter", publishersFilter == null ? new Editorial()
				: publishersFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/publishers", params = { "end" })
	public String publishersEnd(Model model) {
		try {
			// Realizamos la paginacion
			EditorialDao dao = DaoFactory.getEditorialDao();
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource,
						dao.getCountEditoriales(publishersFilter));
			}
			if (publishersPagManager.end()) {
				publishersData = dao.getEditorialesWithPag(publishersFilter,
						publishersPagManager.getPage() - 1,
						publishersPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(""));
		} catch (Exception e) {
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Publishers'"
					+ " para la ruta '/publishers?end'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("publishersData", publishersData);
		model.addAttribute("page", publishersPagManager.getPageLabel());
		model.addAttribute("pageCount",
				publishersPagManager.getPageCountLabel());
		model.addAttribute("filter", publishersFilter == null ? new Editorial()
				: publishersFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/publishers", params = { "pageSize" })
	public String publishersPagesSize(
			@RequestParam("pageSize") String pageSize, Model model) {
		try {
			// Realizamos la paginacion
			EditorialDao dao = DaoFactory.getEditorialDao();
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource,
						dao.getCountEditoriales(publishersFilter));
			}
			if (publishersPagManager.setPageSize(pageSize)) {
				publishersData = dao.getEditorialesWithPag(publishersFilter,
						publishersPagManager.getPage() - 1,
						publishersPagManager.getPageSize());
				model.addAttribute("selectedPageSize", pageSize);
			} else {
				model.addAttribute("selectedPageSize", ""
						+ publishersPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(""));
		} catch (Exception e) {
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Publishers'"
					+ " para la ruta '/publishers?pageSize'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("selectedPageSize", pageSize);
		model.addAttribute("publishersData", publishersData);
		model.addAttribute("page", publishersPagManager.getPageLabel());
		model.addAttribute("pageCount",
				publishersPagManager.getPageCountLabel());
		model.addAttribute("filter", publishersFilter == null ? new Editorial()
				: publishersFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/publishers", params = { "search" })
	public String publishersFilter(Editorial filter, Model model) {
		try {
			// Guardamos el filtro
			publishersFilter = FilterManager.processPublishersFilter(filter);
			// Iniciamos paginacion
			EditorialDao dao = DaoFactory.getEditorialDao();
			publishersPagManager = new PaginationManager(messageSource,
					dao.getCountEditoriales(publishersFilter));
			publishersData = dao.getEditorialesWithPag(publishersFilter,
					publishersPagManager.getPage() - 1,
					publishersPagManager.getPageSize());

			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(""));
		} catch (Exception e) {
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Publishers'"
					+ " para la ruta '/publishers?search'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPublishersList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("publishersData", publishersData);
		model.addAttribute("page", publishersPagManager.getPageLabel());
		model.addAttribute("pageCount",
				publishersPagManager.getPageCountLabel());
		model.addAttribute("filter", publishersFilter == null ? new Editorial()
				: publishersFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/publishers", params = { "create" })
	public String publishersCreate(@RequestParam("create") Integer index,
			Model model) {
		Editorial publisherData = null;
		// Mensaje a mostrar en caso de error
		String msg = "";
		// Si tenemos un indice valido
		if (index >= 0 && publishersData != null) {
			try {
				// Obtenemos todos los datos del editorial seleccionado
				EditorialDao dao = DaoFactory.getEditorialDao();
				publisherData = dao.getEditorial(publishersData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un editorial, creamos uno vacio para que no de
				// error
				if (publisherData == null) {
					publisherData = new Editorial();
				}
				log.error("Error en el controlador 'Publishers'"
						+ " para la ruta '/publishers?create'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos una editorial vacia, para que no de fallos al intentar acceder
			// a algunos campos
			publisherData = new Editorial();
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getPublishersCreateForm(msg));

		// Fijamos variables para la vista
		model.addAttribute("publisherData", publisherData);
		return "commons/body";
	}

	@RequestMapping(value = "/publishers", params = { "delete" })
	public String publishersDelete(@RequestParam("delete") Integer index,
			Model model) {
		// Mensaje a mostrar en caso de error
		String msg = "";
		EditorialDao dao = DaoFactory.getEditorialDao();

		try {
			// Borramos el editorial indicado por el indice recibido
			dao.beginTransaction();
			dao.delete(publishersData.get(index));
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error("Error en el controlador 'Publishers' al borrar un editorial: "
					+ index + " " + publishersData.get(index), e);
			msg = messageSource.getMessage("error.delete", null, null);
		}

		try {
			// Iniciamos paginacion
			publishersPagManager = new PaginationManager(messageSource,
					dao.getCountEditoriales());
			// Buscamos los datos
			publishersData = dao.getEditorialesWithPag(
					publishersPagManager.getPage() - 1,
					publishersPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			publishersFilter = null;
		} catch (Exception e) {
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Publishers'"
					+ " para la ruta '/delete' al mostrar los datos", e);
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getPublishersList(msg));
		// Fijamos variables para la vista
		model.addAttribute("publishersData", publishersData);
		model.addAttribute("page", publishersPagManager.getPageLabel());
		model.addAttribute("pageCount", publishersPagManager.getPageCountLabel());
		model.addAttribute("filter", publishersFilter == null ? new Editorial()
				: publishersFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/publishers", params = { "update" })
	public String publishersUpdate(@RequestParam("update") Integer index, Model model) {
		Editorial publisherData = null;

		// Si tenemos un indice valido
		String msg = "";
		if (index >= 0 && publishersData != null) {
			try {
				// Obtenemos todos los datos del editorial seleccionado
				EditorialDao dao = DaoFactory.getEditorialDao();
				publisherData = dao.getEditorial(publishersData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un editorial, creamos uno vacio para que no de
				// error
				if (publisherData == null) {
					publisherData = new Editorial();
				}
				log.error("Error en el controlador 'Publishers'"
						+ " para la ruta '/publishers?update'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un editorial vacio, para que no de fallos al intentar acceder
			// a algunos campos
			publisherData = new Editorial();
			msg = messageSource.getMessage("publishers.menu.update.noIndexMsg",
					null, null);
		}
		modifiedPublisherId = publisherData.getId();

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getPublishersUpdateForm(msg));

		model.addAttribute("publisherData", publisherData);
		return "commons/body";
	}

	@RequestMapping(value = "/publishers", params = { "read" })
	public String publishersRead(@RequestParam("read") Integer index, Model model) {
		Editorial publisherData = null;
		List<Libro> publisherBooks = null;
		String msg = "";

		// Si tenemos un indice valido
		if (index >= 0 && publishersData != null) {
			// Obtenemos todos los datos del libro seleccionado
			EditorialDao dao = DaoFactory.getEditorialDao();
			try {
				publisherData = dao.getEditorial(publishersData.get(index).getId());
				publisherBooks =  dao.getLibrosEditorial(publishersData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un libro, creamos uno vacio para que no de
				// error
				if (publisherData == null) {
					publisherData = new Editorial();
				}
				if (publisherBooks == null) {
					publisherBooks = new ArrayList<Libro>();
				}
				log.error("Error en el controlador 'Publishers'"
						+ " para la ruta '/publishers?read'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un editorial vacio, para que no de fallos al intentar acceder
			// a algunos campos
			publisherData = new Editorial();
			publisherBooks = new ArrayList<Libro>();
			msg = messageSource.getMessage("publishers.menu.read.noIndexMsg", null,
					null);
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getPublishersReadForm(msg));

		model.addAttribute("publisherData", publisherData);

		model.addAttribute("publisherBooks", publisherBooks);
		return "commons/body";
	}

	@RequestMapping(value = "/publishers", params = { "acceptCreation" })
	public String publishersAcceptCreation(Editorial newPublisher, Model model) {
		String msg = "";
		// Iniciamos la transaccion para guardar el editorial
		EditorialDao dao = DaoFactory.getEditorialDao();
		try {
			dao.beginTransaction();
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processPublisher(newPublisher, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getPublishersCreateForm(messageSource.getMessage(
								"error.publisher.fieldsNotRight", null, null)
								+ " " + newPublisher.getNombre()));
				model.addAttribute("publisherData", newPublisher);
				return "commons/body";
			}
			// Guardamos el editorial
			dao.insert(newPublisher);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Publishers'"
							+ " para la ruta '/publishers?acceptCreation' al crear el editorial",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			publishersPagManager = new PaginationManager(messageSource,
					dao.getCountEditoriales());
			// Buscamos los datos
			publishersData = dao.getEditorialesWithPag(
					publishersPagManager.getPage() - 1,
					publishersPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			publishersFilter = null;
		} catch (Exception e) {
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Publishers'"
					+ " para la ruta '/publishers?acceptCreation'"
					+ " al cargar los datos", e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getPublishersList(msg));
		// Fijamos variables para la vista
		model.addAttribute("publishersData", publishersData);
		model.addAttribute("page", publishersPagManager.getPageLabel());
		model.addAttribute("pageCount", publishersPagManager.getPageCountLabel());
		model.addAttribute("filter", publishersFilter == null ? new Editorial()
				: publishersFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/publishers", params = { "acceptUpdate" })
	public String publishersAcceptUpdate(Editorial newPublisher, Model model) {
		newPublisher.setId(modifiedPublisherId);
		String msg = "";
		// Iniciamos la transaccion para guardar el editorial
		EditorialDao dao = DaoFactory.getEditorialDao();
		try {
			dao.beginTransaction();
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processPublisher(newPublisher, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getPublishersCreateForm(messageSource.getMessage(
								"error.publisher.fieldsNotRight", null, null)
								+ " " + newPublisher.getNombre()));
				model.addAttribute("publisherData", newPublisher);
				return "commons/body";
			}
			// Guardamos el libro
			dao.update(newPublisher);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Publishers'"
							+ " para la ruta '/publishers?acceptUpdate' al modificar el editorial",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			publishersPagManager = new PaginationManager(messageSource,
					dao.getCountEditoriales());
			// Buscamos los datos
			publishersData = dao.getEditorialesWithPag(
					publishersPagManager.getPage() - 1,
					publishersPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			publishersFilter = null;
		} catch (Exception e) {
			if (publishersPagManager == null) {
				publishersPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Publishers'"
					+ " para la ruta '/publishers?acceptCreation'"
					+ " al cargar los datos", e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getPublishersList(msg));
		// Fijamos variables para la vista
		model.addAttribute("publishersData", publishersData);
		model.addAttribute("page", publishersPagManager.getPageLabel());
		model.addAttribute("pageCount", publishersPagManager.getPageCountLabel());
		model.addAttribute("filter", publishersFilter == null ? new Editorial()
				: publishersFilter);
		return "commons/body";
	}

	/**
	 * Metodo para gestionar peticiones AJAX para obtener datos de editoriales
	 * 
	 * @param hint
	 * @return
	 */
	@RequestMapping(value = "/publishers", params = { "getdata" }, method = RequestMethod.POST)
	public @ResponseBody
	List<Editorial> getData(@RequestParam("getdata") String hint) {
		EditorialDao dao = DaoFactory.getEditorialDao();
		try {
			return dao.getEditoriales(hint);
		} catch (Exception e) {
			log.error("Error los datos mediante peticion AJAX"
					+ " de las editoriales", e);
			return new ArrayList<Editorial>();
		}
	}
}
