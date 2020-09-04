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

import org.hibernate.Hibernate;

/**
 * Clase padre abstracta para todos los bean. Contiene un ID y los metodos
 * {@link Bean#equals(Object)} y {@link Bean#hashCode()}
 * 
 * @author javier.vaquero
 * 
 */
@SuppressWarnings("serial")
public abstract class Bean implements Serializable, Cloneable {
	private Integer id;
	/**
	 * nombre de la columna en SQL
	 */
	private String sortedColumn;
	/**
	 * <code>true</code> para orden ascendente, <code>false</code> para descendente
	 */
	private Boolean sortedDirection;

	public Bean() {
		super();
	}

	public Bean(Integer id) {
		super();
		this.id = id;
	}

	public Bean(Bean bean) {
		super();
		this.id = bean.getId();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSortedColumn() {
		return sortedColumn;
	}

	public void setSortedColumn(String sortedColumn) {
		this.sortedColumn = sortedColumn;
	}

	/**
	 * <code>true</code> para orden ascendente, <code>false</code> para descendente
	 */
	public Boolean getSortedDirection() {
		return sortedDirection;
	}

	/**
	 * <code>true</code> para orden ascendente, <code>false</code> para descendente
	 */
	public void setSortedDirection(Boolean sortedDirection) {
		this.sortedDirection = sortedDirection;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (Hibernate.getClass(this) != Hibernate.getClass(other)) {
			return false;
		}

		Bean otherBean = (Bean) other;
		if (this.getId() == null && otherBean.getId() == null) {
			return true;
		}
		if (this.getId() == null || otherBean.getId() == null) {
			return false;
		}
		if (this.getId() == otherBean.getId()) {
			return true;
		}
		if (this.getId().equals(otherBean.getId())) {
			return true;
		}
		return false;
	}

	public int hashCode() {
		if(getId() == null) {
			return 0;
		}
		return this.getId().intValue();
	}

}
