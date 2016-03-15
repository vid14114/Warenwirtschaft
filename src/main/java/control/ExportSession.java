package control;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Created by Viktor on 24.02.2016.
 */
public class ExportSession {
    public static void exportDatabase(String filename) {
        Session session = MyConfig.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.createSQLQuery("SCRIPT DROP TO '" + filename + "' ").list();
        t.commit();
        session.close();
    }
}
