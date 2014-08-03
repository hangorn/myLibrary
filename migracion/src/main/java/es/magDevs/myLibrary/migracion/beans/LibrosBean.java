package es.magDevs.myLibrary.migracion.beans;

import java.math.BigDecimal;
import java.util.Date;

public class LibrosBean {
	private Integer nº_libro;
	private String titulo;
	private String apellidos;
	private String nombre;
	private String nombre_editorial;
	private String nombre_coleccion;
	private String nº_isbn;
	private Integer año_publicacion;
	private Integer nº_edicion;
	private Integer año_c;
	private Integer nº_paginas;
	private Integer tomo;
	private String traductor;
	private String codigo;
	private String sitio;
	private String año_compra;
	private BigDecimal precio;
	private String notas;
	public Integer getNº_libro() {
		return this.nº_libro;
	}
	public void setNº_libro(Integer nº_libro) {
		this.nº_libro=nº_libro;
	}
	public String getTitulo() {
		return this.titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo=titulo;
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
	public String getNombre_editorial() {
		return this.nombre_editorial;
	}
	public void setNombre_editorial(String nombre_editorial) {
		this.nombre_editorial=nombre_editorial;
	}
	public String getNombre_coleccion() {
		return this.nombre_coleccion;
	}
	public void setNombre_coleccion(String nombre_coleccion) {
		this.nombre_coleccion=nombre_coleccion;
	}
	public String getNº_isbn() {
		return this.nº_isbn;
	}
	public void setNº_isbn(String nº_isbn) {
		this.nº_isbn=nº_isbn;
	}
	public Integer getAño_publicacion() {
		return this.año_publicacion;
	}
	public void setAño_publicacion(Integer año_publicacion) {
		this.año_publicacion=año_publicacion;
	}
	public Integer getNº_edicion() {
		return this.nº_edicion;
	}
	public void setNº_edicion(Integer nº_edicion) {
		this.nº_edicion=nº_edicion;
	}
	public Integer getAño_c() {
		return this.año_c;
	}
	public void setAño_c(Integer año_c) {
		this.año_c=año_c;
	}
	public Integer getNº_paginas() {
		return this.nº_paginas;
	}
	public void setNº_paginas(Integer nº_paginas) {
		this.nº_paginas=nº_paginas;
	}
	public Integer getTomo() {
		return this.tomo;
	}
	public void setTomo(Integer tomo) {
		this.tomo=tomo;
	}
	public String getTraductor() {
		return this.traductor;
	}
	public void setTraductor(String traductor) {
		this.traductor=traductor;
	}
	public String getCodigo() {
		return this.codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo=codigo;
	}
	public String getSitio() {
		return this.sitio;
	}
	public void setSitio(String sitio) {
		this.sitio=sitio;
	}
	public String getAño_compra() {
		return this.año_compra;
	}
	public void setAño_compra(String año_compra) {
		this.año_compra=año_compra;
	}
	public BigDecimal getPrecio() {
		return this.precio;
	}
	public void setPrecio(BigDecimal precio) {
		this.precio=precio;
	}
	public String getNotas() {
		return this.notas;
	}
	public void setNotas(String notas) {
		this.notas=notas;
	}


	public String toString() {
		return "nº_libro="+nº_libro+" "+"titulo="+titulo+" "+"apellidos="+apellidos+" "+"nombre="+nombre+" "+"nombre_editorial="+nombre_editorial+" "+"nombre_coleccion="+nombre_coleccion+" "+"nº_isbn="+nº_isbn+" "+"año_publicacion="+año_publicacion+" "+"nº_edicion="+nº_edicion+" "+"año_c="+año_c+" "+"nº_paginas="+nº_paginas+" "+"tomo="+tomo+" "+"traductor="+traductor+" "+"codigo="+codigo+" "+"sitio="+sitio+" "+"año_compra="+año_compra+" "+"precio="+precio+" "+"notas="+notas+" "+"";
	}
}