package control;

import model.Auftrag;
import model.Inventur;
import model.Produktmenge;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Created by Viktor on 16.05.2015.
 */
public enum ProduktmengeSession {
    ;

    ProduktmengeSession() {
    }

    public static List<Produktmenge> getProdukteInAuftrag(Auftrag a) {
        Session session = MyConfig.getSessionFactory().openSession();
        List<Produktmenge> result = session.createQuery("select produkte from Auftrag where auftrNr = " + a.getAuftrNr()).list();
        session.close();
        return result;
    }

    public static List<Produktmenge> getProdukteInInventur(Inventur i) {
        Session session = MyConfig.getSessionFactory().openSession();
        List<Produktmenge> result = session.createQuery("select produkte from Inventur where id = " + i.getId()).list();
        session.close();
        return result;
    }

    public static void removeProduktmenge(Produktmenge p) {
        Session session = MyConfig.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.delete(p);
        t.commit();
        session.close();
    }

    public static void saveProduktmenge(Produktmenge p) {
        Session session = MyConfig.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.saveOrUpdate(p);
        t.commit();
        session.close();
    }
}
