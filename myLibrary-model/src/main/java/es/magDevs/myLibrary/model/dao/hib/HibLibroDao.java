package es.magDevs.myLibrary.model.dao.hib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.dao.LibroDao;

/**
 * Acceso a los datos de libros, usando hibernate
 * 
 * @author javi
 * 
 */
public class HibLibroDao implements LibroDao {

	private SessionFactory sessionFactory;

	/**
	 * Obtiene la sesion actual para realizar operaciones contra el origen de
	 * datos
	 * 
	 * @return
	 */
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public HibLibroDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	public Libro getLibro(int id) {
		Session s = getSession();
		s.beginTransaction();
		Libro libro = (Libro) s.createQuery("FROM Libro WHERE id=" + id)
				.uniqueResult();
		s.getTransaction().commit();
		return libro;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Libro> getLibrosWithPag(int page, int pageSize) {
		return getLibrosWithPag(null, page, pageSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<Libro> getLibrosWithPag(Libro filter, int page, int pageSize) {
		Session s = getSession();
		s.beginTransaction();
		// Obtenemos el filtro
		Criteria query = getFilters(s, filter);
		// Fijamos las opciones de paginacion
		query.setMaxResults(pageSize);
		query.setFirstResult(page * pageSize);
		// Ordenamos por titulo de libro
		if (filter != null) {
			query.addOrder(Property.forName("titulo").asc());
		} else {
			query.addOrder(Property.forName("id").desc());
		}
		// Fijamos los datos que queremos obtener
		query.setProjection(Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("titulo"))
				.add(Projections.property("editorial"))
				.add(Projections.property("tipo"))
				.add(Projections.property("ubicacion"))
				.add(Projections.property("tomo")));
		List<Object[]> l = query.list();
		List<Libro> books = new ArrayList<Libro>();
		// Recorremos los datos obtenidos para convertirlos en objetos de la
		// clase Libro y obtener los autores correspondientes
		for (Object[] objects : l) {
			// Creamos el objeto Libro con los datos obtenidos
			Libro book = new Libro();
			book.setId((Integer) objects[0]);
			book.setTitulo((String) objects[1]);
			book.setEditorial((Editorial) objects[2]);
			book.setTipo((Tipo) objects[3]);
			book.setUbicacion((Ubicacion) objects[4]);
			book.setTomo((Integer) objects[5]);
			// Obtenemos los autores asociados
			Query queryAutores = s
					.createSQLQuery("SELECT a.id, a.nombre, a.apellidos"
							+ " FROM autores a JOIN libros_autores la ON"
							+ " a.id=la.autor where la.libro=" + book.getId());
			List<Object[]> autoresData = queryAutores.list();
			Set<Autor> autores = new HashSet<Autor>();
			for (Object[] autorData : autoresData) {
				autores.add(new Autor((Integer) autorData[0],
						(String) autorData[1], (String) autorData[2], null,
						null, null, null, null));
			}
			book.setAutores(autores);
			books.add(book);
		}

		s.getTransaction().commit();
		return books;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getCountLibros() {
		Session s = getSession();
		s.beginTransaction();
		Long count = (Long) s.createQuery("SELECT count(*) FROM Libro")
				.uniqueResult();
		s.getTransaction().commit();
		return count.intValue();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getCountLibros(Libro filter) {
		if (filter == null) {
			return getCountLibros();
		}
		Session s = getSession();
		s.beginTransaction();
		Criteria query = getFilters(s, filter);
		query.setProjection(Projections.rowCount());

		Long count = (Long) query.uniqueResult();
		s.getTransaction().commit();
		return count.intValue();
	}

	private Criteria getFilters(Session session, Libro filter) {
		Criteria c = session.createCriteria(Libro.class);
		if (filter == null) {
			return c;
		}
		Criteria authorsFilter = null;

		// Recorremos todos los posibles criterios para filtrar:
		// Titulo
		if (!StringUtils.isBlank(filter.getTitulo())) {
			c.add(Restrictions.like("titulo", "%" + filter.getTitulo() + "%"));
		}
		// Editorial
		if (filter.getEditorial() != null
				&& !StringUtils.isBlank(filter.getEditorial().getNombre())) {
			c.createCriteria("editorial").add(
					Restrictions.like("nombre", "%"
							+ filter.getEditorial().getNombre() + "%"));
		}
		// Ubicacion
		if (filter.getUbicacion() != null
				&& !StringUtils.isBlank(filter.getUbicacion().getCodigo())) {
			c.createCriteria("ubicacion").add(
					Restrictions
							.eq("codigo", filter.getUbicacion().getCodigo()));
		}
		// Coleccion
		if (filter.getColeccion() != null
				&& !StringUtils.isBlank(filter.getColeccion().getNombre())) {
			c.createCriteria("coleccion").add(
					Restrictions.like("nombre", "%"
							+ filter.getColeccion().getNombre() + "%"));
		}
		// Tipo
		if (filter.getTipo() != null
				&& !StringUtils.isBlank(filter.getTipo().getDescripcion())) {
			c.createCriteria("tipo").add(
					Restrictions.eq("descripcion", filter.getTipo()
							.getDescripcion()));
		}
		// Notas
		if (!StringUtils.isBlank(filter.getNotas())) {
			c.add(Restrictions.like("notas", "%" + filter.getNotas() + "%"));
		}
		// ISBN
		if (!StringUtils.isBlank(filter.getIsbn())) {
			c.add(Restrictions.like("isbn", "%" + filter.getIsbn() + "%"));
		}
		// Año de compra
		if (filter.getAnnoCompra() != null) {
			c.add(Restrictions.eq("annoCompra", filter.getAnnoCompra()));
		}
		// Nº paginas
		if (filter.getNumPaginas() != null) {
			c.add(Restrictions.eq("numPaginas", filter.getNumPaginas()));
		}

		// Datos del autor
		if (filter.getAutores() != null && filter.getAutores().size() > 0) {
			Autor authorFilter = (Autor) filter.getAutores().toArray()[0];
			// Nombre
			if (!StringUtils.isBlank(authorFilter.getNombre())) {
				authorsFilter = c.createCriteria("autores");
				authorsFilter.add(Restrictions.like("nombre", "%"
						+ authorFilter.getNombre() + "%"));
			}
			// Apellidos
			if (!StringUtils.isBlank(authorFilter.getApellidos())) {
				if (authorsFilter == null) {
					authorsFilter = c.createCriteria("autores");
				}
				authorsFilter.add(Restrictions.like("apellidos", "%"
						+ authorFilter.getApellidos() + "%"));
			}
			// Pais
			if (!StringUtils.isBlank(authorFilter.getPais())) {
				if (authorsFilter == null) {
					authorsFilter = c.createCriteria("autores");
				}
				authorsFilter.add(Restrictions.like("pais",
						"%" + authorFilter.getPais() + "%"));
			}
			// Año de nacimiento
			if (authorFilter.getAnnoNacimiento() != null) {
				if (authorsFilter == null) {
					authorsFilter = c.createCriteria("autores");
				}
				authorsFilter.add(Restrictions.eq("annoNacimiento",
						authorFilter.getAnnoNacimiento()));
			}
		}
		return c;
	}
}
