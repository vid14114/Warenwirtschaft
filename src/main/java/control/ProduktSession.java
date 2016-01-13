package control;

import com.esotericsoftware.minlog.Log;
import model.Produkt;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Created by Viktor on 16.05.2015.
 */
public enum ProduktSession {
    ;

    ProduktSession() {
    }

    public static List<Produkt> getAllProdukte() {
        Session session = MyConfig.getSessionFactory().openSession();
        List<Produkt> result = session.createQuery("from Produkt").list();
        session.close();
        return result;
    }

    public static Produkt getProdukt(int produktNr) {
        Session session = MyConfig.getSessionFactory().openSession();
        Produkt p = (Produkt) session.createQuery("from Produkt where produktNr = " + produktNr).uniqueResult();
        session.close();
        return p;
    }

    public static void saveProdukt(Produkt p) {
        Session session = MyConfig.getSessionFactory().openSession();
        p.calculateUmsatz();
        Transaction t = session.beginTransaction();
        session.saveOrUpdate(p);
        t.commit();
        session.close();
        Log.info("Saving " + p);
    }

    public static void removeProdukt(Produkt p) {
        Session session = MyConfig.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.delete(p);
        t.commit();
        session.close();
        Log.info("Removing " + p);
    }
}
