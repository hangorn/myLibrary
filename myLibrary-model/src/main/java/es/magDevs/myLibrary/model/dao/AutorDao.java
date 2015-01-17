package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Libro;

/**
 * Interfaz para el acceso a los datos de autores
 * 
 * @author javier.vaquero
 * 
 */
public interface AutorDao extends AbstractDao {
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
