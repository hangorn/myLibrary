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

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.magDevs.myLibrary.model.Constants.CONTROLLER;
import es.magDevs.myLibrary.web.controllers.AuthorsController;
import es.magDevs.myLibrary.web.controllers.BooksController;
import es.magDevs.myLibrary.web.controllers.CollectionsController;
import es.magDevs.myLibrary.web.controllers.LendsController;
import es.magDevs.myLibrary.web.controllers.PendingController;
import es.magDevs.myLibrary.web.controllers.PlacesController;
import es.magDevs.myLibrary.web.controllers.PublishersController;
import es.magDevs.myLibrary.web.controllers.ReadController;
import es.magDevs.myLibrary.web.controllers.TranslatorsController;
import es.magDevs.myLibrary.web.controllers.TypesController;
import es.magDevs.myLibrary.web.controllers.UsersController;

/**
 * Factoria para instanciar los posibles controladores
 * 
 * @author javier.vaquero
 *
 */
public class ControllerFactory {
	// Mapeo de controladores
	private Map<CONTROLLER, Controller> controllers = new HashMap<>();

	public ControllerFactory(MessageSource messageSource, PasswordEncoder passwordEncoder) {
		controllers.put(CONTROLLER.TYPES, new TypesController(messageSource));
		controllers.put(CONTROLLER.TRANSLATORS, new TranslatorsController(messageSource));
		controllers.put(CONTROLLER.PUBLISHERS, new PublishersController(messageSource));
		controllers.put(CONTROLLER.PLACES, new PlacesController(messageSource));
		controllers.put(CONTROLLER.COLLECTIONS, new CollectionsController(messageSource));
		controllers.put(CONTROLLER.BOOKS, new BooksController(messageSource));
		controllers.put(CONTROLLER.AUTHORS, new AuthorsController(messageSource));
		controllers.put(CONTROLLER.LENDS, new LendsController(messageSource));
		controllers.put(CONTROLLER.USERS, new UsersController(messageSource));
		controllers.put(CONTROLLER.PENDING, new PendingController(messageSource));
		controllers.put(CONTROLLER.READ, new ReadController(messageSource));
	}

	/**
	 * Obtiene una instancia del controlador indicado
	 * 
	 * @param controller
	 *            tipo de controlador deseado
	 * @return instancia del controlador deseado
	 */
	public Controller getController(CONTROLLER controller) {
		return controllers.get(controller);
	}
}
