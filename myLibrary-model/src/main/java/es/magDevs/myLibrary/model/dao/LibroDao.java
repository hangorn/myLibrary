package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Libro;

/**
 * Interfaz para el acceso a los datos de libros
 * 
 * @author javi
 * 
 */
public interface LibroDao extends AbstractDao {
	/**
	 * * Obtiene una lista de los libros paginada con las opiones indicadas
	 * 
	 * @param page
	 *            numero de pagina
	 * @param pageSize
	 *            tamaño de la pagina
	 * @return
	 * @throws Exception 
	 */
	public List<Libro> getLibrosWithPag(int page, int pageSize) throws Exception;

	/**
	 * Obtiene una lista de los libros, filtrados por los criterios indicados y
	 * paginada
	 * 
	 * @param filter
	 *            criterios de busqueda
	 * @param page
	 *            numero de pagina
	 * @param pageSize
	 *            tamaño de pagina
	 * @return
	 * @throws Exception 
	 */
	public List<Libro> getLibrosWithPag(Libro filter, int page, int pageSize) throws Exception;

	/**
	 * Obtiene el numero total de libros
	 * 
	 * @return
	 * @throws Exception 
	 */
	public int getCountLibros() throws Exception;

	/**
	 * Obtiene el numero total de libros filtrados por los criterios indicados
	 * 
	 * @param filter
	 *            criterios de busqueda
	 * @return
	 * @throws Exception 
	 */
	public int getCountLibros(Libro filter) throws Exception;

	/**
	 * Obtiene el libro referencia por el id indicado
	 * 
	 * @param id
	 *            del libro a obtener
	 * @return todos los datos del libro, incluidos las tablas asociadas
	 * @throws Exception 
	 */
	public Libro getLibro(int id) throws Exception;
}
