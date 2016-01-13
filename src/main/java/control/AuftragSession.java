package control;

import com.esotericsoftware.minlog.Log;
import model.Auftrag;
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

    public static void saveAuftrag(Auftrag a) {
        Session session = MyConfig.getSessionFactory().openSession();
        a.calculateWert();
        Transaction t = session.beginTransaction();
        session.saveOrUpdate(a);
        t.commit();
        session.close();
        Log.info("Saving " + a);
    }

    public static void removeAuftrag(Auftrag a) {
        Session session = MyConfig.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.delete(a);
        t.commit();
        session.close();
        Log.info("Removing " + a);
    }
}
