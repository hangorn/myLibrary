package es.magDevs.myLibrary.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import es.magDevs.myLibrary.web.gui.beans.MenuItem;

@Controller
public class MainController {
	// Spring Message Source
	@Autowired
	private MessageSource messageSource;

	/**
	 * Control para la plantilla del menu
	 */
	private String menu = "commons/empty";
	@ModelAttribute("menuTemplate")
	String getMenu() {
		return menu;
	}
	
	/**
	 * Control para la plantilla de la parte principal
	 */
	private String main = "commons/empty";
	@ModelAttribute("mainTemplate")
	String getMain() {
		return main;
	}
	
	/**
	 * Control para la plantilla del pie
	 */
	private String footer = "commons/empty";
	@ModelAttribute("footerTemplate")
	String getFooter() {
		return footer;
	}

	/**
	 * Lista con los datos de los elementos del menu
	 * 
	 * @return
	 */
	@ModelAttribute("menuItems")
	List<MenuItem> getMenuItems() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		//Obtenemos los elementos del menu
		String[] items = messageSource.getMessage("menu.items", null, null).split(" ");
		for (int i = 0; i < items.length; i++) {
			MenuItem item = new MenuItem();
			item.setText(messageSource.getMessage("menu."+items[i]+".text", null, null));
			item.setImg(messageSource.getMessage("menu."+items[i]+".img", null, null));
			item.setLink(messageSource.getMessage("menu."+items[i]+".link", null, null));
			menuItems.add(item);
		}
		return menuItems;
	}

	@RequestMapping("/")
	public String main() {
		footer = "commons/footer :: footer";
		menu = "commons/menu :: menu";
		return "commons/body";
	}

}
