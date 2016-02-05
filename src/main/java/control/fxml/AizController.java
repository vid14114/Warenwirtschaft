package control.fxml;

import control.ProduktSession;
import control.fxml.dataStructures.AizRow;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Produkt;
import model.Produktmenge;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Viktor on 05.02.2016.
 */
public class AizController {
    final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    final Callback vCellFactory = new VCellFactory();
    final ObservableList<AizRow> data = FXCollections.observableArrayList();
    private final Callback imageCellFactory = new ImageCellFactory();
    Stage dialogStage;
    boolean saveClicked;

    @FXML
    TextField datumField;

    @FXML
    TableView<AizRow> aizTable;
    @FXML
    TableColumn<AizRow, String> mengeColumn;
    @FXML
    private
    TableColumn<AizRow, String> produktNrColumn;
    @FXML
    private
    TableColumn<AizRow, String> bezColumn;
    @FXML
    private
    TableColumn<AizRow, Image> bildColumn;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    void handleNewRow() {
        AizRow e = new AizRow();
        data.add(e);
    }

    @FXML
    void handleCancel() {
        dialogStage.close();
    }

    void initializeBasic() {
        LocalDate ldt = LocalDate.now();
        String date = ldt.format(dtf);
        datumField.setText(date);

        produktNrColumn.setCellValueFactory(cellData -> new SimpleStringProperty("" + cellData.getValue().produktNr.get()));
        produktNrColumn.setCellFactory(column -> EditCell.createStringEditCell());
        produktNrColumn.setOnEditCommit(
                event -> {
                    AizRow e = event.getRowValue();
                    int i = Integer.valueOf(event.getNewValue());
                    e.produktNr.set(i);
                    checkNumbers();
                }
        );
        mengeColumn.setCellValueFactory(cellData -> cellData.getValue().menge);
        mengeColumn.setCellFactory(column -> EditCell.createStringEditCell());
        bezColumn.setCellValueFactory(cellData -> cellData.getValue().bezeichnung);
        bezColumn.setCellFactory(vCellFactory);
        bildColumn.setCellValueFactory(cellData -> cellData.getValue().bild);
        bildColumn.setCellFactory(imageCellFactory);

        aizTable.setItems(data);
        aizTable.setEditable(true);
    }

    @FXML
    void checkNumbers() {
        aizTable.getItems().stream().filter(e -> e.produktNr.get() != 0).forEach(e -> {
            Produkt p = ProduktSession.getProdukt(e.produktNr.get());
            if (p != null) {
                e.bezeichnung.set(p.getKateogrie().name() + ": " + p.getBez());
                if (p.getImageProperty() != null)
                    e.bild.set(p.getImageProperty().get());
            }
        });
        int selectedRow = aizTable.getSelectionModel().getFocusedIndex();
        if (selectedRow + 1 >= aizTable.getItems().size())
            handleNewRow();
    }

    void populateBasic1(Iterable<Produktmenge> la) {
        data.remove(0, 8);
        for (Produktmenge p : la) {
            AizRow e = new AizRow();
            e.produktNr.set(p.getProdukt().getProduktNr());
            e.menge = new SimpleStringProperty(String.valueOf(p.getMenge()));
            data.add(e);
        }
    }

    void populateBasic2(LocalDate ldt) {
        String date = ldt.format(dtf);
        datumField.setText(date);
        checkNumbers();
        for (int j = aizTable.getItems().size(); j < 8; j++)
            handleNewRow();
    }
}
