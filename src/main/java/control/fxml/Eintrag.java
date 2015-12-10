package control.fxml;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

/**
 * Created by Viktor on 09.06.2015.
 */
public class Eintrag {
    final StringProperty bezeichnung = new SimpleStringProperty("");
    final ObjectProperty<Image> bild = new SimpleObjectProperty<>();
    final StringProperty preis = new SimpleStringProperty("");
    StringProperty produktNr = new SimpleStringProperty("");
    StringProperty menge = new SimpleStringProperty("");
}
