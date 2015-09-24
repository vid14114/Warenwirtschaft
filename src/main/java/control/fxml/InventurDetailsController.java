package control.fxml;

import control.InventurSession;
import control.ProduktSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Inventur;
import model.Produkt;
import model.Produktmenge;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Viktor on 09.06.2015.
 */
public class InventurDetailsController {
    private final ObservableList<Eintrag> data = FXCollections.observableArrayList();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final Callback vCellFactory = new VCellFactory();
    private final Callback imageCellFactory = new ImageCellFactory();
    private Stage dialogStage;
    private boolean saveClicked;
    @FXML
    private TextField datumField;
    @FXML
    private TableView<Eintrag> inventurDetailsTable;
    @FXML
    private TableColumn<Eintrag, String> produktNrColumn;
    @FXML
    private TableColumn<Eintrag, String> mengeColumn;
    @FXML
    private TableColumn<Eintrag, String> bezColumn;
    @FXML
    private TableColumn<Eintrag, Image> bildColumn;
    private Inventur toEdit;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void initialize() {
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
        bezColumn.setCellValueFactory(cellData -> cellData.getValue().bezeichnung);
        bezColumn.setCellFactory(vCellFactory);
        bildColumn.setCellValueFactory(cellData -> cellData.getValue().bild);
        bildColumn.setCellFactory(imageCellFactory);

        inventurDetailsTable.setItems(data);
        inventurDetailsTable.setEditable(true);

        if (toEdit == null)
            for (int i = 0; i < 8; i++)
                handleNewRow();
    }

    @FXML
    private void handleNewRow() {
        Eintrag e = new Eintrag();
        data.add(e);
    }

    @FXML
    private void checkNumbers() {
        inventurDetailsTable.getItems().stream().filter(e -> !e.produktNr.get().isEmpty()).forEach(e -> {
            Produkt p = ProduktSession.getProdukt(Integer.valueOf(e.produktNr.get()));
            if (p != null) {
                e.bezeichnung.set(p.getKateogrie().name() + ": " + p.getBez());
                if (p.getImageProperty() != null)
                    e.bild.set(p.getImageProperty().get());
            }
        });
        int selectedRow = inventurDetailsTable.getSelectionModel().getFocusedIndex();
        if (selectedRow + 1 >= inventurDetailsTable.getItems().size())
            handleNewRow();
    }

    public void populateData(Iterable<Produktmenge> la, Inventur i) {
        data.remove(0, 8);
        for (Produktmenge p : la) {
            Eintrag e = new Eintrag();
            e.produktNr = new SimpleStringProperty(String.valueOf(p.getProdukt().getProduktNr()));
            e.menge = new SimpleStringProperty(String.valueOf(p.getMenge()));
            data.add(e);
        }
        toEdit = i;
        LocalDate ldt = i.getDatum();
        String date = ldt.format(dtf);
        datumField.setText(date);
        checkNumbers();
        for (int j = inventurDetailsTable.getItems().size(); j < 8; j++)
            handleNewRow();
    }

    public void showError() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle("Daten korrigieren");
        alert.setContentText("Daten sind nicht korrekt!");
        alert.showAndWait();
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
        inventurDetailsTable.getItems().stream().filter(e -> !e.produktNr.get().isEmpty() && !e.menge.get().isEmpty() && !e.bezeichnung.get().isEmpty()).forEach(e -> {
            Produktmenge p = new Produktmenge();
            p.setProdukt(ProduktSession.getProdukt(Integer.valueOf(e.produktNr.get())));
            p.setMenge(Integer.valueOf(e.menge.get()));
            pm.add(p);
        });
        i.setProdukte(pm);
        InventurSession.saveInventur(i);
        dialogStage.close();
        saveClicked = true;
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
