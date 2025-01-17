package htl.steyr.minesweeper_lmikota;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class MinesweeperButtonController implements Initializable {

    public Label bombLabel;
    public Label infoLabel;
    public Button button;

    private boolean bomb = false; // Liegt eine Bombe hinter edem Button?
    private boolean marked = false; // Wurde das Feld markiert?
    private boolean revealed = false; // Wurde das Feld aufgedeckt?
    private int bombsNearby = 0; // Wieviele Bomben liegen im Umkreis?

    private int col = -1; // die aktuelle Spalte des Buttons
    private int row = -1; // die aktuelle Zeile des Buttons


    public void buttonClicked(MouseEvent mouseEvent) throws BombException {

        /**
         *Prüfe, ob der Button mit der linken oder rechten Maustaste gedrückt wurde.
         *
         * Wenn er mit der Rechten Maustaste gedrückt wurde, so setze eine Flagge 🚩 als Text.
         * Wenn er mit der Linken Maustaste gedrückt wurde, so soll der Button verschwinden.
         */
        if(mouseEvent != null) {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                if (!marked) {
                    marked = true;
                    button.setText("🚩");
                } else {
                    marked = false;
                    button.setText("");
                }
            } else if (mouseEvent.getButton() == MouseButton.PRIMARY && !marked) {
                revealed = true;
                button.setVisible(false);

                if (isBomb()) {
                    throw new BombException();
                } else {
                    //Feld und angrenzende leere Felder aufdecken
                }

            }
        } else {
            revealed = true;
            button.setVisible(false);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        /**
         * initialize ist der frühstmögliche Zeitpunkt, um auf GUI-Elemente zuzugreifen.
         */

        bombLabel.setVisible(false); // verstecke das Element
        infoLabel.setVisible(false); // verstecke das Element

    }

    public void setPosition(int col, int row){
        this.col = col;
        this.row = row;
    }

    public boolean isBomb(){
        return bomb;
    }

    public void setBomb(boolean bomb){
        this.bomb = bomb;

        bombLabel.setVisible(bomb);
    }

    public void setBombsNearby (int num) {
        this.bombsNearby = num;

        if(!isBomb()) {
            bombLabel.setVisible(false);
            infoLabel.setVisible(true);

            infoLabel.setText(Integer.toString(num));
        }
    }
}