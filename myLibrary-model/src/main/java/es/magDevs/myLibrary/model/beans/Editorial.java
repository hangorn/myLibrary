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

@SuppressWarnings("serial")
public class Editorial extends Bean {
	private String nombre;
	private String ciudad;

	public Editorial() {
	}

	public Editorial(Integer id, String nombre, String ciudad) {
		super(id);
		this.nombre = nombre;
		this.ciudad = ciudad;
	}

	public Editorial(Editorial bean) {
		super(bean.getId());
		this.nombre = bean.getNombre();
		this.ciudad = bean.getCiudad();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String toString() {
		return getNombre() + ": " + getCiudad() + " (" + getId() + ")";
	}

	public Editorial clone() {
		return new Editorial(this);
	}
}
