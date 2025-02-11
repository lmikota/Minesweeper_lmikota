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
import java.io.*;
import java.net.URL;
import java.util.*;

public class GamefieldController implements Initializable {
    private int cols;
    private int rows;
    private int bombs;
    private boolean isEndScreen = false;
    private boolean matchWon;
    private String selectedDifficulty;
    private static final String ROOKIE_FILE_NAME = "RookieHS.ser";
    private static final String INTERMEDIATE_FILE_NAME = "IntermediateHS.ser";
    private static final String MASTER_FILE_NAME = "MasterHS.ser";

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
    public GridPane gameFieldGridPane;
    @FXML
    public Text timerDisplay;
    @FXML
    public Text markedFieldsDisplay;
    @FXML
    public Button menuButton;
    @FXML
    public Text highScoreText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    /**
     * Startet das Spiel mit den aktuellen Einstellungen und zeigt das Spielfeld an.
     * <p>
     * Diese Methode setzt die Spielparameter zurück, zeigt den Highscore an,
     * startet den Timer und erstellt das Spielfeld basierend auf den ausgewählten
     * Schwierigkeitsgraden. Außerdem wird die Anzeige der markierten Felder
     * und die Anzahl der Bomben aktualisiert.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */

    public void startButtonClicked() throws IOException, ClassNotFoundException {
        setEndScreen(false);
        markedFieldsDisplay.setText("\uD83D\uDEA9: " + difficultySettingsHashMap.get(getSelectedDifficulty()).getBombs());
        setMarkedFieldsCount(difficultySettingsHashMap.get(getSelectedDifficulty()).getBombs());
        User highScoreUser = fileReader(getFileName());
        if (!highScoreUser.getUsername().equals("DEFAULT_USERNAME")) {
            highScoreText.setText("Highscore: " + highScoreUser);
        } else {
            highScoreText.setText("Be the first one to win!");
        }
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

    /**
     * Schließt das aktuelle Fenster und bringt dich zum Startmenü
     *
     * @param actionEvent
     */

    public void menuButtonClicked(ActionEvent actionEvent) {
        Stage gameFieldStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        gameFieldStage.close();

        MinesweeperApplication application = new MinesweeperApplication();
        try {
            application.start(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Schließt die Anwendung
     */

    public void exitButtonClicked() {
        Platform.exit();
    }

    /**
     * Startet den Timer für das Spiel.
     * <p>
     * Diese Methode startet einen vorerst unendlichen Timer, der jede Sekunde,
     * die vergangene Spielzeit (in Sekunden) aktualisiert und auf dem Timer-Display anzeigt.
     */

    public void startTimer() {
        timerTimeLine = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> {
            setSecondsSinceStart(getSecondsSinceStart() + 1);
            timerDisplay.setText(String.valueOf(getSecondsSinceStart()));
        }));
        timerTimeLine.setCycleCount(Timeline.INDEFINITE);
        timerTimeLine.play();
    }

    /**
     * Stoppt den Timer
     */

    public void stopTimer() {
        timerTimeLine.stop();
    }

    /**
     * Erstellt das Spielfeld-Gitter basierend auf den aktuellen Zeilen- und Spaltenwerten.
     * <p>
     * Diese Methode setzt das Layout des `gameFieldGridPane` zurück und fügt dynamisch
     * Zeilen- und Spaltenbeschränkungen hinzu, basierend auf der Anzahl der Zeilen (rows)
     * und Spalten (cols). Danach werden die Buttons in die Gridpane platziert und die Bomben
     * werden definiert.
     */

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

        try {
            placeButtonsIntoGrid();
        } catch (IOException e) {
            e.printStackTrace();
        }
        defineBombs();
    }

    /**
     * Platziert die Buttons in die gameFieldGridpane.
     * <p>
     * Diese Methode lädt für jede Zelle im Gitter ein neues buttonPane mit einem MinesweeperButtonController,
     * setzt die Position des Buttons im Gitter und fügt ihn zur gameFieldGridPane hinzu. Es wird zwischen
     * zwei Button-Stilen ("lightGreenButton" und "greenButton") gewechselt, damit ein Schachbrettmuster entsteht.
     */

    private void placeButtonsIntoGrid() throws IOException {
        for (int col = 0; col < cols; ++col) {
            for (int row = 0; row < rows; ++row) {
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
            }
        }
    }

    /**
     * Definiert die Positionen der Bomben im Spielfeld.
     * <p>
     * Diese Methode platziert zufällig Bomben auf dem Spielfeld, indem sie für jede Bombe eine zufällige
     * Spalte und Zeile auswählt. Wenn die Position noch keine Bombe enthält, wird sie als Bombe markiert.
     * Der Vorgang wird wiederholt, bis die gewünschte Anzahl an Bomben gesetzt wurde.
     * <p>
     * Ebenfalls werden die Bomben, welche rund um das Feld liegen definiert durch setBombsNearby().
     */

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


    /**
     * Gibt das Kind-Element im GridPane an der angegebenen Spalte und Zeile zurück.
     * <p>
     * Diese Methode durchsucht alle Child Elemente des gameFieldGridPane und vergleicht deren Spalten und Zeilen
     * mit den übergebenen Parametern. Wenn das Element an der angegebenen Position gefunden wird, wird es zurückgegeben.
     * Andernfalls wird `null` zurückgegeben, wenn keine Übereinstimmung gefunden wird.
     *
     * @param col
     * @param row
     * @return Das `Node`-Element an der angegebenen Position oder `null`, wenn kein Element gefunden wurde.
     */

    private Node getChildAt(int col, int row) {
        for (Node child : gameFieldGridPane.getChildren()) {
            if (GridPane.getColumnIndex(child) == col && GridPane.getRowIndex(child) == row) {
                return child;
            }
        }
        return null;

    }

    /**
     * Gibt den Controller des Buttons an der angegebenen Spalten- und Zeilenposition zurück.
     * <p>
     * Diese Methode ruft das Node-Element an der angegebenen Position im gameFieldGridPane ab.
     * Falls das Node-Element existiert, wird der zugehörige MinesweeperButtonController, der als
     * UserData des Nodes gespeichert ist, zurückgegeben. Andernfalls wird null zurückgegeben.
     *
     * @param col
     * @param row
     * @return Der MinesweeperButtonController an der angegebenen Position
     */

    private MinesweeperButtonController getController(int col, int row) {
        Node node = getChildAt(col, row);
        if (node != null) {
            return (MinesweeperButtonController) node.getUserData();
        }
        return null;
    }

    /**
     * Deckt alle Felder am Ende desSpiels auf.
     * <p>
     * Stoppt den Timer und ruft die fileWriter() Funktion auf um falls es einen neuen Highscore gibt,
     * ihn in die Serialiserungs File zu schreiben.
     *
     * @throws FileWriteException wenn beim Schreiben in die File was schief läuft
     */

    public void revealAllFields() throws FileWriteException {
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
        try {
            fileWriter(getFileName());
            loadEndScreen();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new FileWriteException("Error while Writing into File: " + getFileName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gibt den Namen der File zurück, welcher die Highscore User Serialisierung je nach
     * Schwierigkeitsgrad enthält.
     *
     * @return Die File-Name Konstante je nach Schwierigkeit
     */

    public String getFileName() {
        switch (getSelectedDifficulty()) {
            case "Intermediate":
                return INTERMEDIATE_FILE_NAME;
            case "Rookie":
                return ROOKIE_FILE_NAME;
            case "Master":
                return MASTER_FILE_NAME;
            default:
                return "Something went wrong";
        }
    }

    /**
     * Schreibt die Spielerinformationen in eine Datei.
     * <p>
     * Diese Methode überprüft, ob das Spiel gewonnen wurde (isMatchWon()). Wenn dies der Fall ist, wird ein
     * User-Objekt erstellt, das den Spielernamen und die Zeit enthält, die der Spieler benötigt hat, um das Spiel zu beenden.
     * Falls die gespeicherte Zeit des geladenen Benutzers (aus der Datei) größer ist als die aktuelle Spielzeit,
     * wird das neue Benutzerobjekt mit der aktuellen Spielzeit überschrieben. Das Benutzerobjekt wird dann in einer Datei
     * gespeichert, die durch den Dateinamen angegeben ist.
     *
     * @param filename
     * @throws IOException
     * @throws ClassNotFoundException
     */

    public void fileWriter(String filename) throws IOException, ClassNotFoundException {
        if (!isMatchWon()) {
            return;
        }
        User user = null;
        User loadedUser = fileReader(filename);
        if (loadedUser.getFinishTime() <= Integer.parseInt(timerDisplay.getText())) {
            user = new User(playerNameTextField.getText().trim(), loadedUser.getFinishTime(), isMatchWon());
        } else {
            user = new User(playerNameTextField.getText().trim(), Integer.parseInt(timerDisplay.getText()), isMatchWon());
        }
        new ObjectOutputStream(new FileOutputStream(filename)).writeObject(user);
    }

    /**
     * Lest das User Objekt aus der Serialisierungs Datei,
     * falls diese Datei nicht Existiert wird ein defaukt User mit extrem schlechter Zeit angegeben.
     *
     * @param filename der Pfad der .ser File
     * @return Den User der File
     * @throws ClassNotFoundException
     * @throws IOException
     */

    public User fileReader(String filename) throws ClassNotFoundException, IOException {
        if (!new File(filename).exists()) {
            User user = new User("DEFAULT_USERNAME", 10000000, false);
            new ObjectOutputStream(new FileOutputStream(filename)).writeObject(user);
        }
        return (User) new ObjectInputStream(new FileInputStream(filename)).readObject();
    }

    /**
     * Überprüft, ob die Gewinnbedingungen für das Spiel erfüllt sind.
     * <p>
     * Diese Methode prüft, ob alle Felder, die keine Bomben enthalten,
     * aufgedeckt wurden und ob alle Bomben korrekt markiert wurden.
     * Falls beide Bedingungen erfüllt sind, wird das Spiel als gewonnen markiert.
     * Der Timer wird gestoppt, die Spielergebnisse werden in eine Datei geschrieben,
     * und der Endbildschirm wird geladen.
     *
     * @throws FileWriteException
     */

    public void checkWinCondition() throws FileWriteException {
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
            try {
                fileWriter(getFileName());
                loadEndScreen();
            } catch (IOException e) {
                e.printStackTrace();
                throw new FileWriteException("Error while Writing into File: " + getFileName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Setzt die ausgewählte Schwierigkeitsstufe und aktualisiert die Anzeige der markierten Felder.
     * <p>
     * Diese Methode speichert die vom Benutzer gewählte Schwierigkeitsstufe und aktualisiert
     * die Anzeige der markierten Felder, indem die Anzahl der Bomben basierend
     * auf der ausgewählten Schwierigkeit angezeigt wird.
     *
     * @param difficulty
     */

    public void setSelectedDifficulty(String difficulty) {
        this.selectedDifficulty = difficulty;
        markedFieldsDisplay.setText("\uD83D\uDEA9: " + difficultySettingsHashMap.get(difficulty).getBombs());
    }

    /**
     * Lädt und zeigt den Endbildschirm des Spiels an.
     * <p>
     * Diese Methode lädt die FXML-Datei für den Endbildschirm endScreen.fxml und zeigt diesen auf einer neuen stage an,
     * wenn der Endbildschirm noch nicht angezeigt wurde.
     * Dabei wird der EndScreenController mit dem aktuellen GamefieldController
     * verknüpft.
     *
     * @throws IOException
     */

    public void loadEndScreen() throws IOException {
        if (!isEndScreen()) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("endScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            EndScreenController controller = fxmlLoader.getController();
            controller.setGamefieldController(this);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            setEndScreen(true);
        }
    }

    /**
     * Deckt alle benachbarten Felder eines leeren Feldes auf, das keine Bomben in der Nähe hat.
     * Diese Methode prüft alle angrenzenden Felder des angegebenen Feldes
     * und deckt diejenigen auf, die keine Bomben enthalten und noch nicht aufgedeckt wurden.
     * Wenn ein aufgedecktes Feld keine benachbarten Bomben hat,
     * wird die Methode rekursiv auf benachbarte Felder angewendet.
     *
     * @param col Die Spalte des Ausgangsfeldes.
     * @param row Die Zeile des Ausgangsfeldes.
     */

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
        try {
            checkWinCondition();
        } catch (FileWriteException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Berechnet die Anzahl der benachbarten Bomben für ein bestimmtes Feld.
     * Diese Methode prüft alle benachbarten Felder und zählt, wie viele davon Bomben enthalten.
     *
     * @param col
     * @param row
     * @return Die Anzahl der benachbarten Bomben.
     */

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