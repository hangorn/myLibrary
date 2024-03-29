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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.Constants.ACTION;
import es.magDevs.myLibrary.model.Constants.RELATED_ACTION;
import es.magDevs.myLibrary.model.Constants.SECTION;
import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Leido;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.MeGusta;
import es.magDevs.myLibrary.model.beans.Pendiente;
import es.magDevs.myLibrary.model.beans.Prestamo;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.beans.Usuario;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.AutorDao;
import es.magDevs.myLibrary.model.dao.ColeccionDao;
import es.magDevs.myLibrary.model.dao.EditorialDao;
import es.magDevs.myLibrary.model.dao.LibroDao;
import es.magDevs.myLibrary.model.dao.MeGustaDao;
import es.magDevs.myLibrary.model.dao.PrestamoDao;
import es.magDevs.myLibrary.model.dao.TraductorDao;
import es.magDevs.myLibrary.model.dao.UsuarioDao;
import es.magDevs.myLibrary.web.controllers.main.MainController;
import es.magDevs.myLibrary.web.gui.beans.filters.BooksFilter;
import es.magDevs.myLibrary.web.gui.utils.DatesManager;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;
import es.magDevs.myLibrary.web.gui.utils.PaginationManager;
import es.magDevs.myLibrary.web.isbn.IsbnDataProcesor;

/**
 * Controlador para la seccion de libros
 * 
 * @author javier.vaquero
 * 
 */
public class BooksController extends AbstractController {

	public BooksController(MessageSource messageSource) {
		super(messageSource);
	}

	// LOG
	private static final Logger log = Logger.getLogger(BooksController.class);

	/* *****************************************
	 * ********** IMPLEMENTACIONES *************
	 * *****************************************
	 */

	/**
	 * {@inheritDoc}
	 */
	protected SECTION getSection() {
		return SECTION.BOOKS;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Logger getLog() {
		return log;
	}

	/**
	 * {@inheritDoc}
	 */
	protected AbstractDao getDao() {
		return DaoFactory.getLibroDao();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean getNewFilter() {
		return new BooksFilter();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean processFilter(Bean filter) {
		return FilterManager.processBooksFilter((BooksFilter) filter);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean processNewData(Bean newData) {
		return NewDataManager.processBook((Libro) newData, messageSource);
	}

	private Editorial newPublisher;
	private Coleccion newCollection;
	private Map<Integer, Autor> newAuthors;
	private Map<Integer, Traductor> newTranslators;

	/* *****************************************
	 * ************** ACCIONES *****************
	 * *****************************************
	 */
	@Override
	public String create(Integer index, Model model) {
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
		// Si hay un codigo de barras
		if (filter != null && StringUtils.isNotBlank(((Libro) filter).getCb())) {
			try {
				// Obtenemos todos los datos del libro seleccionado
				bookData = new IsbnDataProcesor(e->manageException("isbnDataMiner", e)).getData(((Libro) filter).getCb());
				if (bookData != null) {
					bookData.setId(null);
					// Guardamos los autores que ya tiene asignados el nuevo libro
					if (bookData.getAutores() != null && bookData.getAutores().size() > 0) {
						for (Autor autor : bookData.getAutores()) {
							newAuthors.put(autor.getId(), autor);
						}
					}
					// Guardamos los traductores que ya tiene asignados el nuevo
					// libro
					if (bookData.getTraductores() != null && bookData.getTraductores().size() > 0) {
						for (Traductor traductor : bookData.getTraductores()) {
							newTranslators.put(traductor.getId(), traductor);
						}
					} 
				}
			} catch (Exception e) {
				bookData = new BooksFilter();
				msg = manageException("create", e);
			}
		}
		if (bookData == null) {
			if (index >= 0 && data != null) {
				// Si tenemos un indice valido
				try {
					// Obtenemos todos los datos del libro seleccionado
					bookData = (Libro) getDao().get(
							((Bean) data.get(index)).getId());
					bookData.setId(null);
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
					bookData = new BooksFilter();
					msg = manageException("create", e);
				}
			} else {
				// Creamos un libro vacio, para que no de fallos al intentar acceder
				// a algunos campos
				bookData = new BooksFilter();
			}
		}
		if (filter != null && StringUtils.isNotBlank(((Libro) filter).getCb())) {
			bookData.setCb(((Libro) filter).getCb());
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.CREATE,
				getSection()));
		// Fijamos variables para la vista
		model.addAttribute("elementData", bookData);
		model.addAttribute("currentURL", getSection().get());
		return "commons/body";
	}

	@Override
	public String updateFromId(Integer id, Model model) {
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
		if (id >= 0 && data != null) {
			try {
				// Obtenemos todos los datos del libro seleccionado
				bookData = (Libro) getCompleteData(id);
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
				bookData = new BooksFilter();
				msg = manageException("update", e);
			}
		} else {
			// Creamos un libro vacio, para que no de fallos al intentar acceder
			// a algunos campos
			bookData = new BooksFilter();
			msg = messageSource.getMessage("books.menu.update.noIndexMsg",
					null, LocaleContextHolder.getLocale());
		}
		modifiedElementId = bookData.getId();
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.UPDATE,
				getSection()));
		model.addAttribute("elementData", bookData);
		model.addAttribute("currentURL", getSection().get());
		return "commons/body";
	}

	@Override
	public String acceptCreation(Bean newElement, Model model) {
		Libro newBook = new Libro((Libro) newElement);
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
					// Si tenemos un nueva coleccion, le asignamos el id de la
					// editorial
					if (newBook.getColeccion().getId() == -1) {
						newCollection.getEditorial().setId(editorialId);
					}
				} else {
					msg += messageSource.getMessage(
							"error.publisher.fieldsNotRight", null,
							LocaleContextHolder.getLocale())
							+ " " + newPublisher.getNombre() + "\n";
				}
			}
			// Si tenemos una coleccion nueva la creamos
			if (newBook.getColeccion().getId() == -1) {
				// La coleccion tendra misma editorial que el libro
				newBook.getColeccion().getEditorial()
						.setId(newBook.getEditorial().getId());
				newCollection.getEditorial().setId(
						newBook.getEditorial().getId());
				// Si tenemos una coleccion valida
				if (NewDataManager.processCollection(newCollection,
						messageSource)) {
					ColeccionDao coleccionDao = DaoFactory.getColeccionDao();
					int coleccionId = coleccionDao.insert(newCollection);
					newBook.getColeccion().setId(coleccionId);
				} else {
					msg += messageSource.getMessage(
							"error.collection.fieldsNotRight", null,
							LocaleContextHolder.getLocale())
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
								"error.author.fieldsNotRight", null,
								LocaleContextHolder.getLocale())
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
								"error.translator.fieldsNotRight", null,
								LocaleContextHolder.getLocale())
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
				model.addAllAttributes(FragmentManager.get(messageSource
						.getMessage("error.book.fieldsNotRight", null,
								LocaleContextHolder.getLocale()),
						ACTION.CREATE, getSection()));
				model.addAttribute("elementData", newBook);
				return "commons/body";
			}
			// Guardamos el libro
			dao.insert(newBook);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			msg = manageException("acceptCreation, al crear el libro", e);
		}

		try {
			// Iniciamos paginacion
			pagManager = new PaginationManager(messageSource, dao.getCount(filter));
			// Buscamos los datos
			data = dao.getWithPag(filter, pagManager.getPage() - 1,
					pagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			//filter = null;
		} catch (Exception e) {
			msg = manageException("acceptCreation, al cargar los datos", e);
		}

		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}

	@Override
	public String acceptUpdate(Bean newElement, Model model) {
		Libro newBook = new Libro((Libro) newElement);
		newBook.setId(modifiedElementId);
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
							"error.publisher.fieldsNotRight", null,
							LocaleContextHolder.getLocale())
							+ "\n";
				}
			}
			// Si tenemos una coleccion nueva la creamos
			if (newBook.getColeccion().getId() == -1) {
				// La coleccion tendra misma editorial que el libro
				newCollection.getEditorial().setId(
						newBook.getEditorial().getId());
				// Si tenemos una coleccion valida
				if (NewDataManager.processCollection(newCollection,
						messageSource)) {
					ColeccionDao coleccionDao = DaoFactory.getColeccionDao();
					int coleccionId = coleccionDao.insert(newCollection);
					newBook.getColeccion().setId(coleccionId);
				} else {
					msg += messageSource.getMessage(
							"error.collection.fieldsNotRight", null,
							LocaleContextHolder.getLocale())
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
								"error.author.fieldsNotRight", null,
								LocaleContextHolder.getLocale())
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
								"error.translator.fieldsNotRight", null,
								LocaleContextHolder.getLocale())
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
				model.addAllAttributes(FragmentManager.get(messageSource
						.getMessage("error.book.fieldsNotRight", null,
								LocaleContextHolder.getLocale()),
						ACTION.UPDATE, getSection()));
				model.addAttribute("elementData", newBook);
				return "commons/body";
			}
			// Guardamos el libro
			dao.update(newBook);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			msg = manageException("acceptUpdate, al modificar el libro", e);
		}

		try {
			// Iniciamos paginacion
			pagManager = new PaginationManager(messageSource, dao.getCount(filter));
			// Buscamos los datos
			data = dao.getWithPag(filter, pagManager.getPage() - 1,
					pagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			//filter = null;
		} catch (Exception e) {
			msg = manageException("acceptUpdate, al cargar los datos", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}
	
	@Override
	public String manageRelatedData(RELATED_ACTION action, String dataType, String data) {
		String result = "FAIL";
		if(SECTION.PUBLISHERS.get().equals(dataType) && RELATED_ACTION.NEW.equals(action)) {
			result = newPublisher(data);
		} else if(SECTION.COLLECTIONS.get().equals(dataType) && RELATED_ACTION.NEW.equals(action)) {
			result = newCollection(data);
		} else if(SECTION.AUTHORS.get().equals(dataType)) {
			if(RELATED_ACTION.NEW.equals(action)) {
				result = newAuthor(data);
			} else if(RELATED_ACTION.ADD.equals(action)) {
				result = addAuthor(data);
			} else if(RELATED_ACTION.DELETE.equals(action)) {
				result = quitAuthor(data);
			}
		} else if(SECTION.TRANSLATORS.get().equals(dataType)) {
			if(RELATED_ACTION.NEW.equals(action)) {
				result = newTranslator(data);
			} else if(RELATED_ACTION.ADD.equals(action)) {
				result = addTranslator(data);
			} else if(RELATED_ACTION.DELETE.equals(action)) {
				result = quitTranslator(data);
			}
		} else if("like".equals(dataType)) {
			if(RELATED_ACTION.NEW.equals(action)) {
				// Aprovechamos esta accion para devolver los megustas de un libro
				result = getLikes(data);
			} else if(RELATED_ACTION.ADD.equals(action)) {
				result = likeBook(data);
			}
		}
		return result;
	}

	/**
	 * Metodo que registra la creacion de una editorial, cada vez que se cree
	 * una se remplazara por la ya existente
	 * 
	 * @param requestBody
	 * @return
	 */
	private String newPublisher(String requestBody) {
		try {
			// Tranformamos los datos JSON recibidos en el objeto Editorial
			newPublisher = new ObjectMapper().readValue(requestBody,
					Editorial.class);
		} catch (Exception e) {
			manageException("related", e);
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
	private String newCollection(String requestBody) {
		try {
			// Tranformamos los datos JSON recibidos en el objeto Coleccion
			newCollection = new ObjectMapper().readValue(requestBody,
					Coleccion.class);
		} catch (Exception e) {
			manageException("related", e);
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
	private String addAuthor(String author) {
		try {
			Integer authorId = Integer.valueOf(author);
			if (!newAuthors.containsKey(authorId) && authorId > 0) {
				newAuthors.put(authorId, null);
			}
		} catch (Exception e) {
			manageException("related", e);
			return "FAIL";
		}
		return "OK";
	}

	/**
	 * Metodo que registra que se ha quitado un autor del libro
	 * 
	 * @param requestBody
	 * @return
	 */
	private String quitAuthor(String authorId) {
		String[] ids = authorId.split(",");
		for (int i = 0; i < ids.length; i++) {
			try {
				Integer id = Integer.parseInt(ids[i]);
				newAuthors.remove(id);
			} catch (Exception e) {
				manageException("related", e);
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
	private String newAuthor(String requestBody) {
		try {
			// Tranformamos los datos JSON recibidos en el objeto Autor
			Autor tmpAutor = new ObjectMapper().readValue(requestBody,
					Autor.class);
			if (!newAuthors.containsKey(tmpAutor.getId())) {
				newAuthors.put(tmpAutor.getId(), tmpAutor);
			}
		} catch (Exception e) {
			manageException("related", e);
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
	private String addTranslator(String translator) {
		try {
			Integer translatorId = Integer.valueOf(translator);
			if (!newTranslators.containsKey(translatorId) && translatorId > 0) {
			newTranslators.put(translatorId, null);
		}
		} catch (Exception e) {
			manageException("related", e);
			return "FAIL";
		}
		return "OK";
	}

	/**
	 * Metodo que registra que se ha quitado un traductor del libro
	 * 
	 * @param requestBody
	 * @return
	 */
	private String quitTranslator(String translatorId) {
		String[] ids = translatorId.split(",");
		for (int i = 0; i < ids.length; i++) {
			try {
				Integer id = Integer.parseInt(ids[i]);
				newTranslators.remove(id);
			} catch (Exception e) {
				manageException("related", e);
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
	private String newTranslator(String requestBody) {
		try {
			// Tranformamos los datos JSON recibidos en el objeto Traductor
			Traductor tmpTraductor = new ObjectMapper().readValue(requestBody,
					Traductor.class);
			if (!newTranslators.containsKey(tmpTraductor.getId())) {
				newTranslators.put(tmpTraductor.getId(), tmpTraductor);
			}
		} catch (Exception e) {
			manageException("related", e);
			return "FAIL";
		}
		return "OK";
	}

	private String getLikes(String data) {
		String username = MainController.getUsername();
		Integer bookId = null;
		try {
			bookId = Integer.valueOf(data);
		} catch (Exception e) {
		}
		if (username != null && bookId != null) {
			MeGusta filterMegusta = new MeGusta();
			filterMegusta.setLibro(new Libro());
			filterMegusta.getLibro().setId(bookId);
			try {
				@SuppressWarnings("unchecked")
				List<MeGusta> megustas = DaoFactory.getMegustaDao().getWithPag(filterMegusta, 0, 0);
				megustas.forEach(mg->mg.getLibro().setMeGustaUsrTxt(messageSource.getMessage(mg.getMegusta()?"like":"dislike", null, LocaleContextHolder.getLocale())));
				return new ObjectMapper().writeValueAsString(megustas);
			} catch (Exception e) {
				manageException("error al obtener los (no)me gustas de un libro", e);
			}
		}
		return "FAIL";
	}

	private String likeBook(String data) {
		String username = MainController.getUsername();
		if (username != null && ("1".equals(data) || "0".equals(data))) {
			MeGusta filterMegusta = new MeGusta();
			filterMegusta.setLibro(new Libro());
			filterMegusta.getLibro().setId(modifiedElementId);
			MeGustaDao megustaDao = DaoFactory.getMegustaDao();
			try {
				megustaDao.beginTransaction();
				filterMegusta.setUsuario(DaoFactory.getUsuarioDao().getUser(username));
				@SuppressWarnings("unchecked")
				List<MeGusta> megustas = megustaDao.getWithTransaction(filterMegusta, 0, 0);
				
				String operacion = "+";
				if (megustas.isEmpty()) {
					// Si no habia lo creamos (me gusta / no me gusta)
					filterMegusta.setMegusta("1".equals(data));
					megustaDao.insert(filterMegusta);
				} else if (!data.equals(megustas.get(0).getMegusta() ? "1" : "0")) {
					// Si ya habia y no era igual lo actualizamos (me gusta / no me gusta)
					megustas.get(0).setMegusta("1".equals(data));
					megustaDao.update(megustas.get(0));
					operacion = "*";
				} else {
					// Si ya habia y era igual lo borramos (me gusta / no me gusta)
					megustaDao.delete(megustas.get(0));
					operacion = "-";
				}
				megustaDao.commitTransaction();
				return "OK"+operacion;
			} catch (Exception e) {
				megustaDao.rollbackTransaction();
				manageException("error al marcar como (no)me gusta un libro", e);
			}
		}
		return "FAIL";
	}

	public Class<BooksFilter> getBeanClass() {
		return BooksFilter.class;
	}
	
	@Override
	protected Bean getCompleteData(Integer id) throws Exception {
		// Cargamos los datos del libro
		Libro book = (Libro) super.getCompleteData(id);
		
		// Cargamos los datos de los prestamos para saber si esta actualmente prestado
		PrestamoDao prestamoDao = DaoFactory.getPrestamoDao();
		Prestamo filterPrestamo = new Prestamo();
		filterPrestamo.setLibro(new Libro());
		filterPrestamo.getLibro().setId(book.getId());
		@SuppressWarnings("unchecked")
		List<Prestamo> prestamos = prestamoDao.getWithPag(filterPrestamo, 0, 0);
		if (!prestamos.isEmpty()) {
			book.setPrestamo(prestamos.get(0).getUsuario());
		}
		
		// Comprobamos si el libro ha sido prestado previamente
		Leido filterLeido = new Leido();
		filterLeido.setLibro(new Libro());
		filterLeido.getLibro().setId(book.getId());
		filterLeido.setPrestado(Constants.IS_NOT_NULL);
		book.setHaTenidoPrestamos(DaoFactory.getLeidoDao().getCount(filterLeido) != 0);
		
		String username = MainController.getUsername();
		UsuarioDao usuarioDao = DaoFactory.getUsuarioDao();
		if (username != null) {
			// Cargamos los datos del usuario que ha hecho login
			usuarioDao.beginTransaction();
			Usuario user = usuarioDao.getUser(username);
			usuarioDao.commitTransaction();
			// Cargamos los datos de los libros pendientes para saber si este esta pendiente
			Pendiente filterPendiente = new Pendiente();
			filterPendiente.setLibro(new Libro());
			filterPendiente.getLibro().setId(book.getId());
			filterPendiente.setUsuario(new Usuario());
			filterPendiente.getUsuario().setId(user.getId());
			List<?> pendiente = DaoFactory.getPendienteDao().getWithPag(filterPendiente, 0, 0);
			if (!pendiente.isEmpty()) {
				book.setPendiente(((Pendiente) pendiente.get(0)).getFecha());
			}
			
			// Cargamos los datos de los libros leidos para saber si este esta leido
			filterLeido = new Leido();
			filterLeido.setLibro(new Libro());
			filterLeido.getLibro().setId(book.getId());
			filterLeido.setUsuario(new Usuario());
			filterLeido.getUsuario().setId(user.getId());
			filterLeido.setPrestado(Constants.IS_NULL);
			@SuppressWarnings("unchecked")
			List<Leido> leido = (List<Leido>) DaoFactory.getLeidoDao().getWithPag(filterLeido, 0, 0);
			leido.stream().forEach(l->l.setFechaTxt(DatesManager.int2Presentation(l.getFecha())));
			book.setLeidos(leido);
			
			// Cargamos datos de me gusta
			MeGusta filterMegusta = new MeGusta();
			filterMegusta.setLibro(new Libro());
			filterMegusta.getLibro().setId(book.getId());
			@SuppressWarnings("unchecked")
			List<MeGusta> megustas = DaoFactory.getMegustaDao().getWithPag(filterMegusta, 0, 0);
			int mg = 0, nmg = 0;
			for (MeGusta meGusta : megustas) {
				if (user.getId().equals(meGusta.getUsuario().getId())) {
					book.setMeGustaUsr(meGusta.getMegusta());
				}
				if (meGusta.getMegusta()) {
					mg++;
				} else {
					nmg++;
				}
			}
			book.setMegustas(mg);
			book.setNomegustas(nmg);
		}
		return book;
	}
	
	@Override
	public String cartBooks(Model model, Bean newData, List<Libro> list) {
		Bean elementData = null;
		String msg = "";
		// Si tenemos un indice valido
		if (!CollectionUtils.isEmpty(list)) {
			try {
				// Obtenemos todos los datos seleccionados
				modifiedElementsIds = new ArrayList<>(list.size());
				for (Libro l : list) {
					modifiedElementsIds.add(l.getId());
				}
				elementData = getNewFilter();
			} catch (Exception e) {
				elementData = getNewFilter();
				msg = manageException("cartBooks-acceptMultiupdateSelection", e);
			}
		} else {
			// Creamos un elemento vacio, para que no de fallos al intentar
			// acceder a algunos campos
			elementData = getNewFilter();
			msg = messageSource.getMessage(getSection().get() + ".menu.multiupdate.noIndexMsg", null, LocaleContextHolder.getLocale());
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.MULTIUPDATE, getSection()));
		model.addAttribute("confirmMsg", messageSource.getMessage("multiUpdateConfirmMessage", null, LocaleContextHolder.getLocale())+list.size()+" "+
				messageSource.getMessage("menu."+getSection().get()+".text", null, LocaleContextHolder.getLocale())+"?");
		model.addAttribute("elementData", elementData);
		return "commons/body";
	}
}
