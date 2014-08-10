package es.magDevs.myLibrary.model.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * Bean de los datos de los autores
 * 
 * @author javi
 * 
 */
@SuppressWarnings("serial")
public class Autor extends Bean {

	private String nombre;
	private String apellidos;
	private String pais;
	private String ciudad;
	private Integer annoNacimiento;
	private Integer annoFallecimiento;
	private String notas;
	public Autor() {
	}

	public Autor(Integer id, String nombre, String apellidos, String pais,
			String ciudad, Integer annoNacimiento, Integer annoFallecimiento,
			String notas) {
		super(id);
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.pais = pais;
		this.ciudad = ciudad;
		this.annoNacimiento = annoNacimiento;
		this.annoFallecimiento = annoFallecimiento;
		this.notas = notas;
	}
	
	public Autor(Autor bean) {
		super(bean.getId());
		this.nombre = bean.getNombre();
		this.apellidos = bean.getApellidos();
		this.pais = bean.getPais();
		this.ciudad = bean.getCiudad();
		this.annoNacimiento = bean.getAnnoNacimiento();
		this.annoFallecimiento = bean.getAnnoFallecimiento();
		this.notas = bean.getNotas();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public Integer getAnnoNacimiento() {
		return annoNacimiento;
	}

	public void setAnnoNacimiento(Integer annoNacimiento) {
		this.annoNacimiento = annoNacimiento;
	}

	public Integer getAnnoFallecimiento() {
		return annoFallecimiento;
	}

	public void setAnnoFallecimiento(Integer annoFallecimiento) {
		this.annoFallecimiento = annoFallecimiento;
	}

	public String getNotas() {
		return notas;
	}

	public void setNotas(String notas) {
		this.notas = notas;
	}

	public String toString() {
		String string = "";
		if(getNombre() != null && !getNombre().equals("")) {
			string = getNombre() + " ";
		}
		return string + getApellidos();
	}
	
	public Autor clone() {
		return new Autor(this);
	}
}
