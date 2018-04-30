public class WayPoint {

    private Vector2D position;
    private Cell cell;


    public WayPoint(Vector2D position, Cell cell) {
        this.position = position;
        this.cell = cell;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }
}
