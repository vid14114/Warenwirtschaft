package control.fxml;

import control.fxml.dataStructures.AizRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * Created by Viktor on 09.06.2015.
 */
class VCellFactory implements Callback<TableColumn<AizRow, String>, TableCell<AizRow, String>> {
    @Override
    public TableCell<AizRow, String> call(TableColumn<AizRow, String> param) {
        return new EintragStringTableCell();
    }

    private static class EintragStringTableCell extends TableCell<AizRow, String> {

        @Override
        public void updateItem(final String item, boolean empty) {
            if (item != null) {
                setStyle("-fx-background-color: aliceblue");
                setTextFill(Color.BLACK);
                setText(item);
            } else
                setStyle("-fx-background-color: white");
        }
    }
}
