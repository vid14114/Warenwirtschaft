package control.fxml;

import control.KundeSession;
import javafx.fxml.FXML;
import model.Kunde;

import java.util.List;

/**
 * Created by Viktor on 21.05.2015.
 */
public class NewKundeController extends BasicNewController {

    @FXML
    private void handleAdd() {
        if (isInputValid()) {
            Kunde kunde = new Kunde();
            kunde.setName(nameField.getText());
            KundeSession.saveKunde(kunde);
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
        List<Kunde> kunden = KundeSession.getAllKunden();
        for (Kunde k : kunden)
            if (k.getName().equals(text)) {
                showError("Kunde mit diesem Namen existiert bereits");
                return false;
            }
        return true;
    }

}
