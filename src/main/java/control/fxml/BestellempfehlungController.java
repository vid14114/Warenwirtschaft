package control.fxml;

import control.AuftragSession;
import control.InventurSession;
import control.LieferantSession;
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

/**
 * Created by Viktor on 11.06.2015.
 */
public class BestellempfehlungController {
    private final Callback imageCellFactory = new ImageCellFactory();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final Map<Integer, CalculatedInfo> produktInfos = new TreeMap<>();
    private final ObservableList<CalculatedInfo> dataShow = FXCollections.observableArrayList();
    private final List<Lieferant> lieferanten = LieferantSession.getAllLieferanten();
    private List<CalculatedInfo> dataAll;
    private int wochenI;

    @FXML
    private TextField wochen;
    @FXML
    private TextField vorlaufzeit;
    @FXML
    private ChoiceBox<String> lieferantChoiceBox;
    @FXML
    private TableView<CalculatedInfo> pInfoTable;
    @FXML
    private TableColumn<CalculatedInfo, Number> pNrColumn;
    @FXML
    private TableColumn<CalculatedInfo, String> bezColumn;
    @FXML
    private TableColumn<CalculatedInfo, Image> bildColumn;
    @FXML
    private TableColumn<CalculatedInfo, String> lagerstandColumn;
    @FXML
    private TableColumn<CalculatedInfo, String> daysToEmptyColumn;
    @FXML
    private TableColumn<CalculatedInfo, String> mengeColumn;
    @FXML
    private TableColumn<CalculatedInfo, String> bestellenColumn;
    @FXML
    private RadioButton durchschnitt;
    @FXML
    private RadioButton vorjahr;

    @FXML
    private void initialize() {
        ObservableList<String> lieferantenChoice = FXCollections.observableArrayList();
        lieferantenChoice.add("Alle");
        lieferanten.stream().forEach(l -> lieferantenChoice.add(l.getName()));
        lieferantChoiceBox.setItems(lieferantenChoice);
        lieferantChoiceBox.setValue("Alle");
        wochen.setText("" + 1);
        vorlaufzeit.setText("" + 0);
        calculateProduktinfos(0);

        final ToggleGroup berechnungsmethode = new ToggleGroup();
        durchschnitt.setToggleGroup(berechnungsmethode);
        vorjahr.setToggleGroup(berechnungsmethode);
        durchschnitt.setSelected(true);

        wochen.textProperty().addListener((observable1, oldValue, newValue) -> {
            try {
                int w = Integer.valueOf(newValue);
                wochenI = w;
                recalculate(w, getBerechnungsmethode());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        });

        vorlaufzeit.textProperty().addListener(((observable1, oldValue, newValue) -> {
            try {
                int v = Integer.valueOf(newValue);
                //produktInfos = new TreeMap<>();
                calculateProduktinfos(v);
                recalculate(wochenI, getBerechnungsmethode());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }));

        lieferantChoiceBox.valueProperty().addListener((observable -> {
            recalculate(Integer.valueOf(wochen.getText()), getBerechnungsmethode());
        }));

        berechnungsmethode.selectedToggleProperty().addListener(observable1 -> recalculate(Integer.valueOf(wochen.getText()), getBerechnungsmethode()));

        dataAll = FXCollections.observableArrayList(produktInfos.values());

        pNrColumn.setCellValueFactory(cellData -> cellData.getValue().produktNr);
        bezColumn.setCellValueFactory(cellData -> cellData.getValue().bezeichnung);
        bildColumn.setCellValueFactory(cellData -> cellData.getValue().bild);
        bildColumn.setCellFactory(imageCellFactory);
        lagerstandColumn.setCellValueFactory(cellData -> cellData.getValue().lagerstand);
        daysToEmptyColumn.setCellValueFactory(cellData -> cellData.getValue().tageBisLeer);
        mengeColumn.setCellValueFactory(cellData -> cellData.getValue().gebrauchteMenge);
        bestellenColumn.setCellValueFactory(cellData -> cellData.getValue().bestellen);
        pInfoTable.setItems(dataShow);

        recalculate(Integer.valueOf(wochen.getText()), getBerechnungsmethode());
    }

    private boolean getBerechnungsmethode() {
        return !durchschnitt.isSelected();
    }

    private Map<Integer, Integer> getLastYearSoldData(LocalDate from, LocalDate to) {
        List<Auftrag> auftraege = AuftragSession.getAllAuftraege();
        LocalDate fromLast = from.minusYears(1);
        LocalDate toLast = to.minusYears(1);
        List<Auftrag> auftraegeInZeitraum = new ArrayList<>();
        auftraege.stream().filter(a -> a.getErstellung().isAfter(fromLast) && a.getErstellung().isBefore(toLast)).forEach(auftraegeInZeitraum::add);
        Map<Integer, Integer> data = new HashMap<>();
        for (Auftrag a : auftraegeInZeitraum) {
            for (Produktmenge p : a.getProdukte()) {
                int key = p.getProdukt().getProduktNr();
                if (!data.containsKey(key)) {
                    data.put(key, p.getMenge());
                } else {
                    data.put(key, data.get(key) + p.getMenge());
                }
            }
        }
        return data;
    }

    private void calculateProduktinfos(int vorlaufzeit) {
        for (CalculatedInfo ci : produktInfos.values()) {
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
                    produktInfos.put(key, new CalculatedInfo(key));
                CalculatedInfo ci = produktInfos.get(key);
                ci.bezeichnung.set(pm.getProdukt().getKateogrie().name() + ": " + pm.getProdukt().getBez());
                ci.bild = pm.getProdukt().getImageProperty();
                if (i.getDatum().isAfter(ci.lastInventur)) {
                    ci.lastInventur = i.getDatum();     //aktuellste Inventur
                    ci.inLastInvetur = pm.getMenge();   //Lagerstand der aktuellsten Inventur
                }
            }
        }

        String errorMsg = "Kein Lagerstand verf\u00fcgbar";
        for (Auftrag a : auftraege) {
            for (Produktmenge pm : a.getProdukte()) {
                int key = pm.getProdukt().getProduktNr();
                if (!produktInfos.containsKey(key) || produktInfos.get(key).lastInventur == null) {
                    CalculatedInfo ci = new CalculatedInfo(key);
                    ci.tageBisLeer.set(errorMsg);
                    produktInfos.put(key, ci);
                } else {
                    CalculatedInfo ci = produktInfos.get(key);
                    ci.soldTotal += pm.getMenge();
                    if (a.getErstellung().isBefore(ci.firstSold))
                        ci.firstSold = a.getErstellung();
                    if (a.getErstellung().isAfter(ci.lastInventur))
                        ci.soldAfterLastInventur += pm.getMenge();
                }
            }
        }
        Collection<CalculatedInfo> toRemove = new ArrayList<>();
        for (CalculatedInfo ci : produktInfos.values()) {
            if (!ci.tageBisLeer.get().equals(errorMsg) && !ci.firstSold.equals(LocalDate.MAX)) {
                long days = ChronoUnit.DAYS.between(ci.firstSold, LocalDate.now());
                ci.dailyNeed = (double) ci.soldTotal / (double) days;
                long actualStock = ci.inLastInvetur - ci.soldAfterLastInventur - Math.round(vorlaufzeit * ci.dailyNeed);
                ci.lagerstand.set("" + actualStock);
                if (days != 0) {
                    int daysToEmpty = (int) (actualStock / ci.dailyNeed);
                    ci.tageBisLeer.set(String.valueOf(daysToEmpty));
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
                    CalculatedInfo ci = produktInfos.get(key);
                    ci.lieferzeit = l.getLieferzeit();
                    LocalDate now = LocalDate.now();
                    LocalDate bestellen = now.plusDays(Integer.valueOf(ci.tageBisLeer.get()) - ci.lieferzeit);
                    if (bestellen.isBefore(now) || bestellen.isEqual(now))
                        ci.bestellen.set("SOFORT!");
                    else
                        ci.bestellen.set(bestellen.format(dtf));
                }
            }
        }
    }

    private void recalculate(int wochen, boolean neueBerechnungAktiv) {
        Map<Integer, Integer> lastYear = null;
        if (neueBerechnungAktiv)
            lastYear = getLastYearSoldData(LocalDate.now(), LocalDate.now().plusWeeks(wochen));
        dataShow.clear();
        String lieferant = lieferantChoiceBox.getValue();
        if (lieferant.equals("Alle"))
            dataShow.addAll(dataAll);
        else {
            Collection<Produkt> produkte = LieferantSession.getLieferantByName(lieferant).getProdukte();
            List<Integer> produktNummern = new ArrayList<>();
            produkte.stream().forEach(p -> produktNummern.add(p.getProduktNr()));
            dataAll.stream().filter(ci -> produktNummern.contains(ci.produktNr.get())).forEach(dataShow::add);
        }
        for (CalculatedInfo ci : dataShow) {
            long menge;
            if (neueBerechnungAktiv && lastYear.containsKey(ci.produktNr.get())) {
                menge = lastYear.get(ci.produktNr.get()) - (ci.inLastInvetur - ci.soldAfterLastInventur);
            } else {
                menge = Math.round(ci.dailyNeed * wochen * 7 - (ci.inLastInvetur - ci.soldAfterLastInventur)); //jahre: 365, wochen: 7
            }
            ci.gebrauchteMenge.set("" + Math.max(menge, 0));
        }
        FXCollections.sort(dataShow, (o1, o2) -> {
            int menge1 = Integer.valueOf(o1.tageBisLeer.get());
            int menge2 = Integer.valueOf(o2.tageBisLeer.get());
            return menge1 - menge2;
        });
        pInfoTable.refresh();
    }

    public Map<Integer, CalculatedInfo> calculateForAlarm() {
        calculateProduktinfos(0);
        return produktInfos;
    }
}
