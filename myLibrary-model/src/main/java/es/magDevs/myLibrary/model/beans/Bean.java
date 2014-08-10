package es.magDevs.myLibrary.model.beans;

import java.io.Serializable;

import org.hibernate.Hibernate;

/**
 * Clase padre abstracta para todos los bean. Contiene un ID y los metodos
 * {@link Bean#equals(Object)} y {@link Bean#hashCode()}
 * 
 * @author javi
 * 
 */
@SuppressWarnings("serial")
public abstract class Bean implements Serializable, Cloneable {
	private Integer id;

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

		Autor otherBean = (Autor) other;
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
