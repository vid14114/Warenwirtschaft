package model;

import javax.persistence.*;

/**
 * Created by Viktor on 08.05.2015.
 */
@Entity
public class Produktmenge {
    private Produkt produkt;
    private int id;
    private int menge;

    @OneToOne
    public Produkt getProdukt() {
        return produkt;
    }

    public void setProdukt(Produkt produkt) {
        this.produkt = produkt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    public int getMenge() {
        return menge;
    }

    public void setMenge(int menge) {
        this.menge = menge;
    }
}
