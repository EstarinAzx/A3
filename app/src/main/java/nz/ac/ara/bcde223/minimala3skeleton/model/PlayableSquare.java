package nz.ac.ara.bcde223.minimala3skeleton.model;

/**
 * Represents a playable square on the game board with a color and shape.
 * The eyeball can move to this square if it shares the same color or shape
 * as the square the eyeball is currently on.
 */
public class PlayableSquare extends Square {

    private final Color color;
    private final Shape shape;

    /**
     * Creates a new playable square with the given color and shape.
     * @param color the color of this square
     * @param shape the shape of this square
     */
    public PlayableSquare(Color color, Shape shape) {
        this.color = color;
        this.shape = shape;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public Shape getShape() {
        return this.shape;
    }

    @Override
    public boolean isBlank() {
        return false;
    }
}
