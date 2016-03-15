package control.fxml;

import control.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import model.*;

import java.io.File;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by Viktor on 21.05.2015.
 */
public class KundenOverviewController {
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final Callback imageCellFactory = new ImageCellFactory();
    private List<Produkt> produkte;
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
    private TextField filterField;

    @FXML
    private TableView<Inventur> inventurTable;
    @FXML
    private TableColumn<Inventur, LocalDate> invDateColumn;

    @FXML
    private TableView<Lieferant> lieferantenTable;
    @FXML
    private TableColumn<Lieferant, String> lNameColumn;
    @FXML
    private TableColumn<Lieferant, Number> lieferzeitColumn;

    @FXML
    private TableView<Zulieferung> zulieferungenTable;
    @FXML
    private TableColumn<Zulieferung, LocalDate> zuDatumColumn;

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
                    public void updateItem(LocalDate datum, boolean empty) {
                        super.updateItem(datum, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(datum.format(dtf));
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
                    public void updateItem(String kunde, boolean empty) {
                        super.updateItem(kunde, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(kunde);
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
                    public void updateItem(Number wert, boolean empty) {
                        super.updateItem(wert, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(wert.toString());
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
        pImageColumn.setCellFactory(imageCellFactory);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts(newValue);
        });

        invDateColumn.setCellValueFactory(cellData -> cellData.getValue().getDatumProperty());
        invDateColumn.setCellFactory(new Callback<TableColumn<Inventur, LocalDate>, TableCell<Inventur, LocalDate>>() {
            @Override
            public TableCell<Inventur, LocalDate> call(TableColumn<Inventur, LocalDate> col) {
                final TableCell<Inventur, LocalDate> cell = new TableCell<Inventur, LocalDate>() {
                    @Override
                    public void updateItem(LocalDate invDate, boolean empty) {
                        super.updateItem(invDate, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(invDate.format(dtf));
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
        lNameColumn.setCellFactory(param -> {
            final TableCell<Lieferant, String> cell = new TableCell<Lieferant, String>() {
                @Override
                public void updateItem(String name, boolean empty) {
                    if (!empty)
                        setText(name);
                }
            };
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getClickCount() > 1)
                    handleLieferantDetails();
            });
            return cell;
        });
        lieferzeitColumn.setCellValueFactory(cellData -> cellData.getValue().getLieferzeitProperty());
        lieferzeitColumn.setCellFactory(param -> {
            final TableCell<Lieferant, Number> cell = new TableCell<Lieferant, Number>() {
                @Override
                public void updateItem(Number lieferzeit, boolean empty) {
                    if (!empty)
                        setText(lieferzeit.toString());
                }
            };
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getClickCount() > 1)
                    handleLieferantDetails();
            });
            return cell;
        });

        zuDatumColumn.setCellValueFactory(cellData -> cellData.getValue().getDatumProperty());
        zuDatumColumn.setCellFactory(new Callback<TableColumn<Zulieferung, LocalDate>, TableCell<Zulieferung, LocalDate>>() {
            @Override
            public TableCell<Zulieferung, LocalDate> call(TableColumn<Zulieferung, LocalDate> col) {
                final TableCell<Zulieferung, LocalDate> cell = new TableCell<Zulieferung, LocalDate>() {
                    @Override
                    public void updateItem(LocalDate zuDate, boolean empty) {
                        super.updateItem(zuDate, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(zuDate.format(dtf));
                        }
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getClickCount() > 1) {
                        handleZulieferungDetails();
                    }
                });
                return cell;
            }
        });
    }

    public void filterProducts(String s) {
        if (s.length() > 0) {
            List<Produkt> filteredProducts = produkte.stream().filter(p -> p.getBez().contains(s) || ("" + p.getProduktNr()).contains(s)).collect(Collectors.toList());
            produkteTable.setItems(FXCollections.observableArrayList(filteredProducts));
        } else
            produkteTable.setItems(FXCollections.observableArrayList(produkte));
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
            alert.setContentText("Shop hat bereits Umsatz gemacht und kann nicht mehr entfernt werden");
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
        //produkteTable.setItems(FXCollections.observableArrayList(ProduktSession.getAllProdukte()));
        produkte = ProduktSession.getAllProdukte();
        filterProducts(filterField.getText());
        inventurTable.setItems(FXCollections.observableArrayList(InventurSession.getAllInventuren()));
        lieferantenTable.setItems(FXCollections.observableArrayList(LieferantSession.getAllLieferanten()));
        zulieferungenTable.setItems(FXCollections.observableArrayList(ZulieferungSession.getAllZulieferungen()));
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
    private void handleLieferantDetails() {
        int selectedIndex = lieferantenTable.getSelectionModel().getSelectedIndex();
        Lieferant l = lieferantenTable.getItems().get(selectedIndex);
        boolean refresh = mainApp.showLieferantDetails(l);
        if (refresh)
            handleRefresh();
    }

    @FXML
    private void handleNewZulieferung() {
        boolean refresh = mainApp.showNewZulieferungDialog();
        if (refresh)
            handleRefresh();
    }

    @FXML
    private void handleDeleteZulieferung() {
        int selectedIndex = zulieferungenTable.getSelectionModel().getSelectedIndex();
        Zulieferung z = zulieferungenTable.getItems().get(selectedIndex);
        ZulieferungSession.removeZulieferung(z);
        lieferantenTable.getItems().remove(selectedIndex);
    }

    @FXML
    private void handleZulieferungDetails() {
        int selectedIndex = zulieferungenTable.getSelectionModel().getSelectedIndex();
        Zulieferung z = zulieferungenTable.getItems().get(selectedIndex);
        boolean refresh = mainApp.showZulieferungDetails(z.getProdukte(), z);
        if (refresh)
            handleRefresh();
    }

    @FXML
    private void handleBestellempfehlung() {
        mainApp.showBestellempfehlung();
    }

    @FXML
    private void handleSollIstVergleich() {
        mainApp.showSollIstVergleich();
    }

    @FXML
    private void export() {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyyMMdd");
        String filename = "export" + ldt.format(dtf1) + ".sql";
        ExportSession.exportDatabase(mainApp.getPath() + filename);
    }
}