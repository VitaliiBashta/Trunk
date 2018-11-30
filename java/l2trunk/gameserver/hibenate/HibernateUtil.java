package l2trunk.gameserver.hibenate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

public class HibernateUtil {

    private static SessionFactory sessionFactory ;

    static {
        Configuration configuration = new Configuration();
//        configuration.configure(new File("hibernate.cfg.xml"));
        configuration.configure();
        configuration.setProperty("autocommit", "true");
        sessionFactory = configuration.buildSessionFactory();
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }
}
