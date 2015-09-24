package control.fxml;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Viktor on 22.09.2015.
 */
public class BasicNewController {
    @FXML
    TextField nameField;

    Stage dialogStage;
    boolean addClicked;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void initialize() {
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public boolean isAddClicked() {
        return addClicked;
    }

    public void showError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle("Ungültiger Text");
        alert.setHeaderText("Bitte korrigieren");
        alert.setContentText(error);
        alert.showAndWait();
    }
}
