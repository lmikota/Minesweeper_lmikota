package htl.steyr.minesweeper_lmikota;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.ResourceBundle;

import static javafx.scene.input.MouseButton.SECONDARY;

public class GamefieldController implements Initializable {

    /**
     * Konstanten für die Anzahl der Zeilem, die Anzahl der Spalten und die Anzahl der Bomben
     * <p>
     * static = nur mit KLasse.Variablenname
     * final = Variable kann nur 1 mal ein Wert zugewiesen werden
     */

    public static final int COLS = 10;
    public static final int ROWS = 10;
    public static final int BOMBS = 20;

    public GridPane gameField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Sage Javafx, dass wenn ein Fehler auftritt, die exceptionHandler Methode
        //der GamefliedController Klasse aufgerufen werden soll
        Thread.setDefaultUncaughtExceptionHandler(this::exceptionHandler);
        /*
         * Die Initialize - Methode wird aufgerufen, soblad der Controller mir der FXML - Datei "verheiratet" wurde.
         * In der Initialize - Methode kann auf die GUI - Elemente (Buttons, Labels, ...) zugegriffen werden.
         */

        /*
         * @ToDo:
         *      Befüllen Sie jede Zelle der gameField GridPane mit einem Button
         */

        for (int col = 0; col < COLS; ++col) {
            for (int row = 0; row < ROWS; ++row) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("minesweeperbutton-view.fxml"));
                    Pane buttonPane = loader.load();
                    MinesweeperButtonController controller = loader.getController();
                    buttonPane.setUserData(controller);

                    gameField.add(buttonPane, col, row);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        int bombCount = 0;
        while (bombCount < BOMBS) {
            Random r = new Random();
            int randCols = r.nextInt(COLS);
            int randRows = r.nextInt(ROWS);

            MinesweeperButtonController controller = getController(randCols, randRows);

            if (!controller.isBomb()) {
                controller.setBomb(true);
                ++bombCount;
            }
        }

        for (int col = 0; col < COLS; ++col) {
            for (int row = 0; row < ROWS; ++row) {
                MinesweeperButtonController controller = getController(col, row);
                controller.setBombsNearby(getBombsNearPosition(col, row));
            }
        }
    }

    private Node getChildAt(int col, int row) {
        for (Node child : gameField.getChildren()) {
            if (GridPane.getColumnIndex(child) == col && GridPane.getRowIndex(child) == row) {
                return child;
            }
        }
        return null;

    }

    private MinesweeperButtonController getController(int col, int row) {
        Node node = getChildAt(col, row);
        if (node != null) {
            return (MinesweeperButtonController) node.getUserData();
        }
        return null;
    }

    public void revealFields (int col, int row) {

    }

    private int getBombsNearPosition(int col, int row) {
        int bombsNearby = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {


                    MinesweeperButtonController controller = getController(col + i, row + j);

                    if (controller != null && controller.isBomb()) {
                        ++bombsNearby;
                    }
                }
            }
        }

        return bombsNearby;
    }

    public void exceptionHandler(Thread t, Throwable e) {

        /**
         * @ToDo: Prüfe ob es sich um eine Bombexception handelt.
         * Falls Ja: Decke gesamtes Speilfeld auf
         *          --> Durchlaufe das gesamte Speilfeld (GridPane).
         *          Hole von der aktuellen Zelle den zuständigen MinesweeperButtonController.
         *          Decke jedes Feld auf, indem du die ButtonClicked -
         *          Methode aufrufst (zu  übergeben: null).
         *
         * Falls Nein: Mache nichts - da es ein anderer Fehle war.
         */
        for (int col = 0; col < COLS; ++col) {
            for (int row = 0; row < ROWS; ++row) {
                MinesweeperButtonController controller = getController(col, row);
                try {
                    controller.buttonClicked(null);
                } catch (BombException ex) {

                }
            }
        }
        System.out.println("Ein Fehler ist Aufgetreten");
    }
}