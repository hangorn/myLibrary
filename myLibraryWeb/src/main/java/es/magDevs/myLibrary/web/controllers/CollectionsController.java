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
import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.ColeccionDao;
import es.magDevs.myLibrary.model.dao.EditorialDao;
import es.magDevs.myLibrary.web.gui.beans.MenuItem;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;
import es.magDevs.myLibrary.web.gui.utils.PaginationManager;

/**
 * Controlador para la seccion de colecciones
 * 
 * @collection javi
 * 
 */
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class CollectionsController {
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
			.getLogger(CollectionsController.class);

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
	 * ************* COLECCIONES ***************
	 * *****************************************
	 */
	private PaginationManager collectionsPagManager;
	private List<Coleccion> collectionsData;
	private Coleccion collectionsFilter;
	private Editorial newPublisher;
	private Integer modifiedCollectionId;

	@RequestMapping(value = "/collections")
	public String collections(Model model) {
		try {
			// Iniciamos paginacion
			ColeccionDao dao = DaoFactory.getColeccionDao();
			collectionsPagManager = new PaginationManager(messageSource,
					dao.getCountColecciones());
			// Buscamos los datos
			collectionsData = dao.getColeccionesWithPag(
					collectionsPagManager.getPage() - 1,
					collectionsPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			collectionsFilter = null;
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getCollectionsList(""));
		} catch (Exception e) {
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Collections'"
					+ " para la ruta '/collections'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager
					.getCollectionsList(messageSource.getMessage("error", null,
							null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("collectionsData", collectionsData);
		model.addAttribute("page", collectionsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				collectionsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				collectionsFilter == null ? new Coleccion() : collectionsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/collections", params = { "next" })
	public String collectionsNext(Model model) {
		try {
			// Realizamos la paginacion
			ColeccionDao dao = DaoFactory.getColeccionDao();
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource,
						dao.getCountColecciones(collectionsFilter));
			}
			if (collectionsPagManager.next()) {
				collectionsData = dao.getColeccionesWithPag(collectionsFilter,
						collectionsPagManager.getPage() - 1,
						collectionsPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getCollectionsList(""));
		} catch (Exception e) {
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Collections'"
					+ " para la ruta '/collections?next'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager
					.getCollectionsList(messageSource.getMessage("error", null,
							null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("collectionsData", collectionsData);
		model.addAttribute("page", collectionsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				collectionsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				collectionsFilter == null ? new Coleccion() : collectionsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/collections", params = { "previous" })
	public String collectionsPrevious(Model model) {
		try {
			// Realizamos la paginacion
			ColeccionDao dao = DaoFactory.getColeccionDao();
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource,
						dao.getCountColecciones(collectionsFilter));
			}
			if (collectionsPagManager.previous()) {
				collectionsData = dao.getColeccionesWithPag(collectionsFilter,
						collectionsPagManager.getPage() - 1,
						collectionsPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getCollectionsList(""));
		} catch (Exception e) {
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Collections'"
					+ " para la ruta '/collections?previous'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager
					.getCollectionsList(messageSource.getMessage("error", null,
							null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("collectionsData", collectionsData);
		model.addAttribute("page", collectionsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				collectionsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				collectionsFilter == null ? new Coleccion() : collectionsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/collections", params = { "start" })
	public String collectionsStart(Model model) {
		try {
			// Realizamos la paginacion
			ColeccionDao dao = DaoFactory.getColeccionDao();
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource,
						dao.getCountColecciones(collectionsFilter));
			}
			if (collectionsPagManager.start()) {
				collectionsData = dao.getColeccionesWithPag(collectionsFilter,
						collectionsPagManager.getPage() - 1,
						collectionsPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getCollectionsList(""));
		} catch (Exception e) {
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Collections'"
					+ " para la ruta '/collections?start'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager
					.getCollectionsList(messageSource.getMessage("error", null,
							null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("collectionsData", collectionsData);
		model.addAttribute("page", collectionsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				collectionsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				collectionsFilter == null ? new Coleccion() : collectionsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/collections", params = { "end" })
	public String collectionsEnd(Model model) {
		try {
			// Realizamos la paginacion
			ColeccionDao dao = DaoFactory.getColeccionDao();
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource,
						dao.getCountColecciones(collectionsFilter));
			}
			if (collectionsPagManager.end()) {
				collectionsData = dao.getColeccionesWithPag(collectionsFilter,
						collectionsPagManager.getPage() - 1,
						collectionsPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getCollectionsList(""));
		} catch (Exception e) {
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Collections'"
					+ " para la ruta '/collections?end'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager
					.getCollectionsList(messageSource.getMessage("error", null,
							null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("collectionsData", collectionsData);
		model.addAttribute("page", collectionsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				collectionsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				collectionsFilter == null ? new Coleccion() : collectionsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/collections", params = { "pageSize" })
	public String collectionsPagesSize(
			@RequestParam("pageSize") String pageSize, Model model) {
		try {
			// Realizamos la paginacion
			ColeccionDao dao = DaoFactory.getColeccionDao();
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource,
						dao.getCountColecciones(collectionsFilter));
			}
			if (collectionsPagManager.setPageSize(pageSize)) {
				collectionsData = dao.getColeccionesWithPag(collectionsFilter,
						collectionsPagManager.getPage() - 1,
						collectionsPagManager.getPageSize());
				model.addAttribute("selectedPageSize", pageSize);
			} else {
				model.addAttribute("selectedPageSize", ""
						+ collectionsPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getCollectionsList(""));
		} catch (Exception e) {
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Collections'"
					+ " para la ruta '/collections?pageSize'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager
					.getCollectionsList(messageSource.getMessage("error", null,
							null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("selectedPageSize", pageSize);
		model.addAttribute("collectionsData", collectionsData);
		model.addAttribute("page", collectionsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				collectionsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				collectionsFilter == null ? new Coleccion() : collectionsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/collections", params = { "search" })
	public String collectionsFilter(Coleccion filter, Model model) {
		try {
			// Guardamos el filtro
			collectionsFilter = FilterManager.processCollectionsFilter(filter);
			// Iniciamos paginacion
			ColeccionDao dao = DaoFactory.getColeccionDao();
			collectionsPagManager = new PaginationManager(messageSource,
					dao.getCountColecciones(collectionsFilter));
			collectionsData = dao.getColeccionesWithPag(collectionsFilter,
					collectionsPagManager.getPage() - 1,
					collectionsPagManager.getPageSize());
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getCollectionsList(""));
		} catch (Exception e) {
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Collections'"
					+ " para la ruta '/collections?search'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager
					.getCollectionsList(messageSource.getMessage("error", null,
							null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("collectionsData", collectionsData);
		model.addAttribute("page", collectionsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				collectionsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				collectionsFilter == null ? new Coleccion() : collectionsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/collections", params = { "create" })
	public String collectionsCreate(@RequestParam("create") Integer index,
			Model model) {
		Coleccion collectionData = null;
		// Mensaje a mostrar en caso de error
		String msg = "";
		// Si tenemos un indice valido
		if (index >= 0 && collectionsData != null) {
			try {
				// Obtenemos todos los datos de la coleccion seleccionada
				ColeccionDao dao = DaoFactory.getColeccionDao();
				collectionData = dao.getColeccion(collectionsData.get(index)
						.getId());
			} catch (Exception e) {
				// Si no conseguimos una coleccion, creamos una vacia para que
				// no de
				// error
				if (collectionData == null) {
					collectionData = new Coleccion();
				}
				log.error("Error en el controlador 'Collections'"
						+ " para la ruta '/collections?create'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos una coleccion vacia, para que no de fallos al intentar
			// acceder
			// a algunos campos
			collectionData = new Coleccion();
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getCollectionsCreateForm(msg));

		// Fijamos variables para la vista
		model.addAttribute("collectionData", collectionData);
		return "commons/body";
	}

	@RequestMapping(value = "/collections", params = { "delete" })
	public String collectionsDelete(@RequestParam("delete") Integer index,
			Model model) {
		// Mensaje a mostrar en caso de error
		String msg = "";
		ColeccionDao dao = DaoFactory.getColeccionDao();

		try {
			// Borramos la coleccion indicada por el indice recibido
			dao.beginTransaction();
			dao.delete(collectionsData.get(index));
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Collections' al borrar un coleccion: "
							+ index + " " + collectionsData.get(index), e);
			msg = messageSource.getMessage("error.delete", null, null);
		}

		try {
			// Iniciamos paginacion
			collectionsPagManager = new PaginationManager(messageSource,
					dao.getCountColecciones());
			// Buscamos los datos
			collectionsData = dao.getColeccionesWithPag(
					collectionsPagManager.getPage() - 1,
					collectionsPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			collectionsFilter = null;
		} catch (Exception e) {
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Collections'"
					+ " para la ruta '/delete' al mostrar los datos", e);
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getCollectionsList(msg));
		// Fijamos variables para la vista
		model.addAttribute("collectionsData", collectionsData);
		model.addAttribute("page", collectionsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				collectionsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				collectionsFilter == null ? new Coleccion() : collectionsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/collections", params = { "update" })
	public String collectionsUpdate(@RequestParam("update") Integer index,
			Model model) {
		Coleccion collectionData = null;

		// Si tenemos un indice valido
		String msg = "";
		if (index >= 0 && collectionsData != null) {
			try {
				// Obtenemos todos los datos del coleccion seleccionado
				ColeccionDao dao = DaoFactory.getColeccionDao();
				collectionData = dao.getColeccion(collectionsData.get(index)
						.getId());
			} catch (Exception e) {
				// Si no conseguimos un coleccion, creamos uno vacio para que no
				// de
				// error
				if (collectionData == null) {
					collectionData = new Coleccion();
				}
				log.error("Error en el controlador 'Collections'"
						+ " para la ruta '/collections?update'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un coleccion vacio, para que no de fallos al intentar
			// acceder
			// a algunos campos
			collectionData = new Coleccion();
			msg = messageSource.getMessage(
					"collections.menu.update.noIndexMsg", null, null);
		}
		modifiedCollectionId = collectionData.getId();

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getCollectionsUpdateForm(msg));

		model.addAttribute("collectionData", collectionData);
		return "commons/body";
	}

	@RequestMapping(value = "/collections", params = { "read" })
	public String collectionsRead(@RequestParam("read") Integer index,
			Model model) {
		Coleccion collectionData = null;
		List<Libro> collectionBooks = null;
		String msg = "";

		// Si tenemos un indice valido
		if (index >= 0 && collectionsData != null) {
			// Obtenemos todos los datos del libro seleccionado
			ColeccionDao dao = DaoFactory.getColeccionDao();
			try {
				collectionData = dao.getColeccion(collectionsData.get(index)
						.getId());
				collectionBooks = dao.getLibrosColeccion(collectionsData.get(
						index).getId());
			} catch (Exception e) {
				// Si no conseguimos un libro, creamos uno vacio para que no de
				// error
				if (collectionData == null) {
					collectionData = new Coleccion();
				}
				if (collectionBooks == null) {
					collectionBooks = new ArrayList<Libro>();
				}
				log.error("Error en el controlador 'Collections'"
						+ " para la ruta '/collections?read'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un coleccion vacio, para que no de fallos al intentar
			// acceder
			// a algunos campos
			collectionData = new Coleccion();
			collectionBooks = new ArrayList<Libro>();
			msg = messageSource.getMessage("collections.menu.read.noIndexMsg",
					null, null);
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getCollectionsReadForm(msg));

		model.addAttribute("collectionData", collectionData);

		model.addAttribute("collectionBooks", collectionBooks);
		return "commons/body";
	}

	@RequestMapping(value = "/collections", params = { "acceptCreation" })
	public String collectionsAcceptCreation(Coleccion newCollection, Model model) {
		String msg = "";
		// Iniciamos la transaccion para guardar el coleccion
		ColeccionDao dao = DaoFactory.getColeccionDao();
		try {
			dao.beginTransaction();
			// Si tenemos una editorial nueva la creamos
			if (newCollection.getEditorial().getId() == -1) {
				// Si tenemos una editorial valida
				if (NewDataManager
						.processPublisher(newPublisher, messageSource)) {
					EditorialDao editorialDao = DaoFactory.getEditorialDao();
					int editorialId = editorialDao.insert(newPublisher);
					newCollection.getEditorial().setId(editorialId);
				} else {
					msg += messageSource.getMessage(
							"error.publisher.fieldsNotRight", null, null)
							+ " "
							+ newPublisher.getNombre() + "\n";
				}
			}
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processCollection(newCollection, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getCollectionsCreateForm(messageSource.getMessage(
								"error.collection.fieldsNotRight", null, null)
								+ " " + newCollection.getNombre()));
				model.addAttribute("collectionData", newCollection);
				return "commons/body";
			}
			// Guardamos el coleccion
			dao.insert(newCollection);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Collections'"
							+ " para la ruta '/collections?acceptCreation' al crear el coleccion",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			collectionsPagManager = new PaginationManager(messageSource,
					dao.getCountColecciones());
			// Buscamos los datos
			collectionsData = dao.getColeccionesWithPag(
					collectionsPagManager.getPage() - 1,
					collectionsPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			collectionsFilter = null;
		} catch (Exception e) {
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Collections'"
					+ " para la ruta '/collections?acceptCreation'"
					+ " al cargar los datos", e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getCollectionsList(msg));
		// Fijamos variables para la vista
		model.addAttribute("collectionsData", collectionsData);
		model.addAttribute("page", collectionsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				collectionsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				collectionsFilter == null ? new Coleccion() : collectionsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/collections", params = { "acceptUpdate" })
	public String collectionsAcceptUpdate(Coleccion newCollection, Model model) {
		newCollection.setId(modifiedCollectionId);
		String msg = "";
		// Iniciamos la transaccion para guardar el coleccion
		ColeccionDao dao = DaoFactory.getColeccionDao();
		try {
			dao.beginTransaction();
			// Si tenemos una editorial nueva la creamos
			if (newCollection.getEditorial().getId() == -1) {
				// Si tenemos una editorial valida
				if (NewDataManager
						.processPublisher(newPublisher, messageSource)) {
					EditorialDao editorialDao = DaoFactory.getEditorialDao();
					int editorialId = editorialDao.insert(newPublisher);
					newCollection.getEditorial().setId(editorialId);
				} else {
					msg += messageSource.getMessage(
							"error.publisher.fieldsNotRight", null, null)
							+ "\n";
				}
			}
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processCollection(newCollection, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getCollectionsCreateForm(messageSource.getMessage(
								"error.collection.fieldsNotRight", null, null)
								+ " " + newCollection.getNombre()));
				model.addAttribute("collectionData", newCollection);
				return "commons/body";
			}
			// Guardamos el libro
			dao.update(newCollection);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Collections'"
							+ " para la ruta '/collections?acceptUpdate' al modificar el coleccion",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			collectionsPagManager = new PaginationManager(messageSource,
					dao.getCountColecciones());
			// Buscamos los datos
			collectionsData = dao.getColeccionesWithPag(
					collectionsPagManager.getPage() - 1,
					collectionsPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			collectionsFilter = null;
		} catch (Exception e) {
			if (collectionsPagManager == null) {
				collectionsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Collections'"
					+ " para la ruta '/collections?acceptCreation'"
					+ " al cargar los datos", e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getCollectionsList(msg));
		// Fijamos variables para la vista
		model.addAttribute("collectionsData", collectionsData);
		model.addAttribute("page", collectionsPagManager.getPageLabel());
		model.addAttribute("pageCount",
				collectionsPagManager.getPageCountLabel());
		model.addAttribute("filter",
				collectionsFilter == null ? new Coleccion() : collectionsFilter);
		return "commons/body";
	}

	/**
	 * Metodo para gestionar peticiones AJAX para obtener datos de colecciones
	 * 
	 * @param hint
	 * @return
	 */
	@RequestMapping(value = "/collections", params = { "getdata" }, method = RequestMethod.POST)
	public @ResponseBody
	List<Coleccion> getData(@RequestParam("getdata") String hint,
			@RequestParam("publisherId") Integer publisherId) {
		ColeccionDao dao = DaoFactory.getColeccionDao();
		try {
			return dao.getColecciones(hint, publisherId);
		} catch (Exception e) {
			log.error("Error los datos mediante peticion AJAX"
					+ " de las colecciones", e);
			return new ArrayList<Coleccion>();
		}
	}
}
