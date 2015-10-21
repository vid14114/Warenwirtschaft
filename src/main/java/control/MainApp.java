package control;

import control.fxml.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Viktor on 21.05.2015.
 */
public class MainApp extends Application {
    private Stage primaryStage;
    private AnchorPane rootLayout;

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Zentrallager");
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        initRootLayout();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void initRootLayout() {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/Kunden.fxml");
        System.out.println(path.getPath());
        loader.setLocation(path);
        try {
            rootLayout = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        KundenOverviewController controller = loader.getController();
        controller.setMainApp(this);

        BestellempfehlungController b = new BestellempfehlungController();
        Map<Integer, CalculatedInfo> data = b.calculateForAlarm();
        String msg = "";
        for (CalculatedInfo ci : data.values()) {
            if (Integer.valueOf(ci.tageBisLeer.get()) < 365) {
                msg += ci.produktNr.get() + ": " + ci.tageBisLeer.get() + " Tage\n";
            }
        }
        if (msg.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(getPrimaryStage());
            alert.setTitle("Produkte bald nicht mehr auf Lager");
            alert.setContentText(msg);
            alert.showAndWait();
        }
    }

    public boolean showNewKundeDialog() {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/NewKunde.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            NewKundeController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            return controller.isAddClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showNewAuftragDialog() {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/Auftrag.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            AuftragDetailsController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            return controller.isSaveClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showDetailAuftragDialog(Iterable<Produktmenge> lp, Auftrag a) {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/Auftrag.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            AuftragDetailsController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.populateData(lp, a);
            dialogStage.showAndWait();
            return controller.isSaveClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showJahresvergleich() {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/Jahresvergleich.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showNewProduktDialog() {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/NewProdukt.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            NewProduktController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            return controller.isAddClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showProduktDetailsDialog(Produkt p) {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/NewProdukt.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            NewProduktController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.populateData(p);
            dialogStage.showAndWait();
            return controller.isAddClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showNewInventurDialog() {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/Inventur.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            InventurDetailsController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            return controller.isSaveClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showDetailInventurDialog(Iterable<Produktmenge> lp, Inventur i) {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/Inventur.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            InventurDetailsController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.populateData(lp, i);
            dialogStage.showAndWait();
            return controller.isSaveClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showNewLieferantDialog() {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/NewLieferant.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            NewLieferantController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            return controller.isAddClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showZulieferungDetails(Iterable<Produktmenge> lp, Zulieferung z) {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/Zulieferung.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            ZulieferungDetailsController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.populateData(lp, z);
            dialogStage.showAndWait();
            return controller.isSaveClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showLieferantDetails(Lieferant l) {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/NewLieferant.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            NewLieferantController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.populateData(l);
            dialogStage.showAndWait();
            return controller.isAddClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showNewZulieferungDialog() {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/Zulieferung.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            ZulieferungDetailsController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            return controller.isSaveClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showBestellempfehlung() {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/Bestellempfehlung.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showSollIstVergleich() {
        FXMLLoader loader = new FXMLLoader();
        URL path = getClass().getResource("/fxml/SollIst.fxml");
        loader.setLocation(path);
        try {
            TitledPane dialog = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
