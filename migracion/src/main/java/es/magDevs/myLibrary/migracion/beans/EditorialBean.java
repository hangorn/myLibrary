package es.magDevs.myLibrary.migracion.beans;

import java.math.BigDecimal;
import java.util.Date;

public class EditorialBean {
	private Integer nº_editorial;
	private String nombre_editorial;
	private String ciudad_de_ubicacion;
	public Integer getNº_editorial() {
		return this.nº_editorial;
	}
	public void setNº_editorial(Integer nº_editorial) {
		this.nº_editorial=nº_editorial;
	}
	public String getNombre_editorial() {
		return this.nombre_editorial;
	}
	public void setNombre_editorial(String nombre_editorial) {
		this.nombre_editorial=nombre_editorial;
	}
	public String getCiudad_de_ubicacion() {
		return this.ciudad_de_ubicacion;
	}
	public void setCiudad_de_ubicacion(String ciudad_de_ubicacion) {
		this.ciudad_de_ubicacion=ciudad_de_ubicacion;
	}


	public String toString() {
		return "nº_editorial="+nº_editorial+" "+"nombre_editorial="+nombre_editorial+" "+"ciudad_de_ubicacion="+ciudad_de_ubicacion+" "+"";
	}
}