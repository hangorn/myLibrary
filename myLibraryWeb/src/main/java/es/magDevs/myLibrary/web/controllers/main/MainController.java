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
package es.magDevs.myLibrary.web.controllers.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.Constants.CONTROLLER;
import es.magDevs.myLibrary.model.Constants.RELATED_ACTION;
import es.magDevs.myLibrary.model.Constants.SECTION;
import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.dao.TipoDao;
import es.magDevs.myLibrary.model.dao.UbicacionDao;
import es.magDevs.myLibrary.web.controllers.LoginController;
import es.magDevs.myLibrary.web.gui.beans.MenuItem;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.MailManager;

@SuppressWarnings({ "unchecked", "serial" })
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class MainController implements InitializingBean, Serializable {
	// Nombre de la variable de path para el controlador
	private static final String CTL_NAME = "controllerName";
	// Expresion para la variable de path del controlador
	private static final String CTL_PATH_VAR = "/{" + CTL_NAME + ":[a-zA-Z]+}";
	// Nombre de la variable de path para la accion al gestionar datos asociados
	private static final String RELATED_ACTION_NAME = "actionName";
	// Nombre de la variable de path para el tipo de dato asociado a gestionar
	private static final String RELATED_DATA_TYPE = "dataType";
	// Expresion para la variable de path de datos asociados
	private static final String RELATED_DATA_PATH_VAR = "_{" + RELATED_ACTION_NAME + ":[a-zA-Z]+}_{" + RELATED_DATA_TYPE + ":[a-zA-Z]+}";
	
	/**
	 * Ejecuta las inicializaciones necesarias
	 */
	public void afterPropertiesSet() throws Exception {
		DaoFactory.init();
		factory = new ControllerFactory(messageSource, passwordEncoder);
	}
	
	@ExceptionHandler(Exception.class)
	public String handleError(Model model, HttpServletRequest req, Exception ex) {
		log.error("Error interno al URL="+req.getRequestURI(), ex);
		MailManager.enviarError(ex, req.getHeader("user-agent"));

		model.addAttribute("exception", MailManager.getStackTrace(ex));
		model.addAttribute("url", req.getRequestURL());
		model.addAllAttributes(FragmentManager.getErrorBody());
		model.addAttribute("menuItems", getMenuItems());
		model.addAttribute("languages", getLanguages());
		model.addAttribute("userData", getUserData());
		
		return "commons/body";
	}

	// Spring Message Source
	@Autowired
	private MessageSource messageSource;
	//Codificador de contraseñas
	@Autowired
    private PasswordEncoder passwordEncoder;
	// Factoria de controladores
	private ControllerFactory factory;
	
	// LOG
	private static final Logger log = Logger.getLogger(MainController.class);
	//Cache para elementos del menu
	private Map<Locale, List<MenuItem>> menuItems = new HashMap<Locale, List<MenuItem>>();

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
		Locale locale = LocaleContextHolder.getLocale();
        if (menuItems.containsKey(locale)) {
            return menuItems.get(locale);
        }
        menuItems.put(locale, new ArrayList<MenuItem>());
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
		String submenuItems = messageSource.getMessage("menu.items.submenu", null, null);
		MenuItem subMenu = null;
		for (int i = 0; i < items.length; i++) {
			MenuItem item = new MenuItem();
			item.setText(messageSource.getMessage("menu." + items[i] + ".text", null, locale));
			item.setImg(messageSource.getMessage("menu." + items[i] + ".img", null, null));
			item.setLink(messageSource.getMessage("menu." + items[i] + ".link", null, null));
			item.setIndex(SECTION.getOrder(items[i]));
			if (submenuItems != null && submenuItems.contains(items[i])) {
				if (subMenu == null) {
					subMenu = new MenuItem();
					subMenu.setSubmenu(new ArrayList<MenuItem>());
					menuItems.get(locale).add(subMenu);
				}
				subMenu.getSubmenu().add(item);
			} else {
				subMenu = null;
				menuItems.get(locale).add(item);
			}
		}
		return menuItems.get(locale);
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
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 if(authentication == null){
			 return null;
		 }
		 for (GrantedAuthority authority : authentication.getAuthorities()) {
			String a = authority.getAuthority();
			if(a != null && (a.equals(Constants.ROLE_ROLE_USER) || a.equals(Constants.ROLE_ROLE_ADMIN))) {
				return authentication;
			}
		}
		return null;
	}

	/**
	 * Datos del administrador autenticado, si lo esta, <code>null</code> en caso de
	 * que no haya un administrador autenticado
	 * 
	 * @return datos del usuario
	 */
	@ModelAttribute("adminData")
	Authentication getAdminData() {
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 if(authentication == null){
			 return null;
		 }
		 for (GrantedAuthority authority : authentication.getAuthorities()) {
			String a = authority.getAuthority();
			if(a != null && a.equals(Constants.ROLE_ROLE_ADMIN)) {
				return authentication;
			}
		}
		return null;
	}
	
	/**
	 * Obtiene una instancia de la clase de bean para el controlador indicado por la variable de path
	 * @param controllerName nombre del controlador del que se obtendra una instancia
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@ModelAttribute(CTL_NAME)
	public Bean getControllerBean(@PathVariable(name =CTL_NAME, required=false) String controllerName, @RequestHeader("user-agent") String userAgent) throws InstantiationException, IllegalAccessException {
		es.magDevs.myLibrary.web.controllers.main.Controller c = getController(controllerName, userAgent);
		if(c != null) {
			return c.getBeanClass().newInstance();
		}
		return null;
	}
	
	/**
	 * Obtiene la instancia de un controlador a partir de su nombre
	 * 
	 * @param controllerName
	 * @return instancia del controlador deseado
	 */
	private es.magDevs.myLibrary.web.controllers.main.Controller getController(String controllerName, String userAgent) {
		es.magDevs.myLibrary.web.controllers.main.Controller controller = factory.getController(CONTROLLER.getController(controllerName));
		if (controller != null) {
			controller.setUserAgent(userAgent);
		}
		return controller;
	}
	
	/* *****************************************
	 * ************ CONTROLADORES **************
	 * *****************************************
	 */
	private LoginController loginController;
	private LoginController getLoginController() {
		if (loginController == null) {
            loginController = new LoginController(messageSource, passwordEncoder);
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
	
	@RequestMapping(value = CTL_PATH_VAR)
	public String list(Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).list(model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_next")
	public String listNext(Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).listNext(model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_previous")
	public String listPrevious(Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).listPrevious(model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_start")
	public String listStart(Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).listStart(model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_end")
	public String listEnd(Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).listEnd(model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_pageSize")
	public String listPageSize(@RequestParam("pageSize") String pageSize, Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).listPageSize(pageSize, model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_search")
	public String listSearch(@ModelAttribute(CTL_NAME) Bean filter, Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).filter(filter, model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_create")
	public String create(@RequestParam("create") Integer index, Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).create(index, model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_acceptCreation", params = {"acceptCreation"})
	public String acceptCreation(@ModelAttribute(CTL_NAME) Bean  newData, Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).acceptCreation(newData, model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_acceptCreation")
	public String cancelCreation(Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).list(model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_update")
	public String update(@RequestParam("update") Integer index, Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).update(index, model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_updateid")
	public String updateId(@RequestParam("update") Integer id, Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).updateFromId(id, model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_acceptUpdate", params = {"acceptUpdate"})
	public String acceptUpdate(@ModelAttribute(CTL_NAME) Bean  newData, Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).acceptUpdate(newData, model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_acceptUpdate")
	public String cancelUpdate(Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).list(model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_delete")
	public String delete(@RequestParam("delete") Integer index, Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).delete(index, model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_read")
	public String read(@RequestParam("read") Integer index, Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).read(index, model);
	}
	@RequestMapping(value = CTL_PATH_VAR+"_readid")
	public String readId(@RequestParam("readid") Integer id, Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).readFromId(id, model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_getdata", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Bean> getData2(@RequestParam("getdata") String hint, @RequestParam(value="id", required=false) Integer id, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).getData(hint, id);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_related"+RELATED_DATA_PATH_VAR, method = RequestMethod.POST)
	public @ResponseBody String manageRelatedData(@RequestParam("data") String data, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent,
			@PathVariable(RELATED_ACTION_NAME) String action, @PathVariable(RELATED_DATA_TYPE) String dataType) {
		return getController(controllerName, userAgent).manageRelatedData(RELATED_ACTION.getAction(action), dataType, data);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_multiupdate")
	public String multiupdate(Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).multiupdate(model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_multisearch")
	public String listMultiSearch(@ModelAttribute(CTL_NAME) Bean filter, Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).multiupdate(filter, model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_acceptMultiupdateSelection", params = {"acceptMultiupdateSelection"})
	public String acceptMultiupdateSelection(@RequestParam("acceptMultiupdateSelection") Collection<Integer> index, Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).acceptMultiupdateSelection(index, model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_acceptMultiupdateSelection")
	public String cancelMultiupdateSelection(Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).list(model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_acceptMultiupdate", params = {"acceptUpdate"})
	public String acceptMultiupdate(@ModelAttribute(CTL_NAME) Bean  newData, Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).acceptMultiupdate(newData, model);
	}
	
	@RequestMapping(value = CTL_PATH_VAR+"_acceptMultiupdate")
	public String cancelMultiupdate(Model model, @PathVariable(CTL_NAME) String controllerName, @RequestHeader("user-agent") String userAgent) {
		return getController(controllerName, userAgent).multiupdate(model);
	}
	
	
	
	
	/* *****************************************
	 * **************** LOGIN ******************
	 * *****************************************
	 */
	@RequestMapping(value = "/{"+CTL_NAME+"}login", method = RequestMethod.GET)
	public String login(Model model, @RequestParam(value = "error", required = false) String error,@RequestParam(value = "logout", required = false) String logout) {
		return getLoginController().login(model, error, logout, true);
	}
	@RequestMapping(value = "/{"+CTL_NAME+"}loginForm", method = RequestMethod.GET)
	public String loginForm(Model model, @RequestParam(value = "error", required = false) String error, @RequestParam(value = "logout", required = false) String logout) {
		return getLoginController().login(model, error, logout, false);
	}
    @RequestMapping(value={"/{"+CTL_NAME+"}passwordChange"}, method={RequestMethod.GET})
    public String passwordAcceptChange(Model model) {
        return this.getLoginController().passwordChange(model);
    }
    @RequestMapping(value={"/{"+CTL_NAME+"}passwordacceptChange"}, method={RequestMethod.POST}, params={"acceptChange"})
    public String passwordAcceptChange(Model model, @RequestParam(value="oldPassword") String oldPassword, @RequestParam(value="newPassword1") String newPassword1, @RequestParam(value="newPassword2") String newPassword2) {
        String template = getLoginController().passwordAcceptChange(model, oldPassword, newPassword1, newPassword2);
        if (!model.containsAttribute("error")) {
            return factory.getController(CONTROLLER.BOOKS).list(model);
        }
        return template;
    }
}
