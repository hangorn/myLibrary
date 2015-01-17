package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;

/**
 * Interfaz para el acceso a los datos de editoriales
 * 
 * @author javier.vaquero
 * 
 */
public interface EditorialDao extends AbstractDao {
	/**
	 * Obtiene una lista de las editoriales cuyo nombre empieza por la
	 * sugenrencia indicada
	 * 
	 * @param start
	 * @return
	 * @throws Exception 
	 */
	public List<Editorial> getEditoriales(String start) throws Exception;

	/**
	 * Obtiene una lista de los libros que ha publicado una editorial
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<Libro> getLibrosEditorial(Integer id) throws Exception;
}
