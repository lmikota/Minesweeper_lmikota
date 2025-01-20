package htl.steyr.minesweeper_lmikota;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MinesweeperApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MinesweeperApplication.class.getResource("gamefield-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Minesweeper");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/htl/steyr/minesweeper_lmikota/img/Minesweeper-logo.png")));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}