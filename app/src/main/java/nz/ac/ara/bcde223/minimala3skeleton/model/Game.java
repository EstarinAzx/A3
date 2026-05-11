package nz.ac.ara.sbt.eyeballmaze;

import java.util.ArrayList;
import java.util.List;

/**
 * Main model class for the Eyeball Maze game.
 * Implements all five interfaces to manage levels, goals, squares,
 * the eyeball, and movement logic.
 */
public class Game implements ILevelHolder, IGoalHolder, ISquareHolder,
        IEyeballHolder, IMoving {

    private final List<Level> levels;
    private int currentLevelIndex;

    // Tracks the position of a recently completed goal so it can be
    // turned blank after the eyeball moves away from it
    private Position completedGoalPosition;

    /**
     * Creates a new Game with no levels.
     */
    public Game() {
        this.levels = new ArrayList<>();
        this.currentLevelIndex = -1;
        this.completedGoalPosition = null;
    }

    /**
     * Gets the current level.
     * @return the current Level object
     */
    private Level getCurrentLevel() {
        return this.levels.get(this.currentLevelIndex);
    }

    // ==================== ILevelHolder ====================

    @Override
    public void addLevel(int height, int width) {
        Level level = new Level(height, width);
        this.levels.add(level);
        this.currentLevelIndex = this.levels.size() - 1;
    }

    @Override
    public int getLevelWidth() {
        return getCurrentLevel().getWidth();
    }

    @Override
    public int getLevelHeight() {
        return getCurrentLevel().getHeight();
    }

    @Override
    public void setCurrentLevel(int levelNumber) {
        if (levelNumber < 0 || levelNumber >= this.levels.size()) {
            throw new IllegalArgumentException(
                "Level number " + levelNumber + " is out of range. "
                + "Valid range: 0 to " + (this.levels.size() - 1));
        }
        this.currentLevelIndex = levelNumber;
    }

    @Override
    public int getLevelCount() {
        return this.levels.size();
    }

    // ==================== IGoalHolder ====================

    @Override
    public void addGoal(int row, int column) {
        getCurrentLevel().addGoal(row, column);
    }

    @Override
    public int getGoalCount() {
        return getCurrentLevel().getGoalCount();
    }

    @Override
    public boolean hasGoalAt(int targetRow, int targetColumn) {
        return getCurrentLevel().hasGoalAt(targetRow, targetColumn);
    }

    @Override
    public int getCompletedGoalCount() {
        return getCurrentLevel().getCompletedGoalCount();
    }

    // ==================== ISquareHolder ====================

    @Override
    public void addSquare(Square square, int row, int column) {
        getCurrentLevel().addSquare(square, row, column);
    }

    @Override
    public Color getColorAt(int row, int column) {
        return getCurrentLevel().getColorAt(row, column);
    }

    @Override
    public Shape getShapeAt(int row, int column) {
        return getCurrentLevel().getShapeAt(row, column);
    }

    // ==================== IEyeballHolder ====================

    @Override
    public void addEyeball(int row, int column, Direction direction) {
        getCurrentLevel().addEyeball(row, column, direction);
    }

    @Override
    public int getEyeballRow() {
        return getCurrentLevel().getEyeballRow();
    }

    @Override
    public int getEyeballColumn() {
        return getCurrentLevel().getEyeballColumn();
    }

    @Override
    public Direction getEyeballDirection() {
        return getCurrentLevel().getEyeballDirection();
    }

    // ==================== IMoving ====================

    @Override
    public boolean canMoveTo(int destinationRow, int destinationColumn) {
        // Check direction (includes diagonal check)
        if (!isDirectionOK(destinationRow, destinationColumn)) {
            return false;
        }

        // Check for blank squares on the path
        if (!hasBlankFreePathTo(destinationRow, destinationColumn)) {
            return false;
        }

        // Check same color or same shape
        Level level = getCurrentLevel();
        Color currentColor = level.getColorAt(level.getEyeballRow(), level.getEyeballColumn());
        Shape currentShape = level.getShapeAt(level.getEyeballRow(), level.getEyeballColumn());
        Color destColor = level.getColorAt(destinationRow, destinationColumn);
        Shape destShape = level.getShapeAt(destinationRow, destinationColumn);

        return currentColor == destColor || currentShape == destShape;
    }

    @Override
    public Message messageIfMovingTo(int destinationRow, int destinationColumn) {
        // Check direction first (includes diagonal)
        Message directionMessage = checkDirectionMessage(destinationRow, destinationColumn);
        if (directionMessage != Message.OK) {
            return directionMessage;
        }

        // Check for blanks on the path
        Message blankMessage = checkMessageForBlankOnPathTo(destinationRow, destinationColumn);
        if (blankMessage != Message.OK) {
            return blankMessage;
        }

        // Check same color or same shape
        Level level = getCurrentLevel();
        Color currentColor = level.getColorAt(level.getEyeballRow(), level.getEyeballColumn());
        Shape currentShape = level.getShapeAt(level.getEyeballRow(), level.getEyeballColumn());
        Color destColor = level.getColorAt(destinationRow, destinationColumn);
        Shape destShape = level.getShapeAt(destinationRow, destinationColumn);

        if (currentColor != destColor && currentShape != destShape) {
            return Message.DIFFERENT_SHAPE_OR_COLOR;
        }

        return Message.OK;
    }

    @Override
    public boolean isDirectionOK(int destinationRow, int destinationColumn) {
        return checkDirectionMessage(destinationRow, destinationColumn) == Message.OK;
    }

    @Override
    public Message checkDirectionMessage(int destinationRow, int destinationColumn) {
        Level level = getCurrentLevel();
        int eyeballRow = level.getEyeballRow();
        int eyeballColumn = level.getEyeballColumn();

        int rowDiff = destinationRow - eyeballRow;
        int colDiff = destinationColumn - eyeballColumn;

        // Check for diagonal movement (both row and column change)
        if (rowDiff != 0 && colDiff != 0) {
            return Message.MOVING_DIAGONALLY;
        }

        // Determine the direction of the move
        Direction moveDirection;
        if (rowDiff < 0) {
            moveDirection = Direction.UP;
        } else if (rowDiff > 0) {
            moveDirection = Direction.DOWN;
        } else if (colDiff < 0) {
            moveDirection = Direction.LEFT;
        } else {
            moveDirection = Direction.RIGHT;
        }

        // Check if moving backwards (opposite to current facing direction)
        Direction currentDirection = level.getEyeballDirection();
        if (isOppositeDirection(currentDirection, moveDirection)) {
            return Message.BACKWARDS_MOVE;
        }

        return Message.OK;
    }

    @Override
    public boolean hasBlankFreePathTo(int destinationRow, int destinationColumn) {
        return checkMessageForBlankOnPathTo(destinationRow, destinationColumn) == Message.OK;
    }

    @Override
    public Message checkMessageForBlankOnPathTo(int destinationRow, int destinationColumn) {
        Level level = getCurrentLevel();
        int eyeballRow = level.getEyeballRow();
        int eyeballColumn = level.getEyeballColumn();

        // Check each square between eyeball and destination (exclusive of both endpoints)
        if (eyeballRow == destinationRow) {
            // Horizontal movement
            int step = (destinationColumn > eyeballColumn) ? 1 : -1;
            for (int col = eyeballColumn + step; col != destinationColumn; col += step) {
                Square square = level.getSquareAt(eyeballRow, col);
                if (square == null || square.isBlank()) {
                    return Message.MOVING_OVER_BLANK;
                }
            }
        } else {
            // Vertical movement
            int step = (destinationRow > eyeballRow) ? 1 : -1;
            for (int row = eyeballRow + step; row != destinationRow; row += step) {
                Square square = level.getSquareAt(row, eyeballColumn);
                if (square == null || square.isBlank()) {
                    return Message.MOVING_OVER_BLANK;
                }
            }
        }

        return Message.OK;
    }

    @Override
    public void moveTo(int destinationRow, int destinationColumn) {
        Level level = getCurrentLevel();

        // If the eyeball is leaving a previously completed goal square,
        // make that square blank
        if (this.completedGoalPosition != null) {
            level.makeSquareBlank(
                this.completedGoalPosition.getRow(),
                this.completedGoalPosition.getColumn());
            this.completedGoalPosition = null;
        }

        // Determine the new facing direction based on movement
        Direction newDirection = determineDirection(
            level.getEyeballRow(), level.getEyeballColumn(),
            destinationRow, destinationColumn);

        // Move the eyeball
        level.setEyeballRow(destinationRow);
        level.setEyeballColumn(destinationColumn);
        level.setEyeballDirection(newDirection);

        // Check if the eyeball has landed on a goal
        if (level.hasGoalAt(destinationRow, destinationColumn)) {
            level.completeGoal(destinationRow, destinationColumn);
            // Track that we need to blank this square when the eyeball leaves
            this.completedGoalPosition = new Position(destinationRow, destinationColumn);
        }
    }

    // ==================== Private Helpers ====================

    /**
     * Checks whether two directions are opposite to each other.
     * @param dir1 the first direction
     * @param dir2 the second direction
     * @return true if they are opposite
     */
    private boolean isOppositeDirection(Direction dir1, Direction dir2) {
        return (dir1 == Direction.UP && dir2 == Direction.DOWN)
            || (dir1 == Direction.DOWN && dir2 == Direction.UP)
            || (dir1 == Direction.LEFT && dir2 == Direction.RIGHT)
            || (dir1 == Direction.RIGHT && dir2 == Direction.LEFT);
    }

    /**
     * Determines the direction of movement from one position to another.
     * @param fromRow starting row
     * @param fromCol starting column
     * @param toRow destination row
     * @param toCol destination column
     * @return the direction of movement
     */
    private Direction determineDirection(int fromRow, int fromCol, int toRow, int toCol) {
        if (toRow < fromRow) {
            return Direction.UP;
        } else if (toRow > fromRow) {
            return Direction.DOWN;
        } else if (toCol < fromCol) {
            return Direction.LEFT;
        } else {
            return Direction.RIGHT;
        }
    }
}
