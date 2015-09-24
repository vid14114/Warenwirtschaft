package control.fxml;

import control.AuftragSession;
import control.InventurSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.util.Callback;
import model.Auftrag;
import model.Inventur;
import model.Produktmenge;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Viktor on 02.09.2015.
 */
public class SollIstVergleichController {
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final Callback imageCellFactory = new ImageCellFactory();
    List<Inventur> inventuren = InventurSession.getAllInventuren();
    private Map<Integer, CalculatedInfo> produktInfos = new TreeMap<>();
    private ObservableList<CalculatedInfo> data = FXCollections.observableArrayList();
    @FXML
    private ChoiceBox<String> vonChoiceBox;
    @FXML
    private ChoiceBox<String> bisChoiceBox;

    @FXML
    private TableView<CalculatedInfo> invInfoTable;
    @FXML
    private TableColumn<CalculatedInfo, Number> produktNrColumn;
    @FXML
    private TableColumn<CalculatedInfo, String> bezeichnungColumn;
    @FXML
    private TableColumn<CalculatedInfo, Image> bildColumn;
    @FXML
    private TableColumn<CalculatedInfo, Number> sollColumn;
    @FXML
    private TableColumn<CalculatedInfo, Number> istColumn;
    @FXML
    private TableColumn<CalculatedInfo, String> letzteInvColumn;
    @FXML
    private TableColumn<CalculatedInfo, String> statusColumn;

    @FXML
    private void initialize() {
        inventuren.sort((o1, o2) -> o1.getDatum().compareTo(o2.getDatum()));
        ObservableList<String> datums = FXCollections.observableArrayList();

        produktNrColumn.setCellValueFactory(cellData -> cellData.getValue().produktNr);
        bezeichnungColumn.setCellValueFactory(cellData -> cellData.getValue().bezeichnung);
        bildColumn.setCellValueFactory(cellData -> cellData.getValue().bild);
        bildColumn.setCellFactory(imageCellFactory);
        sollColumn.setCellValueFactory(cellData -> cellData.getValue().sollStand);
        istColumn.setCellValueFactory(cellData -> cellData.getValue().istStand);
        letzteInvColumn.setCellValueFactory(cellData -> cellData.getValue().lastInvProp);
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().status);

        invInfoTable.setItems(data);

        for (int i = 0; i < inventuren.size() - 1; i++)
            datums.add(inventuren.get(i).getDatum().format(dtf));
        vonChoiceBox.setItems(datums);
        vonChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> setBisChoice(newValue));
        vonChoiceBox.setValue(datums.get(0));

    }

    private void setBisChoice(String vonDatum) {
        LocalDate ld = LocalDate.parse(vonDatum, dtf);
        ObservableList<String> datums = FXCollections.observableArrayList();
        inventuren.stream().filter(i -> i.getDatum().isAfter(ld)).forEach(i -> datums.add(i.getDatum().format(dtf)));
        bisChoiceBox.setItems(datums);
        bisChoiceBox.setValue(datums.get(0));
    }

    @FXML
    private void calculate() {
        produktInfos.clear();
        LocalDate von = LocalDate.parse(vonChoiceBox.getValue(), dtf);
        LocalDate bis = LocalDate.parse(bisChoiceBox.getValue(), dtf);
        List<Auftrag> auftraege = AuftragSession.getAllAuftraege();
        List<Auftrag> auftraegeInZeitraum = new ArrayList<>();
        auftraege.stream().filter(a -> a.getErstellung().isAfter(von) && a.getErstellung().isBefore(bis)).forEach(auftraegeInZeitraum::add);
        List<Inventur> inventurenInZeitraum = new ArrayList<>();
        inventuren.stream().filter(i -> i.getDatum().isEqual(von) || i.getDatum().isEqual(bis) || (i.getDatum().isAfter(von) && i.getDatum().isBefore(bis))).forEach(inventurenInZeitraum::add);

        inventurenInZeitraum.sort((o1, o2) -> o2.getDatum().compareTo(o1.getDatum()));

        for (Inventur i : inventurenInZeitraum) {
            for (Produktmenge p : i.getProdukte()) {
                int key = p.getProdukt().getProduktNr();
                if (!produktInfos.containsKey(key)) {
                    CalculatedInfo ci = new CalculatedInfo(key);
                    ci.bezeichnung.set(p.getProdukt().getKateogrie().name() + ": " + p.getProdukt().getBez());
                    ci.bild = p.getProdukt().getImageProperty();
                    produktInfos.put(key, ci);
                }
                CalculatedInfo ci = produktInfos.get(key);
                if (i.getDatum().isAfter(ci.lastInventur)) {
                    ci.lastInventur = i.getDatum();
                    ci.inLastInvetur = p.getMenge();
                }
                if (i.getDatum().isAfter(ci.preLastInventur) && !i.getDatum().isEqual(ci.lastInventur)) {
                    ci.preLastInventur = i.getDatum();
                    ci.inPreLastInventur = p.getMenge();
                }
            }
        }

        Collection<CalculatedInfo> toRemove = new ArrayList<>();
        produktInfos.values().stream().filter(ci -> ci.preLastInventur.isEqual(LocalDate.MIN)).forEach(toRemove::add);
        toRemove.stream().forEach(produktInfos.values()::remove);

        for (Auftrag a : auftraegeInZeitraum) {
            for (Produktmenge p : a.getProdukte()) {
                int key = p.getProdukt().getProduktNr();
                if (produktInfos.containsKey(key)) {
                    CalculatedInfo ci = produktInfos.get(key);
                    if (a.getErstellung().isAfter(ci.preLastInventur)) {
                        ci.soldAfterLastInventur += p.getMenge();
                    }
                }
            }
        }

        data.clear();
        for (CalculatedInfo ci : produktInfos.values()) {
            int soll = ci.inPreLastInventur - ci.soldAfterLastInventur;
            ci.sollStand.set(soll);
            ci.istStand.set(ci.inLastInvetur);
            ci.lastInvProp.set(ci.lastInventur.format(dtf));
            if (soll == ci.inLastInvetur)
                ci.status.set("OK");
            else
                ci.status.set("Schwund");
            if (soll > ci.inLastInvetur || soll == ci.inLastInvetur)
                data.add(ci);
        }
    }
}
