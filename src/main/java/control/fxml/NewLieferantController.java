package control.fxml;

import control.LieferantSession;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.Lieferant;

import java.util.List;

/**
 * Created by Viktor on 08.09.2015.
 */
public class NewLieferantController extends BasicNewController {
    private Lieferant update;

    @FXML
    private TextField lieferzeitField;

    @FXML
    private void handleAdd() {
        if (isInputValid()) {
            Lieferant l;
            if (update != null)
                l = update;
            else {
                l = new Lieferant();
                l.setName(nameField.getText());
            }
            l.setLieferzeit(Integer.valueOf(lieferzeitField.getText()));
            LieferantSession.saveLieferant(l);
            addClicked = true;
            dialogStage.close();
        }
    }

    public void populateData(Lieferant l) {
        update = l;
        nameField.setText(l.getName());
        lieferzeitField.setText("" + l.getLieferzeit());
        nameField.setDisable(true);
    }

    private boolean isInputValid() {
        String text = nameField.getText();
        String lf = lieferzeitField.getText();
        if (text.length() < 1) {
            showError("Leerer Name");
            return false;
        }
        if (lf.length() < 1) {
            showError("Keine Lieferzeit angegeben");
            return false;
        }
        try {
            int l = Integer.valueOf(lf);
            if (l < 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Lieferzeit ist keine positive Zahl");
            return false;
        }
        if (update == null) {
            List<Lieferant> lieferanten = LieferantSession.getAllLieferanten();
            for (Lieferant l : lieferanten)
                if (l.getName().equals(text)) {
                    showError("Lieferant mit diesem Namen existiert bereits");
                    return false;
                }
        }
        return true;
    }
}
