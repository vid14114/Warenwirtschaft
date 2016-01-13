package control;

import com.esotericsoftware.minlog.Log;
import model.Zulieferung;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Created by Viktor on 13.10.2015.
 */
public class ZulieferungSession {

    public static List<Zulieferung> getAllZulieferungen() {
        Session session = MyConfig.getSessionFactory().openSession();
        List<Zulieferung> result = session.createQuery("from Zulieferung").list();
        session.close();
        return result;
    }

    public static void saveZulieferung(Zulieferung z) {
        Session session = MyConfig.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.saveOrUpdate(z);
        t.commit();
        session.close();
        Log.info("Saving " + z);
    }

    public static void removeZulieferung(Zulieferung z) {
        Session session = MyConfig.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.delete(z);
        t.commit();
        session.close();
        Log.info("Removing " + z);
    }
}
