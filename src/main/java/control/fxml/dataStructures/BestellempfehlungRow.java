package control.fxml.dataStructures;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

/**
 * Created by Viktor on 02.02.2016.
 */
public class BestellempfehlungRow extends TableRow {
    public final StringProperty kategorie = new SimpleStringProperty("");
    public final StringProperty vorratswochenProperty = new SimpleStringProperty("");
    public final StringProperty lagerstand = new SimpleStringProperty("");
    public final StringProperty tageBisLeer = new SimpleStringProperty("");
    public final StringProperty bestellmenge = new SimpleStringProperty("");
    public final StringProperty bestelldatum = new SimpleStringProperty("");

    public int soldTotal;
    public int soldAfterLastInventur;
    public int vorratswochen;
    public int inLastInventur;
    public int lieferzeit;
    public double dailyNeed;
    public LocalDate lastInventur = LocalDate.MIN;
    public LocalDate firstSold = LocalDate.MAX;
}
