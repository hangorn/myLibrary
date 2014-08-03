package es.magDevs.myLibrary.migracion.beans;

import java.math.BigDecimal;
import java.util.Date;

public class RevistasBean {
	private Integer id_revista;
	private String titulo_revista;
	private Integer numero;
	private Integer mes_año;
	private String codificacion;
	private String nº_isbn;
	private Integer nº_paginas;
	private String temas;
	private String ubicacion;
	private Integer año_compra;
	private Integer precio;
	private String notas;
	public Integer getId_revista() {
		return this.id_revista;
	}
	public void setId_revista(Integer id_revista) {
		this.id_revista=id_revista;
	}
	public String getTitulo_revista() {
		return this.titulo_revista;
	}
	public void setTitulo_revista(String titulo_revista) {
		this.titulo_revista=titulo_revista;
	}
	public Integer getNumero() {
		return this.numero;
	}
	public void setNumero(Integer numero) {
		this.numero=numero;
	}
	public Integer getMes_año() {
		return this.mes_año;
	}
	public void setMes_año(Integer mes_año) {
		this.mes_año=mes_año;
	}
	public String getCodificacion() {
		return this.codificacion;
	}
	public void setCodificacion(String codificacion) {
		this.codificacion=codificacion;
	}
	public String getNº_isbn() {
		return this.nº_isbn;
	}
	public void setNº_isbn(String nº_isbn) {
		this.nº_isbn=nº_isbn;
	}
	public Integer getNº_paginas() {
		return this.nº_paginas;
	}
	public void setNº_paginas(Integer nº_paginas) {
		this.nº_paginas=nº_paginas;
	}
	public String getTemas() {
		return this.temas;
	}
	public void setTemas(String temas) {
		this.temas=temas;
	}
	public String getUbicacion() {
		return this.ubicacion;
	}
	public void setUbicacion(String ubicacion) {
		this.ubicacion=ubicacion;
	}
	public Integer getAño_compra() {
		return this.año_compra;
	}
	public void setAño_compra(Integer año_compra) {
		this.año_compra=año_compra;
	}
	public Integer getPrecio() {
		return this.precio;
	}
	public void setPrecio(Integer precio) {
		this.precio=precio;
	}
	public String getNotas() {
		return this.notas;
	}
	public void setNotas(String notas) {
		this.notas=notas;
	}


	public String toString() {
		return "id_revista="+id_revista+" "+"titulo_revista="+titulo_revista+" "+"numero="+numero+" "+"mes_año="+mes_año+" "+"codificacion="+codificacion+" "+"nº_isbn="+nº_isbn+" "+"nº_paginas="+nº_paginas+" "+"temas="+temas+" "+"ubicacion="+ubicacion+" "+"año_compra="+año_compra+" "+"precio="+precio+" "+"notas="+notas+" "+"";
	}
}