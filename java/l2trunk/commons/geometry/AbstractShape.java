package l2trunk.commons.geometry;

import l2trunk.gameserver.utils.Location;

public abstract class AbstractShape implements Shape {
    final Point3D max = new Point3D();
    final Point3D min = new Point3D();

    @Override
    public boolean isInside(Location loc) {
        return (min.z <= loc.z) && (max.z >= loc.z) && (isInside(loc.x, loc.y));
    }

    @Override
    public int getXmax() {
        return max.x;
    }

    @Override
    public int getXmin() {
        return min.x;
    }

    @Override
    public int getYmax() {
        return max.y;
    }

    @Override
    public int getYmin() {
        return min.y;
    }

    @Override
    public int getZmax() {
        return max.z;
    }

    public AbstractShape setZmax(int z) {
        max.z = z;
        return this;
    }

    @Override
    public int getZmin() {
        return min.z;
    }

    public AbstractShape setZmin(int z) {
        min.z = z;
        return this;
    }
}
