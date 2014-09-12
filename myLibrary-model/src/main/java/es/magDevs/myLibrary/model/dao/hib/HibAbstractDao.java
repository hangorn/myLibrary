package es.magDevs.myLibrary.model.dao.hib;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import es.magDevs.myLibrary.model.dao.AbstractDao;

/**
 * Clase abstracta con los metodos comunes para los DAO de Hibernate
 * 
 * @author javi
 * 
 */
public class HibAbstractDao implements AbstractDao {

	private SessionFactory sessionFactory;

	/**
	 * Obtiene la sesion actual para realizar operaciones contra el origen de
	 * datos
	 * 
	 * @return
	 */
	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public HibAbstractDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	public void beginTransaction() {
		getSession().beginTransaction();
	}

	/**
	 * {@inheritDoc}
	 */
	public void commitTransaction() {
		getSession().getTransaction().commit();
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollbackTransaction() {
		getSession().getTransaction().rollback();
		;
	}

	/**
	 * {@inheritDoc}
	 */
	public int insert(Object data) {
		Session session = getSession();
		if (!session.isOpen() || session.getTransaction() == null) {
			return 0;
		}
		return (Integer) session.save(data);
	}

	/**
	 * {@inheritDoc}
	 */
	public void delete(Object data) {
		Session session = getSession();
		if (!session.isOpen() || session.getTransaction() == null) {
			return;
		}
		session.delete(data);
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(Object data) {
		Session session = getSession();
		if (!session.isOpen() || session.getTransaction() == null) {
			return;
		}
		session.update(data);
	}
}
