/*-----------------------------------------------------------------------------
 *              Hoehere Technische Bundeslehranstalt STEYR
 *           Fachrichtung Elektronik und Technische Informatik
 *----------------------------------------------------------------------------*/
/**
 * Kurzbeschreibung
 *
 * @author : Leander Mikota
 * @date : 20.12.2024
 * @details A Minesweeper Application with varaianting levels of difficulty,
 * made in JavaFX using recursive method calls by lmikota.
 */
package htl.steyr.minesweeper_lmikota;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Application extends javafx.application.Application {


    private String greenImagePath = "";
    private int rows = 8;
    private int columns = 8;

    @Override
    public void start(Stage stage) throws IOException {
        GridPane gridPane = new GridPane();

        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getColumns(); col++) {
                setGreenImagePath("/htl/steyr/minesweeper_lmikota/img/Solid_green.png");
                Button button = createMinesweeperButton(getGreenImagePath());
                gridPane.add(button, col, row);
            }
        }

        Scene scene = new Scene(gridPane);
        stage.setTitle("Minesweeper");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * This Method creates a Button with an Image insode of it
     *
     * @param    imagePath The Path of the image which will be loaded into the button
     *
     * @return Button
     */
    private Button createMinesweeperButton(String imagePath) {
        URL imageUrl = getClass().getResource(imagePath);
        if (imageUrl != null) {
            Image image = new Image(imageUrl.toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(35);
            imageView.setFitWidth(35);
            Button button = new Button("", imageView);
            button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
            return button;
        } else {
            System.err.println("Image not found: " + imagePath);
            return new Button("Image Error");
        }
    }

    public String getGreenImagePath() {
        return greenImagePath;
    }

    public void setGreenImagePath(String greenImagePath) {
        this.greenImagePath = greenImagePath;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }
}