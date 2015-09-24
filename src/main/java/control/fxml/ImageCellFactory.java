package control.fxml;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * Created by Viktor on 22.09.2015.
 */
public class ImageCellFactory implements Callback<TableColumn<CalculatedInfo, Image>, TableCell<CalculatedInfo, Image>> {
    @Override
    public TableCell<CalculatedInfo, Image> call(TableColumn<CalculatedInfo, Image> param) {
        return new CalculatedInfoImageTableCell();
    }

    private static class CalculatedInfoImageTableCell extends TableCell<CalculatedInfo, Image> {
        @Override
        public void updateItem(Image item, boolean empty) {
            if (!empty) {
                ImageView iv = new ImageView();
                iv.setFitHeight(50);
                iv.setFitWidth(70);
                iv.setImage(item);
                setGraphic(iv);
                setStyle("-fx-background-color: aliceblue");
            }
        }
    }
}
