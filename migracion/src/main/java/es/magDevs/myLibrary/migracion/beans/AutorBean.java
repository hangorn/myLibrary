package es.magDevs.myLibrary.migracion.beans;

import java.math.BigDecimal;
import java.util.Date;

public class AutorBean {
	private Integer nº_autor;
	private String apellidos;
	private String nombre;
	private String pais_de_nacimiento;
	private String lugar_de_nacimiento;
	private Integer año__de_nacimiento;
	private Integer año_fallecimiento;
	private String notas;
	public Integer getNº_autor() {
		return this.nº_autor;
	}
	public void setNº_autor(Integer nº_autor) {
		this.nº_autor=nº_autor;
	}
	public String getApellidos() {
		return this.apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos=apellidos;
	}
	public String getNombre() {
		return this.nombre;
	}
	public void setNombre(String nombre) {
		this.nombre=nombre;
	}
	public String getPais_de_nacimiento() {
		return this.pais_de_nacimiento;
	}
	public void setPais_de_nacimiento(String pais_de_nacimiento) {
		this.pais_de_nacimiento=pais_de_nacimiento;
	}
	public String getLugar_de_nacimiento() {
		return this.lugar_de_nacimiento;
	}
	public void setLugar_de_nacimiento(String lugar_de_nacimiento) {
		this.lugar_de_nacimiento=lugar_de_nacimiento;
	}
	public Integer getAño__de_nacimiento() {
		return this.año__de_nacimiento;
	}
	public void setAño__de_nacimiento(Integer año__de_nacimiento) {
		this.año__de_nacimiento=año__de_nacimiento;
	}
	public Integer getAño_fallecimiento() {
		return this.año_fallecimiento;
	}
	public void setAño_fallecimiento(Integer año_fallecimiento) {
		this.año_fallecimiento=año_fallecimiento;
	}
	public String getNotas() {
		return this.notas;
	}
	public void setNotas(String notas) {
		this.notas=notas;
	}


	public String toString() {
		return "nº_autor="+nº_autor+" "+"apellidos="+apellidos+" "+"nombre="+nombre+" "+"pais_de_nacimiento="+pais_de_nacimiento+" "+"lugar_de_nacimiento="+lugar_de_nacimiento+" "+"año__de_nacimiento="+año__de_nacimiento+" "+"año_fallecimiento="+año_fallecimiento+" "+"notas="+notas+" "+"";
	}
}