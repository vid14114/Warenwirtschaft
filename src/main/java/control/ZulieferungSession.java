package control;

import model.Zulieferung;
import org.hibernate.Session;

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
}
