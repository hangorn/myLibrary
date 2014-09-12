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
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.dao.TipoDao;

/**
 * Acceso a los datos de tipos, usando hibernate
 * 
 * @author javi
 * 
 */
public class HibTipoDao extends HibAbstractDao implements TipoDao {

	public HibTipoDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Tipo> getTipos() throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Tipo> l = s.createQuery("from Tipo").list();
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
	public List<Tipo> getTiposWithPag(int page, int pageSize) throws Exception {
		return getTiposWithPag(null, page, pageSize);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Tipo> getTiposWithPag(Tipo filter, int page, int pageSize)
			throws Exception {
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
			query.addOrder(Property.forName("descripcion").asc());
			List<Tipo> l = query.list();
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
	public int getCountTipos() throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Long count = (Long) s.createQuery("SELECT count(*) FROM Tipo")
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
	public int getCountTipos(Tipo filter) throws Exception {
		if (filter == null) {
			return getCountTipos();
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
	public Tipo getTipo(int id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Tipo tipo = (Tipo) s.createQuery("FROM Tipo WHERE id=:id")
					.setParameter("id", id).uniqueResult();
			s.getTransaction().commit();
			return tipo;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	private Criteria getFilters(Session session, Tipo filter) {
		Criteria c = session.createCriteria(Tipo.class);
		if (filter == null) {
			return c;
		}

		// Recorremos todos los posibles criterios para filtrar:
		// Descripcion
		if (!StringUtils.isBlank(filter.getDescripcion())) {
			c.add(Restrictions.like("descripcion",
					"%" + filter.getDescripcion() + "%"));
		}
		return c;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Libro> getLibrosTipo(Integer id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Libro> l = s.createCriteria(Libro.class)
					.createCriteria("tipo").add(Restrictions.idEq(id)).list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
