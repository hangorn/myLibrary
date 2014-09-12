package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Libro;

/**
 * Interfaz para el acceso a los datos de autores
 * 
 * @author javi
 * 
 */
public interface AutorDao extends AbstractDao {
	/**
	 * * Obtiene una lista de los autores paginada con las opiones indicadas
	 * 
	 * @param page
	 *            numero de pagina
	 * @param pageSize
	 *            tamaño de la pagina
	 * @return
	 * @throws Exception 
	 */
	public List<Autor> getAutoresWithPag(int page, int pageSize) throws Exception;

	/**
	 * Obtiene una lista de los autores, filtrados por los criterios indicados y
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
	public List<Autor> getAutoresWithPag(Autor filter, int page, int pageSize) throws Exception;

	/**
	 * Obtiene el numero total de autores
	 * 
	 * @return
	 * @throws Exception 
	 */
	public int getCountAutores() throws Exception;

	/**
	 * Obtiene el numero total de autores filtrados por los criterios indicados
	 * 
	 * @param filter
	 *            criterios de busqueda
	 * @return
	 * @throws Exception 
	 */
	public int getCountAutores(Autor filter) throws Exception;

	/**
	 * Obtiene el autor referenciado por el ID suministrado
	 * 
	 * @param id
	 *            del autor a obtener
	 * @return todos los datos del autor
	 * @throws Exception 
	 */
	public Autor getAutor(int id) throws Exception;

	/**
	 * Obtiene una lista de los autores cuyo nombre o apellidos empieza por la
	 * sugenrencia indicada
	 * 
	 * @param start
	 * @return
	 * @throws Exception 
	 */
	public List<Autor> getAutores(String start) throws Exception;

	/**
	 * Obtiene una lista de los libros que ha escrito un autor
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<Libro> getLibrosAutor(Integer id) throws Exception;
}
