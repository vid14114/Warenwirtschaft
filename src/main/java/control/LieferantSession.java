package control;

import model.Lieferant;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Created by Viktor on 08.09.2015.
 */
public class LieferantSession {

    public static List<Lieferant> getAllLieferanten() {
        Session session = MyConfig.getSessionFactory().openSession();
        List<Lieferant> result = session.createQuery("from Lieferant").list();
        session.close();
        return result;
    }

    public static Lieferant getLieferantByName(String name) {
        Session session = MyConfig.getSessionFactory().openSession();
        Lieferant l = (Lieferant) session.createQuery("from Lieferant where name = '" + name + '\'').uniqueResult();
        session.close();
        return l;
    }

    public static void saveLieferant(Lieferant l) {
        Session session = MyConfig.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.saveOrUpdate(l);
        t.commit();
        session.close();
    }

    public static void removeLieferant(Lieferant l) {
        Session session = MyConfig.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.delete(l);
        t.commit();
        session.close();
    }
}
