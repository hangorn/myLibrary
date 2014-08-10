package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Libro;

/**
 * Interfaz para el acceso a los datos de libros
 * 
 * @author javi
 * 
 */
public interface LibroDao {
	/**
	 * Obtiene una lista de todos los libros
	 * 
	 * @return
	 */
	public List<Libro> getLibros();

	/**
	 * Obtiene una lista de todos los libros con los filtros especificados
	 * 
	 * @return
	 */
	public List<Libro> getLibros(Libro filter);

	/**
	 * * Obtiene una lista de los libros paginada con la opiones indicadas
	 * 
	 * @param page
	 *            numero de pagina
	 * @param pageSize
	 *            tamaño de la pagina
	 * @return
	 */
	public List<Libro> getLibrosWithPag(int page, int pageSize);

	/**
	 * Obtiene el libro referencia por el id indicado
	 * 
	 * @param id
	 * @return
	 */
	public Libro getLibro(int id);
	
	/**
	 * Obtiene el numero total de libros
	 * @return
	 */
	public int getCountLibros();

}
