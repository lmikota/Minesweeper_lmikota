package htl.steyr.minesweeper_lmikota;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EndScreenController implements Initializable {
    public Button playAgainButton;
    public Text gameOverTextField;
    private GamefieldController gamefieldController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    public void backToMenuButtonClicked(ActionEvent actionEvent) {
        Stage endScreenStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        endScreenStage.close();

        Stage gameFieldStage = (Stage) getGamefieldController().gameFieldGridPane.getScene().getWindow();
        gameFieldStage.close();

        MinesweeperApplication application = new MinesweeperApplication();
        try {
            application.start(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playAgainButtonClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
        getGamefieldController().startButtonClicked();
    }

    public void updateText() {
        if(getGamefieldController().isMatchWon()) {
            gameOverTextField.setText("You Won");
        } else {
            gameOverTextField.setText("You Lost");
        }
    }

    public void exitButtonClicked() {
        Platform.exit();
    }

    public GamefieldController getGamefieldController() {
        return gamefieldController;
    }

    public void setGamefieldController(GamefieldController gamefieldController) {
        this.gamefieldController = gamefieldController;
        updateText();
    }
}
