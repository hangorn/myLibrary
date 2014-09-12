package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Ubicacion;

/**
 * Interfaz para el acceso a los datos de ubicaciones
 * 
 * @author javi
 * 
 */
public interface UbicacionDao extends AbstractDao {
	/**
	 * Obtiene todas las ubicaciones disponibles
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<Ubicacion> getUbicaciones() throws Exception;

	/**
	 * * Obtiene una lista de las ubicaciones paginada con las opiones indicadas
	 * 
	 * @param page
	 *            numero de pagina
	 * @param pageSize
	 *            tamaño de la pagina
	 * @return
	 * @throws Exception 
	 */
	public List<Ubicacion> getUbicacionesWithPag(int page, int pageSize) throws Exception;

	/**
	 * Obtiene una lista de las ubicaciones, filtradas por los criterios
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
	public List<Ubicacion> getUbicacionesWithPag(Ubicacion filter, int page,
			int pageSize) throws Exception;

	/**
	 * Obtiene el numero total de ubicaciones
	 * 
	 * @return
	 * @throws Exception 
	 */
	public int getCountUbicaciones() throws Exception;

	/**
	 * Obtiene el numero total de ubicaciones filtradas por los criterios
	 * indicados
	 * 
	 * @param filter
	 *            criterios de busqueda
	 * @return
	 * @throws Exception 
	 */
	public int getCountUbicaciones(Ubicacion filter) throws Exception;

	/**
	 * Obtiene la ubicacion referenciada por el ID suministrado
	 * 
	 * @param id
	 *            de la ubicacion a obtener
	 * @return todos los datos de la ubicacion
	 * @throws Exception 
	 */
	public Ubicacion getUbicacion(int id) throws Exception;

	/**
	 * Obtiene una lista de los libros que estan en una ubicacion
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<Libro> getLibrosUbicacion(Integer id) throws Exception;
}
