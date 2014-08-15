package es.magDevs.myLibrary.web.controllers;

import java.util.ArrayList;
import java.util.List;

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
import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.dao.LibroDao;
import es.magDevs.myLibrary.model.dao.TipoDao;
import es.magDevs.myLibrary.model.dao.UbicacionDao;
import es.magDevs.myLibrary.web.gui.beans.MenuItem;
import es.magDevs.myLibrary.web.gui.beans.filters.BooksFilter;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.PaginationManager;

/**
 * Controlador para la seccion de libros
 * 
 * @author javi
 * 
 */
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class BooksController {
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

	/**
	 * Lista con los datos de las ubicaciones disponibles
	 * 
	 * @return
	 */
	String[] dataPlaces;

	@ModelAttribute("dataPlaces")
	String[] getDataPlaces() {
		// Si no los tenemos cargados los cargamos
		if (dataPlaces == null) {
			// Obtenemos los datos de las ubicaciones
			UbicacionDao dao = DaoFactory.getUbicacionDao();
			List<Ubicacion> l = dao.getUbicaciones();
			dataPlaces = new String[l.size()];
			// Recorremos la lista para obtener los que nos interesa (los
			// codigos de las ubicaciones)
			for (int i = 0; i < dataPlaces.length; i++) {
				dataPlaces[i] = l.get(i).getCodigo();
			}
		}
		return dataPlaces;
	}

	/**
	 * Lista con los datos de los tipos disponibles
	 * 
	 * @return
	 */
	String[] dataTypes;

	@ModelAttribute("dataTypes")
	String[] getDataTypes() {
		// Si no los tenemos cargados los cargamos
		if (dataTypes == null) {
			// Obtenemos los datos de los tipos
			TipoDao dao = DaoFactory.getTipoDao();
			List<Tipo> l = dao.getTipos();
			dataTypes = new String[l.size()];
			// Recorremos la lista para obtener los que nos interesa (los
			// codigos de las ubicaciones)
			for (int i = 0; i < dataTypes.length; i++) {
				dataTypes[i] = l.get(i).getDescripcion();
			}
		}
		return dataTypes;
	}

	/* *****************************************
	 * *************** LIBROS ******************
	 * *****************************************
	 */
	private PaginationManager booksPagManager;
	private List<Libro> booksData;
	private BooksFilter booksFilter;

	@RequestMapping(value = "/books")
	public String books(Model model) {
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getBooksList());
		// Iniciamos paginacion
		LibroDao dao = DaoFactory.getLibroDao();
		booksPagManager = new PaginationManager(messageSource,
				dao.getCountLibros());
		//Buscamos los datos
		booksData = dao.getLibrosWithPag(booksPagManager.getPage() - 1,
				booksPagManager.getPageSize());
		//Reiniciamos el filtro de busqueda
		booksFilter= null;
		//Fijamos variables para la vista
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "next" })
	public String booksNext(Model model) {
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getBooksList());
		// Realizamos la paginacion
		LibroDao dao = DaoFactory.getLibroDao();
		if (booksPagManager == null) {
			booksPagManager = new PaginationManager(messageSource,
					dao.getCountLibros(booksFilter));
		}
		if (booksPagManager.next()) {
			booksData = dao.getLibrosWithPag(booksFilter,
					booksPagManager.getPage() - 1,
					booksPagManager.getPageSize());
		}
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "previous" })
	public String booksPrevious(Model model) {
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getBooksList());
		// Realizamos la paginacion
		LibroDao dao = DaoFactory.getLibroDao();
		if (booksPagManager == null) {
			booksPagManager = new PaginationManager(messageSource,
					dao.getCountLibros(booksFilter));
		}
		if (booksPagManager.previous()) {
			booksData = dao.getLibrosWithPag(booksFilter,
					booksPagManager.getPage() - 1,
					booksPagManager.getPageSize());
		}
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "start" })
	public String booksStart(Model model) {
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getBooksList());
		// Realizamos la paginacion
		LibroDao dao = DaoFactory.getLibroDao();
		if (booksPagManager == null) {
			booksPagManager = new PaginationManager(messageSource,
					dao.getCountLibros(booksFilter));
		}
		if (booksPagManager.start()) {
			booksData = dao.getLibrosWithPag(booksFilter,
					booksPagManager.getPage() - 1,
					booksPagManager.getPageSize());
		}
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "end" })
	public String booksEnd(Model model) {
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getBooksList());
		// Realizamos la paginacion
		LibroDao dao = DaoFactory.getLibroDao();
		if (booksPagManager == null) {
			booksPagManager = new PaginationManager(messageSource,
					dao.getCountLibros(booksFilter));
		}
		if (booksPagManager.end()) {
			booksData = dao.getLibrosWithPag(booksFilter,
					booksPagManager.getPage() - 1,
					booksPagManager.getPageSize());
		}
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "pageSize" })
	public String booksPagesSize(@RequestParam("pageSize") String pageSize,
			Model model) {
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getBooksList());
		// Realizamos la paginacion
		LibroDao dao = DaoFactory.getLibroDao();
		if (booksPagManager == null) {
			booksPagManager = new PaginationManager(messageSource,
					dao.getCountLibros(booksFilter));
		}
		if (booksPagManager.setPageSize(pageSize)) {
			booksData = dao.getLibrosWithPag(booksFilter,
					booksPagManager.getPage() - 1,
					booksPagManager.getPageSize());
			model.addAttribute("selectedPageSize", pageSize);
		} else {
			model.addAttribute("selectedPageSize",
					"" + booksPagManager.getPageSize());
		}
		model.addAttribute("selectedPageSize", pageSize);
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "search" })
	public String booksFilter(BooksFilter filter, Model model) {
		// Guardamos el filtro
		booksFilter = FilterManager.processBooksFilter(filter);
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getBooksList());
		// Iniciamos paginacion
		LibroDao dao = DaoFactory.getLibroDao();
		booksPagManager = new PaginationManager(messageSource,
				dao.getCountLibros(booksFilter));
		booksData = dao.getLibrosWithPag(booksFilter,
				booksPagManager.getPage() - 1, booksPagManager.getPageSize());

		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	// @RequestMapping(value = "/books", params = { "create" })
	// public String booksCreate(@RequestParam("create") String index, Model
	// model) {
	// System.out.println(index);
	// model.addAttribute("menuTemplate", "commons/menu");
	// model.addAttribute("menuFragment", "menu");
	// model.addAttribute("mainTemplate", "commons/empty");
	// model.addAttribute("mainFragment", "empty");
	// model.addAttribute("footerTemplate", "commons/footer");
	// model.addAttribute("footerFragment", "footer");
	// return "commons/body";
	// }
}
