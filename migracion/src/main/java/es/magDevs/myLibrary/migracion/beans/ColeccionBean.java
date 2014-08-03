package es.magDevs.myLibrary.migracion.beans;

import java.math.BigDecimal;
import java.util.Date;

public class ColeccionBean {
	private Integer nº_coleccion;
	private String nombre_coleccion;
	private String nombre_editorial;
	public Integer getNº_coleccion() {
		return this.nº_coleccion;
	}
	public void setNº_coleccion(Integer nº_coleccion) {
		this.nº_coleccion=nº_coleccion;
	}
	public String getNombre_coleccion() {
		return this.nombre_coleccion;
	}
	public void setNombre_coleccion(String nombre_coleccion) {
		this.nombre_coleccion=nombre_coleccion;
	}
	public String getNombre_editorial() {
		return this.nombre_editorial;
	}
	public void setNombre_editorial(String nombre_editorial) {
		this.nombre_editorial=nombre_editorial;
	}


	public String toString() {
		return "nº_coleccion="+nº_coleccion+" "+"nombre_coleccion="+nombre_coleccion+" "+"nombre_editorial="+nombre_editorial+" "+"";
	}
}