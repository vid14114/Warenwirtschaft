package control.fxml;

import control.AuftragSession;
import control.KundeSession;
import control.ProduktSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Auftrag;
import model.Kunde;
import model.Produkt;
import model.Produktmenge;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Viktor on 28.05.2015.
 */
public class AuftragDetailsController {
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final Callback vCellFactory = new VCellFactory();
    private final Callback imageCellFactory = new ImageCellFactory();
    private final ObservableList<Eintrag> data = FXCollections.observableArrayList();
    private List<String> kList;
    private Stage dialogStage;
    private boolean saveClicked;
    private Auftrag toEdit;
    @FXML
    private ChoiceBox<String> kundeField;
    @FXML
    private TextField datumField;
    @FXML
    private TableView<Eintrag> auftragDetailsTable;
    @FXML
    private TableColumn<Eintrag, String> produktNrColumn;
    @FXML
    private TableColumn<Eintrag, String> mengeColumn;
    @FXML
    private TableColumn<Eintrag, String> bezColumn;
    @FXML
    private TableColumn<Eintrag, Image> bildColumn;
    @FXML
    private TableColumn<Eintrag, String> preisColumn;
    @FXML
    private TextField totalPriceField;

    @FXML
    private void initialize() {
        kList = KundeSession.getAllKundenNamen();
        kundeField.setItems(FXCollections.observableArrayList(kList));
        kundeField.setValue(FXCollections.observableArrayList(kList).get(0));

        LocalDate ldt = LocalDate.now();
        String date = ldt.format(dtf);
        datumField.setText(date);

        produktNrColumn.setCellValueFactory(cellData -> cellData.getValue().produktNr);
        produktNrColumn.setCellFactory(column -> EditCell.createStringEditCell());
        produktNrColumn.setOnEditCommit(
                event -> {
                    Eintrag e = event.getRowValue();
                    e.produktNr.set(event.getNewValue());
                    checkNumbers();
                }
        );

        mengeColumn.setCellValueFactory(cellData -> cellData.getValue().menge);
        mengeColumn.setCellFactory(column -> EditCell.createStringEditCell());
        mengeColumn.setOnEditCommit(
                event -> {
                    Eintrag e = event.getRowValue();
                    e.menge.set(event.getNewValue());
                    checkNumbers();
                }
        );
        bezColumn.setCellValueFactory(cellData -> cellData.getValue().bezeichnung);
        bezColumn.setCellFactory(vCellFactory);
        bildColumn.setCellValueFactory(cellData -> cellData.getValue().bild);
        bildColumn.setCellFactory(imageCellFactory);
        preisColumn.setCellValueFactory(cellData -> cellData.getValue().preis);
        preisColumn.setCellFactory(vCellFactory);

        auftragDetailsTable.setItems(data);
        auftragDetailsTable.setEditable(true);

        if (toEdit == null)
            for (int i = 0; i < 8; i++)
                handleNewRow();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleNewRow() {
        Eintrag e = new Eintrag();
        data.add(e);
    }

    public void populateData(Iterable<Produktmenge> la, Auftrag a) {
        data.remove(0, 8);
        for (Produktmenge p : la) {
            Eintrag e = new Eintrag();
            e.produktNr = new SimpleStringProperty(String.valueOf(p.getProdukt().getProduktNr()));
            e.menge = new SimpleStringProperty(String.valueOf(p.getMenge()));
            data.add(e);
        }
        toEdit = a;
        LocalDate ldt = a.getErstellung();
        String date = ldt.format(dtf);
        datumField.setText(date);
        kundeField.setValue(a.getKunde().getName());
        checkNumbers();
        for (int i = auftragDetailsTable.getItems().size(); i < 8; i++)
            handleNewRow();
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
        auftragDetailsTable.getItems().stream().filter(e -> !e.produktNr.get().isEmpty() && !e.menge.get().isEmpty() && !e.bezeichnung.get().isEmpty()).forEach(e -> {
            Produktmenge p = new Produktmenge();
            p.setProdukt(ProduktSession.getProdukt(Integer.valueOf(e.produktNr.get())));
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

    public void showError() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle("Daten korrigieren");
        alert.setContentText("Daten sind nicht korrekt");
        alert.showAndWait();
    }

    @FXML
    private void checkNumbers() {
        float total = 0;
        for (Eintrag e : auftragDetailsTable.getItems()) {
            if (!e.produktNr.get().isEmpty()) {
                Produkt p = ProduktSession.getProdukt(Integer.valueOf(e.produktNr.get()));
                if (p != null) {
                    e.bezeichnung.set(p.getKateogrie().name() + ": " + p.getBez());
                    if (p.getImageProperty() != null)
                        e.bild.set(p.getImageProperty().get());
                    if (!e.menge.get().isEmpty()) {
                        double preis = Integer.valueOf(e.menge.get()) * p.getVkPreis();
                        preis = Math.round(preis * 100.0) / 100.0;
                        total += preis;
                        e.preis.set("\u20ac " + preis);
                    }
                }
            }
        }
        totalPriceField.setText("\u20ac " + total);
        int selectedRow = auftragDetailsTable.getSelectionModel().getFocusedIndex();
        if (selectedRow + 1 >= auftragDetailsTable.getItems().size())
            handleNewRow();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
