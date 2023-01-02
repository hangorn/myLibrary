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
package es.magDevs.myLibrary.web.gui.beans;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.beans.Usuario;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Para almacenar, ademas de los datos del usuarios, los posibles
 * roles/autorizaciones que pueda tener
 * 
 * @author javier.vaquero
 *
 */
public class UserBean extends Usuario {
	Set<GrantedAuthority> auths;

	public UserBean() {
	}

	public UserBean(Usuario usuario) {
		super(usuario);
	}

	public Set<GrantedAuthority> getAuths() {
		if (auths == null) {
			auths = new HashSet<GrantedAuthority>();
			if (isEnabledNotNull()) {
				auths.add(new SimpleGrantedAuthority(Constants.ROLE_ROLE_USER));
				if (isAdminNotNull()) {
					auths.add(new SimpleGrantedAuthority(Constants.ROLE_ROLE_ADMIN));
				}
			}
		}
		return auths;
	}

	public void setAuths(Set<GrantedAuthority> auths) {
		this.auths = auths;
	}
}