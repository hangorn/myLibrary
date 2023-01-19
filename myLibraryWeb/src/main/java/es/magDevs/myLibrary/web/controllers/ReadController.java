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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;

import es.magDevs.myLibrary.model.Constants.SECTION;
import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Leido;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Pendiente;
import es.magDevs.myLibrary.model.beans.Usuario;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.LeidoDao;
import es.magDevs.myLibrary.model.dao.PendienteDao;
import es.magDevs.myLibrary.web.controllers.main.MainController;
import es.magDevs.myLibrary.web.gui.utils.DatesManager;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;

/**
 * Controlador para libros marcados como leidos
 * 
 * @author javier.vaquero
 * 
 */
public class ReadController extends AbstractController {

	public ReadController(MessageSource messageSource) {
		super(messageSource);
	}

	// LOG
	private static final Logger log = Logger
			.getLogger(ReadController.class);

	/* *****************************************
	 * ********** IMPLEMENTACIONES *************
	 * *****************************************
	 */

	/**
	 * {@inheritDoc}
	 */
	protected SECTION getSection() {
		return SECTION.READ;
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
		return DaoFactory.getLeidoDao();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean getNewFilter() {
		return new Leido();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean processFilter(Bean filter) {
		return FilterManager.processReadFilter((Leido) filter);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean processNewData(Bean newData) {
		return NewDataManager.processRead((Leido) newData, messageSource);
	}
	
	private Map<Integer, Leido> newReadBooks = new HashMap<>();

	/* *****************************************
	 * ************** ACCIONES *****************
	 * *****************************************
	 */

	public Class<Leido> getBeanClass() {
		return Leido.class;
	}
	
	@Override
	public String acceptCreation(Bean newElement, Model model) {
		Integer userid = MainController.getUserid();
		if (userid != null) {
			markRead(newElement, userid);
		}
		return list(model);
	}

	protected void markRead(Bean newElement, Integer userid) {
		Leido leido = (Leido) newElement;
		if (StringUtils.isEmpty(leido.getFechaTxt())) {
			leido.setFecha(DatesManager.getIntToday());
		} else {
			leido.setFecha(DatesManager.string2Int(leido.getFechaTxt()));
		}
		
		LeidoDao dao = DaoFactory.getLeidoDao();
		PendienteDao daoPendiente = DaoFactory.getPendienteDao();
		try {
			// Buscamos si el libro este pendiente
			Pendiente query = new Pendiente(null, new Libro(), new Usuario(), null);
			query.getLibro().setId(leido.getLibro().getId());
			query.getUsuario().setId(userid);
			List<?> pendientes = daoPendiente.getWithPag(query, 0, 0);
			
			dao.beginTransaction();
			// Guardamos el bean en un mapa con un ID ficticio por si hay que borrarlo
			newReadBooks.put(newElement.getId(), leido);
			leido.setUsuario(new Usuario(userid, null, null, null, null, null, null));
			// Guardamos el libro como leido
			dao.insert(newElement);
			
			// Si estaba marcado como pendiente, lo desmarcamos
			if (!pendientes.isEmpty()) {
				Pendiente prestamo = (Pendiente) pendientes.get(0);
				daoPendiente.delete(prestamo);
			}
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String acceptUpdate(Bean newElement, Model model) {
		Leido leido = (Leido) newElement;
		if (StringUtils.isEmpty(leido.getFechaTxt())) {
			leido.setFecha(DatesManager.getIntToday());
		} else {
			leido.setFecha(DatesManager.string2Int(leido.getFechaTxt()));
		}
		
		Integer userid = MainController.getUserid();
		if (userid != null) {
			LeidoDao dao = DaoFactory.getLeidoDao();
			try {
				dao.beginTransaction();
				(leido).setUsuario(new Usuario(userid, null, null, null, null, null, null));
				dao.update(leido);
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
		LeidoDao dao = DaoFactory.getLeidoDao();
		try {
			Leido query;
			if (index < 0) {
				query = newReadBooks.get(index);
			} else {
				query = new Leido(index, null, null, null);
			}
			dao.beginTransaction();
			dao.delete(query);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			throw new RuntimeException(e);
		}
		return list(model);
	}

	/**
	 * Metodo para gestionar peticiones AJAX para obtener datos del historial de prestamos
	 * 
	 * @param hint texto para buscar usuarios
	 * @param id del libro para el que se buscaran los prestamos
	 * @return datos
	 */
	public List<Leido> getData(String hint, Integer id) {
		try {
			return DaoFactory.getLeidoDao().getHistorialPrestamos(hint, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String cartBooks(Model model, Bean newData, List<Libro> list) {
		String msg = "";
		Integer userid = MainController.getUserid();
		try {
			if (userid != null) {
				LeidoDao dao = DaoFactory.getLeidoDao();
				Leido query = new Leido(null, new Libro(), new Usuario(), null);
				query.getUsuario().setId(userid);
				int count = 0;
				for (Libro libro : list) {
					query.getLibro().setId(libro.getId());
					if (dao.getCount(query) != 0) {				
						msg += messageSource.getMessage("book.already.read", new Object[] {libro.getTitulo()}, LocaleContextHolder.getLocale()) + "\n";
					} else {
						Leido read = (Leido) newData;
						read.setLibro(libro);
						markRead(read , userid);
						count++;
					}
				}
				msg += messageSource.getMessage("books.marked.read", new Object[] {count}, LocaleContextHolder.getLocale()) + "\n";
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		list(model);
		model.addAttribute("scriptMessage", msg);
		return "commons/body";
	}
}
