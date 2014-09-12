package es.magDevs.myLibrary.model.dao.hib;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.AutorDao;

/**
 * Acceso a los datos de autores, usando hibernate
 * 
 * @author javi
 * 
 */
public class HibAutorDao extends HibAbstractDao implements AutorDao {

	public HibAutorDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public List<Autor> getAutoresWithPag(int page, int pageSize)
			throws Exception {
		return getAutoresWithPag(null, page, pageSize);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Autor> getAutoresWithPag(Autor filter, int page, int pageSize)
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
			query.addOrder(Property.forName("apellidos").asc()).addOrder(
					Property.forName("nombre").asc());
			// Fijamos los datos que queremos obtener
			query.setProjection(Projections.projectionList()
					.add(Projections.property("id"))
					.add(Projections.property("nombre"))
					.add(Projections.property("apellidos"))
					.add(Projections.property("pais"))
					.add(Projections.property("annoNacimiento")));
			List<Object[]> l = query.list();
			List<Autor> authors = new ArrayList<Autor>();
			// Recorremos los datos obtenidos para convertirlos en objetos de la
			// clase Autor
			for (Object[] objects : l) {
				// Creamos el objeto Autor con los datos obtenidos
				Autor author = new Autor();
				author.setId((Integer) objects[0]);
				author.setNombre((String) objects[1]);
				author.setApellidos((String) objects[2]);
				author.setPais((String) objects[3]);
				author.setAnnoNacimiento((Integer) objects[4]);
				authors.add(author);
			}
			s.getTransaction().commit();
			return authors;
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
	public int getCountAutores() throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Long count = (Long) s.createQuery("SELECT count(*) FROM Autor")
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
	public int getCountAutores(Autor filter) throws Exception {
		if (filter == null) {
			return getCountAutores();
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
	public Autor getAutor(int id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			Autor autor = (Autor) s.createQuery("FROM Autor WHERE id=:id")
					.setParameter("id", id).uniqueResult();
			s.getTransaction().commit();
			return autor;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}

	private Criteria getFilters(Session session, Autor filter) {
		Criteria c = session.createCriteria(Autor.class);
		if (filter == null) {
			return c;
		}

		// Recorremos todos los posibles criterios para filtrar:
		// Nombre
		if (!StringUtils.isBlank(filter.getNombre())) {
			c.add(Restrictions.like("nombre", "%" + filter.getNombre() + "%"));
		}
		// Apellidos
		if (!StringUtils.isBlank(filter.getApellidos())) {
			c.add(Restrictions.like("apellidos", "%" + filter.getApellidos()
					+ "%"));
		}
		// Pais
		if (!StringUtils.isBlank(filter.getPais())) {
			c.add(Restrictions.like("pais", "%" + filter.getPais() + "%"));
		}
		// Ciudad
		if (!StringUtils.isBlank(filter.getCiudad())) {
			c.add(Restrictions.like("ciudad", "%" + filter.getCiudad() + "%"));
		}
		// Año de nacimiento
		if (filter.getAnnoNacimiento() != null) {
			c.add(Restrictions.eq("annoNacimiento", filter.getAnnoNacimiento()));
		}
		// Año de fallecimiento
		if (filter.getAnnoFallecimiento() != null) {
			c.add(Restrictions.eq("annoFallecimiento",
					filter.getAnnoFallecimiento()));
		}
		// Notas
		if (!StringUtils.isBlank(filter.getNotas())) {
			c.add(Restrictions.like("notas", "%" + filter.getNotas() + "%"));
		}
		return c;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Autor> getAutores(String start) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Autor> l = s
					.createQuery(
							"FROM Autor WHERE nombre LIKE :nombre OR apellidos LIKE :apellidos")
					.setParameter("nombre", start+"%")
					.setParameter("apellidos", start+"%").list();
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
	public List<Libro> getLibrosAutor(Integer id) throws Exception {
		Session s = null;
		try {
			s = getSession();
			s.beginTransaction();
			List<Libro> l = s.createCriteria(Libro.class)
					.createCriteria("autores").add(Restrictions.idEq(id))
					.list();
			s.getTransaction().commit();
			return l;
		} catch (Exception e) {
			s.getTransaction().rollback();
			throw e;
		}
	}
}
