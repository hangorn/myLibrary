import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;

import junit.framework.TestCase;

public class TestHB extends TestCase {
	private SessionFactory sessionFactory;

	@Override
	protected void setUp() throws Exception {
		Properties conf = new Properties();
		conf.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("credentials.properties"));
		// A SessionFactory is set up once for an application!
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure("/hibernate/mysql.hibernate.cfg.xml") // configures settings from hibernate.cfg.xml
				.applySetting(AvailableSettings.PASS, conf.getProperty("password"))
				.build();
		try {
			sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			e.printStackTrace();
			// The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
			// so destroy it manually.
			StandardServiceRegistryBuilder.destroy( registry );
		}
	}

	@Override
	protected void tearDown() throws Exception {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	public void testBasicUsage() {
		// create a couple of events...
		Session session = sessionFactory.openSession();
		Long count = (Long) session.createQuery("SELECT count(*) FROM Libro").uniqueResult();
		System.out.println(count);
		session.close();
	}
}
