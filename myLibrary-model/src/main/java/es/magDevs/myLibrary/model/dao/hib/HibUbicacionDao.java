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
import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.dao.UbicacionDao;

/**
 * Acceso a los datos de ubicaciones, usando hibernate
 * 
 * @author javi
 * 
 */
public class HibUbicacionDao extends HibAbstractDao implements UbicacionDao {

	public HibUbicacionDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Ubicacion> getUbicaciones() throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Ubicacion> l = s.createQuery("from Ubicacion order by codigo")
					.list();
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
	public List<Ubicacion> getUbicacionesWithPag(int page, int pageSize)
			throws Exception {
		return getUbicacionesWithPag(null, page, pageSize);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public Ubicacion getUbicacion(int id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Ubicacion ubicacion = (Ubicacion) s.createQuery(
					"FROM Ubicacion WHERE id=:id")
					.setParameter("id", id).uniqueResult();
			s.getTransaction().commit();
			return ubicacion;
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
	public List<Ubicacion> getUbicacionesWithPag(Ubicacion filter, int page,
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
			query.addOrder(Property.forName("codigo").asc());
			List<Ubicacion> l = query.list();
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
	public int getCountUbicaciones() throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Long count = (Long) s.createQuery("SELECT count(*) FROM Ubicacion")
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
	public int getCountUbicaciones(Ubicacion filter) throws Exception {
		if (filter == null) {
			return getCountUbicaciones();
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

	private Criteria getFilters(Session session, Ubicacion filter) {
		Criteria c = session.createCriteria(Ubicacion.class);
		if (filter == null) {
			return c;
		}

		// Recorremos todos los posibles criterios para filtrar:
		// Codigo
		if (!StringUtils.isBlank(filter.getCodigo())) {
			c.add(Restrictions.like("codigo", "%" + filter.getCodigo() + "%"));
		}
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
	public List<Libro> getLibrosUbicacion(Integer id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Libro> l = s.createCriteria(Libro.class)
					.createCriteria("ubicacion").add(Restrictions.idEq(id))
					.list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
