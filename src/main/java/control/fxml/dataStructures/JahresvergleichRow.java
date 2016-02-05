package control.fxml.dataStructures;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Created by Viktor on 05.02.2016.
 */
public class JahresvergleichRow extends TableRow {
    public final IntegerProperty stuckzahl1 = new SimpleIntegerProperty();
    public final DoubleProperty umsatz1 = new SimpleDoubleProperty();
    public final IntegerProperty stuckzahl2 = new SimpleIntegerProperty();
    public final DoubleProperty umsatz2 = new SimpleDoubleProperty();
}
