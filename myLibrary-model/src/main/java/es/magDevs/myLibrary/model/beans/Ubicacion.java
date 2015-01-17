package es.magDevs.myLibrary.model.beans;

/**
 * Bean de los datos de las ubicaciones
 * 
 * @author javier.vaquero
 * 
 */
@SuppressWarnings("serial")
public class Ubicacion extends Bean {

	private String codigo;
	private String descripcion;

	public Ubicacion() {
		super();
	}

	public Ubicacion(Integer id, String codigo, String descripcion) {
		super(id);
		this.codigo = codigo;
		this.descripcion = descripcion;
	}

	public Ubicacion(Ubicacion bean) {
		super(bean.getId());
		this.codigo = bean.getCodigo();
		this.descripcion = bean.getDescripcion();
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String toString() {
		return getCodigo() + ": " + getDescripcion() + " (" + getId() + ")";
	}

	public Ubicacion clone() {
		return new Ubicacion(this);
	}
}
