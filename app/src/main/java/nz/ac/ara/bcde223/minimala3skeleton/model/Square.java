package nz.ac.ara.bcde223.minimala3skeleton.model;

/**
 * Abstract base class representing a square on the game board.
 * Squares can be either playable (with a color and shape) or blank.
 */
public abstract class Square {

    /**
     * Gets the color of this square.
     * @return the color of this square
     */
    public abstract Color getColor();

    /**
     * Gets the shape of this square.
     * @return the shape of this square
     */
    public abstract Shape getShape();

    /**
     * Checks whether this square is blank.
     * @return true if this square is blank, false otherwise
     */
    public abstract boolean isBlank();
}
