package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Libro;

/**
 * Interfaz para el acceso a los datos de tipos
 * 
 * @author javier.vaquero
 * 
 */
public interface TipoDao extends AbstractDao {
	/**
	 * Obtiene una lista de los libros que de un tipo
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<Libro> getLibrosTipo(Integer id) throws Exception;
}
