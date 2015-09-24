package control;

import model.Kunde;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Created by Viktor on 16.05.2015.
 */
public enum KundeSession {
    ;

    KundeSession() {
    }

    public static List<Kunde> getAllKunden() {
        Session session = MyConfig.getSessionFactory().openSession();
        List<Kunde> result = session.createQuery("from Kunde").list();
        session.close();
        return result;
    }

    public static List<String> getAllKundenNamen() {
        Session session = MyConfig.getSessionFactory().openSession();
        List<String> result = session.createQuery("select name from Kunde").list();
        session.close();
        return result;
    }

    public static Kunde getKundeByName(String name) {
        Session session = MyConfig.getSessionFactory().openSession();
        Kunde k = (Kunde) session.createQuery("from Kunde where name = '" + name + '\'').uniqueResult();
        session.close();
        return k;
    }

    public static Kunde getKunde(int id) {
        Session session = MyConfig.getSessionFactory().openSession();
        Kunde k = (Kunde) session.createQuery("from Kunde where kdNr = " + id).uniqueResult();
        session.close();
        return k;
    }

    public static void saveKunde(Kunde k) {
        Session session = MyConfig.getSessionFactory().openSession();
        k.calculateUmsatz();
        Transaction t = session.beginTransaction();
        session.saveOrUpdate(k);
        t.commit();
        session.close();
    }

    public static void removeKunde(Kunde k) {
        Session session = MyConfig.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.delete(k);
        t.commit();
        session.close();
    }
}
