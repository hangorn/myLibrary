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
 * Bean con los datos de colecciones
 * 
 * @author javier.vaquero
 * 
 */
@SuppressWarnings("serial")
public class Coleccion extends Bean {
	private String nombre;
	private Editorial editorial;

	public Coleccion() {
		super();
		this.editorial = new Editorial();
	}

	public Coleccion(Integer id, String nombre, Editorial editorial) {
		super(id);
		this.nombre = nombre;
		this.editorial = editorial;
	}

	public Coleccion(Coleccion bean) {
		super(bean.getId());
		this.nombre = bean.getNombre();
		this.editorial = bean.getEditorial();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Editorial getEditorial() {
		return editorial;
	}

	public void setEditorial(Editorial editorial) {
		this.editorial = editorial;
	}

	public String toString() {
		return getNombre() + " (" + getId() + ")";
	}

	public Coleccion clone() {
		return new Coleccion(this);
	}
}
