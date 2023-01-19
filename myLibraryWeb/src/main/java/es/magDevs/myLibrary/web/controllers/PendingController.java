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

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;

import es.magDevs.myLibrary.model.Constants.SECTION;
import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Pendiente;
import es.magDevs.myLibrary.model.beans.Usuario;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.PendienteDao;
import es.magDevs.myLibrary.web.controllers.main.MainController;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;

/**
 * Controlador para libros marcados como pendientes
 * 
 * @author javier.vaquero
 * 
 */
public class PendingController extends AbstractController {

	public PendingController(MessageSource messageSource) {
		super(messageSource);
	}

	// LOG
	private static final Logger log = Logger
			.getLogger(PendingController.class);

	/* *****************************************
	 * ********** IMPLEMENTACIONES *************
	 * *****************************************
	 */

	/**
	 * {@inheritDoc}
	 */
	protected SECTION getSection() {
		return SECTION.PENDING;
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
		return DaoFactory.getPendienteDao();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean getNewFilter() {
		return new Pendiente();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean processFilter(Bean filter) {
		return FilterManager.processPendingFilter((Pendiente) filter);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean processNewData(Bean newData) {
		return NewDataManager.processPending((Pendiente) newData, messageSource);
	}

	/* *****************************************
	 * ************** ACCIONES *****************
	 * *****************************************
	 */

	public Class<Pendiente> getBeanClass() {
		return Pendiente.class;
	}
	
	@Override
	public String acceptCreation(Bean newElement, Model model) {
		Integer userid = MainController.getUserid();
		if (userid != null) {
			markPending((Pendiente) newElement, userid);
		}
		model.addAllAttributes(FragmentManager.getEmptyBody(""));
		return "commons/body";
	}

	protected void markPending(Pendiente newElement, Integer userid) {
		PendienteDao dao = DaoFactory.getPendienteDao();
		try {
			dao.beginTransaction();
			newElement.setUsuario(new Usuario(userid, null, null, null, null, null, null));
			dao.insert(newElement);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			throw e;
		}
	}
	
	@Override
	public String acceptUpdate(Bean newElement, Model model) {
		Integer userid = MainController.getUserid();
		if (userid != null) {
			PendienteDao dao = DaoFactory.getPendienteDao();
			try {
				dao.beginTransaction();
				((Pendiente) newElement).setUsuario(new Usuario(userid, null, null, null, null, null, null));
				dao.update(newElement);
				dao.commitTransaction();
			} catch (Exception e) {
				dao.rollbackTransaction();
				throw new RuntimeException(e);
			}
		}
		model.addAllAttributes(FragmentManager.getEmptyBody(""));
		return "commons/body";
	}
	
	@Override
	public String delete(Integer index, Model model) {
		Integer userid = MainController.getUserid();
		if (userid != null) {
			PendienteDao dao = DaoFactory.getPendienteDao();
			try {
				Pendiente query = new Pendiente(null, new Libro(), new Usuario(), null);
				query.getLibro().setId(index);
				query.getUsuario().setId(userid);
				Pendiente pendiente = (Pendiente) dao.getWithPag(query, 0, 0).get(0);
				dao.beginTransaction();
				dao.delete(pendiente);
				dao.commitTransaction();
			} catch (Exception e) {
				dao.rollbackTransaction();
				throw new RuntimeException(e);
			}
		}
		return list(model);
	}
	
	@Override
	public String cartBooks(Model model, Bean newData, List<Libro> list) {
		String msg = "";
		Integer userid = MainController.getUserid();
		try {
			if (userid != null) {
				PendienteDao dao = DaoFactory.getPendienteDao();
				Pendiente query = new Pendiente(null, new Libro(), new Usuario(), null);
				query.getUsuario().setId(userid);
				int count = 0;
				for (Libro libro : list) {
					query.getLibro().setId(libro.getId());
					if (dao.getCount(query) != 0) {				
						msg += messageSource.getMessage("book.already.pending", new Object[] {libro.getTitulo()}, LocaleContextHolder.getLocale()) + "\n";
					} else {
						Pendiente pendiente = (Pendiente) newData;
						pendiente.setLibro(libro);
						markPending(pendiente , userid);
						count++;
					}
				}
				msg += messageSource.getMessage("books.marked.pending", new Object[] {count}, LocaleContextHolder.getLocale()) + "\n";
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		list(model);
		model.addAttribute("scriptMessage", msg);
		return "commons/body";
	}
}
