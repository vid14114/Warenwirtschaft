package control;

import model.Inventur;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Created by Viktor on 16.05.2015.
 */
public enum InventurSession {
    ;

    InventurSession() {
    }

    public static List<Inventur> getAllInventuren() {
        Session session = MyConfig.getSessionFactory().openSession();
        List<Inventur> result = session.createQuery("from Inventur").list();
        session.close();
        return result;
    }

    public static Inventur getInventur(int id) {
        Session session = MyConfig.getSessionFactory().openSession();
        Inventur i = (Inventur) session.createQuery("from Inventur where id = " + id).uniqueResult();
        session.close();
        return i;
    }

    public static void saveInventur(Inventur i) {
        Session session = MyConfig.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.saveOrUpdate(i);
        t.commit();
        session.close();
    }

    public static void removeInventur(Inventur i) {
        Session session = MyConfig.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.delete(i);
        t.commit();
        session.close();
    }
}
