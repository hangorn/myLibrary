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
 * Bean con los datos de traductores
 * @author javier.vaquero
 *
 */
@SuppressWarnings("serial")
public class Traductor extends Bean {
	private String nombre;
	private String nombreExacto;

	public Traductor() {
	}

	public Traductor(Integer id, String nombre) {
		super(id);
		this.nombre = nombre;
	}

	public Traductor(Traductor bean) {
		super(bean.getId());
		this.nombre = bean.getNombre();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombreExacto() {
		return nombreExacto;
	}

	public void setNombreExacto(String nombreExacto) {
		this.nombreExacto = nombreExacto;
	}

	public String toString() {
		return getNombre() + " (" + getId() + ")";
	}

	public Traductor clone() {
		return new Traductor(this);
	}
}
