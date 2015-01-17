package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Traductor;

/**
 * Interfaz para el acceso a los datos de traductores
 * 
 * @author javier.vaquero
 * 
 */
public interface TraductorDao extends AbstractDao {
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
