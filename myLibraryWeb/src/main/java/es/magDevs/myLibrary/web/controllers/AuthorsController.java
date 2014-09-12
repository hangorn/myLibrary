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
import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.AutorDao;
import es.magDevs.myLibrary.web.gui.beans.MenuItem;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;
import es.magDevs.myLibrary.web.gui.utils.PaginationManager;

/**
 * Controlador para la seccion de autores
 * 
 * @author javi
 * 
 */
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class AuthorsController {
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
	private static final Logger log = Logger.getLogger(AuthorsController.class);

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
	 * *************** AUTORES *****************
	 * *****************************************
	 */
	private PaginationManager authorsPagManager;
	private List<Autor> authorsData;
	private Autor authorsFilter;
	private Integer modifiedAuthorId;

	@RequestMapping(value = "/authors")
	public String authors(Model model) {
		try {
			// Iniciamos paginacion
			AutorDao dao = DaoFactory.getAutorDao();
			authorsPagManager = new PaginationManager(messageSource,
					dao.getCountAutores());
			// Buscamos los datos
			authorsData = dao.getAutoresWithPag(
					authorsPagManager.getPage() - 1,
					authorsPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			authorsFilter = null;
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(""));
		} catch (Exception e) {
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Authors'"
					+ " para la ruta '/authors'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("authorsData", authorsData);
		model.addAttribute("page", authorsPagManager.getPageLabel());
		model.addAttribute("pageCount", authorsPagManager.getPageCountLabel());
		model.addAttribute("filter", authorsFilter == null ? new Autor()
				: authorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/authors", params = { "next" })
	public String authorsNext(Model model) {
		try {
			// Realizamos la paginacion
			AutorDao dao = DaoFactory.getAutorDao();
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource,
						dao.getCountAutores(authorsFilter));
			}
			if (authorsPagManager.next()) {
				authorsData = dao.getAutoresWithPag(authorsFilter,
						authorsPagManager.getPage() - 1,
						authorsPagManager.getPageSize());
			}

			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(""));
		} catch (Exception e) {
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Authors'"
					+ " para la ruta '/authors?next'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("authorsData", authorsData);
		model.addAttribute("page", authorsPagManager.getPageLabel());
		model.addAttribute("pageCount", authorsPagManager.getPageCountLabel());
		model.addAttribute("filter", authorsFilter == null ? new Autor()
				: authorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/authors", params = { "previous" })
	public String authorsPrevious(Model model) {
		try {
			// Realizamos la paginacion
			AutorDao dao = DaoFactory.getAutorDao();
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource,
						dao.getCountAutores(authorsFilter));
			}
			if (authorsPagManager.previous()) {
				authorsData = dao.getAutoresWithPag(authorsFilter,
						authorsPagManager.getPage() - 1,
						authorsPagManager.getPageSize());
			}
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(""));
		} catch (Exception e) {
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Authors'"
					+ " para la ruta '/authors?previous'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("authorsData", authorsData);
		model.addAttribute("page", authorsPagManager.getPageLabel());
		model.addAttribute("pageCount", authorsPagManager.getPageCountLabel());
		model.addAttribute("filter", authorsFilter == null ? new Autor()
				: authorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/authors", params = { "start" })
	public String authorsStart(Model model) {
		try {
			// Realizamos la paginacion
			AutorDao dao = DaoFactory.getAutorDao();
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource,
						dao.getCountAutores(authorsFilter));
			}
			if (authorsPagManager.start()) {
				authorsData = dao.getAutoresWithPag(authorsFilter,
						authorsPagManager.getPage() - 1,
						authorsPagManager.getPageSize());
			}

			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(""));
		} catch (Exception e) {
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Authors'"
					+ " para la ruta '/authors?start'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("authorsData", authorsData);
		model.addAttribute("page", authorsPagManager.getPageLabel());
		model.addAttribute("pageCount", authorsPagManager.getPageCountLabel());
		model.addAttribute("filter", authorsFilter == null ? new Autor()
				: authorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/authors", params = { "end" })
	public String authorsEnd(Model model) {
		try {
			// Realizamos la paginacion
			AutorDao dao = DaoFactory.getAutorDao();
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource,
						dao.getCountAutores(authorsFilter));
			}
			if (authorsPagManager.end()) {
				authorsData = dao.getAutoresWithPag(authorsFilter,
						authorsPagManager.getPage() - 1,
						authorsPagManager.getPageSize());
			}

			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(""));
		} catch (Exception e) {
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Authors'"
					+ " para la ruta '/authors?end'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("authorsData", authorsData);
		model.addAttribute("page", authorsPagManager.getPageLabel());
		model.addAttribute("pageCount", authorsPagManager.getPageCountLabel());
		model.addAttribute("filter", authorsFilter == null ? new Autor()
				: authorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/authors", params = { "pageSize" })
	public String authorsPagesSize(@RequestParam("pageSize") String pageSize,
			Model model) {
		try {
			// Realizamos la paginacion
			AutorDao dao = DaoFactory.getAutorDao();
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource,
						dao.getCountAutores(authorsFilter));
			}
			if (authorsPagManager.setPageSize(pageSize)) {
				authorsData = dao.getAutoresWithPag(authorsFilter,
						authorsPagManager.getPage() - 1,
						authorsPagManager.getPageSize());
				model.addAttribute("selectedPageSize", pageSize);
			} else {
				model.addAttribute("selectedPageSize",
						"" + authorsPagManager.getPageSize());
			}

			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(""));
		} catch (Exception e) {
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Authors'"
					+ " para la ruta '/authors?pageSize'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("selectedPageSize", pageSize);
		model.addAttribute("authorsData", authorsData);
		model.addAttribute("page", authorsPagManager.getPageLabel());
		model.addAttribute("pageCount", authorsPagManager.getPageCountLabel());
		model.addAttribute("filter", authorsFilter == null ? new Autor()
				: authorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/authors", params = { "search" })
	public String authorsFilter(Autor filter, Model model) {
		try {
			// Guardamos el filtro
			authorsFilter = FilterManager.processAuthorsFilter(filter);
			// Iniciamos paginacion
			AutorDao dao = DaoFactory.getAutorDao();
			authorsPagManager = new PaginationManager(messageSource,
					dao.getCountAutores(authorsFilter));
			authorsData = dao.getAutoresWithPag(authorsFilter,
					authorsPagManager.getPage() - 1,
					authorsPagManager.getPageSize());
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(""));
		} catch (Exception e) {
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Authors'"
					+ " para la ruta '/authors=search'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getAuthorsList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("authorsData", authorsData);
		model.addAttribute("page", authorsPagManager.getPageLabel());
		model.addAttribute("pageCount", authorsPagManager.getPageCountLabel());
		model.addAttribute("filter", authorsFilter == null ? new Autor()
				: authorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/authors", params = { "create" })
	public String authorsCreate(@RequestParam("create") Integer index,
			Model model) {
		Autor authorData = null;
		// Mensaje a mostrar en caso de error
		String msg = "";
		// Si tenemos un indice valido
		if (index >= 0 && authorsData != null) {
			try {
				// Obtenemos todos los datos del autor seleccionado
				AutorDao dao = DaoFactory.getAutorDao();
				authorData = dao.getAutor(authorsData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un autor, creamos uno vacio para que no de
				// error
				if (authorData == null) {
					authorData = new Autor();
				}
				log.error("Error en el controlador 'Authors'"
						+ " para la ruta '/authors?create'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un autor vacio, para que no de fallos al intentar acceder
			// a algunos campos
			authorData = new Autor();
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getAuthorsCreateForm(msg));

		// Fijamos variables para la vista
		model.addAttribute("authorData", authorData);
		return "commons/body";
	}

	@RequestMapping(value = "/authors", params = { "delete" })
	public String authorsDelete(@RequestParam("delete") Integer index,
			Model model) {
		// Mensaje a mostrar en caso de error
		String msg = "";
		AutorDao dao = DaoFactory.getAutorDao();

		try {
			// Borramos el autor indicado por el indice recibido
			dao.beginTransaction();
			dao.delete(authorsData.get(index));
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error("Error en el controlador 'Authors' al borrar un autor: "
					+ index + " " + authorsData.get(index), e);
			msg = messageSource.getMessage("error.delete", null, null);
		}

		try {
			// Iniciamos paginacion
			authorsPagManager = new PaginationManager(messageSource,
					dao.getCountAutores());
			// Buscamos los datos
			authorsData = dao.getAutoresWithPag(
					authorsPagManager.getPage() - 1,
					authorsPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			authorsFilter = null;
		} catch (Exception e) {
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Authors'"
					+ " para la ruta '/delete' al mostrar los datos", e);
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getAuthorsList(msg));
		// Fijamos variables para la vista
		model.addAttribute("authorsData", authorsData);
		model.addAttribute("page", authorsPagManager.getPageLabel());
		model.addAttribute("pageCount", authorsPagManager.getPageCountLabel());
		model.addAttribute("filter", authorsFilter == null ? new Autor()
				: authorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/authors", params = { "update" })
	public String authorsUpdate(@RequestParam("update") Integer index, Model model) {
		Autor authorData = null;

		// Si tenemos un indice valido
		String msg = "";
		if (index >= 0 && authorsData != null) {
			try {
				// Obtenemos todos los datos del autor seleccionado
				AutorDao dao = DaoFactory.getAutorDao();
				authorData = dao.getAutor(authorsData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un autor, creamos uno vacio para que no de
				// error
				if (authorData == null) {
					authorData = new Autor();
				}
				log.error("Error en el controlador 'Authors'"
						+ " para la ruta '/authors?update'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un autor vacio, para que no de fallos al intentar acceder
			// a algunos campos
			authorData = new Autor();
			msg = messageSource.getMessage("authors.menu.update.noIndexMsg",
					null, null);
		}
		modifiedAuthorId = authorData.getId();

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getAuthorsUpdateForm(msg));

		model.addAttribute("authorData", authorData);
		return "commons/body";
	}

	@RequestMapping(value = "/authors", params = { "read" })
	public String authorsRead(@RequestParam("read") Integer index, Model model) {
		Autor authorData = null;
		List<Libro> authorBooks = null;
		String msg = "";

		// Si tenemos un indice valido
		if (index >= 0 && authorsData != null) {
			// Obtenemos todos los datos del libro seleccionado
			AutorDao dao = DaoFactory.getAutorDao();
			try {
				authorData = dao.getAutor(authorsData.get(index).getId());
				authorBooks =  dao.getLibrosAutor(authorsData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un libro, creamos uno vacio para que no de
				// error
				if (authorData == null) {
					authorData = new Autor();
				}
				if (authorBooks == null) {
					authorBooks = new ArrayList<Libro>();
				}
				log.error("Error en el controlador 'Authors'"
						+ " para la ruta '/authors?read'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un autor vacio, para que no de fallos al intentar acceder
			// a algunos campos
			authorData = new Autor();
			authorBooks = new ArrayList<Libro>();
			msg = messageSource.getMessage("authors.menu.read.noIndexMsg", null,
					null);
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getAuthorsReadForm(msg));

		model.addAttribute("authorData", authorData);

		model.addAttribute("authorBooks", authorBooks);
		return "commons/body";
	}

	@RequestMapping(value = "/authors", params = { "acceptCreation" })
	public String authorsAcceptCreation(Autor newAuthor, Model model) {
		String msg = "";
		// Iniciamos la transaccion para guardar el autor
		AutorDao dao = DaoFactory.getAutorDao();
		try {
			dao.beginTransaction();
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processAuthor(newAuthor, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getAuthorsCreateForm(messageSource.getMessage(
								"error.author.fieldsNotRight", null, null)
								+ " " + newAuthor.getApellidos()));
				model.addAttribute("authorData", newAuthor);
				return "commons/body";
			}
			// Guardamos el autor
			dao.insert(newAuthor);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Authors'"
							+ " para la ruta '/authors?acceptCreation' al crear el autor",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			authorsPagManager = new PaginationManager(messageSource,
					dao.getCountAutores());
			// Buscamos los datos
			authorsData = dao.getAutoresWithPag(
					authorsPagManager.getPage() - 1,
					authorsPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			authorsFilter = null;
		} catch (Exception e) {
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Authors'"
					+ " para la ruta '/authors?acceptCreation'"
					+ " al cargar los datos", e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getAuthorsList(msg));
		// Fijamos variables para la vista
		model.addAttribute("authorsData", authorsData);
		model.addAttribute("page", authorsPagManager.getPageLabel());
		model.addAttribute("pageCount", authorsPagManager.getPageCountLabel());
		model.addAttribute("filter", authorsFilter == null ? new Autor()
				: authorsFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/authors", params = { "acceptUpdate" })
	public String authorsAcceptUpdate(Autor newAuthor, Model model) {
		newAuthor.setId(modifiedAuthorId);
		String msg = "";
		// Iniciamos la transaccion para guardar el autor
		AutorDao dao = DaoFactory.getAutorDao();
		try {
			dao.beginTransaction();
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processAuthor(newAuthor, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getAuthorsCreateForm(messageSource.getMessage(
								"error.author.fieldsNotRight", null, null)
								+ " " + newAuthor.getApellidos()));
				model.addAttribute("authorData", newAuthor);
				return "commons/body";
			}
			// Guardamos el libro
			dao.update(newAuthor);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Authors'"
							+ " para la ruta '/authors?acceptUpdate' al modificar el autor",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			authorsPagManager = new PaginationManager(messageSource,
					dao.getCountAutores());
			// Buscamos los datos
			authorsData = dao.getAutoresWithPag(
					authorsPagManager.getPage() - 1,
					authorsPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			authorsFilter = null;
		} catch (Exception e) {
			if (authorsPagManager == null) {
				authorsPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Authors'"
					+ " para la ruta '/authors?acceptCreation'"
					+ " al cargar los datos", e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getAuthorsList(msg));
		// Fijamos variables para la vista
		model.addAttribute("authorsData", authorsData);
		model.addAttribute("page", authorsPagManager.getPageLabel());
		model.addAttribute("pageCount", authorsPagManager.getPageCountLabel());
		model.addAttribute("filter", authorsFilter == null ? new Autor()
				: authorsFilter);
		return "commons/body";
	}

	/**
	 * Metodo para gestionar peticiones AJAX para obtener datos de autores
	 * 
	 * @param hint
	 * @return
	 */
	@RequestMapping(value = "/authors", params = { "getdata" }, method = RequestMethod.POST)
	public @ResponseBody
	List<Autor> getData(@RequestParam("getdata") String hint) {
		AutorDao dao = DaoFactory.getAutorDao();
		try {
			return dao.getAutores(hint);
		} catch (Exception e) {
			log.error("Error los datos mediante peticion AJAX"
					+ " de los autores", e);
			return new ArrayList<Autor>();
		}
	}
}
