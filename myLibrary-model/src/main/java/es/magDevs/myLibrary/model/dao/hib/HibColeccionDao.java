package es.magDevs.myLibrary.model.dao.hib;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.ColeccionDao;

/**
 * Acceso a los datos de colecciones, usando hibernate
 * 
 * @author javier.vaquero
 * 
 */
public class HibColeccionDao extends HibAbstractDao implements ColeccionDao {

	public HibColeccionDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.COLLECTIONS_TABLE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Boolean> getOrders() {
		Map<String, Boolean> orders = new LinkedHashMap<String, Boolean>();
		orders.put("nombre", true);
		return orders;
	}

	/** 
	 * {@inheritDoc}
	 */
	protected Criteria getFilters(Session session, Bean f) {
		Coleccion filter = (Coleccion)f;
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
					.createQuery("FROM Coleccion WHERE nombre LIKE :nombre ORDER BY nombre")
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
							"FROM Coleccion WHERE nombre LIKE :nombre AND editorial.id = :id ORDER BY nombre")
					.setParameter("nombre", start + "%")
					.setParameter("id", idEditorial).list();
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
					.addOrder(Property.forName("titulo").asc()).list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
