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
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.EditorialDao;

/**
 * Acceso a los datos de editoriales, usando hibernate
 * 
 * @author javier.vaquero
 * 
 */
public class HibEditorialDao extends HibAbstractDao implements EditorialDao {

	public HibEditorialDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.PUBLISHERS_TABLE);
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
		Editorial filter = (Editorial)f;
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
	 */
	@SuppressWarnings("unchecked")
	public List<Editorial> getEditoriales(String start) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Editorial> l = s
					.createQuery("FROM Editorial WHERE nombre LIKE :nombre ORDER BY nombre")
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
	 */
	@SuppressWarnings("unchecked")
	public List<Libro> getLibrosEditorial(Integer id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Libro> l = s.createCriteria(Libro.class)
					.createCriteria("editorial").add(Restrictions.idEq(id))
					.addOrder(Property.forName("titulo").asc()).list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
