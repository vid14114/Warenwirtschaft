package control.fxml;

import control.LieferantSession;
import javafx.fxml.FXML;
import model.Lieferant;

import java.util.List;

/**
 * Created by Viktor on 08.09.2015.
 */
public class NewLieferantController extends BasicNewController {

    @FXML
    private void handleAdd() {
        if (isInputValid()) {
            Lieferant l = new Lieferant();
            l.setName(nameField.getText());
            LieferantSession.saveLieferant(l);
            addClicked = true;
            dialogStage.close();
        }
    }

    private boolean isInputValid() {
        String text = nameField.getText();
        if (text.length() < 1) {
            showError("Leerer Name");
            return false;
        }
        List<Lieferant> lieferanten = LieferantSession.getAllLieferanten();
        for (Lieferant l : lieferanten)
            if (l.getName().equals(text)) {
                showError("Lieferant mit diesem Namen existiert bereits");
                return false;
            }
        return true;
    }
}
