package nz.ac.ara.sbt.eyeballmaze;

public interface ISquareHolder {
    public void addSquare(Square square, int row, int column);
    public Color getColorAt(int row, int column);
    public Shape getShapeAt(int row, int column);
}
