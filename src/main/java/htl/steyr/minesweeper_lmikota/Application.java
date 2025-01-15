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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        Controller controller = new Controller();
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
        difficultyEasyButton.setOnAction(actionEvent -> controller.createMinesweeperField("Easy",stage));
        difficultyMediumButton.setOnAction(actionEvent -> controller.createMinesweeperField("Medium",stage));
        difficultyHardButton.setOnAction(actionEvent -> controller.createMinesweeperField("Hard",stage));


        Scene scene = new Scene(difficultyOptionPane);
        stage.setTitle("Minesweeper");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }




}