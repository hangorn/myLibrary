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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Usuario;
import es.magDevs.myLibrary.model.dao.UsuarioDao;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;

public class LoginController {
	private MessageSource messageSource;
	private static final Logger log = Logger.getLogger(LoginController.class);
    private PasswordEncoder passwordEncoder;

    public LoginController(MessageSource messageSource, PasswordEncoder passwordEncoder) {
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
    }

	/**
	 * Mostrar el formulario de autenticacion
	 * 
	 * @param model
	 *            modelo de datos de Spring
	 * @param params
	 *            parametros de la peticion http
	 * @param needed
	 *            flag para saber si la autenticacion es necesaria o ha sido
	 *            solicitada
	 * @return vista
	 */
	public String login(Model model, String error, String logout,
			boolean needed) {
		// Fijamos atributos para saber si hay error
		model.addAttribute("error", error!=null);
		// Fijamos atributos para saber si viene de logout
		model.addAttribute("logout", logout!=null);
		// Fijamos atributos para saber si el login a sido solicitado o por el
		// contrario es necesario
		model.addAttribute("needed", needed);
		model.addAllAttributes(FragmentManager.getLogin(null));
		return "commons/body";
    }

	/**
	 * Solicitud de cambio de contraseña
	 * @param model modelo de Spring
	 * @param oldPassword contraseña antigua
	 * @param newPassword1 contraseña nueva
	 * @param newPassword2 confirmacion de contraseña nueva
	 * @return
	 */
    public String passwordAcceptChange(Model model, String oldPassword, String newPassword1, String newPassword2) {
        String error = null;
        //Obtenemos el nombre de usuario a partir del contexto
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UsuarioDao dao = DaoFactory.getUsuarioDao();
        //Obtenemos los datos del usuario
        Usuario usuario = null;
        try {
            dao.beginTransaction();
            usuario = dao.getUser(username);
            dao.commitTransaction();
        }
        catch (Exception e1) {
            dao.rollbackTransaction();
            log.error((Object)"Error al obtener los datos del usuario para cambiar de contrase\u00f1a", e1);
        }
        if (!newPassword1.equals(newPassword2)) {
            //Si no coincide la confirmacion, error
        	error = messageSource.getMessage("login.form.error.passwordChange.noEqual", null, LocaleContextHolder.getLocale());
        } else if (StringUtils.isBlank((CharSequence)username) || usuario == null || !passwordEncoder.matches(oldPassword, usuario.getPassword())) {
            //Si el nombre de usario esta vacio, no existe el usuario, o no coinciden las contraseñas, error
        	error = messageSource.getMessage("login.form.error.passwordChange", null, LocaleContextHolder.getLocale());
        } else {
        	//Realizamos el cambio
            dao.beginTransaction();
            try {
                int changes = dao.updatePassword(usuario, this.passwordEncoder.encode((CharSequence)newPassword1));
                if (changes != 1) {
                	//Si la actualizacion a afectado a mas de un usuario se deshace, esto no es posible
                    dao.rollbackTransaction();
                    error = messageSource.getMessage("login.form.error.passwordChange", null, LocaleContextHolder.getLocale());
                } else {
                    dao.commitTransaction();
                }
            }
            catch (Exception e) {
                log.error((Object)"Error al actualizar contrase\u00f1a", e);
                dao.rollbackTransaction();
                error = this.messageSource.getMessage("login.form.error.passwordChange", null, LocaleContextHolder.getLocale());
            }
        }
        if (error != null) {
            model.addAttribute("error", (Object)error);
            model.addAllAttributes(FragmentManager.getPasswordChange(null));
        } else {
            model.addAttribute("scriptMessage", messageSource.getMessage("login.form.passwordChanged", null, LocaleContextHolder.getLocale()));
        }
        return "commons/body";
    }

    public String passwordChange(Model model) {
        model.addAllAttributes(FragmentManager.getPasswordChange(null));
        return "commons/body";
	}
}
