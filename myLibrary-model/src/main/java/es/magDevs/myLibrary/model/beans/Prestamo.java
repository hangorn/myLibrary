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
 * Bean de los datos de un prestamo
 * 
 * @author javier.vaquero
 */
@SuppressWarnings("serial")
public class Prestamo extends Bean {

	private Libro libro;
	private Usuario usuario;
	private String fecha;
	private String autoresTxt;

	public Prestamo() {
	}

	public Prestamo(Integer id, Libro libro, Usuario usuario, String fecha) {
		super(id);
		this.libro = libro;
		this.usuario = usuario;
		this.fecha = fecha;
	}

	public Prestamo(Prestamo bean) {
		super(bean.getId());
		this.libro = bean.libro;
		this.usuario = bean.usuario;
		this.fecha = bean.fecha;
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

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getAutoresTxt() {
		return autoresTxt;
	}

	public void setAutoresTxt(String autoresTxt) {
		this.autoresTxt = autoresTxt;
	}

	public String toString() {
		return libro.getTitulo() + ":" + usuario.getNombre() + " (" + getId() + ")";
	}

	public Prestamo clone() {
		return new Prestamo(this);
	}
}
