package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Tipo;

/**
 * Interfaz para el acceso a los datos de tipos
 * 
 * @author javi
 * 
 */
public interface TipoDao extends AbstractDao {
	/**
	 * Obtiene una lista con todos los tipos disponibles
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<Tipo> getTipos() throws Exception;

	/**
	 * * Obtiene una lista de las tipos paginada con las opiones indicadas
	 * 
	 * @param page
	 *            numero de pagina
	 * @param pageSize
	 *            tamaño de la pagina
	 * @return
	 * @throws Exception 
	 */
	public List<Tipo> getTiposWithPag(int page, int pageSize) throws Exception;

	/**
	 * Obtiene una lista de las tipos, filtradas por los criterios
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
	public List<Tipo> getTiposWithPag(Tipo filter, int page,
			int pageSize) throws Exception;

	/**
	 * Obtiene el numero total de tipos
	 * 
	 * @return
	 * @throws Exception 
	 */
	public int getCountTipos() throws Exception;

	/**
	 * Obtiene el numero total de tipos filtradas por los criterios
	 * indicados
	 * 
	 * @param filter
	 *            criterios de busqueda
	 * @return
	 * @throws Exception 
	 */
	public int getCountTipos(Tipo filter) throws Exception;


	/**
	 * Obtiene el tipo referenciado por el ID indicado
	 * 
	 * @param id
	 *            del tipo a obtener
	 * @return todos los datos del tipo
	 * @throws Exception 
	 */
	public Tipo getTipo(int id) throws Exception;

	/**
	 * Obtiene una lista de los libros que de un tipo
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<Libro> getLibrosTipo(Integer id) throws Exception;
}
