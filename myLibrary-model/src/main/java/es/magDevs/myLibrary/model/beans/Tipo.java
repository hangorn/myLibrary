package es.magDevs.myLibrary.model.beans;

/**
 * Bean de los datos de los tipos de libros
 * 
 * @author javier.vaquero
 */
@SuppressWarnings("serial")
public class Tipo extends Bean {

	private String descripcion;

	public Tipo() {
	}

	public Tipo(Integer id, String descripcion) {
		super(id);
		this.descripcion = descripcion;
	}

	public Tipo(Tipo bean) {
		super(bean.getId());
		this.descripcion = bean.getDescripcion();

	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String toString() {
		return getDescripcion() + " (" + getId() + ")";
	}

	public Tipo clone() {
		return new Tipo(this);
	}
}
