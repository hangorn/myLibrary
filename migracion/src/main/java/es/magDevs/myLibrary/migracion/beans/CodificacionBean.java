package es.magDevs.myLibrary.migracion.beans;

import java.math.BigDecimal;
import java.util.Date;

public class CodificacionBean {
	private Integer número_codificacion;
	private String codigo;
	public Integer getNúmero_codificacion() {
		return this.número_codificacion;
	}
	public void setNúmero_codificacion(Integer número_codificacion) {
		this.número_codificacion=número_codificacion;
	}
	public String getCodigo() {
		return this.codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo=codigo;
	}


	public String toString() {
		return "número_codificacion="+número_codificacion+" "+"codigo="+codigo+" "+"";
	}
}