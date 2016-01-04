package control.fxml;

import control.LieferantSession;
import control.ProduktSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Kategorie;
import model.Lieferant;
import model.Produkt;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Viktor on 26.05.2015.
 */
public class NewProduktController {
    private Stage dialogStage;
    private boolean addClicked;
    private byte[] bImg;
    private List<Lieferant> lieferanten;
    private Produkt update;

    @FXML
    private TextField produktNrField;
    @FXML
    private ChoiceBox<String> kategorieField;
    @FXML
    private TextField bezField;
    @FXML
    private TextField vkPreisField;
    @FXML
    private TextField ekPreisField;
    @FXML
    private ImageView imageView;
    @FXML
    private ListView<String> lieferantenList;
    @FXML
    private TextField vorratswochenField;

    @FXML
    private void initialize() {
        Kategorie[] values = Kategorie.values();
        Collection<String> names = new ArrayList<>();
        for (Kategorie k : values) {
            names.add(k.name());
        }
        kategorieField.setItems(FXCollections.observableArrayList(names));
        kategorieField.setValue(FXCollections.observableArrayList(names).get(0));
        lieferanten = LieferantSession.getAllLieferanten();
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll(lieferanten.stream().map(Lieferant::getName).collect(Collectors.toList()));
        lieferantenList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lieferantenList.setItems(list);
    }

    @FXML
    private void handleAdd() {
        if (isInputValid()) {
            List<String> selectedLieferanten = lieferantenList.getSelectionModel().getSelectedItems();
            Produkt p;
            if (update == null) {
                p = new Produkt();
                p.setProduktNr(Integer.valueOf(produktNrField.getText()));
                p.setVkPreis(Float.valueOf(vkPreisField.getText().replace(',', '.')));
            } else
                p = update;
            String kat = kategorieField.getValue();
            p.setKateogrie(Kategorie.valueOf(kat));
            p.setBez(bezField.getText());
            p.setEkPreis(ekPreisField.getText().length() < 1 ? 0 : Float.valueOf(ekPreisField.getText().replace(',', '.')));
            p.setImage(bImg);
            p.setVorratswochen(Integer.valueOf(vorratswochenField.getText()));
            ProduktSession.saveProdukt(p);
            for (String s : selectedLieferanten) {
                Lieferant l = LieferantSession.getLieferantByName(s);
                l.getProdukte().add(p);
                LieferantSession.saveLieferant(l);
            }
            addClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public void populateData(Produkt p) {
        update = p;
        produktNrField.setText("" + p.getProduktNr());
        kategorieField.setValue(p.getKateogrie().name());
        bezField.setText(p.getBez());
        vkPreisField.setText("" + p.getVkPreis());
        ekPreisField.setText("" + p.getEkPreis());
        if (p.getImageProperty() != null)
            imageView.setImage(p.getImageProperty().get());

        lieferanten.stream().filter(l -> l.getProdukte().contains(p)).forEach(l -> lieferantenList.getSelectionModel().select(l.getName()));
        lieferanten.stream().forEach(l -> l.getProdukte().remove(p));
        lieferanten.stream().forEach(LieferantSession::saveLieferant);

        vorratswochenField.setText("" + p.getVorratswochen());

        produktNrField.setEditable(false);
        produktNrField.setDisable(true);
        vkPreisField.setEditable(false);
        vkPreisField.setDisable(true);
    }

    private boolean isInputValid() {
        List<String> fields = new ArrayList<>();
        fields.add(produktNrField.getText());
        fields.add(bezField.getText());
        String tmp = vkPreisField.getText();
        String ek = ekPreisField.getText();
        String vWochen = vorratswochenField.getText();
        fields.add(tmp.replace(',', '.'));
        fields.add(vWochen);

        for (String s : fields) {
            if (s.length() < 1) {
                showError("Felder dürfen nicht leer sein");
                return false;
            }
        }
        int produktNr;
        try {
            produktNr = Integer.valueOf(fields.get(0));
            if (produktNr < 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("ProduktNr ist keine positive Zahl");
            return false;
        }

        try {
            float vkPreis = Float.valueOf(fields.get(2));
            if (vkPreis <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Verkaufspreis ist keine positive Zahl");
            return false;
        }
        try {
            if (ek.length() > 0) {
                float ekPreis = Float.valueOf(ek.replace(',', '.'));
                if (ekPreis < 0)
                    throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showError("Einkaufspreis ist keine positive Zahl");
        }
        try {
            int vorratswochen = Integer.valueOf(vWochen);
            if (vorratswochen < 1)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Vorratswochen ist keine positive Zahl");
            return false;
        }
        if (update == null) {
            List<Produkt> produkte = ProduktSession.getAllProdukte();
            for (Produkt p : produkte)
                if (produktNr == p.getProduktNr()) {
                    showError("Produkt mit dieser Nummer existiert bereits");
                    return false;
                }
        }
        return true;
    }

    @FXML
    private void handleDurchsuchen() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Bild auswählen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.jpg", "*.png")
        );
        File f = fileChooser.showOpenDialog(dialogStage);
        BufferedImage bi = ImageIO.read(f);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "png", baos);
            bImg = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image i = SwingFXUtils.toFXImage(bi, null);
        imageView.setImage(i);
    }

    private void showError(String error) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle("Ungültiger Text");
        alert.setHeaderText("Bitte korrigieren");
        alert.setContentText(error);
        alert.showAndWait();
    }

    public boolean isAddClicked() {
        return addClicked;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
