package model;

import control.AuftragSession;
import control.KundeSession;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Created by Viktor on 08.05.2015.
 */
@Entity
public class Auftrag {

    private IntegerProperty auftrNr;
    private ObjectProperty<LocalDate> erstellung;
    private ObservableList<Produktmenge> produkte;
    private DoubleProperty wert;
    private Kunde kunde;
    private StringProperty kundeName;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getAuftrNr() {
        if (auftrNr == null)
            return 0;
        return auftrNr.get();
    }

    public void setAuftrNr(int auftrNr) {
        this.auftrNr = new SimpleIntegerProperty(auftrNr);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && ((Auftrag) obj).getAuftrNr() == getAuftrNr();
    }

    @Basic
    @Convert(converter = LocalDateConverter.class)
    public LocalDate getErstellung() {
        if (erstellung == null)
            return null;
        return erstellung.get();
    }

    public void setErstellung(LocalDate erstellung) {
        this.erstellung = new SimpleObjectProperty<>(erstellung);
    }

    @Transient
    public ObjectProperty<LocalDate> getErstellungProperty() {
        return erstellung;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public List<Produktmenge> getProdukte() {
        return produkte;
    }

    public void setProdukte(Collection<Produktmenge> produkte) {
        this.produkte = FXCollections.observableArrayList(produkte);
    }

    public void calculateWert() {
        double umsatz = produkte.parallelStream().mapToDouble(p -> Math.round(p.getMenge() * p.getProdukt().getVkPreis() * 100.0) / 100.0).sum();
        setWert(umsatz);
    }

    @Transient
    public DoubleProperty getWertProperty() {
        return wert;
    }

    @Basic
    public Double getWert() {
        return wert.get();
    }

    public void setWert(Double wert) {
        this.wert = wert != null ? new SimpleDoubleProperty(wert) : new SimpleDoubleProperty(0);
    }

    public void findKunde() {
        List<Kunde> kunden = KundeSession.getAllKunden();
        setKunde(kunden.parallelStream().filter(k -> k.getAuftraege().contains(this)).findAny().get());
        AuftragSession.saveAuftrag(this);

    }

    @ManyToOne
    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
        kundeName = kunde.getNameProperty();
    }

    @Transient
    public StringProperty getKundeProperty() {
        return kundeName;
    }
}