package htl.steyr.minesweeper_lmikota;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ResourceBundle;

public class MinesweeperButtonController implements Initializable {

    @FXML
    public Label bombLabel;
    @FXML
    public Label infoLabel;
    @FXML
    public Button button;

    private boolean bomb = false; // Liegt eine Bombe hinter dem Button?
    private boolean marked = false; // Wurde das Feld markiert?
    private boolean revealed = false; // Wurde das Feld aufgedeckt?
    private int bombsNearby = 0; // Wieviele Bomben liegen im Umkreis?
    private GamefieldController gamefieldController;

    private int col = -1; // die aktuelle Spalte des Buttons
    private int row = -1; // die aktuelle Zeile des Buttons

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bombLabel.setVisible(false); // verstecke das Element
        infoLabel.setVisible(false); // verstecke das Element
        bombLabel.setTextAlignment(TextAlignment.CENTER);
        infoLabel.setTextAlignment(TextAlignment.CENTER);
    }

    /**
     * Lässt flaggen auf die Buttons setzen (sekundäre Maustaste) und ruft bei click mit der primären Maustaste
     * die reveal() funktion auf
     *
     * @param mouseEvent
     */
    public void buttonClicked(MouseEvent mouseEvent) {
        if (getGamefieldController().getMarkedFieldsCount() == 187) {
            getGamefieldController().setMarkedFieldsCount(getGamefieldController().getBombs());
        }
        if (mouseEvent != null) {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                if (!isMarked()) {
                    setMarked(true);
                    button.setText("🚩");
                    getGamefieldController().setMarkedFieldsCount(getGamefieldController().getMarkedFieldsCount() - 1);
                    showMarkedFields(getGamefieldController().getMarkedFieldsCount());
                } else if (isMarked()) {
                    setMarked(false);
                    button.setText("");
                    getGamefieldController().setMarkedFieldsCount(getGamefieldController().getMarkedFieldsCount() + 1);
                    showMarkedFields(getGamefieldController().getMarkedFieldsCount());
                }
            } else if (mouseEvent.getButton() == MouseButton.PRIMARY && !marked) {
                reveal();
                setRevealed(true);
                button.setVisible(false);
            }
        }
    }

    public void showMarkedFields(int markedFieldsCount) {
        getGamefieldController().markedFieldsDisplay.setText("🚩: "+markedFieldsCount);
    }


    public void reveal() {
        revealed = true;
        button.setVisible(false);

        if (isBomb()) {
            bombLabel.setVisible(true);
            getGamefieldController().revalAllFields();
        } else {
            infoLabel.setVisible(true);
            if (getBombsNearby() == 0) {
                getGamefieldController().revealEmptyFields(col,row);
            }
        }
    }

    public void setPosition(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public boolean isBomb() {
        return bomb;
    }

    public void setBomb(boolean bomb) {
        this.bomb = bomb;

        bombLabel.setVisible(bomb);
    }

    public void setBombsNearby(int num) {
        this.bombsNearby = num;

        if (!isBomb()) {
            bombLabel.setVisible(false);
            infoLabel.setVisible(true);

            infoLabel.setText(Integer.toString(num));
        }
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public int getBombsNearby() {
        return bombsNearby;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }


    public GamefieldController getGamefieldController() {
        return gamefieldController;
    }

    public void setGamefieldController(GamefieldController gamefieldController) {
        this.gamefieldController = gamefieldController;
    }

}