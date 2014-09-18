package es.magDevs.myLibrary.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.dao.AutorDao;
import es.magDevs.myLibrary.model.dao.ColeccionDao;
import es.magDevs.myLibrary.model.dao.EditorialDao;
import es.magDevs.myLibrary.model.dao.LibroDao;
import es.magDevs.myLibrary.model.dao.TipoDao;
import es.magDevs.myLibrary.model.dao.TraductorDao;
import es.magDevs.myLibrary.model.dao.UbicacionDao;
import es.magDevs.myLibrary.web.gui.beans.MenuItem;
import es.magDevs.myLibrary.web.gui.beans.filters.BooksFilter;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;
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

	// LOG
	private static final Logger log = Logger.getLogger(BooksController.class);

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
	List<Ubicacion> dataPlaces;

	@ModelAttribute("dataPlaces")
	List<Ubicacion> getDataPlaces() {
		try {
			// Obtenemos los datos de las ubicaciones
			UbicacionDao dao = DaoFactory.getUbicacionDao();
			dataPlaces = dao.getUbicaciones();
		} catch (Exception e) {
			if (dataTypes == null) {
				dataTypes = new ArrayList<Tipo>();
			}
			log.error("Error en el controlador 'Books'"
					+ " al cargar las UBICACIONES", e);
		}
		return dataPlaces;
	}

	/**
	 * Lista con los datos de los tipos disponibles
	 * 
	 * @return
	 */
	List<Tipo> dataTypes;

	@ModelAttribute("dataTypes")
	List<Tipo> getDataTypes() {
		try {
			// Obtenemos los datos de los tipos
			TipoDao dao = DaoFactory.getTipoDao();
			dataTypes = dao.getTipos();
		} catch (Exception e) {
			if (dataTypes == null) {
				dataTypes = new ArrayList<Tipo>();
			}
			log.error("Error en el controlador 'Books'"
					+ " al cargar los TIPOS", e);
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
	private Editorial newPublisher;
	private Coleccion newCollection;
	private Map<Integer, Autor> newAuthors;
	private Map<Integer, Traductor> newTranslators;
	private Integer modifiedBookId;

	@RequestMapping(value = "/books")
	public String books(Model model) {
		try {
			// Iniciamos paginacion
			LibroDao dao = DaoFactory.getLibroDao();
			booksPagManager = new PaginationManager(messageSource,
					dao.getCountLibros());
			// Buscamos los datos
			booksData = dao.getLibrosWithPag(booksPagManager.getPage() - 1,
					booksPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			booksFilter = null;
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(""));
		} catch (Exception e) {
			if (booksPagManager == null) {
				booksPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Books' para la ruta '/books'",
					e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "next" })
	public String booksNext(Model model) {
		try {
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
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(""));
		} catch (Exception e) {
			if (booksPagManager == null) {
				booksPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Books'"
					+ " para la ruta '/books?next'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "previous" })
	public String booksPrevious(Model model) {
		try {
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
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(""));
		} catch (Exception e) {
			if (booksPagManager == null) {
				booksPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Books'"
					+ " para la ruta '/books?previous'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "start" })
	public String booksStart(Model model) {
		try {
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
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(""));
		} catch (Exception e) {
			if (booksPagManager == null) {
				booksPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Books'"
					+ " para la ruta '/books?start'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "end" })
	public String booksEnd(Model model) {
		try {
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
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(""));
		} catch (Exception e) {
			if (booksPagManager == null) {
				booksPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Books'"
					+ " para la ruta '/books?end'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
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
		try {
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
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(""));
		} catch (Exception e) {
			if (booksPagManager == null) {
				booksPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Books'"
					+ " para la ruta '/books?pagSize'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
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
		try {
			// Guardamos el filtro
			booksFilter = FilterManager.processBooksFilter(filter);
			// Iniciamos paginacion
			LibroDao dao = DaoFactory.getLibroDao();
			booksPagManager = new PaginationManager(messageSource,
					dao.getCountLibros(booksFilter));
			booksData = dao.getLibrosWithPag(booksFilter,
					booksPagManager.getPage() - 1,
					booksPagManager.getPageSize());
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(""));
		} catch (Exception e) {
			if (booksPagManager == null) {
				booksPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Books'"
					+ " para la ruta '/books?search'", e);
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.getBooksList(messageSource
					.getMessage("error", null, null)));
		}
		// Fijamos variables para la vista
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "create" })
	public String booksCreate(@RequestParam("create") Integer index, Model model) {
		Libro bookData = null;
		// Creamos un hashmap para guardar los autores que se le asignaran al
		// libro
		if (newAuthors == null) {
			newAuthors = new HashMap<Integer, Autor>();
		} else {
			newAuthors.clear();
		}
		// Creamos un hashmap para guardar los traductores que se le asignaran
		// al libro
		if (newTranslators == null) {
			newTranslators = new HashMap<Integer, Traductor>();
		} else {
			newTranslators.clear();
		}

		// Mensaje a mostrar en caso de error
		String msg = "";
		// Si tenemos un indice valido
		if (index >= 0 && booksData != null) {
			try {
				// Obtenemos todos los datos del libro seleccionado
				LibroDao dao = DaoFactory.getLibroDao();
				bookData = dao.getLibro(booksData.get(index).getId());
				// Guardamos los autores que ya tiene asignados el nuevo libro
				if (bookData.getAutores() != null
						&& bookData.getAutores().size() > 0) {
					for (Autor autor : bookData.getAutores()) {
						newAuthors.put(autor.getId(), autor);
					}
				}
				// Guardamos los traductores que ya tiene asignados el nuevo
				// libro
				if (bookData.getTraductores() != null
						&& bookData.getTraductores().size() > 0) {
					for (Traductor traductor : bookData.getTraductores()) {
						newTranslators.put(traductor.getId(), traductor);
					}
				}
			} catch (Exception e) {
				// Si no conseguimos un libro, creamos uno vacio para que no de
				// error
				if (bookData == null) {
					bookData = new BooksFilter();
				}
				log.error("Error en el controlador 'Books'"
						+ " para la ruta '/books?create'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un libro vacio, para que no de fallos al intentar acceder
			// a algunos campos
			bookData = new BooksFilter();
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getBooksCreateForm(msg));

		// Fijamos variables para la vista
		model.addAttribute("bookData", bookData);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "delete" })
	public String booksDelete(@RequestParam("delete") Integer index, Model model) {
		// Mensaje a mostrar en caso de error
		String msg = "";
		LibroDao dao = DaoFactory.getLibroDao();

		try {
			// Borramos el libro indicado por el indice recibido
			dao.beginTransaction();
			dao.delete(booksData.get(index));
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error("Error en el controlador 'Books' al borrar un libro: "
					+ index + " " + booksData.get(index), e);
			msg = messageSource.getMessage("error.delete", null, null);
		}

		try {
			// Iniciamos paginacion
			booksPagManager = new PaginationManager(messageSource,
					dao.getCountLibros());
			// Buscamos los datos
			booksData = dao.getLibrosWithPag(booksPagManager.getPage() - 1,
					booksPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			booksFilter = null;
		} catch (Exception e) {
			if (booksPagManager == null) {
				booksPagManager = new PaginationManager(messageSource, 0);
			}
			log.error("Error en el controlador 'Books'"
					+ " para la ruta '/delete' al mostrar los datos", e);
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getBooksList(msg));
		// Fijamos variables para la vista
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "update" })
	public String booksUpdate(@RequestParam("update") Integer index, Model model) {
		Libro bookData = null;
		// Creamos un hashmap para guardar los autores que se le asignaran al
		// libro
		if (newAuthors == null) {
			newAuthors = new HashMap<Integer, Autor>();
		} else {
			newAuthors.clear();
		}
		// Creamos un hashmap para guardar los traductores que se le asignaran
		// al libro
		if (newTranslators == null) {
			newTranslators = new HashMap<Integer, Traductor>();
		} else {
			newTranslators.clear();
		}

		// Si tenemos un indice valido
		String msg = "";
		if (index >= 0 && booksData != null) {
			try {
				// Obtenemos todos los datos del libro seleccionado
				LibroDao dao = DaoFactory.getLibroDao();
				bookData = dao.getLibro(booksData.get(index).getId());
				// Guardamos los autores que ya tiene asignados el nuevo libro
				if (bookData.getAutores() != null
						&& bookData.getAutores().size() > 0) {
					for (Autor autor : bookData.getAutores()) {
						newAuthors.put(autor.getId(), autor);
					}
				}
				// Guardamos los traductores que ya tiene asignados el nuevo
				// libro
				if (bookData.getTraductores() != null
						&& bookData.getTraductores().size() > 0) {
					for (Traductor traductor : bookData.getTraductores()) {
						newTranslators.put(traductor.getId(), traductor);
					}
				}
			} catch (Exception e) {
				// Si no conseguimos un libro, creamos uno vacio para que no de
				// error
				if (bookData == null) {
					bookData = new BooksFilter();
				}
				log.error("Error en el controlador 'Books'"
						+ " para la ruta '/books?update'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un libro vacio, para que no de fallos al intentar acceder
			// a algunos campos
			bookData = new BooksFilter();
			msg = messageSource.getMessage("books.menu.update.noIndexMsg",
					null, null);
		}
		modifiedBookId = bookData.getId();

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getBooksUpdateForm(msg));

		model.addAttribute("bookData", bookData);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "read" })
	public String booksRead(@RequestParam("read") Integer index, Model model) {
		Libro bookData = null;
		String msg = "";

		// Si tenemos un indice valido
		if (index >= 0 && booksData != null) {
			// Obtenemos todos los datos del libro seleccionado
			LibroDao dao = DaoFactory.getLibroDao();
			try {
				bookData = dao.getLibro(booksData.get(index).getId());
			} catch (Exception e) {
				// Si no conseguimos un libro, creamos uno vacio para que no de
				// error
				if (bookData == null) {
					bookData = new BooksFilter();
				}
				log.error("Error en el controlador 'Books'"
						+ " para la ruta '/books?read'", e);
				msg = messageSource.getMessage("error", null, null);
			}
		} else {
			// Creamos un libro vacio, para que no de fallos al intentar acceder
			// a algunos campos
			bookData = new BooksFilter();
			msg = messageSource.getMessage("books.menu.read.noIndexMsg", null,
					null);
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getBooksReadForm(msg));

		model.addAttribute("bookData", bookData);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "acceptCreation" })
	public String booksAcceptCreation(Libro newBook, Model model) {
		String msg = "";
		// Iniciamos la transaccion para guardar el libro
		LibroDao dao = DaoFactory.getLibroDao();
		try {
			dao.beginTransaction();
			// Si tenemos una editorial nueva la creamos
			if (newBook.getEditorial().getId() == -1) {
				// Si tenemos una editorial valida
				if (NewDataManager
						.processPublisher(newPublisher, messageSource)) {
					EditorialDao editorialDao = DaoFactory.getEditorialDao();
					int editorialId = editorialDao.insert(newPublisher);
					newBook.getEditorial().setId(editorialId);
					//Si tenemos un nueva coleccion, le asignamos el id de la editorial
					if (newBook.getColeccion().getId() == -1) {
						newCollection.getEditorial().setId(editorialId);
					}
				} else {
					msg += messageSource.getMessage(
							"error.publisher.fieldsNotRight", null, null)
							+ " "
							+ newPublisher.getNombre() + "\n";
				}
			}
			// Si tenemos una coleccion nueva la creamos
			if (newBook.getColeccion().getId() == -1) {
				// La coleccion tendra misma editorial que el libro
				newBook.getColeccion().getEditorial()
						.setId(newBook.getEditorial().getId());
				// Si tenemos una coleccion valida
				if (NewDataManager.processCollection(newCollection,
						messageSource)) {
					newCollection.getEditorial().setId(
							newBook.getEditorial().getId());
					ColeccionDao coleccionDao = DaoFactory.getColeccionDao();
					int coleccionId = coleccionDao.insert(newCollection);
					newBook.getColeccion().setId(coleccionId);
				} else {
					msg += messageSource.getMessage(
							"error.collection.fieldsNotRight", null, null)
							+ " " + newCollection.getNombre() + "\n";
				}
			}

			// Recorremos todos los autores a asignar para añadirlos al libro
			newBook.setAutores(new HashSet<Autor>());
			for (Entry<Integer, Autor> author : newAuthors.entrySet()) {
				Integer authorId = author.getKey();
				// Comprobamos si tenemos que crear el autor
				if (authorId < 0) {
					// Si no tenemos un autor que añadir, lo saltamos
					if (author.getValue() == null) {
						continue;
					}
					if (NewDataManager.processAuthor(author.getValue(),
							messageSource)) {
						AutorDao autorDao = DaoFactory.getAutorDao();
						authorId = autorDao.insert(author.getValue());
					} else {
						msg += messageSource.getMessage(
								"error.author.fieldsNotRight", null, null)
								+ " "
								+ author.getValue().getNombre()
								+ " "
								+ author.getValue().getApellidos() + "\n";
					}
				}
				// Creamos un autor para referenciar al autor asignado
				Autor tmpAutor = new Autor();
				tmpAutor.setId(authorId);
				newBook.getAutores().add(tmpAutor);
			}

			// Recorremos todos los traductores a asignar para añadirlos al
			// libro
			newBook.setTraductores(new HashSet<Traductor>());
			for (Entry<Integer, Traductor> translator : newTranslators
					.entrySet()) {
				Integer translatorId = translator.getKey();
				// Comprobamos si tenemos que crear el traductor
				if (translatorId < 0) {
					// Si no tenemos un traductor que añadir, lo saltamos
					if (translator.getValue() == null) {
						continue;
					}
					if (NewDataManager.processTranslator(translator.getValue(),
							messageSource)) {
						TraductorDao traductorDao = DaoFactory
								.getTraductorDao();
						translatorId = traductorDao.insert(translator
								.getValue());
					} else {
						msg += messageSource.getMessage(
								"error.translator.fieldsNotRight", null, null)
								+ " "
								+ translator.getValue().getNombre()
								+ "\n";
					}
				}
				// Creamos un autor para referenciar al autor asignado
				Traductor tmpTraductor = new Traductor();
				tmpTraductor.setId(translatorId);
				newBook.getTraductores().add(tmpTraductor);
			}

			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processBook(newBook, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getBooksCreateForm(messageSource.getMessage(
								"error.book.fieldsNotRight", null, null)));
				model.addAttribute("bookData", newBook);
				return "commons/body";
			}
			// Guardamos el libro
			dao.insert(newBook);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Books'"
							+ " para la ruta '/books?acceptCreation' al crear el libro",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			booksPagManager = new PaginationManager(messageSource,
					dao.getCountLibros());
			// Buscamos los datos
			booksData = dao.getLibrosWithPag(booksPagManager.getPage() - 1,
					booksPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			booksFilter = null;
		} catch (Exception e) {
			if (booksPagManager == null) {
				booksPagManager = new PaginationManager(messageSource, 0);
			}
			log.error(
					"Error en el controlador 'Books' para la ruta '/books?acceptCreation' al cargar los datos",
					e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getBooksList(msg));
		// Fijamos variables para la vista
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	@RequestMapping(value = "/books", params = { "acceptUpdate" })
	public String booksAcceptUpdate(Libro newBook, Model model) {
		newBook.setId(modifiedBookId);
		String msg = "";
		// Iniciamos la transaccion para guardar el libro
		LibroDao dao = DaoFactory.getLibroDao();
		try {
			dao.beginTransaction();
			// Si tenemos una editorial nueva la creamos
			if (newBook.getEditorial().getId() == -1) {
				// Si tenemos una editorial valida
				if (NewDataManager
						.processPublisher(newPublisher, messageSource)) {
					EditorialDao editorialDao = DaoFactory.getEditorialDao();
					int editorialId = editorialDao.insert(newPublisher);
					newBook.getEditorial().setId(editorialId);
				} else {
					msg += messageSource.getMessage(
							"error.publisher.fieldsNotRight", null, null)
							+ "\n";
				}
			}
			// Si tenemos una coleccion nueva la creamos
			if (newBook.getColeccion().getId() == -1) {
				// La coleccion tendra misma editorial que el libro
				newBook.getColeccion().getEditorial()
						.setId(newBook.getEditorial().getId());
				// Si tenemos una coleccion valida
				if (NewDataManager.processCollection(newCollection,
						messageSource)) {
					newCollection.getEditorial().setId(
							newBook.getEditorial().getId());
					ColeccionDao coleccionDao = DaoFactory.getColeccionDao();
					int coleccionId = coleccionDao.insert(newCollection);
					newBook.getColeccion().setId(coleccionId);
				} else {
					msg += messageSource.getMessage(
							"error.collection.fieldsNotRight", null, null)
							+ "\n";
				}
			}

			// Recorremos todos los autores a asignar para añadirlos al libro
			newBook.setAutores(new HashSet<Autor>());
			for (Entry<Integer, Autor> author : newAuthors.entrySet()) {
				Integer authorId = author.getKey();
				// Comprobamos si tenemos que crear el autor
				if (authorId < 0) {
					// Si no tenemos un autor que añadir, lo saltamos
					if (author.getValue() == null) {
						continue;
					}
					if (NewDataManager.processAuthor(author.getValue(),
							messageSource)) {
						AutorDao autorDao = DaoFactory.getAutorDao();
						authorId = autorDao.insert(author.getValue());
					} else {
						msg += messageSource.getMessage(
								"error.author.fieldsNotRight", null, null)
								+ "\n";
					}
				}
				// Creamos un autor para referenciar al autor asignado
				Autor tmpAutor = new Autor();
				tmpAutor.setId(authorId);
				newBook.getAutores().add(tmpAutor);
			}

			// Recorremos todos los traductores a asignar para añadirlos al
			// libro
			newBook.setTraductores(new HashSet<Traductor>());
			for (Entry<Integer, Traductor> translator : newTranslators
					.entrySet()) {
				Integer translatorId = translator.getKey();
				// Comprobamos si tenemos que crear el traductor
				if (translatorId < 0) {
					// Si no tenemos un traductor que añadir, lo saltamos
					if (translator.getValue() == null) {
						continue;
					}
					if (NewDataManager.processTranslator(translator.getValue(),
							messageSource)) {
						TraductorDao traductorDao = DaoFactory
								.getTraductorDao();
						translatorId = traductorDao.insert(translator
								.getValue());
					} else {
						msg += messageSource.getMessage(
								"error.translator.fieldsNotRight", null, null)
								+ "\n";
					}
				}
				// Creamos un autor para referenciar al autor asignado
				Traductor tmpTraductor = new Traductor();
				tmpTraductor.setId(translatorId);
				newBook.getTraductores().add(tmpTraductor);
			}

			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processBook(newBook, messageSource)) {
				dao.rollbackTransaction();
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager
						.getBooksCreateForm(messageSource.getMessage(
								"error.book.fieldsNotRight", null, null)));
				model.addAttribute("bookData", newBook);
				return "commons/body";
			}
			// Guardamos el libro
			dao.update(newBook);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			log.error(
					"Error en el controlador 'Books'"
							+ " para la ruta '/books?acceptUpdate' al modificar el libro",
					e);
			msg = messageSource.getMessage("error", null, null);
		}

		try {
			// Iniciamos paginacion
			booksPagManager = new PaginationManager(messageSource,
					dao.getCountLibros());
			// Buscamos los datos
			booksData = dao.getLibrosWithPag(booksPagManager.getPage() - 1,
					booksPagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			booksFilter = null;
		} catch (Exception e) {
			if (booksPagManager == null) {
				booksPagManager = new PaginationManager(messageSource, 0);
			}
			log.error(
					"Error en el controlador 'Books' para la ruta '/books?acceptUpdate' al cargar los datos",
					e);
			// Enlazamos fragmentos de plantillas
			msg = messageSource.getMessage("error", null, null);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getBooksList(msg));
		// Fijamos variables para la vista
		model.addAttribute("booksData", booksData);
		model.addAttribute("page", booksPagManager.getPageLabel());
		model.addAttribute("pageCount", booksPagManager.getPageCountLabel());
		model.addAttribute("filter", booksFilter == null ? new BooksFilter()
				: booksFilter);
		return "commons/body";
	}

	/**
	 * Metodo que registra la creacion de una editorial, cada vez que se cree
	 * una se remplazara por la ya existente
	 * 
	 * @param requestBody
	 * @return
	 */
	@RequestMapping(value = "/books/newPublisher", method = RequestMethod.POST)
	public @ResponseBody
	String newPublisher(@RequestBody String requestBody) {
		try {
			// Tranformamos los datos JSON recibidos en el objeto Editorial
			newPublisher = new ObjectMapper().readValue(requestBody,
					Editorial.class);
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}
		return "OK";
	}

	/**
	 * Metodo que registra la creacion de una coleccion, cada vez que se cree
	 * una se remplazara por la ya existente
	 * 
	 * @param requestBody
	 * @return
	 */
	@RequestMapping(value = "/books/newCollection", method = RequestMethod.POST)
	public @ResponseBody
	String newCollection(@RequestBody String requestBody) {
		try {
			// Tranformamos los datos JSON recibidos en el objeto Coleccion
			newCollection = new ObjectMapper().readValue(requestBody,
					Coleccion.class);
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}
		return "OK";
	}

	/**
	 * Metodo que registra que se ha asignado un autor al libro
	 * 
	 * @param requestBody
	 * @return
	 */
	@RequestMapping(value = "/books", params = { "addAuthor" }, method = RequestMethod.POST)
	public @ResponseBody
	String addAuthor(@RequestParam("addAuthor") Integer authorId) {
		if (!newAuthors.containsKey(authorId) && authorId > 0) {
			newAuthors.put(authorId, null);
		}
		return "OK";
	}

	/**
	 * Metodo que registra que se ha quitado un autor del libro
	 * 
	 * @param requestBody
	 * @return
	 */
	@RequestMapping(value = "/books", params = { "quitAuthor" }, method = RequestMethod.POST)
	public @ResponseBody
	String quitAuthor(@RequestParam("quitAuthor") String authorId) {
		String[] ids = authorId.split(",");
		for (int i = 0; i < ids.length; i++) {
			try {
				Integer id = Integer.parseInt(ids[i]);
				newAuthors.remove(id);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return "OK";
	}

	/**
	 * Metodo que registra la creacion de autor
	 * 
	 * @param requestBody
	 * @return
	 */
	@RequestMapping(value = "/books/newAuthor", method = RequestMethod.POST)
	public @ResponseBody
	String newAuthor(@RequestBody String requestBody) {
		try {
			// Tranformamos los datos JSON recibidos en el objeto Autor
			Autor tmpAutor = new ObjectMapper().readValue(requestBody,
					Autor.class);
			if (!newAuthors.containsKey(tmpAutor.getId())) {
				newAuthors.put(tmpAutor.getId(), tmpAutor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}
		return "OK";
	}

	/**
	 * Metodo que registra que se ha asignado un traductor al libro
	 * 
	 * @param requestBody
	 * @return
	 */
	@RequestMapping(value = "/books", params = { "addTranslator" }, method = RequestMethod.POST)
	public @ResponseBody
	String addTranslator(@RequestParam("addTranslator") Integer translatorId) {
		if (!newTranslators.containsKey(translatorId) && translatorId > 0) {
			newTranslators.put(translatorId, null);
		}
		return "OK";
	}

	/**
	 * Metodo que registra que se ha quitado un traductor del libro
	 * 
	 * @param requestBody
	 * @return
	 */
	@RequestMapping(value = "/books", params = { "quitTranslator" }, method = RequestMethod.POST)
	public @ResponseBody
	String quitTranslator(@RequestParam("quitTranslator") String translatorId) {
		String[] ids = translatorId.split(",");
		for (int i = 0; i < ids.length; i++) {
			try {
				Integer id = Integer.parseInt(ids[i]);
				newTranslators.remove(id);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return "OK";
	}

	/**
	 * Metodo que registra la creacion de un traductor
	 * 
	 * @param requestBody
	 * @return
	 */
	@RequestMapping(value = "/books/newTranslator", method = RequestMethod.POST)
	public @ResponseBody
	String newTranslator(@RequestBody String requestBody) {
		try {
			// Tranformamos los datos JSON recibidos en el objeto Traductor
			Traductor tmpTraductor = new ObjectMapper().readValue(requestBody,
					Traductor.class);
			if (!newTranslators.containsKey(tmpTraductor.getId())) {
				newTranslators.put(tmpTraductor.getId(), tmpTraductor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}
		return "OK";
	}
}
