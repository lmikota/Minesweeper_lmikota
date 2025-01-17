package htl.steyr.minesweeper_lmikota;

public class BombException extends Exception{

    public BombException() {
        super("You've clicked on a Bomb!");
    }
}
