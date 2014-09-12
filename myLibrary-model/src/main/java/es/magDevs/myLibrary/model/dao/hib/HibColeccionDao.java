package es.magDevs.myLibrary.model.dao.hib;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.ColeccionDao;

/**
 * Acceso a los datos de colecciones, usando hibernate
 * 
 * @author javi
 * 
 */
public class HibColeccionDao extends HibAbstractDao implements ColeccionDao {

	public HibColeccionDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public List<Coleccion> getColeccionesWithPag(int page, int pageSize)
			throws Exception {
		return getColeccionesWithPag(null, page, pageSize);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Coleccion> getColeccionesWithPag(Coleccion filter, int page,
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
			List<Coleccion> l = query.list();
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
	public int getCountColecciones() throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Long count = (Long) s.createQuery("SELECT count(*) FROM Coleccion")
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
	public int getCountColecciones(Coleccion filter) throws Exception {
		if (filter == null) {
			return getCountColecciones();
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
	public Coleccion getColeccion(int id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Coleccion coleccion = (Coleccion) s
					.createQuery("FROM Coleccion WHERE id=:id")
					.setParameter("id", id).uniqueResult();
			s.getTransaction().commit();
			return coleccion;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	private Criteria getFilters(Session session, Coleccion filter) {
		Criteria c = session.createCriteria(Coleccion.class);
		if (filter == null) {
			return c;
		}

		// Recorremos todos los posibles criterios para filtrar:
		// Nombre
		if (!StringUtils.isBlank(filter.getNombre())) {
			c.add(Restrictions.like("nombre", "%" + filter.getNombre() + "%"));
		}
		// Editorial
		if (filter.getEditorial() != null
				&& !StringUtils.isBlank(filter.getEditorial().getNombre())) {
			c.createCriteria("editorial").add(
					Restrictions.like("nombre", "%"
							+ filter.getEditorial().getNombre() + "%"));
		}
		return c;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Coleccion> getColecciones(String start) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Coleccion> l = s
					.createQuery("FROM Coleccion WHERE nombre LIKE  :nombre")
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
	public List<Coleccion> getColecciones(String start, Integer idEditorial)
			throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Coleccion> l = s
					.createQuery(
							"FROM Coleccion WHERE nombre LIKE :nombre AND editorial.id = :id")
					.setParameter("nombre", start + "%")
					.setParameter("id", idEditorial.toString()).list();
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
	public List<Libro> getLibrosColeccion(Integer id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Libro> l = s.createCriteria(Libro.class)
					.createCriteria("coleccion").add(Restrictions.idEq(id))
					.list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
