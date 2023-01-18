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

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.magDevs.myLibrary.model.Constants.RELATED_ACTION;
import es.magDevs.myLibrary.model.Constants.SECTION;
import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Leido;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Prestamo;
import es.magDevs.myLibrary.model.beans.Usuario;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.LeidoDao;
import es.magDevs.myLibrary.model.dao.PrestamoDao;
import es.magDevs.myLibrary.web.gui.utils.DatesManager;
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
			createLend(dao, (Prestamo) newElement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		model.addAllAttributes(FragmentManager.getEmptyBody(""));
		return "commons/body";
	}

	private void createLend(PrestamoDao dao, Prestamo prestamo) throws Exception {
		try {
			dao.beginTransaction();
			Usuario usuario = prestamo.getUsuario();
			if(newUsers.containsKey(usuario.getUsername())) {
				usuario.setNombre(newUsers.get(usuario.getUsername()));
				usuario.setUsername(null);
				NewDataManager.processUser(usuario, messageSource);
				DaoFactory.getUsuarioDao().insert(usuario);
			}
			prestamo.setUsuario(DaoFactory.getUsuarioDao().getUser(usuario.getUsername()));
			dao.insert(prestamo);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			throw e;
		}
	}
	
	@Override
	public String delete(Integer index, Model model) {
		PrestamoDao dao = DaoFactory.getPrestamoDao();
		try {
			returnLend(index, dao);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list(model);
	}

	protected void returnLend(Integer bookId, PrestamoDao dao) throws Exception {
		LeidoDao leidoDao = DaoFactory.getLeidoDao();
		Prestamo query = new Prestamo(null, new Libro(), null, null);
		query.getLibro().setId(bookId);
		Prestamo prestamo = (Prestamo) dao.getWithPag(query, 0, 0).get(0);

		Leido leido = new Leido();
		leido.setUsuario(prestamo.getUsuario());
		leido.setLibro(prestamo.getLibro());
		leido.setFecha(DatesManager.getIntToday());
		leido.setPrestado(DatesManager.presentation2Int(prestamo.getFecha()));
		
		try {
			dao.beginTransaction();
			dao.delete(prestamo);
			leidoDao.insert(leido);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			throw e;
		}
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
	
	@Override
	public String cartBooks(Model model, Bean newData, List<Libro> list) {
		Prestamo p = (Prestamo) newData;
		boolean prestar;
		if (p.getLibro().getId() == 1) {
			prestar = true;
		} else if (p.getLibro().getId() == -1) {
			prestar = false;
		} else {
			throw new RuntimeException("Valor no esperado para prestar/devolver libros desde el carrito");
		}
		PrestamoDao dao = DaoFactory.getPrestamoDao();
		String msg = "";
		try {
			int count = 0;
			for (Libro libro : list) {
				Prestamo query = new Prestamo(null, new Libro(), null, null);
				query.getLibro().setId(libro.getId());
				boolean prestado = dao.getCount(query) != 0;
				if (prestar) {
					if (!prestado) {				
						Prestamo prestamo = new Prestamo(p);
						prestamo.setLibro(libro);
						createLend(dao, prestamo);
						count++;
					} else {
						msg += messageSource.getMessage("book.already.lent", new Object[] {libro.getTitulo()}, LocaleContextHolder.getLocale()) + "\n";
					}
				} else {
					if (!prestado) {				
						msg += messageSource.getMessage("book.already.returned", new Object[] {libro.getTitulo()}, LocaleContextHolder.getLocale()) + "\n";
					} else {
						returnLend(libro.getId(), dao);
						count++;
					}
				}
			}
			if (prestar) {
				msg += messageSource.getMessage("books.lent", new Object[] {count}, LocaleContextHolder.getLocale()) + "\n";
			} else {
				msg += messageSource.getMessage("books.returned", new Object[] {count}, LocaleContextHolder.getLocale()) + "\n";
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		list(model);
		model.addAttribute("scriptMessage", msg);
		return "commons/body";
	}
}
