package nz.ac.ara.bcde223.minimala3skeleton.viewmodel;

import nz.ac.ara.bcde223.minimala3skeleton.model.BlankSquare;
import nz.ac.ara.bcde223.minimala3skeleton.model.Color;
import nz.ac.ara.bcde223.minimala3skeleton.model.Direction;
import nz.ac.ara.bcde223.minimala3skeleton.model.Game;
import nz.ac.ara.bcde223.minimala3skeleton.model.Message;
import nz.ac.ara.bcde223.minimala3skeleton.model.PlayableSquare;
import nz.ac.ara.bcde223.minimala3skeleton.model.Shape;

/**
 * Sits between the Android UI and the Eyeball Maze {@link Game} model.
 * It owns a {@code Game}, builds the starter level, and exposes simple
 * read-only accessors for rendering plus a {@link #tryMove(Direction)} that
 * delegates all rule decisions to the model.
 */
public class GameViewModel {

    private static final String LEVEL_NAME = "Level 1";

    private Game game;
    private int totalGoals;

    public GameViewModel() {
        reset();
    }

    /** Rebuilds the level from scratch (used by the Reset / Play-again buttons). */
    public void reset() {
        game = new Game();
        setupStarterLevel();
        // The model removes goals as they are completed, so remember the original
        // count to track progress and detect a win.
        totalGoals = game.getGoalCount();
    }

    private void setupStarterLevel() {
        game.addLevel(4, 4);

        game.addSquare(new BlankSquare(), 0, 0);
        game.addSquare(new BlankSquare(), 0, 1);
        game.addSquare(new PlayableSquare(Color.PURPLE, Shape.LIGHTNING), 0, 2);
        game.addSquare(new BlankSquare(), 0, 3);

        game.addSquare(new PlayableSquare(Color.BLUE, Shape.CROSS), 1, 0);
        game.addSquare(new PlayableSquare(Color.BLUE, Shape.STAR), 1, 1);
        game.addSquare(new PlayableSquare(Color.YELLOW, Shape.STAR), 1, 2);
        game.addSquare(new PlayableSquare(Color.YELLOW, Shape.DIAMOND), 1, 3);

        game.addSquare(new PlayableSquare(Color.RED, Shape.CROSS), 2, 0);
        game.addSquare(new PlayableSquare(Color.GREEN, Shape.FLOWER), 2, 1);
        game.addSquare(new PlayableSquare(Color.PURPLE, Shape.CROSS), 2, 2);
        game.addSquare(new PlayableSquare(Color.GREEN, Shape.DIAMOND), 2, 3);

        game.addSquare(new PlayableSquare(Color.RED, Shape.DIAMOND), 3, 0);
        game.addSquare(new PlayableSquare(Color.YELLOW, Shape.LIGHTNING), 3, 1);
        game.addSquare(new PlayableSquare(Color.BLUE, Shape.FLOWER), 3, 2);
        game.addSquare(new PlayableSquare(Color.GREEN, Shape.FLOWER), 3, 3);

        game.addEyeball(3, 0, Direction.UP);
        game.addGoal(1, 2);
        game.addGoal(3, 3);
    }

    // ==================== Board state for the UI ====================

    public String getLevelName() {
        return LEVEL_NAME;
    }

    public int getRows() {
        return game.getLevelHeight();
    }

    public int getCols() {
        return game.getLevelWidth();
    }

    public Color getColorAt(int row, int col) {
        return game.getColorAt(row, col);
    }

    public Shape getShapeAt(int row, int col) {
        return game.getShapeAt(row, col);
    }

    public boolean isBlankAt(int row, int col) {
        return game.getColorAt(row, col) == Color.BLANK;
    }

    public boolean hasGoalAt(int row, int col) {
        return game.hasGoalAt(row, col);
    }

    public boolean isEyeballAt(int row, int col) {
        return game.getEyeballRow() == row && game.getEyeballColumn() == col;
    }

    public Direction getEyeballDirection() {
        return game.getEyeballDirection();
    }

    public int getCompletedGoals() {
        return game.getCompletedGoalCount();
    }

    public int getTotalGoals() {
        return totalGoals;
    }

    public boolean isWon() {
        return game.getCompletedGoalCount() == totalGoals;
    }

    public String getStatusText() {
        if (isWon()) {
            return getLevelName() + " — solved!  Goals: " + totalGoals + "/" + totalGoals;
        }
        return String.format(
                "%s — eyeball at row %d, col %d (facing %s)%nGoals: %d/%d",
                getLevelName(),
                game.getEyeballRow(),
                game.getEyeballColumn(),
                game.getEyeballDirection(),
                game.getCompletedGoalCount(),
                totalGoals);
    }

    /**
     * Attempts to move the eyeball one square in {@code direction}. All validity
     * decisions belong to the model: this only translates a button press into a
     * destination, bounds-checks the grid, asks the model what would happen, and
     * applies the move when the model says it is {@link Message#OK}.
     *
     * @return the model's {@link Message} describing the (attempted) move.
     */
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
