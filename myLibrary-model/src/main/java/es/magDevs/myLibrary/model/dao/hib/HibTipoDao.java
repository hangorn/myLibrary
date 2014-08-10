package es.magDevs.myLibrary.model.dao.hib;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.dao.TipoDao;

/**
 * Acceso a los datos de tipos, usando hibernate
 * @author javi
 *
 */
public class HibTipoDao implements TipoDao {

	private SessionFactory sessionFactory;
	
	/**
	 * Obtiene la sesion actual para realizar operaciones contra el origen de datos
	 * @return
	 */
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public HibTipoDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("unchecked")
	public List<Tipo> getTipos() {
		Session s = getSession();
		s.beginTransaction();
		List<Tipo> l = s.createQuery("from Tipo").list();
		s.getTransaction().commit();
		return l;
	}

	public List<Tipo> getTipos(Tipo filter) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Tipo getTipo(int id) {
		Session s = getSession();
		s.beginTransaction();
		Tipo tipo = (Tipo)s.createQuery("FROM Tipo WHERE id="+id).uniqueResult();
		s.getTransaction().commit();
		return tipo;
	}

	public void insert(Tipo tipo) {
		Session s = getSession();
		s.beginTransaction();
		Integer id = (Integer) s.save(tipo);
		tipo.setId(id);
		s.getTransaction().commit();		
	}

	public void update(Tipo tipo) {
		Session s = getSession();
		s.beginTransaction();
		s.update(tipo);
		s.getTransaction().commit();		
	}
}
