package htl.steyr.minesweeper_lmikota;

public class DifficultySettings {
    private int rows;
    private int cols;
    private int bombs;

    public DifficultySettings (int rows, int cols, int bombs) {
        setBombs(bombs);
        setCols(cols);
        setRows(rows);
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getBombs() {
        return bombs;
    }

    public void setBombs(int bombs) {
        this.bombs = bombs;
    }
}
