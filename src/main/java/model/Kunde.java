package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Viktor on 08.05.2015.
 */
@Entity
public class Kunde {
    private IntegerProperty kdNr;
    private StringProperty name;
    private ObservableList<Auftrag> auftraege;
    private DoubleProperty umsatz;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getKdNr() {
        if (kdNr == null)
            return 0;
        return kdNr.get();
    }

    public void setKdNr(int kdNr) {
        this.kdNr = new SimpleIntegerProperty(kdNr);
    }


    @Basic
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    @Transient
    public StringProperty getNameProperty() {
        return name;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public Collection<Auftrag> getAuftraege() {
        return auftraege;
    }

    public void setAuftraege(Collection<Auftrag> auftraege) {
        this.auftraege = FXCollections.observableArrayList(auftraege);
    }

    @Transient
    public DoubleProperty getUmsatzProperty() {
        return umsatz;
    }

    @Basic
    public Double getUmsatz() {
        return umsatz.get();
    }

    public void setUmsatz(Double umsatz) {
        this.umsatz = umsatz != null ? new SimpleDoubleProperty(umsatz) : new SimpleDoubleProperty(0);
    }

    public void calculateUmsatz() {
        if (auftraege != null) {
            auftraege.stream().forEach(Auftrag::calculateWert);
            double umsatz = auftraege.parallelStream().mapToDouble(Auftrag::getWert).sum();
            setUmsatz(umsatz);
        } else
            setUmsatz(0.0);
    }

    @Override
    public boolean equals(Object obj) {
        Kunde k = (Kunde) obj;
        return k.getKdNr() == getKdNr();
    }

    @Override
    public String toString() {
        return "Kunde (" + getName() + "," + getUmsatz() + ")";
    }
}
