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
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.TraductorDao;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;

/**
 * Controlador para la seccion de traductores
 * 
 * @author javier.vaqueror
 * 
 */
public class TranslatorsController extends AbstractController {

	public TranslatorsController(MessageSource messageSource) {
		super(messageSource);
	}

	// LOG
	private static final Logger log = Logger
			.getLogger(TranslatorsController.class);

	/* *****************************************
	 * ********** IMPLEMENTACIONES *************
	 * *****************************************
	 */

	/**
	 * {@inheritDoc}
	 */
	protected SECTION getSection() {
		return SECTION.TRANSLATORS;
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
		return DaoFactory.getTraductorDao();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean getNewFilter() {
		return new Traductor();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean processFilter(Bean filter) {
		return FilterManager.processTranslatorsFilter((Traductor) filter);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean processNewData(Bean newData) {
		return NewDataManager.processTranslator((Traductor) newData,
				messageSource);
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
		TraductorDao dao = DaoFactory.getTraductorDao();
		try {
			books = dao.getLibrosTraductor(((Bean) data.get(index))
					.getId());
		} catch (Exception e) {
			books = new ArrayList<Libro>();
			manageException("read", e);
		}
		model.addAttribute("translatorBooks", books);
		return "commons/body";
	}

	@Override
	public String readFromId(Integer id, Model model) {
		super.readFromId(id, model);
		List<Libro> books = null;
		// Obtenemos todos los datos del libro seleccionado
		TraductorDao dao = DaoFactory.getTraductorDao();
		try {
			books = dao.getLibrosTraductor(id);
		} catch (Exception e) {
			books = new ArrayList<Libro>();
			manageException("readid", e);
		}
		model.addAttribute("translatorBooks", books);
		return "commons/body";
	}

	/**
	 * Metodo para gestionar peticiones AJAX para obtener datos de traductores
	 * 
	 * @param hint
	 * @return
	 */
	public List<Traductor> getData(String hint) {
		TraductorDao dao = DaoFactory.getTraductorDao();
		try {
			return dao.getTraductores(hint);
		} catch (Exception e) {
			return new ArrayList<Traductor>();
		}
	}
}
