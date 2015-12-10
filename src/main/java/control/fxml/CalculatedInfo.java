package control.fxml;

import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.time.LocalDate;

/**
 * Created by Viktor on 23.06.2015.
 */
public class CalculatedInfo {
    public final IntegerProperty produktNr = new SimpleIntegerProperty();
    public final StringProperty tageBisLeer = new SimpleStringProperty("");
    final StringProperty bezeichnung = new SimpleStringProperty("");
    //Bestellempfehlung
    final StringProperty lagerstand = new SimpleStringProperty("");
    final StringProperty gebrauchteMenge = new SimpleStringProperty("");
    final StringProperty bestellen = new SimpleStringProperty("");
    //Soll-Ist-Vergleich
    final IntegerProperty sollStand = new SimpleIntegerProperty();
    final IntegerProperty istStand = new SimpleIntegerProperty();
    final StringProperty lastInvProp = new SimpleStringProperty("");
    final StringProperty status = new SimpleStringProperty("");
    //Jahresvergleich
    final IntegerProperty stuckzahl1 = new SimpleIntegerProperty();
    final DoubleProperty umsatz1 = new SimpleDoubleProperty();
    final IntegerProperty stuckzahl2 = new SimpleIntegerProperty();
    final DoubleProperty umsatz2 = new SimpleDoubleProperty();
    ObjectProperty<Image> bild = new SimpleObjectProperty<>();
    int lieferzeit;
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
