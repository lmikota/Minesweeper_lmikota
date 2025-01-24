package htl.steyr.minesweeper_lmikota;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

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
    private int markedFieldsCount = 0;

    private int col = -1; // die aktuelle Spalte des Buttons
    private int row = -1; // die aktuelle Zeile des Buttons

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bombLabel.setVisible(false); // verstecke das Element
        infoLabel.setVisible(false); // verstecke das Element
    }

    public void buttonClicked(MouseEvent mouseEvent) {
        if (mouseEvent != null) {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                if (!isMarked()) {
                    setMarked(true);
                    button.setText("🚩");
                    setMarkedFieldsCount(getMarkedFieldsCount() + 1);
                } else if (isMarked()) {
                    setMarked(false);
                    button.setText("");
                    setMarkedFieldsCount(getMarkedFieldsCount() - 1);
                }
            } else if (mouseEvent.getButton() == MouseButton.PRIMARY && !marked) {
                reveal();
                setRevealed(true);
                button.setVisible(false);
            }
        } else {
            setRevealed(true);
            button.setVisible(false);
            reveal();
        }
    }


    public void reveal() {
        revealed = true;
        button.setVisible(false);

        if (isBomb()) {
            bombLabel.setVisible(true);
            gamefieldController.revalAllFields();
        } else {
            infoLabel.setVisible(true);
            if (getBombsNearby() == 0) {
                gamefieldController.revealEmptyFields(col,row);
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


    public int getMarkedFieldsCount() {
        return markedFieldsCount;
    }

    public void setMarkedFieldsCount(int markedFieldsCount) {
        this.markedFieldsCount = markedFieldsCount;
    }
}