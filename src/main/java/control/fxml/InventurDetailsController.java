package control.fxml;

import control.InventurSession;
import control.ProduktSession;
import javafx.fxml.FXML;
import model.Inventur;
import model.Produktmenge;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Viktor on 09.06.2015.
 */
public class InventurDetailsController extends AizController {
    private Inventur toEdit;

    @FXML
    private void initialize() {
        initializeBasic();

        if (toEdit == null)
            for (int i = 0; i < 8; i++)
                handleNewRow();
    }

    public void populateData(Iterable<Produktmenge> la, Inventur i) {
        populateBasic1(la);
        toEdit = i;
        LocalDate ldt = i.getDatum();
        populateBasic2(ldt);
    }

    @FXML
    private void handleSave() {
        if (toEdit != null) {
            InventurSession.removeInventur(toEdit);
        }
        checkNumbers();
        Inventur i = new Inventur();
        LocalDate ld = LocalDate.parse(datumField.getText(), dtf);
        i.setDatum(ld);
        Collection<Produktmenge> pm = new ArrayList<>();
        aizTable.getItems().stream().filter(e -> e.produktNr.get() != 0 && !e.menge.get().isEmpty() && !e.bezeichnung.get().isEmpty()).forEach(e -> {
            Produktmenge p = new Produktmenge();
            p.setProdukt(ProduktSession.getProdukt(e.produktNr.get()));
            p.setMenge(Integer.valueOf(e.menge.get()));
            pm.add(p);
        });
        i.setProdukte(pm);
        InventurSession.saveInventur(i);
        dialogStage.close();
        saveClicked = true;
    }
}
