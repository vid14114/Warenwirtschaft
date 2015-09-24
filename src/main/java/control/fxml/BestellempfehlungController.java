package control.fxml;

import control.AuftragSession;
import control.InventurSession;
import control.LieferantSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.util.Callback;
import model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by Viktor on 11.06.2015.
 */
public class BestellempfehlungController {
    private final Map<Integer, CalculatedInfo> produktInfos = new TreeMap<>();
    private final Callback imageCellFactory = new ImageCellFactory();
    private ObservableList<CalculatedInfo> dataShow = FXCollections.observableArrayList();
    private List<CalculatedInfo> dataAll;
    private List<Lieferant> lieferanten = LieferantSession.getAllLieferanten();

    @FXML
    private Slider jahre;
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
    private void initialize() {
        ObservableList<String> lieferantenChoice = FXCollections.observableArrayList();
        lieferantenChoice.add("Alle");
        lieferanten.stream().forEach(l -> lieferantenChoice.add(l.getName()));
        lieferantChoiceBox.setItems(lieferantenChoice);
        lieferantChoiceBox.setValue("Alle");
        calculateProduktinfos();

        jahre.valueProperty().addListener((observable, oldValue, newValue) -> {
            recalculate(newValue.intValue());
        });

        lieferantChoiceBox.valueProperty().addListener((observable -> {
            recalculate((int) jahre.getValue());
        }));

        dataAll = FXCollections.observableArrayList(produktInfos.values());

        pNrColumn.setCellValueFactory(cellData -> cellData.getValue().produktNr);
        bezColumn.setCellValueFactory(cellData -> cellData.getValue().bezeichnung);
        bildColumn.setCellValueFactory(cellData -> cellData.getValue().bild);
        bildColumn.setCellFactory(imageCellFactory);
        lagerstandColumn.setCellValueFactory(cellData -> cellData.getValue().lagerstand);
        daysToEmptyColumn.setCellValueFactory(cellData -> cellData.getValue().tageBisLeer);
        mengeColumn.setCellValueFactory(cellData -> cellData.getValue().gebrauchteMenge);
        pInfoTable.setItems(dataShow);

        recalculate((int) jahre.getValue());
    }

    public void calculateProduktinfos() {
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
                int actualStock = ci.inLastInvetur - ci.soldAfterLastInventur;
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
    }

    public void recalculate(int y) {
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
            long menge = Math.round(ci.dailyNeed * y * 365 - (ci.inLastInvetur - ci.soldAfterLastInventur));
            ci.gebrauchteMenge.set("" + Math.max(menge, 0));
        }
        FXCollections.sort(dataShow, (o1, o2) -> {
            int menge1 = Integer.valueOf(o1.tageBisLeer.get());
            int menge2 = Integer.valueOf(o2.tageBisLeer.get());
            return menge1 - menge2;
        });
    }

    public Map<Integer, CalculatedInfo> calculateForAlarm() {
        calculateProduktinfos();
        return produktInfos;
    }
}
