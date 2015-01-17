package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Libro;

/**
 * Interfaz para el acceso a los datos de ubicaciones
 * 
 * @author javier.vaquero
 * 
 */
public interface UbicacionDao extends AbstractDao {
	/**
	 * Obtiene una lista de los libros que estan en una ubicacion
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<Libro> getLibrosUbicacion(Integer id) throws Exception;
}
