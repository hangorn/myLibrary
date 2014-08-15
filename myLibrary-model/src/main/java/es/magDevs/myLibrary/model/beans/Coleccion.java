package es.magDevs.myLibrary.model.beans;

/**
 * Bean con los datos de colecciones
 * 
 * @author javi
 * 
 */
@SuppressWarnings("serial")
public class Coleccion extends Bean {
	private String nombre;
	private Editorial editorial;

	public Coleccion() {
		super();
	}

	public Coleccion(Integer id, String nombre, Editorial editorial) {
		super(id);
		this.nombre = nombre;
		this.editorial = editorial;
	}

	public Coleccion(Coleccion bean) {
		super(bean.getId());
		this.nombre = bean.getNombre();
		this.editorial = bean.getEditorial();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Editorial getEditorial() {
		return editorial;
	}

	public void setEditorial(Editorial editorial) {
		this.editorial = editorial;
	}

	public String toString() {
		return getNombre() + " (" + getId() + ")";
	}

	public Coleccion clone() {
		return new Coleccion(this);
	}
}
