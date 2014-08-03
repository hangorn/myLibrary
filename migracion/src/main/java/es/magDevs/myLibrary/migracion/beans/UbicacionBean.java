package es.magDevs.myLibrary.migracion.beans;

import java.math.BigDecimal;
import java.util.Date;

public class UbicacionBean {
	private Integer nº_ubicacion;
	private String sitio;
	public Integer getNº_ubicacion() {
		return this.nº_ubicacion;
	}
	public void setNº_ubicacion(Integer nº_ubicacion) {
		this.nº_ubicacion=nº_ubicacion;
	}
	public String getSitio() {
		return this.sitio;
	}
	public void setSitio(String sitio) {
		this.sitio=sitio;
	}


	public String toString() {
		return "nº_ubicacion="+nº_ubicacion+" "+"sitio="+sitio+" "+"";
	}
}