package htl.steyr.minesweeper_lmikota;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Random;

public class Controller {
    private String difficultySelected = "";
    private int rows = -1;
    private int columns = -1;
    private int totalBombs = 0;
    private boolean marked = false;
    private boolean revealed = false;
    private boolean[][] bombGrid;

    /**
     * This Method creates a Button with a colored background
     *
     * @return Button
     */
    public Button createMinesweeperButton(int row, int col) {
        Button button = new Button();
        button.setPrefSize(35, 35);
        button.setStyle("-fx-background-color: lightgreen; -fx-border-color: black;");
        button.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                if (!button.getText().equals("\uD83D\uDEA9") && !button.getText().equals("\uD83D\uDC80")) {
                    button.setText("\uD83D\uDEA9");
                    setMarked(true);
                } else {
                    button.setText("");
                    setMarked(false);
                }
            } else if (event.getButton() == MouseButton.PRIMARY && !marked) {
                if (!"\uD83D\uDEA9".equals(button.getText())) {
                    if (bombGrid[row][col]) {
                        button.setStyle("-fx-background-color: white; -fx-border-color: black;");
                        button.setText("\uD83D\uDC80");
                    } else {
                        button.setStyle("-fx-background-color: white; -fx-border-color: black;");
                    }
                    setRevealed(true);
                }
            }
        });

        return button;
    }

    /**
     * This Method creates MinesweeperField depending on the difficulty
     *
     * @param difficulty The selected difficulty the MinesweeperField will be loaded in
     * @param stage      The stage reference
     */
    public void createMinesweeperField(String difficulty, Stage stage) {
        setDifficultySelected(difficulty);
        GridPane minesweeperPane = new GridPane();
        switch (difficulty) {
            case "Easy":
                setRows(8);
                setColumns(8);
                totalBombs = 10;
                break;
            case "Medium":
                setRows(16);
                setColumns(16);
                totalBombs = 40;
                break;
            case "Hard":
                setRows(16);
                setColumns(30);
                totalBombs = 99;
                break;
        }

        bombGrid = new boolean[getRows()][getColumns()];
        placeBombs();

        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getColumns(); col++) {
                Button button = createMinesweeperButton(row, col);

                minesweeperPane.add(button, col, row);
                minesweeperPane.setPrefSize(500, 500);
            }
        }
        Scene scene = new Scene(minesweeperPane);
        stage.setTitle("Minesweeper - " + difficulty);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Places bombs randomly on the grid.
     */
    private void placeBombs() {
        Random random = new Random();
        int placedBombs = 0;

        while (placedBombs < totalBombs) {
            int row = random.nextInt(getRows());
            int col = random.nextInt(getColumns());

            if (!bombGrid[row][col]) {
                bombGrid[row][col] = true;
                placedBombs++;
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public String getDifficultySelected() {
        return difficultySelected;
    }

    public void setDifficultySelected(String difficultySelected) {
        this.difficultySelected = difficultySelected;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }
}