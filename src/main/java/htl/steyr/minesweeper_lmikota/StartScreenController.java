package htl.steyr.minesweeper_lmikota;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartScreenController implements Initializable {
    @FXML
    public ChoiceBox difficultyChoiceBox;
    @FXML
    public TextField playerNameTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        difficultyChoiceBox.getItems().addAll("Rookie", "Intermediate", "Master");
        difficultyChoiceBox.setValue("Rookie"); //Default value
        playerNameTextField.setText("User"); //Default value
    }

    public void startButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gamefield-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            GamefieldController controller = fxmlLoader.getController();

            controller.setSelectedDifficulty(difficultyChoiceBox.getValue().toString());
            controller.playerNameTextField.setText(playerNameTextField.getText());
            Stage stage = (Stage) difficultyChoiceBox.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            controller.startButtonClicked();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exitButtonClicked(ActionEvent actionEvent) {
        Platform.exit();
    }
}