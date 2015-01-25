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

import org.springframework.context.MessageSource;
import org.springframework.ui.Model;

import es.magDevs.myLibrary.web.gui.utils.FragmentManager;

public class LoginController {
	private MessageSource messageSource;

	public LoginController(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * Mostrar el formulario de autenticacion
	 * 
	 * @param model
	 *            modelo de datos de Spring
	 * @param params
	 *            parametros de la peticion http
	 * @param needed
	 *            flag para saber si la autenticacion es necesaria o ha sido
	 *            solicitada
	 * @return vista
	 */
	public String login(Model model, String error, String logout,
			boolean needed) {
		// Fijamos atributos para saber si hay error
		model.addAttribute("error", error!=null);
		// Fijamos atributos para saber si viene de logout
		model.addAttribute("logout", logout!=null);
		// Fijamos atributos para saber si el login a sido solicitado o por el
		// contrario es necesario
		model.addAttribute("needed", needed);
		model.addAllAttributes(FragmentManager.getLogin(null));
		return "commons/body";
	}
}
