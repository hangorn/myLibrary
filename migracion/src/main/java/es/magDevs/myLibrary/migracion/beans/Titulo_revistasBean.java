package es.magDevs.myLibrary.migracion.beans;

import java.math.BigDecimal;
import java.util.Date;

public class Titulo_revistasBean {
	private Integer nº_revista;
	private String titulo;
	private String nombre_editorial;
	public Integer getNº_revista() {
		return this.nº_revista;
	}
	public void setNº_revista(Integer nº_revista) {
		this.nº_revista=nº_revista;
	}
	public String getTitulo() {
		return this.titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo=titulo;
	}
	public String getNombre_editorial() {
		return this.nombre_editorial;
	}
	public void setNombre_editorial(String nombre_editorial) {
		this.nombre_editorial=nombre_editorial;
	}


	public String toString() {
		return "nº_revista="+nº_revista+" "+"titulo="+titulo+" "+"nombre_editorial="+nombre_editorial+" "+"";
	}
}