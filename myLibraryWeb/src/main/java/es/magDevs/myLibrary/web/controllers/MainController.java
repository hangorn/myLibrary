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
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
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
import es.magDevs.myLibrary.model.dao.TipoDao;
import es.magDevs.myLibrary.model.dao.UbicacionDao;
import es.magDevs.myLibrary.web.gui.beans.MenuItem;
import es.magDevs.myLibrary.web.gui.beans.filters.BooksFilter;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;

@SuppressWarnings("unchecked")
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class MainController implements InitializingBean {
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
	private static final Logger log = Logger.getLogger(MainController.class);

	/* *****************************************
	 * ********** DATOS DEL MODELO *************
	 * *****************************************
	 */

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
					null, LocaleContextHolder.getLocale()));
			item.setImg(messageSource.getMessage("menu." + items[i] + ".img",
					null, null));
			item.setLink(messageSource.getMessage("menu." + items[i] + ".link",
					null, null));
			menuItems.add(item);
		}
		return menuItems;
	}

	/**
	 * Lista con los datos de los elementos del menu
	 * 
	 * @return
	 */
	@ModelAttribute("languages")
	List<MenuItem> getLanguages() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		// Obtenemos los elementos del menu
		String[] items = messageSource.getMessage("languages", null, null)
				.split(" ");
		for (int i = 0; i < items.length; i++) {
			MenuItem item = new MenuItem();
			item.setText(messageSource.getMessage("languages." + items[i] + ".text",
					null, null));
			item.setImg(messageSource.getMessage("languages." + items[i] + ".img",
					null, null));
			item.setLink("language="+items[i]);
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
	@ModelAttribute("dataPlaces")
	List<Ubicacion> getDataPlaces() {
		try {
			// Obtenemos los datos de las ubicaciones
			UbicacionDao dao = DaoFactory.getUbicacionDao();
			return dao.getAll();
		} catch (Exception e) {
			log.error("Error en el controlador principal"
					+ " al cargar las UBICACIONES", e);
			return new ArrayList<Ubicacion>();
		}
	}

	/**
	 * Lista con los datos de los tipos disponibles
	 * 
	 * @return
	 */
	@ModelAttribute("dataTypes")
	List<Tipo> getDataTypes() {
		try {
			// Obtenemos los datos de los tipos
			TipoDao dao = DaoFactory.getTipoDao();
			return dao.getAll();
		} catch (Exception e) {
			log.error("Error en el controlador principal"
					+ " al cargar los TIPOS", e);
			return new ArrayList<Tipo>();
		}
	}

	/* *****************************************
	 * ************ CONTROLADORES **************
	 * *****************************************
	 */
	private TypesController typesController;
	private TypesController getTypesController() {
		if (typesController == null) {
			typesController = new TypesController(messageSource);
		}
		return typesController;
	}
	private TranslatorsController translatorsController;
	private TranslatorsController getTranslatorsController() {
		if(translatorsController == null) {
			translatorsController = new TranslatorsController(messageSource);
		}
		return translatorsController;
	}
	private PublishersController publishersController;
	private PublishersController getPublishersController() {
		if(publishersController == null) {
			publishersController = new PublishersController(messageSource);
		}
		return publishersController;
	}
	private PlacesController placesController;
	private PlacesController getPlacesController() {
		if(placesController == null) {
			placesController = new PlacesController(messageSource);
		}
		return placesController;
	}
	private CollectionsController collectionsController;
	private CollectionsController getCollectionsController() {
		if (collectionsController == null) {
			collectionsController = new CollectionsController(messageSource);
		}
		return collectionsController;
	}
	private BooksController booksController;
	private BooksController getBooksController() {
		if(booksController == null) {
			booksController = new BooksController(messageSource);
		}
		return booksController;
	}
	private AuthorsController authorsController;
	private AuthorsController getAuthorsController() {
		if (authorsController == null) {
			authorsController = new AuthorsController(messageSource);
		}
		return authorsController;
	}

	/* *****************************************
	 * ******* GESTION DE PETICIONES ***********
	 * *****************************************
	 */

	@RequestMapping("/")
	public String main(Model model) {
		model.addAllAttributes(FragmentManager.getEmptyBody(""));
		return "commons/body";
	}
	
	/* *****************************************
	 * **************** TIPOS ******************
	 * *****************************************
	 */
	@RequestMapping(value = "/types")
	public String types(Model model) {
		return getTypesController().list(model);
	}
	@RequestMapping(value = "/types", params = { "next" })
	public String typesNext(Model model) {
		return getTypesController().listNext(model);
	}
	@RequestMapping(value = "/types", params = { "previous" })
	public String typesPrevious(Model model) {
		return getTypesController().listPrevious(model);
	}
	@RequestMapping(value = "/types", params = { "start" })
	public String typesStart(Model model) {
		return getTypesController().listStart(model);
	}
	@RequestMapping(value = "/types", params = { "end" })
	public String typesEnd(Model model) {
		return getTypesController().listEnd(model);
	}
	@RequestMapping(value = "/types", params = { "pageSize" })
	public String typesPagesSize(@RequestParam("pageSize") String pageSize,
			Model model) {
		return getTypesController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/types", params = { "search" })
	public String typesFilter(Tipo filter, Model model) {
		return getTypesController().filter(filter, model);
	}
	@RequestMapping(value = "/types", params = { "create" })
	public String typesCreate(@RequestParam("create") Integer index, Model model) {
		return getTypesController().create(index, model);
	}
	@RequestMapping(value = "/types", params = { "delete" })
	public String typesDelete(@RequestParam("delete") Integer index, Model model) {
		return getTypesController().delete(index, model);
	}
	@RequestMapping(value = "/types", params = { "update" })
	public String typesUpdate(@RequestParam("update") Integer index, Model model) {
		return getTypesController().update(index, model);
	}
	@RequestMapping(value = "/types", params = { "read" })
	public String typesRead(@RequestParam("read") Integer index, Model model) {
		return getTypesController().read(index, model);
	}
	@RequestMapping(value = "/types", params = { "acceptCreation" })
	public String typesAcceptCreation(Tipo newType, Model model) {
		return getTypesController().acceptCreation(newType, model);
	}
	@RequestMapping(value = "/types", params = { "acceptUpdate" })
	public String typesAcceptUpdate(Tipo newType, Model model) {
		return getTypesController().acceptUpdate(newType, model);
	}
	/* *****************************************
	 * **************** TIPOS ******************
	 * *****************************************
	 */
	@RequestMapping(value = "/translators")
	public String translators(Model model) {
		return getTranslatorsController().list(model);
	}
	@RequestMapping(value = "/translators", params = { "next" })
	public String translatorsNext(Model model) {
		return getTranslatorsController().listNext(model);
	}
	@RequestMapping(value = "/translators", params = { "previous" })
	public String translatorsPrevious(Model model) {
		return getTranslatorsController().listPrevious(model);
	}
	@RequestMapping(value = "/translators", params = { "start" })
	public String translatorsStart(Model model) {
		return getTranslatorsController().listStart(model);
	}
	@RequestMapping(value = "/translators", params = { "end" })
	public String translatorsEnd(Model model) {
		return getTranslatorsController().listEnd(model);
	}
	@RequestMapping(value = "/translators", params = { "pageSize" })
	public String translatorsPagesSize(
			@RequestParam("pageSize") String pageSize, Model model) {
		return getTranslatorsController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/translators", params = { "search" })
	public String translatorsFilter(Traductor filter, Model model) {
		return getTranslatorsController().filter(filter, model);
	}
	@RequestMapping(value = "/translators", params = { "create" })
	public String translatorsCreate(@RequestParam("create") Integer index,
			Model model) {
		return getTranslatorsController().create(index, model);
	}
	@RequestMapping(value = "/translators", params = { "delete" })
	public String translatorsDelete(@RequestParam("delete") Integer index,
			Model model) {
		return getTranslatorsController().delete(index, model);
	}
	@RequestMapping(value = "/translators", params = { "update" })
	public String translatorsUpdate(@RequestParam("update") Integer index, Model model) {
		return getTranslatorsController().update(index, model);
	}
	@RequestMapping(value = "/translators", params = { "read" })
	public String translatorsRead(@RequestParam("read") Integer index,
			Model model) {
		return getTranslatorsController().read(index, model);
	}
	@RequestMapping(value = "/translators", params = { "acceptCreation" })
	public String translatorsAcceptCreation(Traductor newTranslator, Model model) {
		return getTranslatorsController().acceptCreation(newTranslator, model);
	}
	@RequestMapping(value = "/translators", params = { "acceptUpdate" })
	public String translatorsAcceptUpdate(Traductor newTranslator, Model model) {
		return getTranslatorsController().acceptUpdate(newTranslator, model);
	}
	@RequestMapping(value = "/translators", params = { "getdata" }, method = RequestMethod.POST)
	public @ResponseBody
	List<Traductor> getTranslatorsData(@RequestParam("getdata") String hint) {
		return getTranslatorsController().getData(hint);
	}
	/* *****************************************
	 * ************* EDITORIALES ***************
	 * *****************************************
	 */
	@RequestMapping(value = "/publishers")
	public String publishers(Model model) {
		return getPublishersController().list(model);
	}
	@RequestMapping(value = "/publishers", params = { "next" })
	public String publishersNext(Model model) {
		return getPublishersController().listNext(model);
	}
	@RequestMapping(value = "/publishers", params = { "previous" })
	public String publishersPrevious(Model model) {
		return getPublishersController().listPrevious(model);
	}
	@RequestMapping(value = "/publishers", params = { "start" })
	public String publishersStart(Model model) {
		return getPublishersController().listStart(model);
	}
	@RequestMapping(value = "/publishers", params = { "end" })
	public String publishersEnd(Model model) {
		return getPublishersController().listEnd(model);
	}
	@RequestMapping(value = "/publishers", params = { "pageSize" })
	public String publishersPagesSize(
			@RequestParam("pageSize") String pageSize, Model model) {
		return getPublishersController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/publishers", params = { "search" })
	public String publishersFilter(Editorial filter, Model model) {
		return getPublishersController().filter(filter, model);
	}
	@RequestMapping(value = "/publishers", params = { "create" })
	public String publishersCreate(@RequestParam("create") Integer index,
			Model model) {
		return getPublishersController().create(index, model);
	}
	@RequestMapping(value = "/publishers", params = { "delete" })
	public String publishersDelete(@RequestParam("delete") Integer index,
			Model model) {
		return getPublishersController().delete(index, model);
	}
	@RequestMapping(value = "/publishers", params = { "update" })
	public String publishersUpdate(@RequestParam("update") Integer index, Model model) {
		return getPublishersController().update(index, model);
	}
	@RequestMapping(value = "/publishers", params = { "read" })
	public String publishersRead(@RequestParam("read") Integer index, Model model) {
		return getPublishersController().read(index, model);
	}
	@RequestMapping(value = "/publishers", params = { "acceptCreation" })
	public String publishersAcceptCreation(Editorial newPublisher, Model model) {
		return getPublishersController().acceptCreation(newPublisher, model);
	}
	@RequestMapping(value = "/publishers", params = { "acceptUpdate" })
	public String publishersAcceptUpdate(Editorial newPublisher, Model model) {
		return getPublishersController().acceptUpdate(newPublisher, model);
	}
	@RequestMapping(value = "/publishers", params = { "getdata" }, method = RequestMethod.POST)
	public @ResponseBody
	List<Editorial> getPublishersData(@RequestParam("getdata") String hint) {
		return getPublishersController().getData(hint);
	}
	/* *****************************************
	 * ************ UBICACIONES ****************
	 * *****************************************
	 */
	@RequestMapping(value = "/places")
	public String places(Model model) {
		return getPlacesController().list(model);
	}
	@RequestMapping(value = "/places", params = { "next" })
	public String placesNext(Model model) {
		return getPlacesController().listNext(model);
	}
	@RequestMapping(value = "/places", params = { "previous" })
	public String placesPrevious(Model model) {
		return getPlacesController().listPrevious(model);
	}
	@RequestMapping(value = "/places", params = { "start" })
	public String placesStart(Model model) {
		return getPlacesController().listStart(model);
	}
	@RequestMapping(value = "/places", params = { "end" })
	public String placesEnd(Model model) {
		return getPlacesController().listEnd(model);
	}
	@RequestMapping(value = "/places", params = { "pageSize" })
	public String placesPagesSize(@RequestParam("pageSize") String pageSize,
			Model model) {
		return getPlacesController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/places", params = { "search" })
	public String placesFilter(Ubicacion filter, Model model) {
		return getPlacesController().filter(filter, model);
	}
	@RequestMapping(value = "/places", params = { "create" })
	public String placesCreate(@RequestParam("create") Integer index,
			Model model) {
		return getPlacesController().create(index, model);
	}
	@RequestMapping(value = "/places", params = { "delete" })
	public String placesDelete(@RequestParam("delete") Integer index,
			Model model) {
		return getPlacesController().delete(index, model);
	}
	@RequestMapping(value = "/places", params = { "update" })
	public String placesUpdate(@RequestParam("update") Integer index, Model model) {
		return getPlacesController().update(index, model);
	}
	@RequestMapping(value = "/places", params = { "read" })
	public String placesRead(@RequestParam("read") Integer index, Model model) {
		return getPlacesController().read(index, model);
	}
	@RequestMapping(value = "/places", params = { "acceptCreation" })
	public String placesAcceptCreation(Ubicacion newPlace, Model model) {
		return getPlacesController().acceptCreation(newPlace, model);
	}
	@RequestMapping(value = "/places", params = { "acceptUpdate" })
	public String placesAcceptUpdate(Ubicacion newPlace, Model model) {
		return getPlacesController().acceptUpdate(newPlace, model);
	}
	/* *****************************************
	 * ************* COLECCIONES ***************
	 * *****************************************
	 */
	@RequestMapping(value = "/collections")
	public String collections(Model model) {
		return getCollectionsController().list(model);
	}
	@RequestMapping(value = "/collections", params = { "next" })
	public String collectionsNext(Model model) {
		return getCollectionsController().listNext(model);
	}
	@RequestMapping(value = "/collections", params = { "previous" })
	public String collectionsPrevious(Model model) {
		return getCollectionsController().listPrevious(model);
	}
	@RequestMapping(value = "/collections", params = { "start" })
	public String collectionsStart(Model model) {
		return getCollectionsController().listStart(model);
	}
	@RequestMapping(value = "/collections", params = { "end" })
	public String collectionsEnd(Model model) {
		return getCollectionsController().listEnd(model);
	}
	@RequestMapping(value = "/collections", params = { "pageSize" })
	public String collectionsPagesSize(
			@RequestParam("pageSize") String pageSize, Model model) {
		return getCollectionsController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/collections", params = { "search" })
	public String collectionsFilter(Coleccion filter, Model model) {
		return getCollectionsController().filter(filter, model);
	}
	@RequestMapping(value = "/collections", params = { "create" })
	public String collectionsCreate(@RequestParam("create") Integer index,
			Model model) {
		return getCollectionsController().create(index, model);
	}
	@RequestMapping(value = "/collections", params = { "delete" })
	public String collectionsDelete(@RequestParam("delete") Integer index,
			Model model) {
		return getCollectionsController().delete(index, model);
	}
	@RequestMapping(value = "/collections", params = { "update" })
	public String collectionsUpdate(@RequestParam("update") Integer index,
			Model model) {
		return getCollectionsController().update(index, model);
	}
	@RequestMapping(value = "/collections", params = { "read" })
	public String collectionsRead(@RequestParam("read") Integer index,
			Model model) {
		return getCollectionsController().read(index, model);
	}
	@RequestMapping(value = "/collections", params = { "acceptCreation" })
	public String collectionsAcceptCreation(Coleccion newCollection, Model model) {
		return getCollectionsController().acceptCreation(newCollection, model);
	}
	@RequestMapping(value = "/collections", params = { "acceptUpdate" })
	public String collectionsAcceptUpdate(Coleccion newCollection, Model model) {
		return getCollectionsController().acceptUpdate(newCollection, model);
	}
	@RequestMapping(value = "/collections", params = { "getdata" }, method = RequestMethod.POST)
	public @ResponseBody
	List<Coleccion> getData(@RequestParam("getdata") String hint,
			@RequestParam("publisherId") Integer publisherId) {
		return getCollectionsController().getData(hint, publisherId);
	}
	@RequestMapping(value = "/collections/newPublisher", method = RequestMethod.POST)
	public @ResponseBody String newCollectionPublisher(@RequestBody String requestBody) {
		return getCollectionsController().newPublisher(requestBody);
	}
	/* *****************************************
	 * *************** LIBROS ******************
	 * *****************************************
	 */
	@RequestMapping(value = "/books")
	public String books(Model model) {
		return getBooksController().list(model);
	}
	@RequestMapping(value = "/books", params = { "next" })
	public String booksNext(Model model) {
		return getBooksController().listNext(model);
	}
	@RequestMapping(value = "/books", params = { "previous" })
	public String booksPrevious(Model model) {
		return getBooksController().listPrevious(model);
	}
	@RequestMapping(value = "/books", params = { "start" })
	public String booksStart(Model model) {
		return getBooksController().listStart(model);
	}
	@RequestMapping(value = "/books", params = { "end" })
	public String booksEnd(Model model) {
		return getBooksController().listEnd(model);
	}
	@RequestMapping(value = "/books", params = { "pageSize" })
	public String booksPagesSize(@RequestParam("pageSize") String pageSize,
			Model model) {
		return getBooksController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/books", params = { "search" })
	public String booksFilter(BooksFilter filter, Model model) {
		return getBooksController().filter(filter, model);
	}
	@RequestMapping(value = "/books", params = { "create" })
	public String booksCreate(@RequestParam("create") Integer index, Model model) {
		return getBooksController().create(index, model);
	}
	@RequestMapping(value = "/books", params = { "delete" })
	public String booksDelete(@RequestParam("delete") Integer index, Model model) {
		return getBooksController().delete(index, model);
	}
	@RequestMapping(value = "/books", params = { "update" })
	public String booksUpdate(@RequestParam("update") Integer index, Model model) {
		return getBooksController().update(index, model);
	}
	@RequestMapping(value = "/books", params = { "read" })
	public String booksRead(@RequestParam("read") Integer index, Model model) {
		return getBooksController().read(index, model);
	}
	@RequestMapping(value = "/books", params = { "acceptCreation" })
	public String booksAcceptCreation(Libro newBook, Model model) {
		return getBooksController().acceptCreation(newBook, model);
	}
	@RequestMapping(value = "/books", params = { "acceptUpdate" })
	public String booksAcceptUpdate(Libro newBook, Model model) {
		return getBooksController().acceptUpdate(newBook, model);
	}
	@RequestMapping(value = "/books/newPublisher", method = RequestMethod.POST)
	public @ResponseBody
	String newPublisher(@RequestBody String requestBody) {
		return getBooksController().newPublisher(requestBody);
	}
	@RequestMapping(value = "/books/newCollection", method = RequestMethod.POST)
	public @ResponseBody
	String newCollection(@RequestBody String requestBody) {
		return getBooksController().newCollection(requestBody);
	}
	@RequestMapping(value = "/books", params = { "addAuthor" }, method = RequestMethod.POST)
	public @ResponseBody
	String addAuthor(@RequestParam("addAuthor") Integer authorId) {
		return getBooksController().addAuthor(authorId);
	}
	@RequestMapping(value = "/books", params = { "quitAuthor" }, method = RequestMethod.POST)
	public @ResponseBody
	String quitAuthor(@RequestParam("quitAuthor") String authorId) {
		return getBooksController().quitAuthor(authorId);
	}
	@RequestMapping(value = "/books/newAuthor", method = RequestMethod.POST)
	public @ResponseBody
	String newAuthor(@RequestBody String requestBody) {
		return getBooksController().newAuthor(requestBody);
	}
	@RequestMapping(value = "/books", params = { "addTranslator" }, method = RequestMethod.POST)
	public @ResponseBody
	String addTranslator(@RequestParam("addTranslator") Integer translatorId) {
		return getBooksController().addTranslator(translatorId);
	}
	@RequestMapping(value = "/books", params = { "quitTranslator" }, method = RequestMethod.POST)
	public @ResponseBody
	String quitTranslator(@RequestParam("quitTranslator") String translatorId) {
		return getBooksController().quitTranslator(translatorId);
	}
	@RequestMapping(value = "/books/newTranslator", method = RequestMethod.POST)
	public @ResponseBody
	String newTranslator(@RequestBody String requestBody) {
		return getBooksController().newTranslator(requestBody);
	}
	/* *****************************************
	 * *************** AUTORES *****************
	 * *****************************************
	 */
	@RequestMapping(value = "/authors")
	public String authors(Model model) {
		return getAuthorsController().list(model);
	}
	@RequestMapping(value = "/authors", params = { "next" })
	public String authorsNext(Model model) {
		return getAuthorsController().listNext(model);
	}
	@RequestMapping(value = "/authors", params = { "previous" })
	public String authorsPrevious(Model model) {
		return getAuthorsController().listPrevious(model);
	}
	@RequestMapping(value = "/authors", params = { "start" })
	public String authorsStart(Model model) {
		return getAuthorsController().listStart(model);
	}
	@RequestMapping(value = "/authors", params = { "end" })
	public String authorsEnd(Model model) {
		return getAuthorsController().listEnd(model);
	}
	@RequestMapping(value = "/authors", params = { "pageSize" })
	public String authorsPagesSize(@RequestParam("pageSize") String pageSize,
			Model model) {
		return getAuthorsController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/authors", params = { "search" })
	public String authorsFilter(Autor filter, Model model) {
		return getAuthorsController().filter(filter, model);
	}
	@RequestMapping(value = "/authors", params = { "create" })
	public String authorsCreate(@RequestParam("create") Integer index,
			Model model) {
		return getAuthorsController().create(index, model);
	}
	@RequestMapping(value = "/authors", params = { "delete" })
	public String authorsDelete(@RequestParam("delete") Integer index,
			Model model) {
		return getAuthorsController().delete(index, model);
	}
	@RequestMapping(value = "/authors", params = { "update" })
	public String authorsUpdate(@RequestParam("update") Integer index, Model model) {
		return getAuthorsController().update(index, model);
	}
	@RequestMapping(value = "/authors", params = { "read" })
	public String authorsRead(@RequestParam("read") Integer index, Model model) {
		return getAuthorsController().read(index, model);
	}
	@RequestMapping(value = "/authors", params = { "acceptCreation" })
	public String authorsAcceptCreation(Autor newAuthor, Model model) {
		return getAuthorsController().acceptCreation(newAuthor, model);
	}
	@RequestMapping(value = "/authors", params = { "acceptUpdate" })
	public String authorsAcceptUpdate(Autor newAuthor, Model model) {
		return getAuthorsController().acceptUpdate(newAuthor, model);
	}
	@RequestMapping(value = "/authors", params = { "getdata" }, method = RequestMethod.POST)
	public @ResponseBody
	List<Autor> getData(@RequestParam("getdata") String hint) {
		return getAuthorsController().getData(hint);
	}
}
