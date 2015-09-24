package model;

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

    @ManyToMany(fetch = FetchType.EAGER)
    public Collection<Produkt> getProdukte() {
        return produkte;
    }

    public void setProdukte(Collection<Produkt> produkte) {
        this.produkte = produkte;
    }

    @Transient
    public StringProperty getNameProperty() {
        return name;
    }


}
