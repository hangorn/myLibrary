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
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.dao.TipoDao;

/**
 * Acceso a los datos de tipos, usando hibernate
 * 
 * @author javier.vaquero
 * 
 */
public class HibTipoDao extends HibAbstractDao implements TipoDao {

	public HibTipoDao(SessionFactory sessionFactory) {
		super(sessionFactory, Constants.TYPES_TABLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Boolean> getOrders() {
		Map<String, Boolean> orders = new LinkedHashMap<String, Boolean>();
		return orders;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Criteria getFilters(Session session, Bean f) {
		Tipo filter = (Tipo) f;
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
	 */
	@SuppressWarnings("unchecked")
	public List<Libro> getLibrosTipo(Integer id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Libro> l = s.createCriteria(Libro.class)
					.createCriteria("tipo").add(Restrictions.idEq(id))
					.addOrder(Property.forName("titulo").asc()).list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
