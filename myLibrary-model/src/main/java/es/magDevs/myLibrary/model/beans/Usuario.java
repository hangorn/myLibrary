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
package es.magDevs.myLibrary.model.beans;

import org.hibernate.Hibernate;

/**
 * Bean de los datos de las usuarios
 * 
 * @author javier.vaquero
 * 
 */

public class Usuario extends Bean {
	private String username;
	private String password;
	private String email;
	private String nombre;
	private Boolean enabled;
	private Boolean admin;
	
	private Integer prestamos;
	private Integer pendientes;
	private Integer leidos;

	public Usuario() {
	}

	public Usuario(Usuario usuario) {
		if (usuario == null) {
			return;
		}
		setId(usuario.getId());
		this.username = usuario.getUsername();
		this.password = usuario.getPassword();
		this.email = usuario.getEmail();
		this.nombre = usuario.getNombre();
		this.enabled = usuario.getEnabled();
		this.admin = usuario.getAdmin();
	}

	public Usuario(Integer id, String username, String password, String email, String nombre,
			Boolean enabled, Boolean admin) {
		setId(id);
		this.username = username;
		this.password = password;
		this.email = email;
		this.nombre = nombre;
		this.enabled = enabled;
		this.admin = admin;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getAdmin() {
		return this.admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public Integer getPrestamos() {
		return prestamos;
	}

	public void setPrestamos(Integer prestamos) {
		this.prestamos = prestamos;
	}

	public Integer getPendientes() {
		return pendientes;
	}

	public void setPendientes(Integer pendientes) {
		this.pendientes = pendientes;
	}

	public Integer getLeidos() {
		return leidos;
	}

	public void setLeidos(Integer leidos) {
		this.leidos = leidos;
	}

	public Usuario clone() {
		return new Usuario(this);
	}

	public String toString() {
		return getEmail() != null ? getEmail() : getUsername();
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (Hibernate.getClass((Object) this) != Hibernate
				.getClass((Object) other)) {
			return false;
		}
		Usuario otherBean = (Usuario) other;
		if (getUsername() == null && otherBean.getUsername() == null) {
			return true;
		}
		if (getUsername() == null || otherBean.getUsername() == null) {
			return false;
		}
		if (getUsername() == otherBean.getUsername()) {
			return true;
		}
		if (getUsername().equals(otherBean.getUsername())) {
			return true;
		}
		return false;
	}

	public int hashCode() {
		if (getUsername() == null) {
			return 0;
		}
		return getUsername().hashCode();
	}

	public boolean isEnabled() {
		if (getEnabled() == null) {
			return false;
		}
		return getEnabled();
	}

	public boolean isAdmin() {
		if (getAdmin() == null) {
			return false;
		}
		return getAdmin();
	}
}