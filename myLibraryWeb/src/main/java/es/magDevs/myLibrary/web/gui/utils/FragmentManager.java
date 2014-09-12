package es.magDevs.myLibrary.web.gui.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlara la composicion de los framentos en las plantillas. Para ello
 * fijara variables de modelo de Spring a los nombres de los fragmentos
 * correspondientes
 * 
 * @author javi
 * 
 */
public class FragmentManager {
	/**
	 * Cuerpo vacio. Fijara el menu y el pie unicamente
	 * 
	 * @return
	 */
	public static Map<String, Object> getEmptyBody(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("mainTemplate", "commons/empty");
		fragmentMapper.put("mainFragment", "empty");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con la lista de los libros, ademas del menu y el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getBooksList(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 0);
		fragmentMapper.put("mainTemplate", "books/booksList");
		fragmentMapper.put("mainFragment", "booksList");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la creacion de libros, ademas del menu y el
	 * pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getBooksCreateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 0);
		fragmentMapper.put("mainTemplate", "books/booksCreate");
		fragmentMapper.put("mainFragment", "booksCreate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la modificacion de libros, ademas del menu
	 * y el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getBooksUpdateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 0);
		fragmentMapper.put("mainTemplate", "books/booksUpdate");
		fragmentMapper.put("mainFragment", "booksUpdate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la consulta de libros, ademas del menu y el
	 * pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getBooksReadForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 0);
		fragmentMapper.put("mainTemplate", "books/booksRead");
		fragmentMapper.put("mainFragment", "booksRead");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con la lista de los autores, ademas del menu y el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getAuthorsList(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 1);
		fragmentMapper.put("mainTemplate", "authors/authorsList");
		fragmentMapper.put("mainFragment", "authorsList");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la creacion de autores, ademas del menu y
	 * el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getAuthorsCreateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 1);
		fragmentMapper.put("mainTemplate", "authors/authorsCreate");
		fragmentMapper.put("mainFragment", "authorsCreate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la modificacion de autores, ademas del menu
	 * y el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getAuthorsUpdateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 1);
		fragmentMapper.put("mainTemplate", "authors/authorsUpdate");
		fragmentMapper.put("mainFragment", "authorsUpdate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la consulta de autores, ademas del menu y el
	 * pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getAuthorsReadForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 1);
		fragmentMapper.put("mainTemplate", "authors/authorsRead");
		fragmentMapper.put("mainFragment", "authorsRead");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con la lista de las editoriales, ademas del menu y el pie de
	 * pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getPublishersList(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 2);
		fragmentMapper.put("mainTemplate", "publishers/publishersList");
		fragmentMapper.put("mainFragment", "publishersList");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la creacion de editoriales, ademas del menu y
	 * el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getPublishersCreateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 2);
		fragmentMapper.put("mainTemplate", "publishers/publishersCreate");
		fragmentMapper.put("mainFragment", "publishersCreate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la modificacion de editoriales, ademas del menu
	 * y el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getPublishersUpdateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 2);
		fragmentMapper.put("mainTemplate", "publishers/publishersUpdate");
		fragmentMapper.put("mainFragment", "publishersUpdate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la consulta de editoriales, ademas del menu y el
	 * pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getPublishersReadForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 2);
		fragmentMapper.put("mainTemplate", "publishers/publishersRead");
		fragmentMapper.put("mainFragment", "publishersRead");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con la lista de las colecciones, ademas del menu y el pie de
	 * pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getCollectionsList(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 3);
		fragmentMapper.put("mainTemplate", "collections/collectionsList");
		fragmentMapper.put("mainFragment", "collectionsList");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la creacion de colecciones, ademas del menu y
	 * el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getCollectionsCreateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 3);
		fragmentMapper.put("mainTemplate", "collections/collectionsCreate");
		fragmentMapper.put("mainFragment", "collectionsCreate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la modificacion de colecciones, ademas del menu
	 * y el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getCollectionsUpdateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 3);
		fragmentMapper.put("mainTemplate", "collections/collectionsUpdate");
		fragmentMapper.put("mainFragment", "collectionsUpdate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la consulta de colecciones, ademas del menu y el
	 * pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getCollectionsReadForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 3);
		fragmentMapper.put("mainTemplate", "collections/collectionsRead");
		fragmentMapper.put("mainFragment", "collectionsRead");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con la lista de los tipos, ademas del menu y el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getTypesList(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 4);
		fragmentMapper.put("mainTemplate", "types/typesList");
		fragmentMapper.put("mainFragment", "typesList");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la creacion de tipos, ademas del menu y
	 * el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getTypesCreateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 4);
		fragmentMapper.put("mainTemplate", "types/typesCreate");
		fragmentMapper.put("mainFragment", "typesCreate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la modificacion de tipos, ademas del menu
	 * y el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getTypesUpdateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 4);
		fragmentMapper.put("mainTemplate", "types/typesUpdate");
		fragmentMapper.put("mainFragment", "typesUpdate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la consulta de tipos, ademas del menu y el
	 * pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getTypesReadForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 4);
		fragmentMapper.put("mainTemplate", "types/typesRead");
		fragmentMapper.put("mainFragment", "typesRead");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con la lista de las ubicaciones, ademas del menu y el pie de
	 * pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getPlacesList(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 5);
		fragmentMapper.put("mainTemplate", "places/placesList");
		fragmentMapper.put("mainFragment", "placesList");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la creacion de ubicaciones, ademas del menu y
	 * el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getPlacesCreateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 5);
		fragmentMapper.put("mainTemplate", "places/placesCreate");
		fragmentMapper.put("mainFragment", "placesCreate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la modificacion de ubicaciones, ademas del menu
	 * y el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getPlacesUpdateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 5);
		fragmentMapper.put("mainTemplate", "places/placesUpdate");
		fragmentMapper.put("mainFragment", "placesUpdate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la consulta de ubicaciones, ademas del menu y el
	 * pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getPlacesReadForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 5);
		fragmentMapper.put("mainTemplate", "places/placesRead");
		fragmentMapper.put("mainFragment", "placesRead");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con la lista de los traductores, ademas del menu y el pie de
	 * pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getTranslatorsList(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 6);
		fragmentMapper.put("mainTemplate", "translators/translatorsList");
		fragmentMapper.put("mainFragment", "translatorsList");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la creacion de traductores, ademas del menu y
	 * el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getTranslatorsCreateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 6);
		fragmentMapper.put("mainTemplate", "translators/translatorsCreate");
		fragmentMapper.put("mainFragment", "translatorsCreate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la modificacion de traductores, ademas del menu
	 * y el pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getTranslatorsUpdateForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 6);
		fragmentMapper.put("mainTemplate", "translators/translatorsUpdate");
		fragmentMapper.put("mainFragment", "translatorsUpdate");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo con el formulario para la consulta de traductores, ademas del menu y el
	 * pie de pagina
	 * 
	 * @return
	 */
	public static Map<String, Object> getTranslatorsReadForm(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("scriptMessage", msg);
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 6);
		fragmentMapper.put("mainTemplate", "translators/translatorsRead");
		fragmentMapper.put("mainFragment", "translatorsRead");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}
}
