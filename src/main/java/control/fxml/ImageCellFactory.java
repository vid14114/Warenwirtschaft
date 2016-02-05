package control.fxml;

import control.fxml.dataStructures.TableRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * Created by Viktor on 22.09.2015.
 */
public class ImageCellFactory implements Callback<TableColumn<TableRow, Image>, TableCell<TableRow, Image>> {
    @Override
    public TableCell<TableRow, Image> call(TableColumn<TableRow, Image> param) {
        return new CalculatedInfoImageTableCell();
    }

    private static class CalculatedInfoImageTableCell extends TableCell<TableRow, Image> {
        @Override
        public void updateItem(Image item, boolean empty) {
            ImageView iv = new ImageView();
            iv.setFitHeight(50);
            iv.setFitWidth(70);
            setStyle("-fx-background-color: aliceblue");
            if (!empty) {
                iv.setImage(item);
                setGraphic(iv);
            } else
                setGraphic(null);
        }
    }
}
