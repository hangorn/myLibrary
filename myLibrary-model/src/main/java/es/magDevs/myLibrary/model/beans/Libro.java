package es.magDevs.myLibrary.model.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * Bean de los datos de los libros
 * 
 * @author javier.vaquero
 * 
 */
@SuppressWarnings("serial")
public class Libro extends Bean {

	private String titulo;
	private String isbn;
	private Integer annoCompra;
	private Integer annoPublicacion;
	private Integer annoCopyright;
	private Integer numEdicion;
	private Integer numPaginas;
	private Integer tomo;
	private Float precio;
	private String notas;

	private Editorial editorial;
	private Coleccion coleccion;
	private Tipo tipo;
	private Ubicacion ubicacion;

	private Set<Autor> autores;
	private Set<Traductor> traductores;

	public Libro() {
		super();
	}

	public Libro(Integer id, String titulo, String isbn, Integer annoCompra,
			Integer annoPublicacion, Integer annoCopyright, Integer numEdicion,
			Integer numPaginas, Integer tomo, Float precio, String notas,
			Tipo tipo, Set<Autor> autores) {
		super(id);
		this.titulo = titulo;
		this.isbn = isbn;
		this.annoCompra = annoCompra;
		this.annoPublicacion = annoPublicacion;
		this.annoCopyright = annoCopyright;
		this.numEdicion = numEdicion;
		this.numPaginas = numPaginas;
		this.tomo = tomo;
		this.precio = precio;
		this.notas = notas;
		this.tipo = tipo;
		if (autores != null) {
			this.autores = new HashSet<Autor>();
			this.autores.addAll(autores);
		}
	}

	public Libro(Libro bean) {
		super(bean.getId());
		this.titulo = bean.getTitulo();
		this.isbn = bean.getIsbn();
		this.annoCompra = bean.getAnnoCompra();
		this.annoPublicacion = bean.getAnnoPublicacion();
		this.annoCopyright = bean.getAnnoCopyright();
		this.numEdicion = bean.getNumEdicion();
		this.numPaginas = bean.getNumPaginas();
		this.tomo = bean.getTomo();
		this.precio = bean.getPrecio();
		this.notas = bean.getNotas();
		this.tipo = bean.getTipo();
		if (bean.getAutores() != null) {
			this.autores = new HashSet<Autor>();
			this.autores.addAll(bean.getAutores());
		}
	}
	
	public Libro(Integer id, String titulo, String editorial, String tipo, String ubicacion, Integer tomo) {
		super();
		setId(id);
		setTitulo(titulo);
		setEditorial(new Editorial(null, editorial, null));
		setTipo(new Tipo(null, tipo));
		setUbicacion(new Ubicacion(null, ubicacion, null));
		setTomo(tomo);
		setAutores(new HashSet<Autor>());
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Integer getAnnoCompra() {
		return annoCompra;
	}

	public void setAnnoCompra(Integer annoCompra) {
		this.annoCompra = annoCompra;
	}

	public Integer getAnnoPublicacion() {
		return annoPublicacion;
	}

	public void setAnnoPublicacion(Integer annoPublicacion) {
		this.annoPublicacion = annoPublicacion;
	}

	public Integer getAnnoCopyright() {
		return annoCopyright;
	}

	public void setAnnoCopyright(Integer annoCopyright) {
		this.annoCopyright = annoCopyright;
	}

	public Integer getNumEdicion() {
		return numEdicion;
	}

	public void setNumEdicion(Integer numEdicion) {
		this.numEdicion = numEdicion;
	}

	public Integer getNumPaginas() {
		return numPaginas;
	}

	public void setNumPaginas(Integer numPaginas) {
		this.numPaginas = numPaginas;
	}

	public Integer getTomo() {
		return tomo;
	}

	public void setTomo(Integer tomo) {
		this.tomo = tomo;
	}

	public Float getPrecio() {
		return precio;
	}

	public void setPrecio(Float precio) {
		this.precio = precio;
	}

	public String getNotas() {
		return notas;
	}

	public void setNotas(String notas) {
		this.notas = notas;
	}

	public Editorial getEditorial() {
		return editorial;
	}

	public void setEditorial(Editorial editorial) {
		this.editorial = editorial;
	}

	public Coleccion getColeccion() {
		return coleccion;
	}

	public void setColeccion(Coleccion coleccion) {
		this.coleccion = coleccion;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Ubicacion getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(Ubicacion ubicacion) {
		this.ubicacion = ubicacion;
	}

	public Set<Autor> getAutores() {
		return autores;
	}

	public void setAutores(Set<Autor> autores) {
		this.autores = autores;
	}

	public Set<Traductor> getTraductores() {
		return traductores;
	}

	public void setTraductores(Set<Traductor> traductores) {
		this.traductores = traductores;
	}

	public String toString() {
		return getTitulo() + " - " + getIsbn() + " (" + getId() + ")  "
				+ (getTipo() == null ? "Sin tipo" : getTipo().getDescripcion());
	}

	public Libro clone() {
		return new Libro(this);
	}

}
