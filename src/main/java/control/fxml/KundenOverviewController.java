package control.fxml;

import control.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import model.*;

import java.io.File;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * Created by Viktor on 21.05.2015.
 */
public class KundenOverviewController {
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    @FXML
    private TableView<Kunde> kundenTable;
    @FXML
    private TableColumn<Kunde, String> nameColumn;
    @FXML
    private TableColumn<Kunde, Number> umsatzColumn;

    @FXML
    private TableView<Auftrag> auftraegeTable;
    @FXML
    private TableColumn<Auftrag, LocalDate> datumColumn;
    @FXML
    private TableColumn<Auftrag, String> kundeColumn;
    @FXML
    private TableColumn<Auftrag, Number> wertColumn;

    @FXML
    private TableView<Produkt> produkteTable;
    @FXML
    private TableColumn<Produkt, Number> pNrColumn;
    @FXML
    private TableColumn<Produkt, String> bezColumn;
    @FXML
    private TableColumn<Produkt, String> kategorieColumn;
    @FXML
    private TableColumn<Produkt, Number> ekPreisColumn;
    @FXML
    private TableColumn<Produkt, Number> vkPreisColumn;
    @FXML
    private TableColumn<Produkt, Number> pUmsatzColumn;
    @FXML
    private TableColumn<Produkt, Image> pImageColumn;

    @FXML
    private TableView<Inventur> inventurTable;
    @FXML
    private TableColumn<Inventur, LocalDate> invDateColumn;

    @FXML
    private TableView<Lieferant> lieferantenTable;
    @FXML
    private TableColumn<Lieferant, String> lNameColumn;
    private MainApp mainApp;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        umsatzColumn.setCellValueFactory(cellData -> cellData.getValue().getUmsatzProperty());

        datumColumn.setCellValueFactory(cellData -> cellData.getValue().getErstellungProperty());
        datumColumn.setSortable(true);
        datumColumn.setCellFactory(new Callback<TableColumn<Auftrag, LocalDate>, TableCell<Auftrag, LocalDate>>() {
            @Override
            public TableCell<Auftrag, LocalDate> call(TableColumn<Auftrag, LocalDate> col) {
                final TableCell<Auftrag, LocalDate> cell = new TableCell<Auftrag, LocalDate>() {
                    @Override
                    public void updateItem(LocalDate firstName, boolean empty) {
                        super.updateItem(firstName, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(firstName.format(dtf));
                        }
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getClickCount() > 1) {
                        handleAuftragDetails();
                    }
                });
                return cell;
            }
        });
        kundeColumn.setCellValueFactory(cellData -> cellData.getValue().getKundeProperty());
        kundeColumn.setCellFactory(new Callback<TableColumn<Auftrag, String>, TableCell<Auftrag, String>>() {
            @Override
            public TableCell<Auftrag, String> call(TableColumn<Auftrag, String> col) {
                final TableCell<Auftrag, String> cell = new TableCell<Auftrag, String>() {
                    @Override
                    public void updateItem(String firstName, boolean empty) {
                        super.updateItem(firstName, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(firstName);
                        }
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getClickCount() > 1) {
                        handleAuftragDetails();
                    }
                });
                return cell;
            }
        });
        wertColumn.setCellValueFactory(cellData -> cellData.getValue().getWertProperty());
        wertColumn.setCellFactory(new Callback<TableColumn<Auftrag, Number>, TableCell<Auftrag, Number>>() {
            @Override
            public TableCell<Auftrag, Number> call(TableColumn<Auftrag, Number> col) {
                final TableCell<Auftrag, Number> cell = new TableCell<Auftrag, Number>() {
                    @Override
                    public void updateItem(Number firstName, boolean empty) {
                        super.updateItem(firstName, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(firstName.toString());
                        }
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getClickCount() > 1) {
                        handleAuftragDetails();
                    }
                });
                return cell;
            }
        });

        pNrColumn.setCellValueFactory(cellData -> cellData.getValue().getProduktNrProperty());
        pNrColumn.setSortable(true);
        bezColumn.setCellValueFactory(cellData -> cellData.getValue().getBezProperty());
        kategorieColumn.setCellValueFactory(cellData -> cellData.getValue().getKategorieProperty());
        ekPreisColumn.setCellValueFactory(cellData -> cellData.getValue().getEkPreisProperty());
        vkPreisColumn.setCellValueFactory(cellData -> cellData.getValue().getVkPreisProperty());
        pUmsatzColumn.setCellValueFactory(cellData -> cellData.getValue().getUmsatzProperty());
        pImageColumn.setCellValueFactory(cellData -> cellData.getValue().getImageProperty());
        pImageColumn.setCellFactory(param -> new TableCell<Produkt, Image>() {
            @Override
            public void updateItem(Image item, boolean empty) {
                if (!empty) {
                    ImageView iv = new ImageView();
                    iv.setFitHeight(50);
                    iv.setFitWidth(70);
                    iv.setImage(item);
                    setGraphic(iv);
                }
            }
        });

        invDateColumn.setCellValueFactory(cellData -> cellData.getValue().getDatumProperty());
        invDateColumn.setCellFactory(new Callback<TableColumn<Inventur, LocalDate>, TableCell<Inventur, LocalDate>>() {
            @Override
            public TableCell<Inventur, LocalDate> call(TableColumn<Inventur, LocalDate> col) {
                final TableCell<Inventur, LocalDate> cell = new TableCell<Inventur, LocalDate>() {
                    @Override
                    public void updateItem(LocalDate firstName, boolean empty) {
                        super.updateItem(firstName, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(firstName.format(dtf));
                        }
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getClickCount() > 1) {
                        handleInventurDetails();
                    }
                });
                return cell;
            }
        });

        lNameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        try {
            kundenTable.setItems(FXCollections.observableArrayList(KundeSession.getAllKunden()));
        } catch (Throwable t) {
            try {
                String classPath = MainApp.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                String[] arr = classPath.split("/");
                arr[arr.length - 1] = "wawi";
                String path = arr[0];
                for (int i = 1; i < arr.length; i++)
                    path += "/" + arr[i];
                MyConfig.setUrl(path);
                kundenTable.setItems(FXCollections.observableArrayList(KundeSession.getAllKunden()));
            } catch (URISyntaxException e) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select Database Location");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("DB File", "*.h2.db")
                );
                File f = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
                MyConfig.setUrl(f.toString().replace(".h2.db", ""));
                kundenTable.setItems(FXCollections.observableArrayList(KundeSession.getAllKunden()));
            }
        }
        MyConfig.setOk();
        handleRefresh();
    }

    @FXML
    private void handleNewKunde() {
        boolean refresh = mainApp.showNewKundeDialog();
        if (refresh) {
            handleRefresh();
        }
    }

    @FXML
    private void handleDeleteKunde() {
        int selectedIndex = kundenTable.getSelectionModel().getSelectedIndex();
        Kunde rem = kundenTable.getItems().get(selectedIndex);
        if (rem.getUmsatzProperty() == null || rem.getUmsatzProperty().get() == 0) {
            KundeSession.removeKunde(rem);
            kundenTable.getItems().remove(selectedIndex);
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Umsatz != 0");
            alert.setContentText("Kunde hat bereits Umsatz und kann nicht mehr entfernt werden");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleNewAuftrag() {
        boolean refresh = mainApp.showNewAuftragDialog();
        if (refresh) {
            handleRefresh();
        }
    }

    @FXML
    private void handleDeleteAuftrag() {
        int selectedIndex = auftraegeTable.getSelectionModel().getSelectedIndex();
        Auftrag a = auftraegeTable.getItems().get(selectedIndex);
        List<Kunde> lk = KundeSession.getAllKunden();
        for (Kunde k : lk) {
            if (k.getAuftraege().contains(a)) {
                k.getAuftraege().remove(a);
                KundeSession.saveKunde(k);
                break;
            }
        }
        auftraegeTable.getItems().remove(selectedIndex);
        AuftragSession.removeAuftrag(a);
        a.getProdukte().stream().forEach(pm -> ProduktSession.saveProdukt(pm.getProdukt()));
        handleRefresh();
    }

    @FXML
    private void handleAuftragDetails() {
        int selectedIndex = auftraegeTable.getSelectionModel().getSelectedIndex();
        Auftrag a = auftraegeTable.getItems().get(selectedIndex);
        boolean refresh = mainApp.showDetailAuftragDialog(a.getProdukte(), a);
        if (refresh) {
            handleRefresh();
        }
    }

    @FXML
    private void handleJahresvergleich() {
        mainApp.showJahresvergleich();
    }

    @FXML
    private void handleNewProdukt() {
        boolean refresh = mainApp.showNewProduktDialog();
        if (refresh)
            handleRefresh();
    }

    @FXML
    private void handleDeleteProdukt() {
        int selectedIndex = produkteTable.getSelectionModel().getSelectedIndex();
        Produkt p = produkteTable.getItems().get(selectedIndex);
        if (p.getUmsatzProperty() == null || p.getUmsatzProperty().get() == 0) {
            ProduktSession.removeProdukt(p);
            produkteTable.getItems().remove(selectedIndex);
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Umsatz != 0");
            alert.setContentText("Produkt hat bereits Umsatz gemacht und kann nicht mehr entfernt werden");
            alert.showAndWait();
        }
        handleRefresh();
    }

    @FXML
    private void handleProduktDetails() {
        int selectedIndex = produkteTable.getSelectionModel().getSelectedIndex();
        Produkt p = produkteTable.getItems().get(selectedIndex);
        boolean refresh = mainApp.showProduktDetailsDialog(p);
        if (refresh) {
            handleRefresh();
        }
    }

    @FXML
    private void handleRefresh() {
        kundenTable.setItems(FXCollections.observableArrayList(KundeSession.getAllKunden()));
        auftraegeTable.setItems(FXCollections.observableArrayList(AuftragSession.getAllAuftraege()));
        produkteTable.setItems(FXCollections.observableArrayList(ProduktSession.getAllProdukte()));
        inventurTable.setItems(FXCollections.observableArrayList(InventurSession.getAllInventuren()));
        lieferantenTable.setItems(FXCollections.observableArrayList(LieferantSession.getAllLieferanten()));
    }

    @FXML
    private void handleNewInventur() {
        boolean refresh = mainApp.showNewInventurDialog();
        if (refresh) {
            handleRefresh();
        }
    }

    @FXML
    private void handleInventurDetails() {
        int selectedIndex = inventurTable.getSelectionModel().getSelectedIndex();
        Inventur i = inventurTable.getItems().get(selectedIndex);
        boolean refresh = mainApp.showDetailInventurDialog(i.getProdukte(), i);
        if (refresh) {
            handleRefresh();
        }
    }

    @FXML
    private void handleDeleteInventur() {
        int selectedIndex = inventurTable.getSelectionModel().getSelectedIndex();
        Inventur i = inventurTable.getItems().get(selectedIndex);
        InventurSession.removeInventur(i);
        inventurTable.getItems().remove(selectedIndex);
        handleRefresh();
    }

    @FXML
    private void handleNewLieferant() {
        boolean refresh = mainApp.showNewLieferantDialog();
        if (refresh)
            handleRefresh();
    }

    @FXML
    private void handleDeleteLieferant() {
        int selectedIndex = lieferantenTable.getSelectionModel().getSelectedIndex();
        Lieferant rem = lieferantenTable.getItems().get(selectedIndex);
        LieferantSession.removeLieferant(rem);
        lieferantenTable.getItems().remove(selectedIndex);
    }

    @FXML
    private void handleBestellempfehlung() {
        mainApp.showBestellempfehlung();
    }

    @FXML
    private void handleSollIstVergleich() {
        mainApp.showSollIstVergleich();
    }
}