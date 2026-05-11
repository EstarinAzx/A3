package nz.ac.ara.sbt.eyeballmaze;

/**
 * Represents a row/column coordinate position on the game board.
 * Used for tracking goals and eyeball positions.
 */
public class Position {

    private final int row;
    private final int column;

    /**
     * Creates a new position with the given row and column.
     * @param row the row coordinate (0-based)
     * @param column the column coordinate (0-based)
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Gets the row coordinate.
     * @return the row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Gets the column coordinate.
     * @return the column
     */
    public int getColumn() {
        return this.column;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position other)) return false;
        return this.row == other.row && this.column == other.column;
    }

    @Override
    public int hashCode() {
        return 31 * row + column;
    }
}
