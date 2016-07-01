package control.fxml;

import control.AuftragSession;
import control.InventurSession;
import control.LieferantSession;
import control.fxml.dataStructures.BestellempfehlungRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.util.Callback;
import model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Viktor on 11.06.2015.
 */
public class BestellempfehlungController {
    private final Callback imageCellFactory = new ImageCellFactory();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final Map<Integer, BestellempfehlungRow> produktInfos = new TreeMap<>();
    private final ObservableList<BestellempfehlungRow> dataShow = FXCollections.observableArrayList();
    private final List<Lieferant> lieferanten = LieferantSession.getAllLieferanten();
    private List<BestellempfehlungRow> dataAll;

    @FXML
    private TextField vorlaufzeit;
    @FXML
    private ChoiceBox<String> lieferantChoiceBox;
    @FXML
    private TableView<BestellempfehlungRow> pInfoTable;
    @FXML
    private TableColumn<BestellempfehlungRow, Number> pNrColumn;
    @FXML
    private TableColumn<BestellempfehlungRow, String> bezColumn;
    @FXML
    private TableColumn<BestellempfehlungRow, String> kategorieColumn;
    @FXML
    private TableColumn<BestellempfehlungRow, Image> bildColumn;
    @FXML
    private TableColumn<BestellempfehlungRow, String> vorratswochenColumn;
    @FXML
    private TableColumn<BestellempfehlungRow, Number> lagerstandColumn;
    @FXML
    private TableColumn<BestellempfehlungRow, Number> daysToEmptyColumn;
    @FXML
    private TableColumn<BestellempfehlungRow, Number> mengeColumn;
    @FXML
    private TableColumn<BestellempfehlungRow, String> bestellenColumn;
    @FXML
    private RadioButton durchschnitt;
    @FXML
    private RadioButton vorjahr;
    @FXML
    private TextField vorratswochenTextField;
    @FXML
    private TextField filterField;

    @FXML
    private void initialize() {
        ObservableList<String> lieferantenChoice = FXCollections.observableArrayList();
        lieferantenChoice.add("Alle");
        lieferanten.stream().forEach(l -> lieferantenChoice.add(l.getName()));
        lieferantChoiceBox.setItems(lieferantenChoice);
        lieferantChoiceBox.setValue("Alle");
        vorlaufzeit.setText("" + 0);
        calculateProduktinfos(0);

        final ToggleGroup berechnungsmethode = new ToggleGroup();
        durchschnitt.setToggleGroup(berechnungsmethode);
        vorjahr.setToggleGroup(berechnungsmethode);
        durchschnitt.setSelected(true);

        vorlaufzeit.textProperty().addListener(((observable1, oldValue, newValue) -> {
            try {
                int v = Integer.valueOf(newValue);
                calculateProduktinfos(v);
                recalculate(getBerechnungsmethode());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }));

        lieferantChoiceBox.valueProperty().addListener((observable -> {
            recalculate(getBerechnungsmethode());
        }));

        berechnungsmethode.selectedToggleProperty().addListener(observable1 -> recalculate(getBerechnungsmethode()));

        dataAll = FXCollections.observableArrayList(produktInfos.values());

        pNrColumn.setCellValueFactory(cellData -> cellData.getValue().produktNr);
        bezColumn.setCellValueFactory(cellData -> cellData.getValue().bezeichnung);
        kategorieColumn.setCellValueFactory(cellData -> cellData.getValue().kategorie);
        bildColumn.setCellValueFactory(cellData -> cellData.getValue().bild);
        bildColumn.setCellFactory(imageCellFactory);
        vorratswochenColumn.setCellValueFactory(cellData -> cellData.getValue().vorratswochenProperty);
        lagerstandColumn.setCellValueFactory(cellData -> cellData.getValue().lagerstand);
        daysToEmptyColumn.setCellValueFactory(cellData -> cellData.getValue().tageBisLeer);
        mengeColumn.setCellValueFactory(cellData -> cellData.getValue().bestellmenge);
        bestellenColumn.setCellValueFactory(cellData -> cellData.getValue().bestelldatum);
        pInfoTable.setItems(dataShow);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts(newValue);
        });

        vorratswochenTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            changeVorratswochen(Integer.valueOf(newValue));
        }));

        recalculate(getBerechnungsmethode());
    }

    private void changeVorratswochen(int vorratswochen) {
        dataAll.stream().forEach(b -> {
            b.vorratswochen = vorratswochen;
            b.vorratswochenProperty.set("" + b.vorratswochen);
        });
        recalculate(getBerechnungsmethode());
    }

    private void filterProducts(String s) {
        if (s.length() > 0) {
            List<BestellempfehlungRow> filteredProducts = dataShow.stream().filter(p -> p.bezeichnung.get().contains(s) || ("" + p.produktNr.get()).contains(s)).collect(Collectors.toList());
            pInfoTable.setItems(FXCollections.observableArrayList(filteredProducts));
        } else
            pInfoTable.setItems(dataShow);
    }

    private boolean getBerechnungsmethode() {
        return !durchschnitt.isSelected();
    }

    private Map<Integer, Integer> getLastYearSoldData(LocalDate from, LocalDate to) {
        List<Auftrag> auftraege = AuftragSession.getAllAuftraege();
        LocalDate fromLast = from.minusYears(1);
        LocalDate toLast = to.minusYears(1);
        int modificator = 1;
        if (toLast.isAfter(LocalDate.now())) {
            modificator = from.until(to).getYears();
        }
        List<Auftrag> auftraegeInZeitraum = new ArrayList<>();
        auftraege.stream().filter(a -> a.getErstellung().isAfter(fromLast) && a.getErstellung().isBefore(toLast)).forEach(auftraegeInZeitraum::add);
        Map<Integer, Integer> data = new HashMap<>();
        for (Auftrag a : auftraegeInZeitraum) {
            for (Produktmenge p : a.getProdukte()) {
                int key = p.getProdukt().getProduktNr();
                if (!data.containsKey(key)) {
                    data.put(key, p.getMenge() * modificator);
                } else {
                    data.put(key, data.get(key) + (p.getMenge() * modificator));
                }
            }
        }
        return data;
    }

    private void calculateProduktinfos(int vorlaufzeit) {
        for (BestellempfehlungRow ci : produktInfos.values()) {
            ci.soldTotal = 0;
            ci.soldAfterLastInventur = 0;
        }
        List<Auftrag> auftraege = AuftragSession.getAllAuftraege();
        List<Inventur> inventuren = InventurSession.getAllInventuren();

        inventuren.sort((o1, o2) -> o2.getDatum().compareTo(o1.getDatum()));

        for (Inventur i : inventuren) {
            for (Produktmenge pm : i.getProdukte()) {
                int key = pm.getProdukt().getProduktNr();
                if (!produktInfos.containsKey(key))
                    produktInfos.put(key, new BestellempfehlungRow());
                BestellempfehlungRow ci = produktInfos.get(key);
                ci.produktNr.set(key);
                ci.bezeichnung.set(pm.getProdukt().getBez());
                ci.kategorie.set(pm.getProdukt().getKateogrie().name());
                ci.bild = pm.getProdukt().getImageProperty();
                ci.vorratswochen = pm.getProdukt().getVorratswochen();
                ci.vorratswochenProperty.set("" + ci.vorratswochen);
                if (i.getDatum().isAfter(ci.lastInventur)) {
                    ci.lastInventur = i.getDatum();     //aktuellste Inventur
                    ci.inLastInventur = pm.getMenge();   //Lagerstand der aktuellsten Inventur
                }
            }
        }

        for (Auftrag a : auftraege) {
            for (Produktmenge pm : a.getProdukte()) {
                int key = pm.getProdukt().getProduktNr();
                if (produktInfos.containsKey(key)) {
                    BestellempfehlungRow ci = produktInfos.get(key);
                    ci.soldTotal += pm.getMenge();
                    if (a.getErstellung().isBefore(ci.firstSold))
                        ci.firstSold = a.getErstellung();
                    if (a.getErstellung().isAfter(ci.lastInventur))
                        ci.soldAfterLastInventur += pm.getMenge();
                }
            }
        }

        Collection<BestellempfehlungRow> toRemove = new ArrayList<>();
        for (BestellempfehlungRow ci : produktInfos.values()) {
            if (!ci.firstSold.equals(LocalDate.MAX)) {
                long days = ChronoUnit.DAYS.between(ci.firstSold, LocalDate.now());
                ci.dailyNeed = (double) ci.soldTotal / (double) days;
                long actualStock = ci.inLastInventur - ci.soldAfterLastInventur - Math.round(vorlaufzeit * ci.dailyNeed);
                ci.lagerstand.set(actualStock);
                if (days != 0) {
                    int daysToEmpty = (int) (actualStock / ci.dailyNeed);
                    ci.tageBisLeer.set(daysToEmpty);
                } else
                    toRemove.add(ci);
            } else
                toRemove.add(ci);
        }

        toRemove.stream().forEach(ci -> produktInfos.values().remove(ci));

        Collection<Lieferant> lieferanten = LieferantSession.getAllLieferanten();
        for (Lieferant l : lieferanten) {
            l.getProdukte().stream().filter(p -> produktInfos.containsKey(p.getProduktNr())).forEach(p -> produktInfos.get(p.getProduktNr()).lieferzeit = l.getLieferzeit());
            for (Produkt p : l.getProdukte()) {
                int key = p.getProduktNr();
                if (produktInfos.containsKey(key)) {
                    BestellempfehlungRow ci = produktInfos.get(key);
                    LocalDate now = LocalDate.now();
                    LocalDate bestellen = now.plusDays(Integer.valueOf(ci.tageBisLeer.get()) - ci.lieferzeit);
                    if (bestellen.isBefore(now) || bestellen.isEqual(now))
                        ci.bestelldatum.set("SOFORT!");
                    else
                        ci.bestelldatum.set(bestellen.format(dtf));
                }
            }
        }
    }

    private void recalculate(boolean neueBerechnungAktiv) {
        Map<Integer, Integer> lastYear = null;
        int maxWochen = dataAll.stream().mapToInt(a -> a.vorratswochen).max().getAsInt();
        if (neueBerechnungAktiv)
            lastYear = getLastYearSoldData(LocalDate.now(), LocalDate.now().plusWeeks(maxWochen));
        dataShow.clear();
        String lieferant = lieferantChoiceBox.getValue();
        if (lieferant.equals("Alle"))
            dataShow.addAll(dataAll);
        else {
            Collection<Produkt> produkte = LieferantSession.getLieferantByName(lieferant).getProdukte();
            List<Integer> produktNummern = new LinkedList<>();
            produkte.stream().forEach(p -> produktNummern.add(p.getProduktNr()));
            dataAll.stream().filter(ci -> produktNummern.contains(ci.produktNr.get())).forEach(dataShow::add);
        }
        for (BestellempfehlungRow ci : dataShow) {
            long menge;
            if (neueBerechnungAktiv && lastYear.containsKey(ci.produktNr.get())) {
                menge = (lastYear.get(ci.produktNr.get()) - (ci.inLastInventur - ci.soldAfterLastInventur)) * ci.vorratswochen / maxWochen;
            } else {
                menge = Math.round(ci.dailyNeed * ci.vorratswochen * 7 - (ci.inLastInventur - ci.soldAfterLastInventur)); //jahre: 365, wochen: 7
            }
            ci.bestellmenge.set(Math.max(menge, 0));
        }
        FXCollections.sort(dataShow, (o1, o2) -> {
            int menge1 = Integer.valueOf(o1.tageBisLeer.get());
            int menge2 = Integer.valueOf(o2.tageBisLeer.get());
            return menge1 - menge2;
        });
        pInfoTable.refresh();
    }

    public Map<Integer, BestellempfehlungRow> calculateForAlarm() {
        calculateProduktinfos(0);
        return produktInfos;
    }
}
