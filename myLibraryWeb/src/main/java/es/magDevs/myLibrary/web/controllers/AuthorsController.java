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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;

import es.magDevs.myLibrary.model.Constants.ACTION;
import es.magDevs.myLibrary.model.Constants.SECTION;
import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.AutorDao;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
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
		Autor authorData = null;
		List<Libro> authorBooks = null;
		String msg = "";
		// Si tenemos un indice valido
		if (index >= 0 && data != null) {
			// Obtenemos todos los datos del libro seleccionado
			AutorDao dao = DaoFactory.getAutorDao();
			try {
				authorData = (Autor) dao.get(((Bean) data.get(index)).getId());
				authorBooks = dao.getLibrosAutor(((Bean) data.get(index))
						.getId());
			} catch (Exception e) {
				if (authorData == null) {
					authorData = new Autor();
				}
				authorBooks = new ArrayList<Libro>();
				msg = manageException("read", e);
			}
		} else {
			// Creamos un autor vacio, para que no de fallos al intentar acceder
			// a algunos campos
			authorData = new Autor();
			authorBooks = new ArrayList<Libro>();
			msg = messageSource.getMessage("authors.menu.read.noIndexMsg",
					null, LocaleContextHolder.getLocale());
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.READ,
				SECTION.AUTHORS));
		model.addAttribute("elementData", authorData);
		model.addAttribute("authorBooks", authorBooks);
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
