package control.fxml;

import control.AuftragSession;
import control.KundeSession;
import control.ProduktSession;
import control.fxml.dataStructures.AizRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import model.Auftrag;
import model.Kunde;
import model.Produkt;
import model.Produktmenge;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Viktor on 28.05.2015.
 */
public class AuftragDetailsController extends AizController {

    private List<String> kList;
    private Auftrag toEdit;

    @FXML
    private ChoiceBox<String> kundeField;

    @FXML
    private TableColumn<AizRow, String> preisColumn;
    @FXML
    private TextField totalPriceField;

    @FXML
    private void initialize() {
        initializeBasic();
        kList = KundeSession.getAllKundenNamen();
        kundeField.setItems(FXCollections.observableArrayList(kList));
        kundeField.setValue(FXCollections.observableArrayList(kList).get(0));

        mengeColumn.setOnEditCommit(
                event -> {
                    AizRow e = event.getRowValue();
                    e.menge.set(event.getNewValue());
                    checkNumbers();
                }
        );

        preisColumn.setCellValueFactory(cellData -> cellData.getValue().preis);
        preisColumn.setCellFactory(vCellFactory);

        if (toEdit == null)
            for (int i = 0; i < 8; i++)
                handleNewRow();
    }

    public void populateData(Iterable<Produktmenge> la, Auftrag a) {
        data.remove(0, 8);
        for (Produktmenge p : la) {
            AizRow e = new AizRow();
            e.produktNr.set(p.getProdukt().getProduktNr());
            e.menge.set(String.valueOf(p.getMenge()));
            data.add(e);
        }
        toEdit = a;
        LocalDate ldt = a.getErstellung();
        populateBasic2(ldt);
        kundeField.setValue(a.getKunde().getName());
    }

    @FXML
    private void handleSave() {
        if (toEdit != null) {
            List<Kunde> lk = KundeSession.getAllKunden();
            for (Kunde k : lk) {
                if (k.getAuftraege().contains(toEdit)) {
                    k.getAuftraege().remove(toEdit);
                    KundeSession.saveKunde(k);
                    break;
                }
            }
            AuftragSession.removeAuftrag(toEdit);
        }
        checkNumbers();

        Auftrag a = new Auftrag();
        LocalDate ld = LocalDate.parse(datumField.getText(), dtf);
        a.setErstellung(ld);
        Collection<Produktmenge> pm = new ArrayList<>();
        aizTable.getItems().stream().filter(e -> e.produktNr.get() != 0 && !e.menge.get().isEmpty() && !e.bezeichnung.get().isEmpty()).forEach(e -> {
            Produktmenge p = new Produktmenge();
            p.setProdukt(ProduktSession.getProdukt(e.produktNr.get()));
            p.setMenge(Integer.valueOf(e.menge.get()));
            pm.add(p);
        });
        a.setProdukte(pm);
        Kunde k = KundeSession.getKundeByName(kList.get(kundeField.getSelectionModel().getSelectedIndex()));
        a.setKunde(k);
        AuftragSession.saveAuftrag(a);
        k.getAuftraege().add(a);
        KundeSession.saveKunde(k);
        a.getProdukte().stream().forEach(pmenge -> ProduktSession.saveProdukt(pmenge.getProdukt()));
        dialogStage.close();
        saveClicked = true;
    }

    @FXML
    void checkNumbers() {
        super.checkNumbers();
        float total = 0;
        for (AizRow e : aizTable.getItems()) {
            if (!e.menge.get().isEmpty() && e.produktNr.get() != 0) {
                Produkt p = ProduktSession.getProdukt(e.produktNr.get());
                double preis = Integer.valueOf(e.menge.get()) * p.getVkPreis();
                preis = Math.round(preis * 100.0) / 100.0;
                total += preis;
                e.preis.set("\u20ac " + preis);
            }
        }
        totalPriceField.setText("\u20ac " + total);
    }

}
