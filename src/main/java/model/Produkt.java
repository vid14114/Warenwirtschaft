package model;

import control.AuftragSession;
import javafx.beans.property.*;
import javafx.scene.image.Image;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by Viktor on 08.05.2015.
 */
@Entity
public class Produkt {
    private IntegerProperty produktNr;
    private StringProperty bez;
    private FloatProperty ekPreis;
    private FloatProperty vkPreis;
    private Kategorie kateogrie;
    private DoubleProperty umsatz;
    private byte[] image;
    private ObjectProperty<Image> fxImage;

    @Id
    public int getProduktNr() {
        return produktNr.get();
    }

    public void setProduktNr(int produktNr) {
        this.produktNr = new SimpleIntegerProperty(produktNr);
    }

    @Transient
    public IntegerProperty getProduktNrProperty() {
        return produktNr;
    }

    @Basic
    public String getBez() {
        return bez.get();
    }

    public void setBez(String bez) {
        this.bez = new SimpleStringProperty(bez);
    }

    @Transient
    public StringProperty getKategorieProperty() {
        return new SimpleStringProperty(kateogrie.name());
    }

    @Transient
    public StringProperty getBezProperty() {
        return bez;
    }

    @Basic
    public float getVkPreis() {
        return vkPreis.get();
    }

    public void setVkPreis(float vkPreis) {
        this.vkPreis = new SimpleFloatProperty(vkPreis);
    }

    @Transient
    public FloatProperty getVkPreisProperty() {
        return vkPreis;
    }

    @Basic
    public float getEkPreis() {
        if (ekPreis != null)
            return ekPreis.get();
        return 0;
    }

    public void setEkPreis(float ekPreis) {
        this.ekPreis = new SimpleFloatProperty(ekPreis);
    }

    @Transient
    public FloatProperty getEkPreisProperty() {
        return ekPreis;
    }


    @Basic
    public Kategorie getKateogrie() {
        return kateogrie;
    }

    public void setKateogrie(Kategorie kateogrie) {
        this.kateogrie = kateogrie;
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
        List<Auftrag> auftraege = AuftragSession.getAllAuftraege();
        double umsatz = 0;
        for (Auftrag a : auftraege) {
            for (Produktmenge pm : a.getProdukte()) {
                if (pm.getProdukt().equals(this))
                    umsatz += Math.round(pm.getMenge() * getVkPreis() * 100.0) / 100.0;
            }
        }
        setUmsatz(umsatz);
    }

    @Column(name = "bigimage", length = 2000000)
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        if (image != null) {
            this.image = image;
            readImage();
        }
    }

    private void readImage() {
        ByteArrayInputStream bais = new ByteArrayInputStream(image);
        fxImage = new SimpleObjectProperty<>(new Image(bais));
    }

    @Transient
    public ObjectProperty<Image> getImageProperty() {
        return fxImage;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && ((Produkt) obj).getProduktNr() == getProduktNr();
    }
}