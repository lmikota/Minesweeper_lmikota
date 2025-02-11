package htl.steyr.minesweeper_lmikota;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    @FXML
    public Button playAgainButton;
    @FXML
    public Text gameOverTextField;
    private GamefieldController gamefieldController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * Beendet das aktuelle Spiel und kehrt zum Hauptmenü zurück.
     *
     * Diese Methode schließt sowohl das Endbildschirm-Fenster als auch das Spielfeld-Fenster
     * und startet die Anwendung neu, um das Hauptmenü erneut anzuzeigen.
     *
     * @param actionEvent
     */

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

    /**
     * Startet das Spiel erneut, nachdem der Play-Again-Button geklickt wurde.
     *
     * Diese Methode schließt das aktuelle Fenster und ruft die Methode auf startbuttonclicked(); auf um das Spiel
     * von vorne zu starten.
     *
     * @param actionEvent
     */

    public void playAgainButtonClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
        try {
            getGamefieldController().startButtonClicked();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gibt in einem Textfeld an, ob du verloren/gewonnen hast.
     */

    public void updateText() {
        if (getGamefieldController().isMatchWon()) {
            gameOverTextField.setText("You Won");
        } else {
            gameOverTextField.setText("You Lost");
        }
    }

    /**
     * Schließt die Anwendung
     */

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
