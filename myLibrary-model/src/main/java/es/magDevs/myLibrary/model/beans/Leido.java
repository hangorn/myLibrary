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
 * Bean de los datos de un libro pendiente de leer
 * 
 * @author javier.vaquero
 */
@SuppressWarnings("serial")
public class Leido extends Bean {

	private Libro libro;
	private Usuario usuario;
	private Integer fecha;
	
	private String fechaTxt;
	private Integer fechaMin;
	private Integer fechaMax;
	private String fechaMinTxt;
	private String fechaMaxTxt;

	public Leido() {
	}

	public Leido(Integer id, Libro libro, Usuario usuario, Integer fecha) {
		super(id);
		this.libro = libro;
		this.usuario = usuario;
		this.fecha = fecha;
	}

	public Leido(Leido bean) {
		super(bean.getId());
		this.libro = bean.libro;
		this.usuario = bean.usuario;
		this.fecha = bean.fecha;
		this.fechaMin = bean.fechaMin;
		this.fechaMax = bean.fechaMax;
		this.fechaTxt = bean.fechaTxt;
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

	public Integer getFecha() {
		return fecha;
	}

	public void setFecha(Integer fecha) {
		this.fecha = fecha;
	}

	public Integer getFechaMin() {
		return fechaMin;
	}

	public void setFechaMin(Integer fechaMin) {
		this.fechaMin = fechaMin;
	}

	public Integer getFechaMax() {
		return fechaMax;
	}

	public void setFechaMax(Integer fechaMax) {
		this.fechaMax = fechaMax;
	}

	public String getFechaTxt() {
		return fechaTxt;
	}

	public void setFechaTxt(String fechaTxt) {
		this.fechaTxt = fechaTxt;
	}

	public String getFechaMinTxt() {
		return fechaMinTxt;
	}

	public void setFechaMinTxt(String fechaMinTxt) {
		this.fechaMinTxt = fechaMinTxt;
	}

	public String getFechaMaxTxt() {
		return fechaMaxTxt;
	}

	public void setFechaMaxTxt(String fechaMaxTxt) {
		this.fechaMaxTxt = fechaMaxTxt;
	}

	public String toString() {
		return libro.getTitulo() + ":" + usuario.getNombre() + " (" + getId() + ")";
	}

	public Leido clone() {
		return new Leido(this);
	}
}
