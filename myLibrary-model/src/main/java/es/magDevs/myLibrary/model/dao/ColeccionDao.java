package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Libro;

/**
 * Interfaz para el acceso a los datos de colecciones
 * 
 * @author javi
 * 
 */
public interface ColeccionDao extends AbstractDao {
	/**
	 * * Obtiene una lista de las colecciones paginada con las opiones indicadas
	 * 
	 * @param page
	 *            numero de pagina
	 * @param pageSize
	 *            tamaño de la pagina
	 * @return
	 * @throws Exception 
	 */
	public List<Coleccion> getColeccionesWithPag(int page, int pageSize) throws Exception;

	/**
	 * Obtiene una lista de las colecciones, filtradas por los criterios
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
	public List<Coleccion> getColeccionesWithPag(Coleccion filter, int page,
			int pageSize) throws Exception;

	/**
	 * Obtiene el numero total de colecciones
	 * 
	 * @return
	 * @throws Exception 
	 */
	public int getCountColecciones() throws Exception;

	/**
	 * Obtiene el numero total de colecciones filtradas por los criterios
	 * indicados
	 * 
	 * @param filter
	 *            criterios de busqueda
	 * @return
	 * @throws Exception 
	 */
	public int getCountColecciones(Coleccion filter) throws Exception;

	/**
	 * Obtiene la coleccion referenciada por el ID indicado
	 * 
	 * @param id
	 *            de la coleccion a obtener
	 * @return todos los datos de la coleccion
	 * @throws Exception 
	 */
	public Coleccion getColeccion(int id) throws Exception;

	/**
	 * Obtiene una lista de las colecciones cuyo nombre empieza por la
	 * sugerencia indicada
	 * 
	 * @param start
	 * @return
	 * @throws Exception 
	 */
	public List<Coleccion> getColecciones(String start) throws Exception;

	/**
	 * Obtiene una lista de las colecciones cuyo nombre empieza por la
	 * sugerencia indicada y pertenecen a la editorial seleccionada
	 * 
	 * @param start
	 * @return
	 * @throws Exception 
	 */
	public List<Coleccion> getColecciones(String start, Integer idEditorial) throws Exception;

	/**
	 * Obtiene una lista de los libros que tiene una coleccion
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<Libro> getLibrosColeccion(Integer id) throws Exception;
}
