package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Created by Viktor on 16.05.2015.
 */

@Entity
public class Inventur {
    private IntegerProperty id;
    private ObjectProperty<LocalDate> datum;
    private ObservableList<Produktmenge> produkte;

    public Inventur() {
        LocalDate ld = LocalDate.now();
        setDatum(ld);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        if (id == null)
            return 0;
        return id.get();
    }

    public void setId(int id) {
        this.id = new SimpleIntegerProperty(id);
    }

    @Basic
    @Convert(converter = LocalDateConverter.class)
    public LocalDate getDatum() {
        return datum.get();
    }

    public void setDatum(LocalDate datum) {
        this.datum = new SimpleObjectProperty<>(datum);
    }

    @Transient
    public ObjectProperty<LocalDate> getDatumProperty() {
        return datum;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public List<Produktmenge> getProdukte() {
        return produkte;
    }

    public void setProdukte(Collection<Produktmenge> produkte) {
        this.produkte = FXCollections.observableArrayList(produkte);
    }
}
