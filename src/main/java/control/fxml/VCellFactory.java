package control.fxml;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * Created by Viktor on 09.06.2015.
 */
public class VCellFactory implements Callback<TableColumn<Eintrag, String>, TableCell<Eintrag, String>> {
    @Override
    public TableCell<Eintrag, String> call(TableColumn<Eintrag, String> param) {
        return new EintragStringTableCell();
    }

    private static class EintragStringTableCell extends TableCell<Eintrag, String> {

        @Override
        public void updateItem(final String item, boolean empty) {
            if (item != null) {
                setStyle("-fx-background-color: aliceblue");
                setTextFill(Color.BLACK);
                setText(item);
                //setStyle("-fx-prompt-text-fill: black");
            } else
                setStyle("-fx-background-color: white");
        }
    }
}
