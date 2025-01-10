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

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Application extends javafx.application.Application {


    private String greenImagePath = "";
    private String difficultySelected = "";
    private int rows = 8;
    private int columns = 8;

    @Override
    public void start(Stage stage) throws IOException {
        HBox difficultyOptionPane = new HBox();
        difficultyOptionPane.setAlignment(Pos.CENTER);
        Text text = new Text("Choose Difficulty");

        Button difficultyEasyButton = new Button("Easy");
        Button difficultyMediumButton = new Button("Medium");
        Button difficultyHardButton = new Button("Hard");

        difficultyOptionPane.getChildren().addAll(text,difficultyEasyButton,difficultyMediumButton,difficultyHardButton);

        difficultyHardButton.setPrefSize(100,50);
        difficultyEasyButton.setPrefSize(100,50);
        difficultyMediumButton.setPrefSize(100,50);
        difficultyEasyButton.setOnAction(actionEvent -> createMinesweeperField("Easy",stage));
        difficultyMediumButton.setOnAction(actionEvent -> createMinesweeperField("Medium",stage));
        difficultyHardButton.setOnAction(actionEvent -> createMinesweeperField("Hard",stage));


        Scene scene = new Scene(difficultyOptionPane);
        stage.setTitle("Minesweeper");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * This Method creates a Button with an Image inside of it
     *
     * @param    imagePath The Path of the image which will be loaded into the button
     *
     * @return Button
     */
    private Button createMinesweeperButton(String imagePath) {
        URL imageUrl = getClass().getResource(imagePath);
        if (imageUrl != null) {
//            Image image = new Image(imageUrl.toString());
//            ImageView imageView = new ImageView(image);
//            imageView.setFitHeight(35);
//            imageView.setFitWidth(35);
//            Button button = new Button("", imageView);
            Button button = new Button();
//            button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
            button.setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    button.setText("\uD83D\uDEA9");
                }
            });
            button.setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    /**
                     * @ToDo
                     *
                     *Methode Aufrufen die den Button wegmacht und das Feld aufdeckt
                     */
                }
            });
            return button;
        } else {
            System.err.println("Image not found: " + imagePath);
            return new Button("Image Error");
        }
    }

    /**
     * This Method creates MinesweeperField depending on the difficulty
     *
     * @param    difficulty The selected difficulty the MinesweeperField will be loaded in
     * @param    stage The stage reference
     *
     */
    private void createMinesweeperField (String difficulty, Stage stage) {
        setDifficultySelected(difficulty);
        GridPane minesweeperPane = new GridPane();
        switch (difficulty) {
            case "Easy":
                setRows(8);
                setColumns(8);
                break;
            case "Medium":
                setRows(16);
                setColumns(16);
                break;
            case "Hard":
                setRows(16);
                setColumns(30);
                break;
        }

        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getColumns(); col++) {
                setGreenImagePath("/htl/steyr/minesweeper_lmikota/img/Solid_green.png");
                Button button = createMinesweeperButton(getGreenImagePath());
                minesweeperPane.add(button, col, row);
            }
        }

        Scene scene = new Scene(minesweeperPane);
        stage.setTitle("Minesweeper");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
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

    public String getDifficultySelected() {
        return difficultySelected;
    }

    public void setDifficultySelected(String difficultySelected) {
        this.difficultySelected = difficultySelected;
    }

}