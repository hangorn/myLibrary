package es.magDevs.myLibrary.model;

import java.io.IOException;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.magDevs.myLibrary.model.dao.AutorDao;
import es.magDevs.myLibrary.model.dao.EditorialDao;
import es.magDevs.myLibrary.model.dao.LibroDao;
import es.magDevs.myLibrary.model.dao.TipoDao;
import es.magDevs.myLibrary.model.dao.UbicacionDao;
import es.magDevs.myLibrary.model.dao.hib.HibAutorDao;
import es.magDevs.myLibrary.model.dao.hib.HibColeccionDao;
import es.magDevs.myLibrary.model.dao.hib.HibEditorialDao;
import es.magDevs.myLibrary.model.dao.hib.HibLibroDao;
import es.magDevs.myLibrary.model.dao.hib.HibTipoDao;
import es.magDevs.myLibrary.model.dao.hib.HibTraductorDao;
import es.magDevs.myLibrary.model.dao.hib.HibUbicacionDao;

/**
 * Factoria para instanciar los DAOs
 * 
 * @author javier.vaquero
 * 
 */
public class DaoFactory {
	private static boolean init = false;
	private static final int HIBERNATE = 1;

	private static Logger log = LoggerFactory.getLogger(DaoFactory.class);
	private static int dataAccessType;
	private static Properties conf;

	public static void init() {
		if (!init) {
			// Obtenemos la configuracion
			conf = new Properties();
			try {
				// Obtemos el properties de la configuracion
				conf.load(Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("configuration.properties"));
			} catch (IOException e) {
				log.error(
						"No se ha podido obtener el fichero de configuracion.",
						e);
				throw new ExceptionInInitializerError(e);
			}
			String dataAccess = conf.getProperty("dataAccess");
			if ("hibernate".equals(dataAccess)) {
				dataAccessType = HIBERNATE;
			}
			init = true;
		}
	}

	private static final SessionFactory sessionFactory = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {
		if (!init) {
			init();
		}
		// Hibernate
		if (dataAccessType == HIBERNATE) {
			try {
				// Cargamos la configuracion de hibernate de donde corresponda
				Configuration hibernateConfiguration = new Configuration()
						.configure(conf
								.getProperty("hibernate.configurationFile"));
				ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
						.applySettings(hibernateConfiguration.getProperties())
						.build();
				// creamos una factorias de sesiones
				SessionFactory factory = hibernateConfiguration
						.buildSessionFactory(serviceRegistry);
				log.info("Iniciada factoria hibernate para la instanciacion de los DAO disponibles.");
				return factory;
			} catch (Exception ex) {
				log.error("No se ha podido iniciar la factoria hibernate.", ex);
				throw new ExceptionInInitializerError(ex);
			}
		} else {
			return null;
		}
	}

	/**
	 * Proporciona el dao correspondiente para acceder a los datos de los
	 * autores
	 * 
	 * @return
	 */
	public static AutorDao getAutorDao() {
		if (dataAccessType == HIBERNATE) {
			return new HibAutorDao(sessionFactory);
		} else {
			return null;
		}
	}

	/**
	 * Proporciona el dao correspondiente para acceder a los datos de los tipos
	 * 
	 * @return
	 */
	public static TipoDao getTipoDao() {
		if (dataAccessType == HIBERNATE) {
			return new HibTipoDao(sessionFactory);
		} else {
			return null;
		}
	}

	/**
	 * Proporciona el dao correspondiente para acceder a los datos de los libros
	 * 
	 * @return
	 */
	public static LibroDao getLibroDao() {
		if (dataAccessType == HIBERNATE) {
			return new HibLibroDao(sessionFactory);
		} else {
			return null;
		}
	}

	/**
	 * Proporciona el dao correspondiente para acceder a los datos de las
	 * ubicaciones
	 * 
	 * @return
	 */
	public static UbicacionDao getUbicacionDao() {
		if (dataAccessType == HIBERNATE) {
			return new HibUbicacionDao(sessionFactory);
		} else {
			return null;
		}
	}

	/**
	 * Proporciona el dao correspondiente para acceder a los datos de las
	 * editoriales
	 * 
	 * @return
	 */
	public static EditorialDao getEditorialDao() {
		if (dataAccessType == HIBERNATE) {
			return new HibEditorialDao(sessionFactory);
		} else {
			return null;
		}
	}

	/**
	 * Proporciona el dao correspondiente para acceder a los datos de las
	 * colecciones
	 * 
	 * @return
	 */
	public static HibColeccionDao getColeccionDao() {
		if (dataAccessType == HIBERNATE) {
			return new HibColeccionDao(sessionFactory);
		} else {
			return null;
		}
	}

	/**
	 * Proporciona el dao correspondiente para acceder a los datos de los
	 * traductores
	 * 
	 * @return
	 */
	public static HibTraductorDao getTraductorDao() {
		if (dataAccessType == HIBERNATE) {
			return new HibTraductorDao(sessionFactory);
		} else {
			return null;
		}
	}
}
