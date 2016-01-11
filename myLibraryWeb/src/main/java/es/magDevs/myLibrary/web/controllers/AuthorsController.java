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
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;

import es.magDevs.myLibrary.model.Constants.SECTION;
import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.AutorDao;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;

/**
 * Controlador para la seccion de autores
 * 
 * @author javier.vaquero
 * 
 */
public class AuthorsController extends AbstractController {

	public AuthorsController(MessageSource messageSource) {
		super(messageSource);
	}

	// LOG
	private static final Logger log = Logger.getLogger(AuthorsController.class);

	/* *****************************************
	 * ********** IMPLEMENTACIONES *************
	 * *****************************************
	 */

	/**
	 * {@inheritDoc}
	 */
	protected SECTION getSection() {
		return SECTION.AUTHORS;
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
		return DaoFactory.getAutorDao();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean getNewFilter() {
		return new Autor();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean processFilter(Bean filter) {
		return FilterManager.processAuthorsFilter((Autor) filter);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean processNewData(Bean newData) {
		return NewDataManager.processAuthor((Autor) newData, messageSource);
	}

	/* *****************************************
	 * ************** ACCIONES *****************
	 * *****************************************
	 */
	@Override
	public String read(Integer index, Model model) {
		super.read(index, model);
		List<Libro> books = null;
		// Obtenemos todos los datos del libro seleccionado
		AutorDao dao = DaoFactory.getAutorDao();
		try {
			books = dao.getLibrosAutor(((Bean) data.get(index))
					.getId());
		} catch (Exception e) {
			books = new ArrayList<Libro>();
			manageException("read", e);
		}
		model.addAttribute("authorBooks", books);
		return "commons/body";
	}

	@Override
	public String readFromId(Integer id, Model model) {
		super.readFromId(id, model);
		List<Libro> books = null;
		// Obtenemos todos los datos del libro seleccionado
		AutorDao dao = DaoFactory.getAutorDao();
		try {
			books = dao.getLibrosAutor(id);
		} catch (Exception e) {
			books = new ArrayList<Libro>();
			manageException("readid", e);
		}
		model.addAttribute("authorBooks", books);
		return "commons/body";
	}

	/**
	 * Metodo para gestionar peticiones AJAX para obtener datos de autores
	 * 
	 * @param hint
	 * @return
	 */
	public List<Autor> getData(String hint) {
		AutorDao dao = DaoFactory.getAutorDao();
		try {
			return dao.getAutores(hint);
		} catch (Exception e) {
			log.error("Error los datos mediante peticion AJAX"
					+ " de los autores", e);
			return new ArrayList<Autor>();
		}
	}
}
