package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Traductor;

/**
 * Interfaz para el acceso a los datos de traductores
 * 
 * @author javi
 * 
 */
public interface TraductorDao extends AbstractDao {
	/**
	 * * Obtiene una lista de los traductores paginada con las opiones indicadas
	 * 
	 * @param page
	 *            numero de pagina
	 * @param pageSize
	 *            tamaño de la pagina
	 * @return
	 * @throws Exception 
	 */
	public List<Traductor> getTraductoresWithPag(int page, int pageSize) throws Exception;

	/**
	 * Obtiene una lista de los traductores, filtradas por los criterios
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
	public List<Traductor> getTraductoresWithPag(Traductor filter, int page,
			int pageSize) throws Exception;

	/**
	 * Obtiene el numero total de traductores
	 * 
	 * @return
	 * @throws Exception 
	 */
	public int getCountTraductores() throws Exception;

	/**
	 * Obtiene el numero total de traductores filtradas por los criterios
	 * indicados
	 * 
	 * @param filter
	 *            criterios de busqueda
	 * @return
	 * @throws Exception 
	 */
	public int getCountTraductores(Traductor filter) throws Exception;

	/**
	 * Obtiene el traductor referenciado por el ID indicado
	 * 
	 * @param id
	 *            del traductor a obtener
	 * @return todos los datos del traductor
	 * @throws Exception 
	 */
	public Traductor getTraductor(int id) throws Exception;

	/**
	 * Obtiene una lista de los traductores cuyo nombre empieza por la
	 * sugenrencia indicada
	 * 
	 * @param start
	 * @return
	 * @throws Exception 
	 */
	public List<Traductor> getTraductores(String start) throws Exception;

	/**
	 * Obtiene una lista de los libros traducidos por un traductor
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<Libro> getLibrosTraductor(Integer id) throws Exception;
}
