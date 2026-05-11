package nz.ac.ara.bcde223.minimala3skeleton.viewmodel;

import nz.ac.ara.bcde223.minimala3skeleton.model.Color;
import nz.ac.ara.bcde223.minimala3skeleton.model.Direction;
import nz.ac.ara.bcde223.minimala3skeleton.model.Game;
import nz.ac.ara.bcde223.minimala3skeleton.model.Message;
import nz.ac.ara.bcde223.minimala3skeleton.model.PlayableSquare;
import nz.ac.ara.bcde223.minimala3skeleton.model.Shape;

public class GameViewModel {

    private final Game game;

    public GameViewModel() {
        this.game = new Game();
        setupStarterLevel();
    }

    private void setupStarterLevel() {
        game.addLevel(3, 3);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                game.addSquare(new PlayableSquare(Color.RED, Shape.DIAMOND), row, col);
            }
        }
        game.addEyeball(1, 1, Direction.RIGHT);
        game.addGoal(1, 2);
    }

    public String getStatusText() {
        return String.format(
                "Eyeball: row %d, col %d (facing %s)%nGoals: %d/%d",
                game.getEyeballRow(),
                game.getEyeballColumn(),
                game.getEyeballDirection(),
                game.getCompletedGoalCount(),
                game.getGoalCount());
    }

    public Message tryMove(Direction direction) {
        int row = game.getEyeballRow();
        int col = game.getEyeballColumn();
        int destRow = row;
        int destCol = col;
        switch (direction) {
            case UP:    destRow = row - 1; break;
            case DOWN:  destRow = row + 1; break;
            case LEFT:  destCol = col - 1; break;
            case RIGHT: destCol = col + 1; break;
        }
        if (destRow < 0 || destRow >= game.getLevelHeight()
                || destCol < 0 || destCol >= game.getLevelWidth()) {
            return Message.MOVING_OVER_BLANK;
        }
        Message message = game.messageIfMovingTo(destRow, destCol);
        if (message == Message.OK) {
            game.moveTo(destRow, destCol);
        }
        return message;
    }
}
