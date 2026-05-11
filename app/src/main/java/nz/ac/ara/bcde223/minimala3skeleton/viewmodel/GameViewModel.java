package nz.ac.ara.bcde223.minimala3skeleton.viewmodel;

import nz.ac.ara.bcde223.minimala3skeleton.model.Game;

public class GameViewModel {

    private final Game game;

    public GameViewModel() {
        game = new Game();
    }

    // User actions
    public void onIncreaseScore() {
        game.increaseScore();
    }

    public void onResetScore() {
        game.resetScore();
    }

    // Data for UI
    public String getScoreText() {
        return "Score: " + game.getScore();
    }

    public boolean isResetEnabled() {
        return game.getScore() > 0;
    }
}
