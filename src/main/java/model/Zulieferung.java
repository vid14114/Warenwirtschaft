package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

/**
 * Created by Viktor on 13.10.2015.
 */
@Entity
public class Zulieferung {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private IntegerProperty zuNr;
    private ObjectProperty<LocalDate> datum;
    private ObservableList<Produktmenge> produkte;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getZuNr() {
        if (zuNr == null)
            return 0;
        return zuNr.get();
    }

    public void setZuNr(int zuNr) {
        this.zuNr = new SimpleIntegerProperty(zuNr);
    }

    @Transient
    public ObjectProperty<LocalDate> getDatumProperty() {
        return datum;
    }

    @Basic
    @Convert(converter = LocalDateConverter.class)
    public LocalDate getErstellung() {
        if (datum == null)
            return null;
        return datum.get();
    }

    public void setErstellung(LocalDate datum) {
        this.datum = new SimpleObjectProperty<>(datum);
    }

    @OneToMany(cascade = CascadeType.ALL)
    public List<Produktmenge> getProdukte() {
        return produkte;
    }

    public void setProdukte(Collection<Produktmenge> produkte) {
        this.produkte = FXCollections.observableArrayList(produkte);
    }

    @Override
    public String toString() {
        return "Zulieferung (" + getErstellung().format(dtf) + ")";
    }
}
