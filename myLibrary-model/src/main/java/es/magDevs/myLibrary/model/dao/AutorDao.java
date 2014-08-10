package es.magDevs.myLibrary.model.dao;

import java.util.List;
import es.magDevs.myLibrary.model.beans.Autor;

/**
 * Interfaz para el acceso a los datos de autores
 * 
 * @author javi
 * 
 */
public interface AutorDao {

	public List<Autor> getAutores();

	public List<Autor> getAutores(Autor filter);

	public Autor getAutor(int id);
}
