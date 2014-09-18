package es.magDevs.myLibrary.model.dao.hib;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.dao.TraductorDao;

/**
 * Acceso a los datos de traductores, usando hibernate
 * 
 * @author javi
 * 
 */
public class HibTraductorDao extends HibAbstractDao implements TraductorDao {

	public HibTraductorDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public List<Traductor> getTraductoresWithPag(int page, int pageSize)
			throws Exception {
		return getTraductoresWithPag(null, page, pageSize);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Traductor> getTraductoresWithPag(Traductor filter, int page,
			int pageSize) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			// Obtenemos el filtro
			Criteria query = getFilters(s, filter);
			// Fijamos las opciones de paginacion
			query.setMaxResults(pageSize);
			query.setFirstResult(page * pageSize);
			// Ordenamos por titulo de libro
			query.addOrder(Property.forName("nombre").asc());
			List<Traductor> l = query.list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public int getCountTraductores() throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Long count = (Long) s.createQuery("SELECT count(*) FROM Traductor")
					.uniqueResult();
			s.getTransaction().commit();
			return count.intValue();
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public int getCountTraductores(Traductor filter) throws Exception {
		if (filter == null) {
			return getCountTraductores();
		}
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Criteria query = getFilters(s, filter);
			query.setProjection(Projections.rowCount());
			Long count = (Long) query.uniqueResult();
			s.getTransaction().commit();
			return count.intValue();
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public Traductor getTraductor(int id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Traductor traductor = (Traductor) s
					.createQuery("FROM Traductor WHERE id=:id")
					.setParameter("id", id).uniqueResult();
			s.getTransaction().commit();
			return traductor;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	private Criteria getFilters(Session session, Traductor filter) {
		Criteria c = session.createCriteria(Traductor.class);
		if (filter == null) {
			return c;
		}

		// Recorremos todos los posibles criterios para filtrar:
		// Nombre
		if (!StringUtils.isBlank(filter.getNombre())) {
			c.add(Restrictions.like("nombre", "%" + filter.getNombre() + "%"));
		}
		return c;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Traductor> getTraductores(String start) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Traductor> l = s
					.createQuery("FROM Traductor WHERE nombre LIKE :nombre")
					.setParameter("nombre", start + "%").list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Libro> getLibrosTraductor(Integer id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Libro> l = s.createCriteria(Libro.class)
					.createCriteria("traductores").add(Restrictions.idEq(id))
					.list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
