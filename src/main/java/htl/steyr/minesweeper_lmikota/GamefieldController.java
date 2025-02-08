package htl.steyr.minesweeper_lmikota;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GamefieldController implements Initializable {
    private int cols;
    private int rows;
    private int bombs;
    private boolean isEndScreen = false;
    private boolean matchWon;
    private String selectedDifficulty;

    private int secondsSinceStart;
    private Timeline timerTimeLine;
    private MinesweeperButtonController minesweeperButtonController;
    public int markedFieldsCount = 187;


    public HashMap<String, DifficultySettings> difficultySettingsHashMap = new HashMap<>() {{
        put("Rookie", new DifficultySettings(6, 10, 7));
        put("Intermediate", new DifficultySettings(9, 15, 15));
        put("Master", new DifficultySettings(15, 25, 50));
    }};

    @FXML
    public Text playerNameTextField;
    @FXML
    public ChoiceBox difficultyChoiceBox;
    @FXML
    public GridPane gameFieldGridPane;
    @FXML
    public Text timerDisplay;
    @FXML
    public Text markedFieldsDisplay;
    @FXML
    public Button startButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public void startButtonClicked() {
        setEndScreen(false);
        markedFieldsDisplay.setText("\uD83D\uDEA9: " + difficultySettingsHashMap.get(getSelectedDifficulty()).getBombs());
        setMarkedFieldsCount(difficultySettingsHashMap.get(getSelectedDifficulty()).getBombs());
        if (timerTimeLine != null) {
            stopTimer();
        }
        setSecondsSinceStart(0);
        startTimer();
        setRows(difficultySettingsHashMap.get(getSelectedDifficulty()).getRows());
        setCols(difficultySettingsHashMap.get(getSelectedDifficulty()).getCols());
        setBombs(difficultySettingsHashMap.get(getSelectedDifficulty()).getBombs());
        createGameFieldGrid();
    }

    public void exitButtonClicked() {
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
                    controller.setGamefieldController(this);
                    controller.setPosition(col, row);

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
        while (bombCount < getBombs()) {
            Random r = new Random();
            int randCols = r.nextInt(getCols());
            int randRows = r.nextInt(getRows());

            MinesweeperButtonController controller = getController(randCols, randRows);
            if (!controller.isBomb()) {
                controller.setBomb(true);
                ++bombCount;
            }
        }

        for (int col = 0; col < getCols(); ++col) {
            for (int row = 0; row < getRows(); ++row) {
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


    public void revealAllFields() {
        for (int col = 0; col < getCols(); col++) {
            for (int row = 0; row < getRows(); row++) {
                MinesweeperButtonController controller = getController(col, row);
                if (controller != null) {
                    if (!controller.isRevealed()) {
                        controller.reveal();
                    }
                }
            }
        }
        stopTimer();
        fileWriter();
        loadEndScreen();
    }

    /**
     * @ToDo Exceptionhandling wenn es Probleme beim writen gab
     */
//Integer.parseInt(timerDisplay.getText())
    public void fileWriter() {
        User user = new User(playerNameTextField.getText().trim(), 10, isMatchWon());
        BufferedWriter writer;
        Gson gson = new Gson();
        try {
            writer = new BufferedWriter(new FileWriter("htl/steyr/minesweeper_lmikota/UsersPersonalRecords.json"));
            writer.write(gson.toJson(user.getClass()));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void checkWinCondition() {
        boolean allNonBombCellsRevealed = true;
        boolean allBombsCorrectlyMarked = true;

        for (int col = 0; col < getCols(); col++) {
            for (int row = 0; row < getRows(); row++) {
                MinesweeperButtonController controller = getController(col, row);

                if (controller != null) {
                    if (!controller.isBomb() && !controller.isRevealed()) {
                        allNonBombCellsRevealed = false;
                    }
                    if (controller.isBomb() && !controller.isMarked()) {
                        allBombsCorrectlyMarked = false;
                    }
                }
            }
        }

        if (allBombsCorrectlyMarked && allNonBombCellsRevealed) {
            setMatchWon(true);
            stopTimer();
            fileWriter();
            loadEndScreen();
        }
    }

    public void setSelectedDifficulty(String difficulty) {
        this.selectedDifficulty = difficulty;
        markedFieldsDisplay.setText("\uD83D\uDEA9: " + difficultySettingsHashMap.get(difficulty).getBombs());
    }

    public void loadEndScreen() {
        if (!isEndScreen()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("endScreen.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                EndScreenController controller = fxmlLoader.getController();
                controller.setGamefieldController(this);
                stage.setScene(scene);
                stage.show();
                setEndScreen(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void revealEmptyFields(int col, int row) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                // das Feld 0,0 ist das Feld auf dem ich stehe und muss nicht mehr aufgedeckt werden
                //also wird die nächste Iteration aufgerufen
                if (i == 0 && j == 0) {
                    continue;
                }

                int nextCol = col + i;
                int nextRow = row + j;

                // Prüfe ob das Feld innerhalb des Spielfelds ist
                if (nextCol >= 0 && nextCol < getCols() && nextRow >= 0 && nextRow < getRows()) {
                    MinesweeperButtonController neighbor = getController(nextCol, nextRow);

                    if (neighbor != null && !neighbor.isRevealed() && !neighbor.isBomb()) {
                        // Wenn es keine Bombe ist, decke ich es auf
                        neighbor.setPosition(nextCol, nextRow); // Setze die Position für spätere Verwendung
                        neighbor.reveal();

                        // Wenn dieses Feld auch keine Bomben in der Nähe hat,
                        // wird die Rekursion durch den reveal() Aufruf fortgesetzt
                    }
                }
            }
        }
        checkWinCondition();
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

    public int getMarkedFieldsCount() {
        return markedFieldsCount;
    }

    public void setMarkedFieldsCount(int markedFieldsCount) {
        this.markedFieldsCount = markedFieldsCount;
    }

    public boolean isEndScreen() {
        return isEndScreen;
    }

    public void setEndScreen(boolean endScreen) {
        isEndScreen = endScreen;
    }

    public boolean isMatchWon() {
        return matchWon;
    }

    public void setMatchWon(boolean matchWon) {
        this.matchWon = matchWon;
    }

    public String getSelectedDifficulty() {
        return selectedDifficulty;
    }
}