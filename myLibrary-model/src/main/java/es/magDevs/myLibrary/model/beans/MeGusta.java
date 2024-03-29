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

/**
 * Bean de los datos para marcar como Me gusta un libro
 * 
 * @author javier.vaquero
 */
@SuppressWarnings("serial")
public class MeGusta extends Bean {

	private Libro libro;
	private Usuario usuario;
	private Boolean megusta;

	public MeGusta() {
	}

	public MeGusta(Integer id, Libro libro, Usuario usuario) {
		super(id);
		this.libro = libro;
		this.usuario = usuario;
	}

	public MeGusta(MeGusta bean) {
		super(bean.getId());
		this.libro = bean.libro;
		this.usuario = bean.usuario;
		this.megusta = bean.megusta;
	}

	public Libro getLibro() {
		return libro;
	}

	public void setLibro(Libro libro) {
		this.libro = libro;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Boolean getMegusta() {
		return megusta;
	}

	public void setMegusta(Boolean megusta) {
		this.megusta = megusta;
	}

	public String toString() {
		return libro.getTitulo() + ":" + usuario.getNombre() + " (" + getId() + ")";
	}

	public MeGusta clone() {
		return new MeGusta(this);
	}
}
