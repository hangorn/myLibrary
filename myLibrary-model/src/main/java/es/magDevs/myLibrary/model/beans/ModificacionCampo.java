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

import java.io.Serializable;

@SuppressWarnings("serial")
public class ModificacionCampo implements Serializable, Cloneable {
	private Integer idModificacion;
	private String columna;
	private String valor;

	public ModificacionCampo() {

	}

	public ModificacionCampo(Integer idModificacion, String columna, String valor) {
		super();
		this.idModificacion = idModificacion;
		this.columna = columna;
		this.valor = valor;
	}
	
	public ModificacionCampo(ModificacionCampo bean) {
		super();
		this.idModificacion = bean.idModificacion;
		this.columna = bean.columna;
		this.valor = bean.valor;
	}
	
	public Integer getIdModificacion() {
		return idModificacion;
	}
	public void setIdModificacion(Integer idModificacion) {
		this.idModificacion = idModificacion;
	}
	public String getColumna() {
		return columna;
	}
	public void setColumna(String columna) {
		this.columna = columna;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public String toString() {
		return idModificacion + ":" + columna + " (" + valor + ")";
	}

	public ModificacionCampo clone() {
		return new ModificacionCampo(this);
	}
}
