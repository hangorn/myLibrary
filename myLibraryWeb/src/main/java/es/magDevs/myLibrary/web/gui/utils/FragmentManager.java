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
package es.magDevs.myLibrary.web.gui.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import es.magDevs.myLibrary.model.Constants.ACTION;
import es.magDevs.myLibrary.model.Constants.SECTION;

/**
 * Controlara la composicion de los framentos en las plantillas. Para ello
 * fijara variables de modelo de Spring a los nombres de los fragmentos
 * correspondientes
 * 
 * @author javier.vaquero
 * 
 */
public class FragmentManager {
	/**
	 * Obtiene mapeo con los elementos comunes a todas las posibilidades: menu,
	 * pie de pagina, y mensaje
	 * 
	 * @param msg
	 * @return
	 */
	private static Map<String, Object> getCommonElements(String msg) {
		Map<String, Object> fragmentMapper = new HashMap<String, Object>();
		// Mensaje
		fragmentMapper.put("scriptMessage", msg);
		// Menu
		fragmentMapper.put("menuTemplate", "commons/menu");
		fragmentMapper.put("menuFragment", "menu");
		// Pie
		fragmentMapper.put("footerTemplate", "commons/footer");
		fragmentMapper.put("footerFragment", "footer");
		return fragmentMapper;
	}

	/**
	 * Cuerpo vacio. Fijara el menu y el pie unicamente
	 * 
	 * @return
	 */
	public static Map<String, Object> getEmptyBody(String msg) {
		Map<String, Object> fragmentMapper = getCommonElements(msg);
		// Fragmento vacio
		fragmentMapper.put("mainTemplate", "commons/empty");
		fragmentMapper.put("mainFragment", "empty");
		return fragmentMapper;
	}

	/**
	 * Cuerpo vacio. Fijara el menu y el pie unicamente
	 * 
	 * @return
	 */
	public static Map<String, Object> getErrorBody() {
		Map<String, Object> fragmentMapper = getCommonElements(null);
		// Fragmento vacio
		fragmentMapper.put("mainTemplate", "commons/error");
		fragmentMapper.put("mainFragment", "error");
		return fragmentMapper;
	}

	/**
	 * Obtiene el mapeo con los elementos comunes (menu, pie de pagina y
	 * mensaje), para la operacion (Listar, Crear, Modificar o Consultar) y la
	 * seccion indicada (Libros, Autores, Colecciones, ...)
	 * 
	 * @param msg
	 *            mensaje que se mostrara al cargar la pagina si no esta vacio
	 * @param action
	 *            accion para la que se cargara la pagina ({@link ACTION#CREATE}
	 *            , {@link ACTION#LIST}, {@link ACTION#READ} o
	 *            {@link ACTION#READ})
	 * @param section
	 *            seccion para la que se cargara la pagina (
	 *            {@link SECTION#BOOKS}, {@link SECTION#AUTHORS},
	 *            {@link SECTION#COLLECTIONS}, ...)
	 * @return
	 */
	public static Map<String, Object> get(String msg, ACTION action,
			SECTION section) {
		Map<String, Object> fragmentMapper = getCommonElements(msg);
		
		fragmentMapper.put("menuSelected", section.getOrder());

		fragmentMapper.put("mainTemplate", "/commons/"+action.get());
		fragmentMapper.put("mainFragment", action.get());
		fragmentMapper.put("section", section.get());
		fragmentMapper.put("action", action.get());
		fragmentMapper.put("sectionsNoCrud", new HashSet<>(Arrays.asList(SECTION.LENDS.get())));
		
		return fragmentMapper;
	}
	
	/**
	 * Obtiene el mapeo con los elementos comunes (menu, pie de pagina y
	 * mensaje) y con la plantilla de login
	 * 
	 * @param msg
	 *            mensaje que se mostrara al cargar la pagina si no esta vacio
	 * @return
	 */
	public static Map<String, Object> getLogin(String msg) {
		Map<String, Object> fragmentMapper = getCommonElements(msg);
		fragmentMapper.put("mainTemplate", "login/login");
		fragmentMapper.put("mainFragment", "login");
		return fragmentMapper;
	}

	/**
	 * Obtiene el mapeo con los elementos comunes (menu, pie de pagina y
	 * mensaje) y con la plantilla de cambio de contraseña
	 * 
	 * @param msg
	 *            mensaje que se mostrara al cargar la pagina si no esta vacio
	 * @return
	 */
	public static Map<String, Object> getPasswordChange(String msg) {
        Map<String, Object> fragmentMapper = FragmentManager.getCommonElements(msg);
        fragmentMapper.put("mainTemplate", "login/passwordChange");
        fragmentMapper.put("mainFragment", "passwordChange");
        return fragmentMapper;
    }
}
