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
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.EditorialDao;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;

/**
 * Controlador para la seccion de editoriales
 * 
 * @author javier.vaquero
 * 
 */
public class PublishersController extends AbstractController {
	public PublishersController(MessageSource messageSource) {
		super(messageSource);
	}

	// LOG
	private static final Logger log = Logger
			.getLogger(PublishersController.class);

	/* *****************************************
	 * ********** IMPLEMENTACIONES *************
	 * *****************************************
	 */

	/**
	 * {@inheritDoc}
	 */
	protected SECTION getSection() {
		return SECTION.PUBLISHERS;
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
		return DaoFactory.getEditorialDao();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean getNewFilter() {
		return new Editorial();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean processFilter(Bean filter) {
		return FilterManager.processPublishersFilter((Editorial) filter);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean processNewData(Bean newData) {
		return NewDataManager.processPublisher((Editorial) newData,
				messageSource);
	}

	/* *****************************************
	 * ************* EDITORIALES ***************
	 * *****************************************
	 */
	@Override
	public String read(Integer index, Model model) {
		super.read(index, model);
		List<Libro> books = null;
		// Obtenemos todos los datos del libro seleccionado
		EditorialDao dao = DaoFactory.getEditorialDao();
		try {
			books = dao.getLibrosEditorial(((Bean) data.get(index))
					.getId());
		} catch (Exception e) {
			books = new ArrayList<Libro>();
			manageException("read", e);
		}
		model.addAttribute("publisherBooks", books);
		return "commons/body";
	}

	@Override
	public String readFromId(Integer id, Model model) {
		super.readFromId(id, model);
		List<Libro> books = null;
		// Obtenemos todos los datos del libro seleccionado
		EditorialDao dao = DaoFactory.getEditorialDao();
		try {
			books = dao.getLibrosEditorial(id);
		} catch (Exception e) {
			books = new ArrayList<Libro>();
			manageException("readid", e);
		}
		model.addAttribute("publisherBooks", books);
		return "commons/body";
	}

	/**
	 * Metodo para gestionar peticiones AJAX para obtener datos de editoriales
	 * 
	 * @param hint
	 * @return
	 */
	public List<Editorial> getData(String hint) {
		EditorialDao dao = DaoFactory.getEditorialDao();
		try {
			return dao.getEditoriales(hint);
		} catch (Exception e) {
			log.error("Error los datos mediante peticion AJAX"
					+ " de las editoriales", e);
			return new ArrayList<Editorial>();
		}
	}
}
