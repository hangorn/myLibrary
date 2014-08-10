package es.magDevs.myLibrary.model.beans;

/**
 * Bean con los datos de traductores
 * @author javi
 *
 */
@SuppressWarnings("serial")
public class Traductor extends Bean {
	private String nombre;

	public Traductor() {
	}

	public Traductor(Integer id, String nombre) {
		super(id);
		this.nombre = nombre;
	}

	public Traductor(Traductor bean) {
		super(bean.getId());
		this.nombre = bean.getNombre();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String toString() {
		return getNombre() + " (" + getId() + ")";
	}

	public Traductor clone() {
		return new Traductor(this);
	}
}
