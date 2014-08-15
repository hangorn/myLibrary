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
	public static Map<String, String> getEmptyBody() {
		Map<String, String> fragmentMapper = new HashMap<String, String>();
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
	public static Map<String, Object> getBooksList() {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		fragmentMapper.put("menuSelected", 0);
		fragmentMapper.put("mainTemplate", "books/booksList");
		fragmentMapper.put("mainFragment", "booksList");
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}
}
