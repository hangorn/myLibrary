package es.magDevs.myLibrary.model.dao.hib;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.LibroDao;

/**
 * Acceso a los datos de libros, usando hibernate
 * 
 * @author javi
 * 
 */
public class HibLibroDao implements LibroDao {

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

	public HibLibroDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public List<Libro> getLibros() {
		Session s = getSession();
		s.beginTransaction();
		List<Libro> l = s.createQuery("from Libro").list();
		s.getTransaction().commit();
		return l;
	}

	public List<Libro> getLibros(Libro filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public Libro getLibro(int id) {
		Session s = getSession();
		s.beginTransaction();
		Libro libro = (Libro) s.createQuery("FROM Libro WHERE id=" + id)
				.uniqueResult();
		s.getTransaction().commit();
		return libro;
	}

	@SuppressWarnings("unchecked")
	public List<Libro> getLibrosWithPag(int page, int pageSize) {
		Session s = getSession();
		s.beginTransaction();
		Query query = s.createQuery("from Libro order by titulo");
		query.setMaxResults(pageSize);
		query.setFirstResult(page * pageSize);
		List<Libro> l = query.list();
		s.getTransaction().commit();
		return l;
	}

	public int getCountLibros() {
		Session s = getSession();
		s.beginTransaction();
		Long count = (Long) s.createQuery("SELECT count(*) FROM Libro").uniqueResult();
		s.getTransaction().commit();
		return count.intValue();
	}
}
