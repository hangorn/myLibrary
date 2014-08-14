package es.magDevs.myLibrary.model.dao.hib;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.dao.UbicacionDao;

/**
 * Acceso a los datos de ubicaciones, usando hibernate
 * 
 * @author javi
 * 
 */
public class HibUbicacionDao implements UbicacionDao {

	private SessionFactory sessionFactory;

	/**
	 * Obtiene la sesion actual para realizar operaciones contra el origen de
	 * datos
	 * 
	 * @return
	 */
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public HibUbicacionDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public List<Ubicacion> getUbicaciones() {
		Session s = getSession();
		s.beginTransaction();
		List<Ubicacion> l = s.createQuery("from Ubicacion order by codigo").list();
		s.getTransaction().commit();
		return l;
	}
}
