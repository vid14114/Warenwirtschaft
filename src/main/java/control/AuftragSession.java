package control;

import model.Auftrag;
import model.Kunde;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Created by Viktor on 16.05.2015.
 */
public enum AuftragSession {
    ;

    AuftragSession() {
    }

    public static List<Auftrag> getAllAuftraege() {
        Session session = MyConfig.getSessionFactory().openSession();
        List<Auftrag> result = session.createQuery("from Auftrag").list();
        session.close();
        result.stream().filter(a -> a.getKunde() == null).forEach(Auftrag::findKunde);
        return result;
    }

    public static List<Auftrag> getAuftraegeByKunde(Kunde k) {
        Session session = MyConfig.getSessionFactory().openSession();
        List<Auftrag> result = (List<Auftrag>) session.createQuery("select auftraege from Kunde where kdNr = " + k.getKdNr()).list();
        session.close();
        return result;
    }

    public static Auftrag getAuftrag(int id) {
        Session session = MyConfig.getSessionFactory().openSession();
        Auftrag a = (Auftrag) session.createQuery("from Auftrag where auftrNr = " + id).uniqueResult();
        session.close();
        return a;
    }

    public static void saveAuftrag(Auftrag a) {
        Session session = MyConfig.getSessionFactory().openSession();
        a.calculateWert();
        Transaction t = session.beginTransaction();
        session.saveOrUpdate(a);
        t.commit();
        session.close();
    }

    public static void removeAuftrag(Auftrag a) {
        Session session = MyConfig.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.delete(a);
        t.commit();
        session.close();
    }
}
