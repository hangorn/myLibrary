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
 * Bean de los datos de las ubicaciones
 * 
 * @author javier.vaquero
 * 
 */
@SuppressWarnings("serial")
public class Ubicacion extends Bean {

	private String codigo;
	private String descripcion;

	public Ubicacion() {
		super();
	}

	public Ubicacion(Integer id, String codigo, String descripcion) {
		super(id);
		this.codigo = codigo;
		this.descripcion = descripcion;
	}

	public Ubicacion(Ubicacion bean) {
		super(bean.getId());
		this.codigo = bean.getCodigo();
		this.descripcion = bean.getDescripcion();
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String toString() {
		return getCodigo() + ": " + getDescripcion() + " (" + getId() + ")";
	}

	public Ubicacion clone() {
		return new Ubicacion(this);
	}
}
