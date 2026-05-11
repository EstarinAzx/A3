package nz.ac.ara.bcde223.minimala3skeleton.model;

/**
 * Represents an empty/blank square on the game board.
 * Blank squares cannot be landed on and block movement paths.
 */
public class BlankSquare extends Square {

    @Override
    public Color getColor() {
        return Color.BLANK;
    }

    @Override
    public Shape getShape() {
        return Shape.BLANK;
    }

    @Override
    public boolean isBlank() {
        return true;
    }
}
