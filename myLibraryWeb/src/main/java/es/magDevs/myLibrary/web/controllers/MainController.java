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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import es.magDevs.myLibrary.model.Constants.SECTION;
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
	//Cache para elementos del menu
	private List<MenuItem> menuItems = null;

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
		if(menuItems != null) {
			return menuItems;
		}
		menuItems = new ArrayList<MenuItem>();
		// Obtenemos los elementos del menu
		String[] items = messageSource.getMessage("menu.items", null, null)
				.split(" ");
		//Ordenamos 
		Arrays.sort(items, new Comparator<String>() {
			public int compare(String o1, String o2) {
				int i1 = SECTION.getOrder(o1);
				int i2 = SECTION.getOrder(o2);
				if(i1 == i2) {
					return 0;
				} else if(i1 > i2) {
					return 1;
				}
				return -1;
			}
		});
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

	/**
	 * Datos del usuario autenticado, si lo esta, <code>null</code> en caso de
	 * que no haya un usuario autenticado
	 * 
	 * @return datos del usuario
	 */
	@ModelAttribute("userData")
	Authentication getUserData() {
		 Authentication authentication =
			      SecurityContextHolder.getContext().getAuthentication();
		 if(authentication == null){
			 return null;
		 }
		 boolean auth = false;
		 for (GrantedAuthority authority : authentication.getAuthorities()) {
			String a = authority.getAuthority();
			if(a != null && (a.equals("ROLE_USER") || a.equals("ROLE_ADMIN"))) {
				auth=true;
				break;
			}
		}
		if(!auth) {
			return null;
		}
		return authentication;
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
	private LoginController loginController;
	private LoginController getLoginController() {
		if (loginController == null) {
			loginController = new LoginController(messageSource);
		}
		return loginController;
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
	@RequestMapping(value = "/typesnext")
	public String typesNext(Model model) {
		return getTypesController().listNext(model);
	}
	@RequestMapping(value = "/typesprevious")
	public String typesPrevious(Model model) {
		return getTypesController().listPrevious(model);
	}
	@RequestMapping(value = "/typesstart")
	public String typesStart(Model model) {
		return getTypesController().listStart(model);
	}
	@RequestMapping(value = "/typesend")
	public String typesEnd(Model model) {
		return getTypesController().listEnd(model);
	}
	@RequestMapping(value = "/typespageSize")
	public String typesPagesSize(@RequestParam("pageSize") String pageSize,
			Model model) {
		return getTypesController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/typessearch")
	public String typesFilter(Tipo filter, Model model) {
		return getTypesController().filter(filter, model);
	}
	@RequestMapping(value = "/typescreate")
	public String typesCreate(@RequestParam("create") Integer index, Model model) {
		return getTypesController().create(index, model);
	}
	@RequestMapping(value = "/typesdelete")
	public String typesDelete(@RequestParam("delete") Integer index, Model model) {
		return getTypesController().delete(index, model);
	}
	@RequestMapping(value = "/typesupdate")
	public String typesUpdate(@RequestParam("update") Integer index, Model model) {
		return getTypesController().update(index, model);
	}
	@RequestMapping(value = "/typesread")
	public String typesRead(@RequestParam("read") Integer index, Model model) {
		return getTypesController().read(index, model);
	}
	@RequestMapping(value = "/typesacceptCreation", params = {"acceptCreation"})
	public String typesAcceptCreation(Tipo newType, Model model) {
		return getTypesController().acceptCreation(newType, model);
	}
	@RequestMapping(value = "/typesacceptUpdate", params = {"acceptUpdate"})
	public String typesAcceptUpdate(Tipo newType, Model model) {
		return getTypesController().acceptUpdate(newType, model);
	}
	@RequestMapping(value = "/typesacceptCreation")
	public String typesCancelAcceptCreation(Tipo newType, Model model) {
		return getTypesController().list(model);
	}
	@RequestMapping(value = "/typesacceptUpdate")
	public String typesCancelAcceptUpdate(Tipo newType, Model model) {
		return getTypesController().list(model);
	}
	/* *****************************************
	 * ************ TRADUCTORES ****************
	 * *****************************************
	 */
	@RequestMapping(value = "/translators")
	public String translators(Model model) {
		return getTranslatorsController().list(model);
	}
	@RequestMapping(value = "/translatorsnext")
	public String translatorsNext(Model model) {
		return getTranslatorsController().listNext(model);
	}
	@RequestMapping(value = "/translatorsprevious")
	public String translatorsPrevious(Model model) {
		return getTranslatorsController().listPrevious(model);
	}
	@RequestMapping(value = "/translatorsstart")
	public String translatorsStart(Model model) {
		return getTranslatorsController().listStart(model);
	}
	@RequestMapping(value = "/translatorsend")
	public String translatorsEnd(Model model) {
		return getTranslatorsController().listEnd(model);
	}
	@RequestMapping(value = "/translatorspageSize")
	public String translatorsPagesSize(
			@RequestParam("pageSize") String pageSize, Model model) {
		return getTranslatorsController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/translatorssearch")
	public String translatorsFilter(Traductor filter, Model model) {
		return getTranslatorsController().filter(filter, model);
	}
	@RequestMapping(value = "/translatorscreate")
	public String translatorsCreate(@RequestParam("create") Integer index,
			Model model) {
		return getTranslatorsController().create(index, model);
	}
	@RequestMapping(value = "/translatorsdelete")
	public String translatorsDelete(@RequestParam("delete") Integer index,
			Model model) {
		return getTranslatorsController().delete(index, model);
	}
	@RequestMapping(value = "/translatorsupdate")
	public String translatorsUpdate(@RequestParam("update") Integer index, Model model) {
		return getTranslatorsController().update(index, model);
	}
	@RequestMapping(value = "/translatorsread")
	public String translatorsRead(@RequestParam("read") Integer index,
			Model model) {
		return getTranslatorsController().read(index, model);
	}
	@RequestMapping(value = "/translatorsacceptCreation", params = {"acceptCreation"})
	public String translatorsAcceptCreation(Traductor newTranslator, Model model) {
		return getTranslatorsController().acceptCreation(newTranslator, model);
	}
	@RequestMapping(value = "/translatorsacceptUpdate", params = {"acceptUpdate"})
	public String translatorsAcceptUpdate(Traductor newTranslator, Model model) {
		return getTranslatorsController().acceptUpdate(newTranslator, model);
	}
	@RequestMapping(value = "/translatorsacceptCreation")
	public String translatorsCancelAcceptCreation(Traductor newTranslator, Model model) {
		return getTranslatorsController().list(model);
	}
	@RequestMapping(value = "/translatorsacceptUpdate")
	public String translatorsCancelAcceptUpdate(Traductor newTranslator, Model model) {
		return getTranslatorsController().list(model);
	}
	@RequestMapping(value = "/translatorsgetdata", method = RequestMethod.POST)
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
	@RequestMapping(value = "/publishersnext")
	public String publishersNext(Model model) {
		return getPublishersController().listNext(model);
	}
	@RequestMapping(value = "/publishersprevious")
	public String publishersPrevious(Model model) {
		return getPublishersController().listPrevious(model);
	}
	@RequestMapping(value = "/publishersstart")
	public String publishersStart(Model model) {
		return getPublishersController().listStart(model);
	}
	@RequestMapping(value = "/publishersend")
	public String publishersEnd(Model model) {
		return getPublishersController().listEnd(model);
	}
	@RequestMapping(value = "/publisherspageSize")
	public String publishersPagesSize(
			@RequestParam("pageSize") String pageSize, Model model) {
		return getPublishersController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/publisherssearch")
	public String publishersFilter(Editorial filter, Model model) {
		return getPublishersController().filter(filter, model);
	}
	@RequestMapping(value = "/publisherscreate")
	public String publishersCreate(@RequestParam("create") Integer index,
			Model model) {
		return getPublishersController().create(index, model);
	}
	@RequestMapping(value = "/publishersdelete")
	public String publishersDelete(@RequestParam("delete") Integer index,
			Model model) {
		return getPublishersController().delete(index, model);
	}
	@RequestMapping(value = "/publishersupdate")
	public String publishersUpdate(@RequestParam("update") Integer index, Model model) {
		return getPublishersController().update(index, model);
	}
	@RequestMapping(value = "/publishersread")
	public String publishersRead(@RequestParam("read") Integer index, Model model) {
		return getPublishersController().read(index, model);
	}
	@RequestMapping(value = "/publishersacceptCreation", params = {"acceptCreation"})
	public String publishersAcceptCreation(Editorial newPublisher, Model model) {
		return getPublishersController().acceptCreation(newPublisher, model);
	}
	@RequestMapping(value = "/publishersacceptUpdate", params = {"acceptUpdate"})
	public String publishersAcceptUpdate(Editorial newPublisher, Model model) {
		return getPublishersController().acceptUpdate(newPublisher, model);
	}
	@RequestMapping(value = "/publishersacceptCreation")
	public String publishersCancelAcceptCreation(Editorial newPublisher, Model model) {
		return getPublishersController().list(model);
	}
	@RequestMapping(value = "/publishersacceptUpdate")
	public String publishersCancelAcceptUpdate(Editorial newPublisher, Model model) {
		return getPublishersController().list(model);
	}
	@RequestMapping(value = "/publishersgetdata", method = RequestMethod.POST)
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
	@RequestMapping(value = "/placesnext")
	public String placesNext(Model model) {
		return getPlacesController().listNext(model);
	}
	@RequestMapping(value = "/placesprevious")
	public String placesPrevious(Model model) {
		return getPlacesController().listPrevious(model);
	}
	@RequestMapping(value = "/placesstart")
	public String placesStart(Model model) {
		return getPlacesController().listStart(model);
	}
	@RequestMapping(value = "/placesend")
	public String placesEnd(Model model) {
		return getPlacesController().listEnd(model);
	}
	@RequestMapping(value = "/placespageSize")
	public String placesPagesSize(@RequestParam("pageSize") String pageSize,
			Model model) {
		return getPlacesController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/placessearch")
	public String placesFilter(Ubicacion filter, Model model) {
		return getPlacesController().filter(filter, model);
	}
	@RequestMapping(value = "/placescreate")
	public String placesCreate(@RequestParam("create") Integer index,
			Model model) {
		return getPlacesController().create(index, model);
	}
	@RequestMapping(value = "/placesdelete")
	public String placesDelete(@RequestParam("delete") Integer index,
			Model model) {
		return getPlacesController().delete(index, model);
	}
	@RequestMapping(value = "/placesupdate")
	public String placesUpdate(@RequestParam("update") Integer index, Model model) {
		return getPlacesController().update(index, model);
	}
	@RequestMapping(value = "/placesread")
	public String placesRead(@RequestParam("read") Integer index, Model model) {
		return getPlacesController().read(index, model);
	}
	@RequestMapping(value = "/placesacceptCreation", params = {"acceptCreation"})
	public String placesAcceptCreation(Ubicacion newPlace, Model model) {
		return getPlacesController().acceptCreation(newPlace, model);
	}
	@RequestMapping(value = "/placesacceptUpdate", params = {"acceptUpdate"})
	public String placesAcceptUpdate(Ubicacion newPlace, Model model) {
		return getPlacesController().acceptUpdate(newPlace, model);
	}
	@RequestMapping(value = "/placesacceptCreation")
	public String placesCancelAcceptCreation(Ubicacion newPlace, Model model) {
		return getPlacesController().list(model);
	}
	@RequestMapping(value = "/placesacceptUpdate")
	public String placesCancelAcceptUpdate(Ubicacion newPlace, Model model) {
		return getPlacesController().list(model);
	}
	/* *****************************************
	 * ************* COLECCIONES ***************
	 * *****************************************
	 */
	@RequestMapping(value = "/collections")
	public String collections(Model model) {
		return getCollectionsController().list(model);
	}
	@RequestMapping(value = "/collectionsnext")
	public String collectionsNext(Model model) {
		return getCollectionsController().listNext(model);
	}
	@RequestMapping(value = "/collectionsprevious")
	public String collectionsPrevious(Model model) {
		return getCollectionsController().listPrevious(model);
	}
	@RequestMapping(value = "/collectionsstart")
	public String collectionsStart(Model model) {
		return getCollectionsController().listStart(model);
	}
	@RequestMapping(value = "/collectionsend")
	public String collectionsEnd(Model model) {
		return getCollectionsController().listEnd(model);
	}
	@RequestMapping(value = "/collectionspageSize")
	public String collectionsPagesSize(
			@RequestParam("pageSize") String pageSize, Model model) {
		return getCollectionsController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/collectionssearch")
	public String collectionsFilter(Coleccion filter, Model model) {
		return getCollectionsController().filter(filter, model);
	}
	@RequestMapping(value = "/collectionscreate")
	public String collectionsCreate(@RequestParam("create") Integer index,
			Model model) {
		return getCollectionsController().create(index, model);
	}
	@RequestMapping(value = "/collectionsdelete")
	public String collectionsDelete(@RequestParam("delete") Integer index,
			Model model) {
		return getCollectionsController().delete(index, model);
	}
	@RequestMapping(value = "/collectionsupdate")
	public String collectionsUpdate(@RequestParam("update") Integer index,
			Model model) {
		return getCollectionsController().update(index, model);
	}
	@RequestMapping(value = "/collectionsread")
	public String collectionsRead(@RequestParam("read") Integer index,
			Model model) {
		return getCollectionsController().read(index, model);
	}
	@RequestMapping(value = "/collectionsacceptCreation", params = {"acceptCreation"})
	public String collectionsAcceptCreation(Coleccion newCollection, Model model) {
		return getCollectionsController().acceptCreation(newCollection, model);
	}
	@RequestMapping(value = "/collectionsacceptCreation")
	public String collectionsCancelAcceptCreation(Coleccion newCollection, Model model) {
		return getCollectionsController().list(model);
	}
	@RequestMapping(value = "/collectionsacceptUpdate", params = {"acceptUpdate"})
	public String collectionsAcceptUpdate(Coleccion newCollection, Model model) {
		return getCollectionsController().acceptUpdate(newCollection, model);
	}
	@RequestMapping(value = "/collectionsacceptUpdate")
	public String collectionsCancelAcceptUpdate(Coleccion newCollection, Model model) {
		return getCollectionsController().list(model);
	}
	@RequestMapping(value = "/collectionsgetdata", method = RequestMethod.POST)
	public @ResponseBody
	List<Coleccion> getData(@RequestParam("getdata") String hint,
			@RequestParam("publisherId") Integer publisherId) {
		return getCollectionsController().getData(hint, publisherId);
	}
	@RequestMapping(value = "/collectionsnewPublisher", method = RequestMethod.POST)
	public @ResponseBody String newCollectionPublisher(@RequestParam("json") String requestBody) {
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
	@RequestMapping(value = "/booksnext")
	public String booksNext(Model model) {
		return getBooksController().listNext(model);
	}
	@RequestMapping(value = "/booksprevious")
	public String booksPrevious(Model model) {
		return getBooksController().listPrevious(model);
	}
	@RequestMapping(value = "/booksstart")
	public String booksStart(Model model) {
		return getBooksController().listStart(model);
	}
	@RequestMapping(value = "/booksend")
	public String booksEnd(Model model) {
		return getBooksController().listEnd(model);
	}
	@RequestMapping(value = "/bookspageSize")
	public String booksPagesSize(@RequestParam("pageSize") String pageSize,
			Model model) {
		return getBooksController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/bookssearch")
	public String booksFilter(BooksFilter filter, Model model) {
		return getBooksController().filter(filter, model);
	}
	@RequestMapping(value = "/bookscreate")
	public String booksCreate(@RequestParam("create") Integer index, Model model) {
		return getBooksController().create(index, model);
	}
	@RequestMapping(value = "/booksdelete")
	public String booksDelete(@RequestParam("delete") Integer index, Model model) {
		return getBooksController().delete(index, model);
	}
	@RequestMapping(value = "/booksupdate")
	public String booksUpdate(@RequestParam("update") Integer index, Model model) {
		return getBooksController().update(index, model);
	}
	@RequestMapping(value = "/booksread")
	public String booksRead(@RequestParam("read") Integer index, Model model) {
		return getBooksController().read(index, model);
	}
	@RequestMapping(value = "/booksacceptCreation", params = {"acceptCreation"})
	public String booksAcceptCreation(Libro newBook, Model model) {
		return getBooksController().acceptCreation(newBook, model);
	}
	@RequestMapping(value = "/booksacceptCreation")
	public String booksCancelAcceptCreation(Libro newBook, Model model) {
		return getBooksController().list(model);
	}
	@RequestMapping(value = "/booksacceptUpdate", params = {"acceptUpdate"})
	public String booksAcceptUpdate(Libro newBook, Model model) {
		return getBooksController().acceptUpdate(newBook, model);
	}@RequestMapping(value = "/booksacceptUpdate")
	public String booksCancelAcceptUpdate(Libro newBook, Model model) {
		return getBooksController().list(model);
	}
	@RequestMapping(value = "/booksnewPublisher", method = RequestMethod.POST)
	public @ResponseBody
	String newPublisher(@RequestParam("json") String requestBody) {
		return getBooksController().newPublisher(requestBody);
	}
	@RequestMapping(value = "/booksnewCollection", method = RequestMethod.POST)
	public @ResponseBody
	String newCollection(@RequestParam("json") String requestBody) {
		return getBooksController().newCollection(requestBody);
	}
	@RequestMapping(value = "/booksaddAuthor", method = RequestMethod.POST)
	public @ResponseBody
	String addAuthor(@RequestParam("addAuthor") Integer authorId) {
		return getBooksController().addAuthor(authorId);
	}
	@RequestMapping(value = "/booksquitAuthor", method = RequestMethod.POST)
	public @ResponseBody
	String quitAuthor(@RequestParam("quitAuthor") String authorId) {
		return getBooksController().quitAuthor(authorId);
	}
	@RequestMapping(value = "/booksnewAuthor", method = RequestMethod.POST)
	public @ResponseBody
	String newAuthor(@RequestParam("json") String requestBody) {
		return getBooksController().newAuthor(requestBody);
	}
	@RequestMapping(value = "/booksaddTranslator", method = RequestMethod.POST)
	public @ResponseBody
	String addTranslator(@RequestParam("addTranslator") Integer translatorId) {
		return getBooksController().addTranslator(translatorId);
	}
	@RequestMapping(value = "/booksquitTranslator", method = RequestMethod.POST)
	public @ResponseBody
	String quitTranslator(@RequestParam("quitTranslator") String translatorId) {
		return getBooksController().quitTranslator(translatorId);
	}
	@RequestMapping(value = "/booksnewTranslator", method = RequestMethod.POST)
	public @ResponseBody
	String newTranslator(@RequestParam("json") String requestBody) {
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
	@RequestMapping(value = "/authorsnext")
	public String authorsNext(Model model) {
		return getAuthorsController().listNext(model);
	}
	@RequestMapping(value = "/authorsprevious")
	public String authorsPrevious(Model model) {
		return getAuthorsController().listPrevious(model);
	}
	@RequestMapping(value = "/authorsstart")
	public String authorsStart(Model model) {
		return getAuthorsController().listStart(model);
	}
	@RequestMapping(value = "/authorsend")
	public String authorsEnd(Model model) {
		return getAuthorsController().listEnd(model);
	}
	@RequestMapping(value = "/authorspageSize")
	public String authorsPagesSize(@RequestParam("pageSize") String pageSize,
			Model model) {
		return getAuthorsController().listPageSize(pageSize, model);
	}
	@RequestMapping(value = "/authorssearch")
	public String authorsFilter(Autor filter, Model model) {
		return getAuthorsController().filter(filter, model);
	}
	@RequestMapping(value = "/authorscreate")
	public String authorsCreate(@RequestParam("create") Integer index,
			Model model) {
		return getAuthorsController().create(index, model);
	}
	@RequestMapping(value = "/authorsdelete")
	public String authorsDelete(@RequestParam("delete") Integer index,
			Model model) {
		return getAuthorsController().delete(index, model);
	}
	@RequestMapping(value = "/authorsupdate")
	public String authorsUpdate(@RequestParam("update") Integer index, Model model) {
		return getAuthorsController().update(index, model);
	}
	@RequestMapping(value = "/authorsread")
	public String authorsRead(@RequestParam("read") Integer index, Model model) {
		return getAuthorsController().read(index, model);
	}
	@RequestMapping(value = "/authorsacceptCreation", params = {"acceptCreation"})
	public String authorsAcceptCreation(Autor newAuthor, Model model) {
		return getAuthorsController().acceptCreation(newAuthor, model);
	}
	@RequestMapping(value = "/authorsacceptCreation")
	public String authorsCancelAcceptCreation(Autor newAuthor, Model model) {
		return getAuthorsController().list(model);
	}
	@RequestMapping(value = "/authorsacceptUpdate", params = {"acceptUpdate"})
	public String authorsAcceptUpdate(Autor newAuthor, Model model) {
		return getAuthorsController().acceptUpdate(newAuthor, model);
	}
	@RequestMapping(value = "/authorsacceptUpdate")
	public String authorsCancelAcceptUpdate(Autor newAuthor, Model model) {
		return getAuthorsController().list(model);
	}
	@RequestMapping(value = "/authorsgetdata", method = RequestMethod.POST)
	public @ResponseBody
	List<Autor> getData(@RequestParam("getdata") String hint) {
		return getAuthorsController().getData(hint);
	}
	/* *****************************************
	 * **************** LOGIN ******************
	 * *****************************************
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, @RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout) {
		return getLoginController().login(model, error, logout, true);
	}
	@RequestMapping(value = "/loginForm", method = RequestMethod.GET)
	public String loginForm(Model model, @RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout) {
		return getLoginController().login(model, error, logout, false);
	}
}
