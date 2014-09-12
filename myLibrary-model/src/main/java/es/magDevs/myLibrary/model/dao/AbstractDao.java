package es.magDevs.myLibrary.model.dao;

/**
 * Interfaz para la clase abstracta de los DAOs
 * 
 * @author javi
 * 
 */
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
}
