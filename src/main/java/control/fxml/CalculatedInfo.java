package control.fxml;

import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.time.LocalDate;

/**
 * Created by Viktor on 23.06.2015.
 */
public class CalculatedInfo {
    public IntegerProperty produktNr = new SimpleIntegerProperty();
    public StringProperty tageBisLeer = new SimpleStringProperty("");
    StringProperty bezeichnung = new SimpleStringProperty("");
    ObjectProperty<Image> bild = new SimpleObjectProperty<>();
    //Bestellempfehlung
    StringProperty lagerstand = new SimpleStringProperty("");
    StringProperty gebrauchteMenge = new SimpleStringProperty("");
    StringProperty bestellen = new SimpleStringProperty("");
    int lieferzeit;

    //Soll-Ist-Vergleich
    IntegerProperty sollStand = new SimpleIntegerProperty();
    IntegerProperty istStand = new SimpleIntegerProperty();
    StringProperty lastInvProp = new SimpleStringProperty("");
    StringProperty status = new SimpleStringProperty("");

    //Jahresvergleich
    IntegerProperty stuckzahl1 = new SimpleIntegerProperty();
    DoubleProperty umsatz1 = new SimpleDoubleProperty();
    IntegerProperty stuckzahl2 = new SimpleIntegerProperty();
    DoubleProperty umsatz2 = new SimpleDoubleProperty();

    LocalDate firstSold = LocalDate.MAX;
    LocalDate preLastInventur = LocalDate.MIN;
    LocalDate lastInventur = LocalDate.MIN;
    int soldTotal;
    int soldAfterLastInventur;
    int inLastInvetur;
    int inPreLastInventur;
    double dailyNeed;

    CalculatedInfo(int produktNr) {
        this.produktNr.set(produktNr);
    }

    CalculatedInfo() {

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        CalculatedInfo ci = (CalculatedInfo) obj;
        return ci.produktNr.get() == produktNr.get();
    }
}
