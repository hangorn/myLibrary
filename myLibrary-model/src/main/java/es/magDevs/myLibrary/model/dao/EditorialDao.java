package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;

/**
 * Interfaz para el acceso a los datos de editoriales
 * 
 * @author javi
 * 
 */
public interface EditorialDao extends AbstractDao {
	/**
	 * * Obtiene una lista de las editoriales paginada con las opiones indicadas
	 * 
	 * @param page
	 *            numero de pagina
	 * @param pageSize
	 *            tamaño de la pagina
	 * @return
	 * @throws Exception 
	 */
	public List<Editorial> getEditorialesWithPag(int page, int pageSize) throws Exception;

	/**
	 * Obtiene una lista de las editoriales, filtradas por los criterios
	 * indicados y paginada
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
	public List<Editorial> getEditorialesWithPag(Editorial filter, int page,
			int pageSize) throws Exception;

	/**
	 * Obtiene el numero total de editoriales
	 * 
	 * @return
	 * @throws Exception 
	 */
	public int getCountEditoriales() throws Exception;

	/**
	 * Obtiene el numero total de editoriales filtradas por los criterios
	 * indicados
	 * 
	 * @param filter
	 *            criterios de busqueda
	 * @return
	 * @throws Exception 
	 */
	public int getCountEditoriales(Editorial filter) throws Exception;

	/**
	 * Obtiene la editorial referenciada por el ID indicado
	 * 
	 * @param id
	 *            de la editorial a obtener
	 * @return todos los datos de la editorial
	 * @throws Exception 
	 */
	public Editorial getEditorial(int id) throws Exception;

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
