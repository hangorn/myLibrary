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
 * Bean de los datos de los tipos de libros
 * 
 * @author javier.vaquero
 */
@SuppressWarnings("serial")
public class Tipo extends Bean {

	private String descripcion;

	public Tipo() {
	}

	public Tipo(Integer id, String descripcion) {
		super(id);
		this.descripcion = descripcion;
	}

	public Tipo(Tipo bean) {
		super(bean.getId());
		this.descripcion = bean.getDescripcion();

	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String toString() {
		return getDescripcion() + " (" + getId() + ")";
	}

	public Tipo clone() {
		return new Tipo(this);
	}
}
