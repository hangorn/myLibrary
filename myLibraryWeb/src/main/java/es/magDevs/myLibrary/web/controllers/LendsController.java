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
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.magDevs.myLibrary.model.Constants.RELATED_ACTION;
import es.magDevs.myLibrary.model.Constants.SECTION;
import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Prestamo;
import es.magDevs.myLibrary.model.beans.Usuario;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.PrestamoDao;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;

/**
 * Controlador para prestamos de libros
 * 
 * @author javier.vaquero
 * 
 */
public class LendsController extends AbstractController {

	public LendsController(MessageSource messageSource) {
		super(messageSource);
	}

	// LOG
	private static final Logger log = Logger
			.getLogger(LendsController.class);

	/* *****************************************
	 * ********** IMPLEMENTACIONES *************
	 * *****************************************
	 */

	/**
	 * {@inheritDoc}
	 */
	protected SECTION getSection() {
		return SECTION.LENDS;
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
		return DaoFactory.getPrestamoDao();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean getNewFilter() {
		return new Prestamo();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean processFilter(Bean filter) {
		return FilterManager.processLendsFilter((Prestamo) filter);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean processNewData(Bean newData) {
		return NewDataManager.processLend((Prestamo) newData, messageSource);
	}
	
	private Map<String, String> newUsers;

	/* *****************************************
	 * ************** ACCIONES *****************
	 * *****************************************
	 */

	public Class<Prestamo> getBeanClass() {
		return Prestamo.class;
	}
	
	@Override
	public String create(Integer index, Model model) {
		if (newUsers == null) {
			newUsers = new HashMap<>();
		} else {
			newUsers.clear();
		}
		model.addAllAttributes(FragmentManager.getEmptyBody(""));
		return "commons/body";
	}
	
	@Override
	public String acceptCreation(Bean newElement, Model model) {
		PrestamoDao dao = DaoFactory.getPrestamoDao();
		try {
			dao.beginTransaction();
			Usuario usuario = ((Prestamo) newElement).getUsuario();
			if(newUsers.containsKey(usuario.getUsername())) {
				usuario.setNombre(newUsers.get(usuario.getUsername()));
				usuario.setUsername(null);
				NewDataManager.processUser(usuario, messageSource);
				DaoFactory.getUsuarioDao().insert(usuario);
			}
			dao.insert(newElement);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			manageException("acceptCreation", e);
		}
		model.addAllAttributes(FragmentManager.getEmptyBody(""));
		return "commons/body";
	}
	
	@Override
	public String delete(Integer index, Model model) {
		PrestamoDao dao = DaoFactory.getPrestamoDao();
		try {
			Prestamo query = new Prestamo(null, new Libro(), null, null);
			query.getLibro().setId(index);
			Prestamo prestamo = (Prestamo) dao.getWithPag(query, 0, 0).get(0);
			dao.beginTransaction();
			dao.delete(prestamo);
			dao.commitTransaction();
		} catch (Exception e) {
			manageException("acceptCreation", e);
			dao.rollbackTransaction();
		}
		model.addAllAttributes(FragmentManager.getEmptyBody(""));
		return "commons/body";
	}
	
	@Override
	public String manageRelatedData(RELATED_ACTION action, String dataType, String data) {
		String result = "FAIL";
		if(SECTION.USERS.get().equals(dataType) && RELATED_ACTION.NEW.equals(action)) {
			try {
				// Tranformamos los datos JSON recibido en el objeto Usuario
				Usuario newUser = new ObjectMapper().readValue(data,
						Usuario.class);
				newUsers.put(newUser.getUsername(), newUser.getNombre());
				result = "OK";
			} catch (Exception e) {
				manageException("_related_"+dataType+"_"+action.get(), e);
			}
		}
		return result;
	}
}
