package control.fxml.dataStructures;

import javafx.beans.property.*;
import javafx.scene.image.Image;

/**
 * Created by Viktor on 02.02.2016.
 */
public class TableRow {
    public final IntegerProperty produktNr = new SimpleIntegerProperty();
    public final StringProperty bezeichnung = new SimpleStringProperty("");
    public ObjectProperty<Image> bild = new SimpleObjectProperty<>();
}
