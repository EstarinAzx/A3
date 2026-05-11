package nz.ac.ara.sbt.eyeballmaze;

public interface IEyeballHolder {
    public void addEyeball(int row, int column, Direction direction);
    public int getEyeballRow();
    public int getEyeballColumn();
    public Direction getEyeballDirection();
}
