package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Bean;

/**
 * Interfaz para la clase abstracta de los DAOs
 * 
 * @author javier.vaquero
 * 
 */
@SuppressWarnings("rawtypes")
public interface AbstractDao {
	/**
	 * Inicia una transaccion
	 */
	public void beginTransaction();

	/**
	 * Finaliza una transaccion
	 */
	public void commitTransaction();

	/**
	 * Cancela una transaccion
	 */
	public void rollbackTransaction();

	/**
	 * Inserta un nuevo dato en la base de datos
	 * 
	 * @param libro
	 *            datos a insertar
	 * @return id del dato introducido
	 */
	public int insert(Object data);

	/**
	 * Elimina un dato de la base de datos
	 * 
	 * @param data
	 *            objeto de la clase que se quiera borrar y que ha de contener
	 *            su ID
	 */
	public void delete(Object data);

	/**
	 * Modifica un dato de la base de datos
	 * 
	 * @param data
	 *            objeto de la clase que se quiera modificar
	 */
	public void update(Object data);
	
	/**
	 * Obtiene el dato referenciado por el ID suministrado
	 * 
	 * @param id
	 *            del dato a obtener
	 * @return dato
	 * @throws Exception 
	 */
	public Bean get(int id) throws Exception;
	

	/**
	 * * Obtiene una lista de datos paginada con las opiones indicadas
	 * 
	 * @param page
	 *            numero de pagina
	 * @param pageSize
	 *            tamaño de la pagina
	 * @return
	 * @throws Exception 
	 */
	public List getWithPag(int page, int pageSize) throws Exception;
	
	/**
	 * Obtiene una lista de datos, filtrados por los criterios indicados y
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
	public List getWithPag(Bean filter, int page, int pageSize) throws Exception;
	
	/**
	 * Obtiene una lista de todos los datos
	 * @return
	 * @throws Exception
	 */
	public List getAll() throws Exception;
	
	/**
	 * Obtiene el numero total de datos
	 * 
	 * @return
	 * @throws Exception 
	 */
	public int getCount() throws Exception;

	/**
	 * Obtiene el numero total de datos filtrados por los criterios indicados
	 * 
	 * @param filter
	 *            criterios de busqueda
	 * @return
	 * @throws Exception 
	 */
	public int getCount(Bean filter) throws Exception;
}
