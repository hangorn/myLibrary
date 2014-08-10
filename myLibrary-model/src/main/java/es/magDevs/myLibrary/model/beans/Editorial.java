package es.magDevs.myLibrary.model.beans;

import java.util.Set;

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
