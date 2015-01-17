package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Libro;

/**
 * Interfaz para el acceso a los datos de colecciones
 * 
 * @author javier.vaquero
 * 
 */
public interface ColeccionDao extends AbstractDao {

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
