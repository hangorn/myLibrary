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
import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.dao.UbicacionDao;
import es.magDevs.myLibrary.web.gui.beans.MenuItem;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;
import es.magDevs.myLibrary.web.gui.utils.PaginationManager;

/**
 * Controlador para la seccion de ubicaciones
 * 
 * @place javi
 * 
 */
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class PlacesController {
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
	private static final Logger log = Logger.getLogger(PlacesController.class);

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
	private PaginationManager placesPagManager;
	private List<Ubicacion> placesData;
	private Ubicacion placesFilter;
	private Integer modifiedPlaceId;

	@RequestMapping(value = "/places")
	public String places(Model model) {
		try {
			// Iniciamos paginacion
			UbicacionDao dao = DaoFactory.getUbicacionDao();
			placesPagManager = new PaginationManager(messageSource,
					dao.getCountUbicaciones());
			// Buscamos los datos
			placesData = dao.getUbicacionesWithPag(
					placesPagManager.getPage() - 1,
					placesPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			placesFilter = null;
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(""));
		} catch (Exception e) {
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Places'"
					+ " para la ruta '/places'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("placesData", placesData);
		model.addAttribute("page", placesPagManager.getPageLabel());
		model.addAttribute("pageCount", placesPagManager.getPageCountLabel());
		model.addAttribute("filter", placesFilter == null ? new Ubicacion()
				: placesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/places", params = { "next" })
	public String placesNext(Model model) {
		try {
			// Realizamos la paginacion
			UbicacionDao dao = DaoFactory.getUbicacionDao();
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource,
						dao.getCountUbicaciones(placesFilter));
			}
			if (placesPagManager.next()) {
				placesData = dao.getUbicacionesWithPag(placesFilter,
						placesPagManager.getPage() - 1,
						placesPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(""));
		} catch (Exception e) {
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Places'"
					+ " para la ruta '/places?next'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("placesData", placesData);
		model.addAttribute("page", placesPagManager.getPageLabel());
		model.addAttribute("pageCount", placesPagManager.getPageCountLabel());
		model.addAttribute("filter", placesFilter == null ? new Ubicacion()
				: placesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/places", params = { "previous" })
	public String placesPrevious(Model model) {
		try {
			// Realizamos la paginacion
			UbicacionDao dao = DaoFactory.getUbicacionDao();
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource,
						dao.getCountUbicaciones(placesFilter));
			}
			if (placesPagManager.previous()) {
				placesData = dao.getUbicacionesWithPag(placesFilter,
						placesPagManager.getPage() - 1,
						placesPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(""));
		} catch (Exception e) {
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Places'"
					+ " para la ruta '/places?previous'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("placesData", placesData);
		model.addAttribute("page", placesPagManager.getPageLabel());
		model.addAttribute("pageCount", placesPagManager.getPageCountLabel());
		model.addAttribute("filter", placesFilter == null ? new Ubicacion()
				: placesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/places", params = { "start" })
	public String placesStart(Model model) {
		try {
			// Realizamos la paginacion
			UbicacionDao dao = DaoFactory.getUbicacionDao();
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource,
						dao.getCountUbicaciones(placesFilter));
			}
			if (placesPagManager.start()) {
				placesData = dao.getUbicacionesWithPag(placesFilter,
						placesPagManager.getPage() - 1,
						placesPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(""));
		} catch (Exception e) {
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Places'"
					+ " para la ruta '/places?start'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("placesData", placesData);
		model.addAttribute("page", placesPagManager.getPageLabel());
		model.addAttribute("pageCount", placesPagManager.getPageCountLabel());
		model.addAttribute("filter", placesFilter == null ? new Ubicacion()
				: placesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/places", params = { "end" })
	public String placesEnd(Model model) {
		try {
			// Realizamos la paginacion
			UbicacionDao dao = DaoFactory.getUbicacionDao();
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource,
						dao.getCountUbicaciones(placesFilter));
			}
			if (placesPagManager.end()) {
				placesData = dao.getUbicacionesWithPag(placesFilter,
						placesPagManager.getPage() - 1,
						placesPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(""));
		} catch (Exception e) {
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Places'"
					+ " para la ruta '/places?end'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("placesData", placesData);
		model.addAttribute("page", placesPagManager.getPageLabel());
		model.addAttribute("pageCount", placesPagManager.getPageCountLabel());
		model.addAttribute("filter", placesFilter == null ? new Ubicacion()
				: placesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/places", params = { "pageSize" })
	public String placesPagesSize(@RequestParam("pageSize") String pageSize,
			Model model) {
		try {
			// Realizamos la paginacion
			UbicacionDao dao = DaoFactory.getUbicacionDao();
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource,
						dao.getCountUbicaciones(placesFilter));
			}
			if (placesPagManager.setPageSize(pageSize)) {
				placesData = dao.getUbicacionesWithPag(placesFilter,
						placesPagManager.getPage() - 1,
						placesPagManager.getPageSize());
				model.addAttribute("selectedPageSize", pageSize);
			} else {
				model.addAttribute("selectedPageSize",
						"" + placesPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(""));
		} catch (Exception e) {
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Places'"
					+ " para la ruta '/places?pageSize'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("selectedPageSize", pageSize);
		model.addAttribute("placesData", placesData);
		model.addAttribute("page", placesPagManager.getPageLabel());
		model.addAttribute("pageCount", placesPagManager.getPageCountLabel());
		model.addAttribute("filter", placesFilter == null ? new Ubicacion()
				: placesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/places", params = { "search" })
	public String placesFilter(Ubicacion filter, Model model) {
		try {
			// Guardamos el filtro
			placesFilter = FilterManager.processPlacesFilter(filter);
			// Iniciamos paginacion
			UbicacionDao dao = DaoFactory.getUbicacionDao();
			placesPagManager = new PaginationManager(messageSource,
					dao.getCountUbicaciones(placesFilter));
			placesData = dao.getUbicacionesWithPag(placesFilter,
					placesPagManager.getPage() - 1,
					placesPagManager.getPageSize());

			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(""));
		} catch (Exception e) {
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Places'"
					+ " para la ruta '/places?search'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getPlacesList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("placesData", placesData);
		model.addAttribute("page", placesPagManager.getPageLabel());
		model.addAttribute("pageCount", placesPagManager.getPageCountLabel());
		model.addAttribute("filter", placesFilter == null ? new Ubicacion()
				: placesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/places", params = { "create" })
	public String placesCreate(@RequestParam("create") Integer index,
			Model model) {
		Ubicacion placeData = null;
		// Mensaje a mostrar en caso de error
		String msg = "";
		// Si tenemos un indice valido
		if (index >= 0 && placesData != null) {
			try {
				// Obtenemos todos los datos del ubicacion seleccionado
				UbicacionDao dao = DaoFactory.getUbicacionDao();
				placeData = dao.getUbicacion(placesData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un ubicacion, creamos uno vacio para que no de
				// error
				if (placeData == null) {
					placeData = new Ubicacion();
				}
				log.error("Error en el controlador 'Places'"
						+ " para la ruta '/places?create'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un ubicacion vacio, para que no de fallos al intentar acceder
			// a algunos campos
			placeData = new Ubicacion();
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getPlacesCreateForm(msg));

		// Fijamos variables para la vista
		model.addAttribute("placeData", placeData);
		return "commons/body";
	}

	@RequestMapping(value = "/places", params = { "delete" })
	public String placesDelete(@RequestParam("delete") Integer index,
			Model model) {
		// Mensaje a mostrar en caso de error
		String msg = "";
		UbicacionDao dao = DaoFactory.getUbicacionDao();

		try {
			// Borramos el ubicacion indicado por el indice recibido
			dao.beginTransaction();
			dao.delete(placesData.get(index));
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error("Error en el controlador 'Places' al borrar un ubicacion: "
					+ index + " " + placesData.get(index), e);
			msg = messageSource.getMessage("error.delete", null, null);
		}

		try {
			// Iniciamos paginacion
			placesPagManager = new PaginationManager(messageSource,
					dao.getCountUbicaciones());
			// Buscamos los datos
			placesData = dao.getUbicacionesWithPag(
					placesPagManager.getPage() - 1,
					placesPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			placesFilter = null;
		} catch (Exception e) {
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Places'"
					+ " para la ruta '/delete' al mostrar los datos", e);
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getPlacesList(msg));
		// Fijamos variables para la vista
		model.addAttribute("placesData", placesData);
		model.addAttribute("page", placesPagManager.getPageLabel());
		model.addAttribute("pageCount", placesPagManager.getPageCountLabel());
		model.addAttribute("filter", placesFilter == null ? new Ubicacion()
				: placesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/places", params = { "update" })
	public String placesUpdate(@RequestParam("update") Integer index, Model model) {
		Ubicacion placeData = null;

		// Si tenemos un indice valido
		String msg = "";
		if (index >= 0 && placesData != null) {
			try {
				// Obtenemos todos los datos del ubicacion seleccionado
				UbicacionDao dao = DaoFactory.getUbicacionDao();
				placeData = dao.getUbicacion(placesData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un ubicacion, creamos uno vacio para que no de
				// error
				if (placeData == null) {
					placeData = new Ubicacion();
				}
				log.error("Error en el controlador 'Places'"
						+ " para la ruta '/places?update'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un ubicacion vacio, para que no de fallos al intentar acceder
			// a algunos campos
			placeData = new Ubicacion();
			msg = messageSource.getMessage("places.menu.update.noIndexMsg",
					null, null);
		}
		modifiedPlaceId = placeData.getId();

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getPlacesUpdateForm(msg));

		model.addAttribute("placeData", placeData);
		return "commons/body";
	}

	@RequestMapping(value = "/places", params = { "read" })
	public String placesRead(@RequestParam("read") Integer index, Model model) {
		Ubicacion placeData = null;
		List<Libro> placeBooks = null;
		String msg = "";

		// Si tenemos un indice valido
		if (index >= 0 && placesData != null) {
			// Obtenemos todos los datos del libro seleccionado
			UbicacionDao dao = DaoFactory.getUbicacionDao();
			try {
				placeData = dao.getUbicacion(placesData.get(index).getId());
				placeBooks =  dao.getLibrosUbicacion(placesData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un libro, creamos uno vacio para que no de
				// error
				if (placeData == null) {
					placeData = new Ubicacion();
				}
				if (placeBooks == null) {
					placeBooks = new ArrayList<Libro>();
				}
				log.error("Error en el controlador 'Places'"
						+ " para la ruta '/places?read'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un ubicacion vacio, para que no de fallos al intentar acceder
			// a algunos campos
			placeData = new Ubicacion();
			placeBooks = new ArrayList<Libro>();
			msg = messageSource.getMessage("places.menu.read.noIndexMsg", null,
					null);
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getPlacesReadForm(msg));

		model.addAttribute("placeData", placeData);

		model.addAttribute("placeBooks", placeBooks);
		return "commons/body";
	}

	@RequestMapping(value = "/places", params = { "acceptCreation" })
	public String placesAcceptCreation(Ubicacion newPlace, Model model) {
		String msg = "";
		// Iniciamos la transaccion para guardar el ubicacion
		UbicacionDao dao = DaoFactory.getUbicacionDao();
		try {
			dao.beginTransaction();
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processPlace(newPlace, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getPlacesCreateForm(messageSource.getMessage(
								"error.place.fieldsNotRight", null, null)
								+ " " + newPlace.getCodigo()));
				model.addAttribute("placeData", newPlace);
				return "commons/body";
			}
			// Guardamos el ubicacion
			dao.insert(newPlace);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Places'"
							+ " para la ruta '/places?acceptCreation' al crear el ubicacion",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			placesPagManager = new PaginationManager(messageSource,
					dao.getCountUbicaciones());
			// Buscamos los datos
			placesData = dao.getUbicacionesWithPag(
					placesPagManager.getPage() - 1,
					placesPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			placesFilter = null;
		} catch (Exception e) {
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Places'"
					+ " para la ruta '/places?acceptCreation'"
					+ " al cargar los datos", e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getPlacesList(msg));
		// Fijamos variables para la vista
		model.addAttribute("placesData", placesData);
		model.addAttribute("page", placesPagManager.getPageLabel());
		model.addAttribute("pageCount", placesPagManager.getPageCountLabel());
		model.addAttribute("filter", placesFilter == null ? new Ubicacion()
				: placesFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/places", params = { "acceptUpdate" })
	public String placesAcceptUpdate(Ubicacion newPlace, Model model) {
		newPlace.setId(modifiedPlaceId);
		String msg = "";
		// Iniciamos la transaccion para guardar el ubicacion
		UbicacionDao dao = DaoFactory.getUbicacionDao();
		try {
			dao.beginTransaction();
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processPlace(newPlace, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getPlacesCreateForm(messageSource.getMessage(
								"error.place.fieldsNotRight", null, null)
								+ " " + newPlace.getCodigo()));
				model.addAttribute("placeData", newPlace);
				return "commons/body";
			}
			// Guardamos el libro
			dao.update(newPlace);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Places'"
							+ " para la ruta '/places?acceptUpdate' al modificar el ubicacion",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			placesPagManager = new PaginationManager(messageSource,
					dao.getCountUbicaciones());
			// Buscamos los datos
			placesData = dao.getUbicacionesWithPag(
					placesPagManager.getPage() - 1,
					placesPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			placesFilter = null;
		} catch (Exception e) {
			if (placesPagManager == null) {
				placesPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Places'"
					+ " para la ruta '/places?acceptCreation'"
					+ " al cargar los datos", e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getPlacesList(msg));
		// Fijamos variables para la vista
		model.addAttribute("placesData", placesData);
		model.addAttribute("page", placesPagManager.getPageLabel());
		model.addAttribute("pageCount", placesPagManager.getPageCountLabel());
		model.addAttribute("filter", placesFilter == null ? new Ubicacion()
				: placesFilter);
		return "commons/body";
	}
}
