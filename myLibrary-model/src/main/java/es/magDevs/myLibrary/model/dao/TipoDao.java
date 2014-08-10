package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Tipo;

/**
 * Interfaz para el acceso a los datos de tipos
 * 
 * @author javi
 * 
 */
public interface TipoDao {

	public List<Tipo> getTipos();

	public List<Tipo> getTipos(Tipo filter);

	public Tipo getTipo(int id);

	public void insert(Tipo tipo);
	
	public void update(Tipo tipo);
	
	

}
