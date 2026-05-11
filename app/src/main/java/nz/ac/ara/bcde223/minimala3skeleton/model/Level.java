package nz.ac.ara.bcde223.minimala3skeleton.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single level in the Eyeball Maze game.
 * Each level has a grid of squares, goals, and an eyeball.
 */
public class Level {

    private final int height;
    private final int width;
    private final Square[][] grid;
    private final List<Position> goals;
    private int completedGoalCount;

    // Eyeball state
    private int eyeballRow;
    private int eyeballColumn;
    private Direction eyeballDirection;
    private boolean hasEyeball;

    /**
     * Creates a new level with the given dimensions.
     * @param height number of rows
     * @param width number of columns
     */
    public Level(int height, int width) {
        this.height = height;
        this.width = width;
        this.grid = new Square[height][width];
        this.goals = new ArrayList<>();
        this.completedGoalCount = 0;
        this.hasEyeball = false;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    // ==================== Square Management ====================

    /**
     * Adds a square to the grid at the given position.
     * @param square the square to add
     * @param row the row position
     * @param column the column position
     * @throws IllegalArgumentException if position is outside level bounds
     */
    public void addSquare(Square square, int row, int column) {
        validatePosition(row, column);
        this.grid[row][column] = square;
    }

    /**
     * Gets the square at the given position.
     * @param row the row
     * @param column the column
     * @return the square at that position, or null if empty
     */
    public Square getSquareAt(int row, int column) {
        return this.grid[row][column];
    }

    /**
     * Gets the color of the square at the given position.
     * @param row the row
     * @param column the column
     * @return the color
     */
    public Color getColorAt(int row, int column) {
        Square square = this.grid[row][column];
        if (square == null) {
            return Color.BLANK;
        }
        return square.getColor();
    }

    /**
     * Gets the shape of the square at the given position.
     * @param row the row
     * @param column the column
     * @return the shape
     */
    public Shape getShapeAt(int row, int column) {
        Square square = this.grid[row][column];
        if (square == null) {
            return Shape.BLANK;
        }
        return square.getShape();
    }

    // ==================== Goal Management ====================

    /**
     * Adds a goal at the given position.
     * @param row the row
     * @param column the column
     * @throws IllegalArgumentException if position is outside level bounds
     */
    public void addGoal(int row, int column) {
        validatePosition(row, column);
        this.goals.add(new Position(row, column));
    }

    public int getGoalCount() {
        return this.goals.size();
    }

    public boolean hasGoalAt(int row, int column) {
        Position target = new Position(row, column);
        return this.goals.contains(target);
    }

    public int getCompletedGoalCount() {
        return this.completedGoalCount;
    }

    /**
     * Completes the goal at the given position by removing it from the list
     * and incrementing the completed count.
     * @param row the row
     * @param column the column
     */
    public void completeGoal(int row, int column) {
        Position target = new Position(row, column);
        if (this.goals.remove(target)) {
            this.completedGoalCount++;
        }
    }

    // ==================== Eyeball Management ====================

    /**
     * Adds the eyeball at the given position facing the given direction.
     * @param row the row
     * @param column the column
     * @param direction the facing direction
     * @throws IllegalArgumentException if position is outside level bounds
     */
    public void addEyeball(int row, int column, Direction direction) {
        validatePosition(row, column);
        this.eyeballRow = row;
        this.eyeballColumn = column;
        this.eyeballDirection = direction;
        this.hasEyeball = true;
    }

    public int getEyeballRow() {
        return this.eyeballRow;
    }

    public int getEyeballColumn() {
        return this.eyeballColumn;
    }

    public Direction getEyeballDirection() {
        return this.eyeballDirection;
    }

    public void setEyeballRow(int row) {
        this.eyeballRow = row;
    }

    public void setEyeballColumn(int column) {
        this.eyeballColumn = column;
    }

    public void setEyeballDirection(Direction direction) {
        this.eyeballDirection = direction;
    }

    /**
     * Replaces the square at the given position with a blank square.
     * Used when the eyeball leaves a completed goal square.
     * @param row the row
     * @param column the column
     */
    public void makeSquareBlank(int row, int column) {
        this.grid[row][column] = new BlankSquare();
    }

    // ==================== Helpers ====================

    /**
     * Validates that a position is within the level bounds.
     * @param row the row
     * @param column the column
     * @throws IllegalArgumentException if outside bounds
     */
    private void validatePosition(int row, int column) {
        if (row < 0 || row >= this.height || column < 0 || column >= this.width) {
            throw new IllegalArgumentException(
                "Position (" + row + ", " + column + ") is outside level bounds ("
                + this.height + " x " + this.width + ")");
        }
    }
}
