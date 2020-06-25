/**
 * Copyright (c) 2014-2015, Javier Vaquero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * required by applicable law or agreed to in writing, software
 * under the License is distributed on an "AS IS" BASIS,
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * the License for the specific language governing permissions and
 * under the License.
 */
package es.magDevs.myLibrary.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.magDevs.myLibrary.model.dao.AutorDao;
import es.magDevs.myLibrary.model.dao.ColeccionDao;
import es.magDevs.myLibrary.model.dao.EditorialDao;
import es.magDevs.myLibrary.model.dao.LibroDao;
import es.magDevs.myLibrary.model.dao.PendienteDao;
import es.magDevs.myLibrary.model.dao.PrestamoDao;
import es.magDevs.myLibrary.model.dao.TipoDao;
import es.magDevs.myLibrary.model.dao.TraductorDao;
import es.magDevs.myLibrary.model.dao.UbicacionDao;
import es.magDevs.myLibrary.model.dao.UsuarioDao;
import es.magDevs.myLibrary.model.dao.hib.HibAutorDao;
import es.magDevs.myLibrary.model.dao.hib.HibColeccionDao;
import es.magDevs.myLibrary.model.dao.hib.HibEditorialDao;
import es.magDevs.myLibrary.model.dao.hib.HibLibroDao;
import es.magDevs.myLibrary.model.dao.hib.HibPendienteDao;
import es.magDevs.myLibrary.model.dao.hib.HibPrestamoDao;
import es.magDevs.myLibrary.model.dao.hib.HibTipoDao;
import es.magDevs.myLibrary.model.dao.hib.HibTraductorDao;
import es.magDevs.myLibrary.model.dao.hib.HibUbicacionDao;
import es.magDevs.myLibrary.model.dao.hib.HibUsuarioDao;

/**
 * Factoria para instanciar los DAOs
 * 
 * @author javier.vaquero
 * 
 */
public class DaoFactory {
	private static Boolean init = Boolean.FALSE;
	private static final int HIBERNATE = 1;
	
	private static final String ENVVAR_USER = "MYLIB_DB_USER";
	private static final String ENVVAR_PASS = "MYLIB_DB_PASSWORD";
	private static final String ENVVAR_URL = "MYLIB_DB_URL";

	private static Logger log = LoggerFactory.getLogger(DaoFactory.class);
	private static int dataAccessType;
	private static Properties conf;

	public static void init() {
		synchronized (init) {
			if (!init) {
				// Obtenemos la configuracion
				conf = new Properties();
				try {
					// Obtemos el properties de la configuracion
					conf.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration.properties"));
					// Obtemos el properties de credenciales
					InputStream credentialsProperties = Thread.currentThread().getContextClassLoader().getResourceAsStream("credentials.properties");
					if (credentialsProperties != null) {
						conf.load(credentialsProperties);
					} else {
						// Si no tenemos properties, intentamos sacar las credenciasles de la variables de entorno
						String user = System.getenv(ENVVAR_USER);
						String pass = System.getenv(ENVVAR_PASS);
						String url = System.getenv(ENVVAR_URL);
						if(user == null || pass == null) {
							throw new ExceptionInInitializerError("No se han configurado los datos de conexion a la base de datos");
						}
						conf.setProperty("username", user);
						conf.setProperty("password", pass);
						conf.setProperty("url", url);
					}
				} catch (IOException e) {
					log.error("No se ha podido obtener el fichero de configuracion.", e);
					throw new ExceptionInInitializerError(e);
				}
				String dataAccess = conf.getProperty("dataAccess");
				if ("hibernate".equals(dataAccess)) {
					dataAccessType = HIBERNATE;
				}
				init = true;
			}
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
				// Cargamos las credenciales del fichero correspondiente
				setHibernateProperty(hibernateConfiguration, "hibernate.connection.password", conf.getProperty("password"));
				setHibernateProperty(hibernateConfiguration, "hibernate.connection.username", conf.getProperty("username"));
				setHibernateProperty(hibernateConfiguration, "hibernate.connection.url", conf.getProperty("url"));
				// Creamos una factorias de sesiones
				ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
						.applySettings(hibernateConfiguration.getProperties()).build();
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
	 * Fija una propiedad de configuracion de Hibernate
	 * 
	 * @param configuration de Hibernate
	 * @param key
	 *            clave de la propiedad de configuracion de Hibernate
	 * @param value
	 *            valor de la propiedad de configuracion de Hibernate, si es
	 *            <code>null</code> no se realizara ninguna operacion
	 */
	private static void setHibernateProperty(Configuration configuration, String key, String value) {
		if(value != null) {
			configuration.getProperties().setProperty(key, value);
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
	public static ColeccionDao getColeccionDao() {
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
	public static TraductorDao getTraductorDao() {
		if (dataAccessType == HIBERNATE) {
			return new HibTraductorDao(sessionFactory);
		} else {
			return null;
		}
	}

	/**
	 * Proporciona el dao correspondiente para acceder a los datos de los
	 * usuarios
	 * 
	 * @return
	 */
	public static UsuarioDao getUsuarioDao() {
		if (dataAccessType == 1) {
			return new HibUsuarioDao(sessionFactory);
		}
		return null;
	}

	/**
	 * Proporciona el dao correspondiente para acceder a los datos de los
	 * prestamos
	 * 
	 * @return
	 */
	public static PrestamoDao getPrestamoDao() {
		if (dataAccessType == 1) {
			return new HibPrestamoDao(sessionFactory);
		}
		return null;
	}

	/**
	 * Proporciona el dao correspondiente para acceder a los datos de los
	 * libros pendientes
	 * 
	 * @return
	 */
	public static PendienteDao getPendienteDao() {
		if (dataAccessType == 1) {
			return new HibPendienteDao(sessionFactory);
		}
		return null;
	}
}
