package control.fxml.dataStructures;

import javafx.beans.property.*;

import java.time.LocalDate;

public class BestellempfehlungRow extends TableRow {
    public final StringProperty kategorie = new SimpleStringProperty("");
    public final StringProperty vorratswochenProperty = new SimpleStringProperty("");
    public final LongProperty lagerstand = new SimpleLongProperty();
    public final IntegerProperty tageBisLeer = new SimpleIntegerProperty();
    public final LongProperty bestellmenge = new SimpleLongProperty();
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
