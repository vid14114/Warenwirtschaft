package control;

import model.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * Created by Viktor on 16.05.2015.
 */
public enum MyConfig {
    ;
    private static SessionFactory sessionFactory;
    private static String url;
    private static boolean ok;

    MyConfig() {
    }

    public static void setUrl(String u) {
        url = u;
    }

    public static void setOk() {
        ok = true;
    }

    public static SessionFactory getSessionFactory() {
        if (!ok) {
            Configuration configuration = new Configuration();
            configuration.configure()
                    .addAnnotatedClass(Auftrag.class)
                    .addAnnotatedClass(Kunde.class)
                    .addAnnotatedClass(Produkt.class)
                    .addAnnotatedClass(Produktmenge.class)
                    .addAnnotatedClass(Inventur.class)
                    .addAnnotatedClass(Lieferant.class)
                    .addAnnotatedClass(Zulieferung.class);
            if (url != null)
                configuration.setProperty("hibernate.connection.url", "jdbc:h2:file:" + url);
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        return sessionFactory;
    }
}
