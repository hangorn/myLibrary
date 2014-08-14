package es.magDevs.myLibrary.model.dao.hib;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.dao.AutorDao;

/**
 * Acceso a los datos de autores, usando hibernate
 * 
 * @author javi
 * 
 */
public class HibAutorDao implements AutorDao {

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

	public HibAutorDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public List<Autor> getAutores() {
		Session s = getSession();
		s.beginTransaction();
		List<Autor> l = s.createQuery("from Autor").list();
		s.getTransaction().commit();
		return l;
	}

	public List<Autor> getAutores(Autor filter) {
		return null;
	}

	public Autor getAutor(int id) {
		Session s = getSession();
		s.beginTransaction();
		Autor autor = (Autor) s.createQuery("FROM Autor WHERE id=" + id)
				.uniqueResult();
		s.getTransaction().commit();
		return autor;
	}
}
