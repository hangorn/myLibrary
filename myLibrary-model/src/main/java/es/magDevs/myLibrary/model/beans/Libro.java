/**
 * Copyright (c) 2014-2015, Javier Vaquero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * required by applicable law or agreed to in writing, software
 * under the License is distributed on an "AS IS" BASIS,
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * the License for the specific language governing permissions and
 * under the License.
 */
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
	
	private Usuario prestamo;

	public Libro() {
		super();
	}

	public Libro(Integer id, String titulo, String isbn, Integer annoCompra,
			Integer annoPublicacion, Integer annoCopyright, Integer numEdicion,
			Integer numPaginas, Integer tomo, Float precio, String notas,
			Editorial editorial, Coleccion coleccion, Tipo tipo, Ubicacion ubicacion,
			Set<Autor> autores, Set<Traductor> traductores) {
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
		this.editorial = editorial;;
		this.coleccion = coleccion;
		this.tipo = tipo;
		this.ubicacion = ubicacion;
		this.autores = autores;
		this.traductores = traductores;
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
		this.editorial = bean.getEditorial();
		this.coleccion = bean.getColeccion();
		this.tipo = bean.getTipo();
		this.ubicacion = bean.getUbicacion();
		this.autores = bean.getAutores();
		this.traductores = bean.getTraductores();
		this.prestamo = bean.getPrestamo();
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

	public Usuario getPrestamo() {
		return prestamo;
	}

	public void setPrestamo(Usuario prestamo) {
		this.prestamo = prestamo;
	}

	public String toString() {
		return getTitulo() + " - " + getIsbn() + " (" + getId() + ")  "
				+ (getTipo() == null ? "Sin tipo" : getTipo().getDescripcion());
	}

	public Libro clone() {
		return new Libro(this);
	}

}
