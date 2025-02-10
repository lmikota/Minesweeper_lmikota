package htl.steyr.minesweeper_lmikota;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private int finishTime;
    private boolean matchWon;

    public User(String username, int finishTime, boolean matchWon) {
        setUsername(username);
        setFinishTime(finishTime);
        setMatchWon(matchWon);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public boolean isMatchWon() {
        return matchWon;
    }

    @Override
    public String toString() {
        return username + " | " + finishTime + "s";
    }

    public void setMatchWon(boolean matchWon) {
        this.matchWon = matchWon;
    }
}
