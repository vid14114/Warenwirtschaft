package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Viktor on 08.09.2015.
 */
@Entity
public class Lieferant {
    private StringProperty name;
    private IntegerProperty lieferzeit;
    private Collection<Produkt> produkte;

    @Id
    public String getName() {
        if (name != null)
            return name.get();
        return "";
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    @Transient
    public StringProperty getNameProperty() {
        return name;
    }

    @Column(nullable = true)
    public int getLieferzeit() {
        if (lieferzeit != null)
            return lieferzeit.get();
        return 0;
    }

    public void setLieferzeit(int lieferzeit) {
        this.lieferzeit = new SimpleIntegerProperty(lieferzeit);
    }

    @Transient
    public IntegerProperty getLieferzeitProperty() {
        return lieferzeit;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    public Collection<Produkt> getProdukte() {
        return produkte;
    }

    public void setProdukte(Collection<Produkt> produkte) {
        this.produkte = produkte;
    }


}
