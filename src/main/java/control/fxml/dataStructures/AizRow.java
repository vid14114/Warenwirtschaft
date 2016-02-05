package control.fxml.dataStructures;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Viktor on 02.02.2016.
 */
public class AizRow extends TableRow {
    public final StringProperty preis = new SimpleStringProperty("");
    public StringProperty menge = new SimpleStringProperty("");
}
