package control.fxml;

import control.AuftragSession;
import control.KundeSession;
import control.LieferantSession;
import control.ProduktSession;
import control.fxml.dataStructures.AizRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Viktor on 28.05.2015.
 */
public class AuftragDetailsController extends AizController {

    private final List<Lieferant> lieferanten = LieferantSession.getAllLieferanten();
    @FXML
    ChoiceBox<String> lieferantenChoiceBox;
    private List<String> kList;
    private Auftrag toEdit;
    private List<Produkt> produkte;
    @FXML
    private ChoiceBox<String> kundeField;
    @FXML
    private TableColumn<AizRow, String> preisColumn;
    @FXML
    private TextField totalPriceField;
    @FXML
    private TableView<Produkt> produkteTable;
    @FXML
    private TableColumn<Produkt, Number> suchePNrColumn;
    @FXML
    private TableColumn<Produkt, String> sucheBezColumn;
    @FXML
    private TableColumn<Produkt, Image> sucheBildColumn;
    @FXML
    private TableColumn<Produkt, Number> sucheVkPreisColumn;
    @FXML
    private TextField filterField;

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

        suchePNrColumn.setCellValueFactory(cellData -> cellData.getValue().getProduktNrProperty());
        sucheBezColumn.setCellValueFactory(cellData -> cellData.getValue().getBezProperty());
        sucheBildColumn.setCellValueFactory(cellData -> cellData.getValue().getImageProperty());
        sucheBildColumn.setCellFactory(imageCellFactory);
        sucheVkPreisColumn.setCellValueFactory(cellData -> cellData.getValue().getVkPreisProperty());

        produkte = ProduktSession.getAllProdukte();
        produkteTable.setItems(FXCollections.observableArrayList(produkte));

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts();
        });

        ObservableList<String> lieferantenChoice = FXCollections.observableArrayList();
        lieferantenChoice.add("Alle");
        lieferanten.stream().forEach(l -> lieferantenChoice.add(l.getName()));
        lieferantenChoiceBox.setItems(lieferantenChoice);
        lieferantenChoiceBox.setValue("Alle");
        lieferantenChoiceBox.valueProperty().addListener((observable -> {
            filterProducts();
        }));
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

    @FXML
    private void handleTransfer() {
        int selectedIndex = produkteTable.getSelectionModel().getSelectedIndex();
        Produkt p = produkteTable.getItems().get(selectedIndex);
        for (int i = 0; i < aizTable.getItems().size(); i++) {
            AizRow ar = aizTable.getItems().get(i);
            if (ar.produktNr.get() == 0) {
                ar.produktNr.set(p.getProduktNr());
                checkNumbers();
                if (i + 1 == aizTable.getItems().size())
                    handleNewRow();
                aizTable.getColumns().get(0).setVisible(false);
                aizTable.getColumns().get(0).setVisible(true);
                break;
            }
        }
    }

    public void filterProducts() {
        String s = filterField.getText().toLowerCase();
        String lieferant = lieferantenChoiceBox.getValue();
        List<Produkt> filter1 = produkte;
        if (!lieferant.equals("Alle")) {
            Collection<Produkt> produkte = LieferantSession.getLieferantByName(lieferant).getProdukte();
            List<Integer> produktNummern = new LinkedList<>();
            produkte.stream().forEach(p -> produktNummern.add(p.getProduktNr()));
            filter1 = produkte.stream().filter(ci -> produktNummern.contains(ci.getProduktNr())).collect(Collectors.toList());
        }

        if (s.length() > 0) {
            List<Produkt> filteredProducts = filter1.stream().filter(p -> p.getBez().toLowerCase().contains(s) || ("" + p.getProduktNr()).contains(s) || p.getKateogrie().name().toLowerCase().contains(s)).collect(Collectors.toList());
            produkteTable.setItems(FXCollections.observableArrayList(filteredProducts));
        } else
            produkteTable.setItems(FXCollections.observableArrayList(filter1));
    }
}
