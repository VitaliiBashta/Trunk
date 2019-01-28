package l2trunk.commons.geometry;

public class Point2D implements Cloneable {
    public int x;
    public int y;

    Point2D() {
    }

    Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Point2D clone() {
        return new Point2D(this.x, this.y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (o.getClass() != getClass())
            return false;
        return equals((Point2D) o);
    }

    private boolean equals(Point2D p) {
        return equals(p.x, p.y);
    }

    private boolean equals(int x, int y) {
        return (this.x == x) && (this.y == y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "[x: " + this.x + " y: " + this.y + "]";
    }
}
