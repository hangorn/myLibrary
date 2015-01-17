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
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.dao.TraductorDao;

/**
 * Acceso a los datos de traductores, usando hibernate
 * 
 * @author javier.vaquero
 * 
 */
@SuppressWarnings("unchecked")
public class HibTraductorDao extends HibAbstractDao implements TraductorDao {

	public HibTraductorDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.TRANSLATORS_TABLE);
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
		Traductor filter = (Traductor) f;
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
	 */
	public List<Traductor> getTraductores(String start) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Traductor> l = s
					.createQuery("FROM Traductor WHERE nombre LIKE :nombre ORDER BY nombre")
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
	public List<Libro> getLibrosTraductor(Integer id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Libro> l = s.createCriteria(Libro.class)
					.createCriteria("traductores").add(Restrictions.idEq(id))
					.addOrder(Property.forName("titulo").asc()).list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
