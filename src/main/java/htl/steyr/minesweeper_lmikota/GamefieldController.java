package htl.steyr.minesweeper_lmikota;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;

public class GamefieldController implements Initializable {
    private int cols;
    private int rows;
    private int bombs;

    private int secondsSinceStart;
    private Timeline timerTimeLine;
    private MinesweeperButtonController minesweeperButtonController;

    private HashMap<String, DifficultySettings> difficultySettingsHashMap = new HashMap<>() {{
        put("Rookie", new DifficultySettings(6, 10, 12));
        put("Intermediate", new DifficultySettings(9, 15, 32));
        put("Master", new DifficultySettings(15, 25, 80));
    }};

    @FXML
    public ChoiceBox difficultyChoiceBox;
    @FXML
    public GridPane gameFieldGridPane;
    @FXML
    public Text timerDisplay;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Thread.setDefaultUncaughtExceptionHandler(this::exceptionHandler);
        difficultyChoiceBox.getItems().addAll("Rookie", "Intermediate", "Master");
        difficultyChoiceBox.setValue("Rookie"); //Default value

    }

    public void startButtonClicked(ActionEvent actionEvent) {
        startTimer();
        setRows(difficultySettingsHashMap.get(difficultyChoiceBox.getValue()).getRows());
        setCols(difficultySettingsHashMap.get(difficultyChoiceBox.getValue()).getCols());
        setBombs(difficultySettingsHashMap.get(difficultyChoiceBox.getValue()).getBombs());
        createGameFieldGrid();
    }

    public void exitButtonClicked(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void startTimer() {
        timerTimeLine = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> {
            setSecondsSinceStart(getSecondsSinceStart() + 1);
            timerDisplay.setText("Timer: " + getSecondsSinceStart());
        }));
        timerTimeLine.setCycleCount(Timeline.INDEFINITE);
        timerTimeLine.play();
    }

    public void stopTimer() {
        timerTimeLine.stop();
    }


    private void createGameFieldGrid() {

        gameFieldGridPane.getChildren().clear();
        gameFieldGridPane.getColumnConstraints().clear();
        gameFieldGridPane.getRowConstraints().clear();

        double columnPercentage = 100.0 / cols;
        double rowPercentage = 100.0 / rows;

        for (int i = 0; i < cols; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(columnPercentage);
            columnConstraints.setHgrow(Priority.ALWAYS);
            gameFieldGridPane.getColumnConstraints().add(columnConstraints);
        }

        for (int i = 0; i < rows; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(rowPercentage);
            rowConstraints.setVgrow(Priority.ALWAYS);
            gameFieldGridPane.getRowConstraints().add(rowConstraints);
        }

        placeButtonsIntoGrid();
        defineBombs();
    }


    private void placeButtonsIntoGrid() {
        for (int col = 0; col < cols; ++col) {
            for (int row = 0; row < rows; ++row) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("minesweeperbutton-view.fxml"));
                    Pane buttonPane = loader.load();
                    MinesweeperButtonController controller = loader.getController();
                    buttonPane.setUserData(controller);
//                    switch (difficultyChoiceBox.getValue().toString()) {
//                        case "Rookie":
//                            controller.bombLabel.getStyleClass().add("rookie");
//                            controller.infoLabel.getStyleClass().add("rookie");
//                            controller.button.getStyleClass().add("rookie");
//                            break;
//                        case "Intermediate":
//                            controller.bombLabel.getStyleClass().add("intermediate");
//                            controller.infoLabel.getStyleClass().add("intermediate");
//                            controller.button.getStyleClass().add("intermediate");
//                            break;
//                        case "Master":
//                            controller.bombLabel.getStyleClass().add("master");
//                            controller.infoLabel.getStyleClass().add("master");
//                            controller.button.getStyleClass().add("master");
//                            break;
//                    }
                    if ((col + row) % 2 == 0) {
                        controller.button.getStyleClass().add("lightGreenButton");
                    } else {
                        controller.button.getStyleClass().add("greenButton");
                    }

                    gameFieldGridPane.add(buttonPane, col, row);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void defineBombs() {
        int bombCount = 0;
        while (bombCount < bombs) {
            Random r = new Random();
            int randCols = r.nextInt(cols);
            int randRows = r.nextInt(rows);

            MinesweeperButtonController controller = getController(randCols, randRows);

            if (!controller.isBomb()) {
                controller.setBomb(true);
                ++bombCount;
            }
        }

        for (int col = 0; col < cols; ++col) {
            for (int row = 0; row < rows; ++row) {
                MinesweeperButtonController controller = getController(col, row);
                controller.setBombsNearby(getBombsNearPosition(col, row));
            }
        }
    }

    private Node getChildAt(int col, int row) {
        for (Node child : gameFieldGridPane.getChildren()) {
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


    public void revealFields(final int COL, final int ROW) {
        for (Integer col = (-1); col <= 1; ++col) {

            for (Integer row = (-1); row <= 1; ++row) {

                if (COL + col >= 0 && COL + col < getCols() && ROW + row >= 0 && ROW + row < getRows()) {
                    minesweeperButtonController = getController(COL + col, ROW + row);

                    if (minesweeperButtonController != null && !minesweeperButtonController.isRevealed() && !minesweeperButtonController.isBomb()) {
                        try {
                            minesweeperButtonController.reveal();
                        } catch (BombException e) {
                            System.out.println("Game Over!");
                        }

                        if (minesweeperButtonController.getBombsNearby() == 0) {
                            revealFields(COL + col, ROW + row);
                        }
                    }
                }
            }
        }
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

    private void exceptionHandler(Thread t, Throwable e) {
        stopTimer();
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
        for (int col = 0; col < cols; ++col) {
            for (int row = 0; row < rows; ++row) {
                MinesweeperButtonController controller = getController(col, row);
                try {
                    controller.buttonClicked(null);
                } catch (BombException ex) {
                    System.out.println("You clicked a Bomb!");
                }
            }
        }
    }

    public int getCols() {
        return this.cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getRows() {
        return this.rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getBombs() {
        return this.bombs;
    }

    public void setBombs(int bombs) {
        this.bombs = bombs;
    }

    public int getSecondsSinceStart() {
        return secondsSinceStart;
    }

    public void setSecondsSinceStart(int secondsSinceStart) {
        this.secondsSinceStart = secondsSinceStart;
    }
}