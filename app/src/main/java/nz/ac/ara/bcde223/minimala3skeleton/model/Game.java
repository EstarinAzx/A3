package nz.ac.ara.bcde223.minimala3skeleton.model;

public class Game {

    private int score;

    public Game() {
        score = 0;
    }

    public void increaseScore() {
        score++;
    }

    public void resetScore() {
        score = 0;
    }

    public int getScore() {
        return score;
    }
}