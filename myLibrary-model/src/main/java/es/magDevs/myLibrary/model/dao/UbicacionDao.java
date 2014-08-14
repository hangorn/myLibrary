package es.magDevs.myLibrary.model.dao;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Ubicacion;

/**
 * Interfaz para el acceso a los datos de ubicaciones
 * 
 * @author javi
 * 
 */
public interface UbicacionDao {

	public List<Ubicacion> getUbicaciones();
}
