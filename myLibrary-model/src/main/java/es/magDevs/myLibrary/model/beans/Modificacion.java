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
 * Bean de los datos de un cambio de una fila
 * 
 * @author javier.vaquero
 */
@SuppressWarnings("serial")
public class Modificacion extends Bean {

	private Integer idDato;
	private String tabla;
	private String fecha; 

	public Modificacion() {
	}

	public Modificacion(Integer id, Integer idDato, String tabla, String fecha) {
		super(id);
		this.idDato = idDato;
		this.tabla = tabla;
		this.fecha = fecha;
	}

	public Modificacion(Modificacion bean) {
		super(bean.getId());
		this.idDato = bean.idDato;
		this.tabla = bean.tabla;
		this.fecha = bean.fecha;
	}

	public Integer getIdDato() {
		return idDato;
	}

	public void setIdDato(Integer idDato) {
		this.idDato = idDato;
	}

	public String getTabla() {
		return tabla;
	}

	public void setTabla(String tabla) {
		this.tabla = tabla;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String toString() {
		return tabla + ":" + idDato + " (" + getId() + ")";
	}

	public Modificacion clone() {
		return new Modificacion(this);
	}
}
