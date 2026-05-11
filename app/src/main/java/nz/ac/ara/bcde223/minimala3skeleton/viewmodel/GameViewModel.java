package nz.ac.ara.bcde223.minimala3skeleton.viewmodel;

import java.util.ArrayList;
import java.util.List;

import nz.ac.ara.bcde223.minimala3skeleton.model.BlankSquare;
import nz.ac.ara.bcde223.minimala3skeleton.model.Color;
import nz.ac.ara.bcde223.minimala3skeleton.model.Direction;
import nz.ac.ara.bcde223.minimala3skeleton.model.Game;
import nz.ac.ara.bcde223.minimala3skeleton.model.Message;
import nz.ac.ara.bcde223.minimala3skeleton.model.PlayableSquare;
import nz.ac.ara.bcde223.minimala3skeleton.model.Shape;
import nz.ac.ara.bcde223.minimala3skeleton.model.Square;

/**
 * Sits between the Android UI and the Eyeball Maze {@link Game} model.
 * It owns a {@code Game}, builds a level from a level-data file
 * (see {@code assets/level1.txt}), and exposes simple read-only accessors for
 * rendering plus a {@link #tryMove(Direction)} that delegates all rule
 * decisions to the model.
 *
 * <p>This class does <em>no</em> rule logic: loading a level just translates the
 * file's data into model calls ({@code addLevel}, {@code addSquare},
 * {@code addEyeball}, {@code addGoal}); move validity and win/lose stay in the
 * {@code model} package.</p>
 */
public class GameViewModel {

    private Game game;
    private int totalGoals;
    private String levelName = "Level";

    /** Raw text of the level file, kept so {@link #reset()} can rebuild it. */
    private String levelData;

    /**
     * Creates an empty view-model. Call {@link #loadLevel(String)} with the
     * contents of a level file before using any of the board accessors.
     */
    public GameViewModel() {
    }

    /**
     * Loads a level from the text of a level file (see {@code assets/level1.txt}).
     * The text is remembered so {@link #reset()} can rebuild the same level.
     */
    public void loadLevel(String levelText) {
        this.levelData = levelText;
        reset();
    }

    /** Rebuilds the current level from scratch (used by Reset / Play-again). */
    public void reset() {
        game = new Game();
        buildLevelFrom(levelData);
        // The model removes goals as they are completed, so remember the
        // original count to track progress and detect a win.
        totalGoals = game.getGoalCount();
    }

    // ==================== Level-file parsing ====================

    private void buildLevelFrom(String levelText) {
        String name = "Level";
        int height = 0;
        int width = 0;
        int eyeballRow = 0;
        int eyeballCol = 0;
        Direction eyeballDir = Direction.UP;
        List<int[]> goals = new ArrayList<>();
        List<String> boardRows = new ArrayList<>();

        boolean readingSquares = false;
        for (String rawLine : levelText.split("\\r?\\n")) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (readingSquares) {
                boardRows.add(line);
                continue;
            }

            int eq = line.indexOf('=');
            if (eq < 0) {
                continue;
            }
            String key = line.substring(0, eq).trim();
            String value = line.substring(eq + 1).trim();

            switch (key) {
                case "name":
                    name = value;
                    break;
                case "height":
                    height = Integer.parseInt(value);
                    break;
                case "width":
                    width = Integer.parseInt(value);
                    break;
                case "eyeball": {
                    String[] parts = value.split(",");
                    eyeballRow = Integer.parseInt(parts[0].trim());
                    eyeballCol = Integer.parseInt(parts[1].trim());
                    eyeballDir = Direction.valueOf(parts[2].trim().toUpperCase());
                    break;
                }
                case "goals":
                    for (String pair : value.split(";")) {
                        String[] coords = pair.split(",");
                        goals.add(new int[]{
                                Integer.parseInt(coords[0].trim()),
                                Integer.parseInt(coords[1].trim())
                        });
                    }
                    break;
                case "squares":
                    readingSquares = true;
                    break;
                default:
                    break;
            }
        }

        levelName = name;
        game.addLevel(height, width);
        for (int row = 0; row < boardRows.size(); row++) {
            String[] cells = boardRows.get(row).split(",");
            for (int col = 0; col < cells.length; col++) {
                game.addSquare(squareFromToken(cells[col].trim()), row, col);
            }
        }
        game.addEyeball(eyeballRow, eyeballCol, eyeballDir);
        for (int[] goal : goals) {
            game.addGoal(goal[0], goal[1]);
        }
    }

    private static Square squareFromToken(String token) {
        if (token.equalsIgnoreCase("BLANK") || token.equals(".")) {
            return new BlankSquare();
        }
        String[] parts = token.split("-");
        Color color = Color.valueOf(parts[0].trim().toUpperCase());
        Shape shape = Shape.valueOf(parts[1].trim().toUpperCase());
        return new PlayableSquare(color, shape);
    }

    // ==================== Board state for the UI ====================

    public String getLevelName() {
        return levelName;
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
