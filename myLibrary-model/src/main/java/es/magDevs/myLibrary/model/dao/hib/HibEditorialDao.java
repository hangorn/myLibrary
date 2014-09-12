package es.magDevs.myLibrary.model.dao.hib;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.EditorialDao;

/**
 * Acceso a los datos de editoriales, usando hibernate
 * 
 * @author javi
 * 
 */
public class HibEditorialDao extends HibAbstractDao implements EditorialDao {

	public HibEditorialDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public List<Editorial> getEditorialesWithPag(int page, int pageSize)
			throws Exception {
		return getEditorialesWithPag(null, page, pageSize);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Editorial> getEditorialesWithPag(Editorial filter, int page,
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
			List<Editorial> l = query.list();
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
	public int getCountEditoriales() throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Long count = (Long) s.createQuery("SELECT count(*) FROM Editorial")
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
	public int getCountEditoriales(Editorial filter) throws Exception {
		if (filter == null) {
			return getCountEditoriales();
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
	public Editorial getEditorial(int id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Editorial editorial = (Editorial) s
					.createQuery("FROM Editorial WHERE id=:id")
					.setParameter("id", id).uniqueResult();
			s.getTransaction().commit();
			return editorial;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	private Criteria getFilters(Session session, Editorial filter) {
		Criteria c = session.createCriteria(Editorial.class);
		if (filter == null) {
			return c;
		}

		// Recorremos todos los posibles criterios para filtrar:
		// Nombre
		if (!StringUtils.isBlank(filter.getNombre())) {
			c.add(Restrictions.like("nombre", "%" + filter.getNombre() + "%"));
		}
		// Ciudad
		if (!StringUtils.isBlank(filter.getCiudad())) {
			c.add(Restrictions.like("ciudad", "%" + filter.getCiudad() + "%"));
		}
		return c;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Editorial> getEditoriales(String start) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Editorial> l = s
					.createQuery("FROM Editorial WHERE nombre LIKE :nombre")
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
	public List<Libro> getLibrosEditorial(Integer id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Libro> l = s.createCriteria(Libro.class)
					.createCriteria("editorial").add(Restrictions.idEq(id))
					.list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
