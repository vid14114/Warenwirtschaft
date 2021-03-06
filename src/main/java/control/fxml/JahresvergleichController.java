package control.fxml;

import control.AuftragSession;
import control.KundeSession;
import control.ProduktSession;
import control.fxml.dataStructures.JahresvergleichRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.util.Callback;
import model.Auftrag;
import model.Kunde;
import model.Produkt;
import model.Produktmenge;

import java.time.LocalDate;
import java.util.*;


/**
 * Created by Viktor on 10.09.2015.
 */
public class JahresvergleichController {

    private final Map<Integer, JahresvergleichRow> produktInfos = new TreeMap<>();
    private final Callback imageCellFactory = new ImageCellFactory();
    private final ObservableList<JahresvergleichRow> dataShow = FXCollections.observableArrayList();
    private final LocalDate now = LocalDate.now();
    @FXML
    private ChoiceBox<String> kundenChoiceBox;

    @FXML
    private Label day1Label;
    @FXML
    private ChoiceBox<Number> year1ChoiceBox;
    @FXML
    private Label day2Label;
    @FXML
    private ChoiceBox<Number> year2ChoiceBox;

    @FXML
    private TableView<JahresvergleichRow> pInfoTable;
    @FXML
    private TableColumn<JahresvergleichRow, Number> pNrColumn;
    @FXML
    private TableColumn<JahresvergleichRow, String> bezColumn;
    @FXML
    private TableColumn<JahresvergleichRow, Image> bildColumn;
    @FXML
    private TableColumn<JahresvergleichRow, Number> s1Column;
    @FXML
    private TableColumn<JahresvergleichRow, Number> u1Column;
    @FXML
    private TableColumn<JahresvergleichRow, Number> s2Column;
    @FXML
    private TableColumn<JahresvergleichRow, Number> u2Column;

    @FXML
    private void initialize() {
        ObservableList<String> kunden = FXCollections.observableArrayList(KundeSession.getAllKundenNamen());
        kunden.add(0, "Alle");
        kundenChoiceBox.setItems(kunden);
        kundenChoiceBox.setValue("Alle");


        String dm = now.getDayOfMonth() + "." + now.getMonthValue() + ".";
        day1Label.setText("1.1. - " + dm);
        day2Label.setText("1.1. - " + dm);

        List<Auftrag> auftraege = AuftragSession.getAllAuftraege();
        auftraege.sort(Comparator.comparing(Auftrag::getErstellung));
        LocalDate oldestAuftrag = auftraege.get(0).getErstellung();

        ObservableList<Number> years = FXCollections.observableArrayList();
        for (int i = now.getYear(); i >= oldestAuftrag.getYear(); i--) {
            years.add(i);
        }
        year1ChoiceBox.setItems(years);
        year1ChoiceBox.setValue(years.get(1));
        year2ChoiceBox.setItems(years);
        year2ChoiceBox.setValue(years.get(0));

        calculate();

        pNrColumn.setCellValueFactory(cellData -> cellData.getValue().produktNr);
        bezColumn.setCellValueFactory(cellData -> cellData.getValue().bezeichnung);
        bildColumn.setCellValueFactory(cellData -> cellData.getValue().bild);
        bildColumn.setCellFactory(imageCellFactory);
        s1Column.setCellValueFactory(cellData -> cellData.getValue().stuckzahl1);
        u1Column.setCellValueFactory(cellData -> cellData.getValue().umsatz1);
        s2Column.setCellValueFactory(cellData -> cellData.getValue().stuckzahl2);
        u2Column.setCellValueFactory(cellData -> cellData.getValue().umsatz2);
        pInfoTable.setItems(dataShow);

        kundenChoiceBox.valueProperty().addListener(observable -> calculate());
        year1ChoiceBox.valueProperty().addListener(observable -> calculate());
        year2ChoiceBox.valueProperty().addListener(observable -> calculate());
    }

    private void calculate() {
        produktInfos.clear();
        dataShow.clear();
        int year1 = year1ChoiceBox.getValue().intValue();
        int year2 = year2ChoiceBox.getValue().intValue();
        LocalDate von1 = now.withYear(year1).withMonth(1).withDayOfMonth(1);
        LocalDate bis1 = now.withYear(year1);
        LocalDate von2 = now.withYear(year2).withMonth(1).withDayOfMonth(1);
        LocalDate bis2 = now.withYear(year2);

        List<Auftrag> auftraege = AuftragSession.getAllAuftraege();
        List<Auftrag> auftraegeInZeitraum1 = new ArrayList<>();
        List<Auftrag> auftraegeInZeitraum2 = new ArrayList<>();
        auftraege.stream().filter(a -> a.getErstellung().isAfter(von1) && a.getErstellung().isBefore(bis1)).forEach(auftraegeInZeitraum1::add);
        auftraege.stream().filter(a -> a.getErstellung().isAfter(von2) && a.getErstellung().isBefore(bis2)).forEach(auftraegeInZeitraum2::add);
        if (!kundenChoiceBox.getValue().equals("Alle")) {
            Kunde k = KundeSession.getKundeByName(kundenChoiceBox.getValue());
            List<Auftrag> aKunde1 = new ArrayList<>();
            List<Auftrag> aKunde2 = new ArrayList<>();
            auftraegeInZeitraum1.stream().filter(a -> a.getKunde().equals(k)).forEach(aKunde1::add);
            auftraegeInZeitraum2.stream().filter(a -> a.getKunde().equals(k)).forEach(aKunde2::add);
            auftraegeInZeitraum1 = aKunde1;
            auftraegeInZeitraum2 = aKunde2;
        }

        zeitraumIntoInfos(auftraegeInZeitraum1, 1);
        zeitraumIntoInfos(auftraegeInZeitraum2, 2);

        for (JahresvergleichRow ci : produktInfos.values()) {
            Produkt p = ProduktSession.getProdukt(ci.produktNr.get());
            ci.umsatz1.set(Math.round(ci.stuckzahl1.get() * p.getVkPreis() * 100.0) / 100.0);
            ci.umsatz2.set(Math.round(ci.stuckzahl2.get() * p.getVkPreis() * 100.0) / 100.0);
            dataShow.add(ci);
        }
    }

    private void zeitraumIntoInfos(List<Auftrag> zeitraum, int column) {
        for (Auftrag a : zeitraum) {
            for (Produktmenge pm : a.getProdukte()) {
                int key = pm.getProdukt().getProduktNr();
                if (!produktInfos.containsKey(key)) {
                    JahresvergleichRow ci = new JahresvergleichRow();
                    ci.produktNr.set(key);
                    ci.bezeichnung.set(pm.getProdukt().getKateogrie().name() + ": " + pm.getProdukt().getBez());
                    ci.bild = pm.getProdukt().getImageProperty();
                    produktInfos.put(key, ci);
                }
                JahresvergleichRow ci = produktInfos.get(key);
                if (column == 1)
                    ci.stuckzahl1.set(ci.stuckzahl1.get() + pm.getMenge());
                if (column == 2)
                    ci.stuckzahl2.set(ci.stuckzahl2.get() + pm.getMenge());
            }
        }
    }
}
