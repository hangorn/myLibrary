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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import es.magDevs.myLibrary.model.Constants.ACTION;
import es.magDevs.myLibrary.model.Constants.RELATED_ACTION;
import es.magDevs.myLibrary.model.Constants.SECTION;
import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Leido;
import es.magDevs.myLibrary.model.beans.Pendiente;
import es.magDevs.myLibrary.model.beans.Prestamo;
import es.magDevs.myLibrary.model.beans.Usuario;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.UsuarioDao;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;

/**
 * Controlador para la seccion de usuarios
 * 
 * @author javier.vaquero
 * 
 */
public class UsersController extends AbstractController {
	public UsersController(MessageSource messageSource, PasswordEncoder passwordEncoder) {
		super(messageSource);
		this.passwordEncoder = passwordEncoder;
	}

    private PasswordEncoder passwordEncoder;

	// LOG
	private static final Logger log = Logger
			.getLogger(UsersController.class);

	/* *****************************************
	 * ********** IMPLEMENTACIONES *************
	 * *****************************************
	 */

	/**
	 * {@inheritDoc}
	 */
	protected SECTION getSection() {
		return SECTION.USERS;
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
		return DaoFactory.getUsuarioDao();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean getNewFilter() {
		return new Usuario();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean processFilter(Bean filter) {
		return FilterManager.processUsersFilter((Usuario) filter);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean processNewData(Bean newData) {
		return NewDataManager.processUser((Usuario) newData, messageSource);
	}

	/* ***********************************
	 * ************* USERS ***************
	 * ***********************************
	 */

	/**
	 * Metodo para gestionar peticiones AJAX para obtener datos de usuarios
	 * 
	 * @param hint
	 * @return
	 */
	public List<Usuario> getData(String hint) {
		UsuarioDao dao = DaoFactory.getUsuarioDao();
		try {
			return dao.getUsers(hint);
		} catch (Exception e) {
			log.error("Error los datos mediante peticion AJAX de los usuarios", e);
			return new ArrayList<Usuario>();
		}
	}

	public Class<Usuario> getBeanClass() {
		return Usuario.class;
	}
	
	@Override
	protected boolean isMultiUpdateDisabled() {
		return true;
	}
	
	@Override
	public String delete(Integer index, Model model) {
		Usuario usr = (Usuario) data.get(index);
		if (usr.getCountPrestamos() != 0 || usr.getCountPendientes() != 0 || usr.getCountLeidos() != 0) {
			String msg = messageSource.getMessage("users.menu.delete.with.books", null, LocaleContextHolder.getLocale());
			// Enlazamos fragmentos de plantillas
			model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST, getSection()));
			// Fijamos variables para la vista
			setModelData(model);
			return "commons/body";
		}
		return super.delete(index, model);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Bean getCompleteData(Integer id) throws Exception {
		Usuario usuario = (Usuario) super.getCompleteData(id);
		
		Prestamo filterPrestamo = new Prestamo();
		filterPrestamo.setUsuario(usuario);
		usuario.setPrestamos(DaoFactory.getPrestamoDao().getWithPag(filterPrestamo, 0, 0));
		
		Pendiente filterPendiente = new Pendiente();
		filterPendiente.setUsuario(usuario);
		usuario.setPendientes(DaoFactory.getPendienteDao().getWithPag(filterPendiente, 0, 0));
		
		Leido filterLeido = new Leido();
		filterLeido.setUsuario(usuario);
		usuario.setLeidos(DaoFactory.getLeidoDao().getWithPag(filterLeido, 0, 0));
		
		return usuario;
	}
	
	@Override
	public String manageRelatedData(RELATED_ACTION action, String dataType, String data) {
		if (RELATED_ACTION.NEW.equals(action) && "access".equals(dataType)) {
			if (data == null) {
				return "FAIL";
			}
			int separatorIndex = data.indexOf('|');
			if (separatorIndex < 1 || separatorIndex == data.length()-1) {
				return "FAIL";
			}
			Integer userId = Integer.valueOf(data.substring(0, separatorIndex));
			String pass = data.substring(separatorIndex+1);
			//Realizamos el cambio
	        UsuarioDao dao = DaoFactory.getUsuarioDao();
            dao.beginTransaction();
            try {
                int changes = dao.createPassword(userId, this.passwordEncoder.encode((CharSequence)pass));
                if (changes != 1) {
                	//Si la actualizacion a afectado a mas de un usuario se deshace, esto no es posible
                    dao.rollbackTransaction();
    				return "FAIL";
                } else {
                    dao.commitTransaction();
                }
            }
            catch (Exception e) {
                log.error((Object)"Error al actualizar contrase\u00f1a", e);
                dao.rollbackTransaction();
				return "FAIL";
            }
			
			return "OK";
		}
		return super.manageRelatedData(action, dataType, data);
	}
}
