package control.fxml;

import control.ProduktSession;
import control.ZulieferungSession;
import javafx.fxml.FXML;
import model.Produktmenge;
import model.Zulieferung;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Viktor on 14.10.2015.
 */
public class ZulieferungDetailsController extends AizController {
    private Zulieferung toEdit;

    @FXML
    private void initialize() {
        initializeBasic();

        if (toEdit == null)
            for (int i = 0; i < 8; i++)
                handleNewRow();
    }

    public void populateData(Iterable<Produktmenge> la, Zulieferung i) {
        populateBasic1(la);
        toEdit = i;
        LocalDate ldt = i.getErstellung();
        populateBasic2(ldt);
    }

    @FXML
    private void handleSave() {
        if (toEdit != null) {
            ZulieferungSession.removeZulieferung(toEdit);
        }
        checkNumbers();
        Zulieferung i = new Zulieferung();
        LocalDate ld = LocalDate.parse(datumField.getText(), dtf);
        i.setErstellung(ld);
        Collection<Produktmenge> pm = new ArrayList<>();
        aizTable.getItems().stream().filter(e -> e.produktNr.get() != 0 && !e.menge.get().isEmpty() && !e.bezeichnung.get().isEmpty()).forEach(e -> {
            Produktmenge p = new Produktmenge();
            p.setProdukt(ProduktSession.getProdukt(e.produktNr.get()));
            p.setMenge(Integer.valueOf(e.menge.get()));
            pm.add(p);
        });
        i.setProdukte(pm);
        ZulieferungSession.saveZulieferung(i);
        dialogStage.close();
        saveClicked = true;
    }
}
