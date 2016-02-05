package control.fxml.dataStructures;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

/**
 * Created by Viktor on 05.02.2016.
 */
public class SollIstVergleichRow extends TableRow {
    public final IntegerProperty sollStand = new SimpleIntegerProperty();
    public final IntegerProperty istStand = new SimpleIntegerProperty();
    public final StringProperty lastInvProp = new SimpleStringProperty("");
    public final StringProperty status = new SimpleStringProperty("");

    public int inLastInvetur;
    public int inPreLastInventur;
    public LocalDate lastInventur = LocalDate.MIN;
    public LocalDate preLastInventur = LocalDate.MIN;
}
