package nz.ac.ara.bcde223.minimala3skeleton.model;

public interface IEyeballHolder {
    public void addEyeball(int row, int column, Direction direction);
    public int getEyeballRow();
    public int getEyeballColumn();
    public Direction getEyeballDirection();
}
