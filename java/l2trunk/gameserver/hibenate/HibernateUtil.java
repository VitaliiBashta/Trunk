package l2trunk.gameserver.hibenate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

//    static {
//
//    }

    public static Session getSession() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
//        configuration.configure();
            configuration.setProperty("autocommit", "true");
            sessionFactory = configuration.buildSessionFactory();
        }
        return sessionFactory.openSession();
    }
}
