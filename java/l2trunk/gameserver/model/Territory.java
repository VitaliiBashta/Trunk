package l2trunk.gameserver.model;

import l2trunk.commons.geometry.Point3D;
import l2trunk.commons.geometry.Shape;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.templates.spawn.SpawnRange;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;

public final class Territory implements Shape, SpawnRange {
    private final Point3D max = new Point3D();
    private final Point3D min = new Point3D();

    private final List<Shape> include = new ArrayList<>(1);
    private final List<Shape> exclude = new ArrayList<>(1);

    public Territory() {

    }

    public static Location getRandomLoc(Territory territory) {
        return getRandomLoc(territory, 0);
    }

    public static Location getRandomLoc(Territory territory, int geoIndex) {
        Location pos = new Location();

        List<Shape> territories = territory.getTerritories();

        loop:
        for (int i = 0; i < 100; i++) {
            Shape shape = Rnd.get(territories);

            pos.x = Rnd.get(shape.getXmin(), shape.getXmax());
            pos.y = Rnd.get(shape.getYmin(), shape.getYmax());
            pos.z = shape.getZmin() + (shape.getZmax() - shape.getZmin()) / 2;

            if (territory.isInside(pos.x, pos.y)) {
                // Не спаунить в колонны, стены и прочее.
                int tempz = GeoEngine.getHeight(pos, geoIndex);
                if (shape.getZmin() != shape.getZmax()) {
                    if (tempz < shape.getZmin() || tempz > shape.getZmax())
                        continue;
                } else if (tempz < shape.getZmin() - 200 || tempz > shape.getZmin() + 200)
                    continue;

                pos.z = tempz;

                int geoX = pos.x - World.MAP_MIN_X >> 4;
                int geoY = pos.y - World.MAP_MIN_Y >> 4;

                // Если местность подозрительная - пропускаем
                for (int x = geoX - 1; x <= geoX + 1; x++)
                    for (int y = geoY - 1; y <= geoY + 1; y++)
                        if (GeoEngine.NgetNSWE(x, y, tempz, geoIndex) != GeoEngine.NSWE_ALL)
                            continue loop;

                return pos;
            }
        }

        return pos;
    }

    public Territory add(Shape shape) {
        if (include.isEmpty()) {
            max.x = shape.getXmax();
            max.y = shape.getYmax();
            max.z = shape.getZmax();
            min.x = shape.getXmin();
            min.y = shape.getYmin();
            min.z = shape.getZmin();
        } else {
            max.x = Math.max(max.x, shape.getXmax());
            max.y = Math.max(max.y, shape.getYmax());
            max.z = Math.max(max.z, shape.getZmax());
            min.x = Math.min(min.x, shape.getXmin());
            min.y = Math.min(min.y, shape.getYmin());
            min.z = Math.min(min.z, shape.getZmin());
        }

        include.add(shape);
        return this;
    }

    public void addBanned(Shape shape) {
        exclude.add(shape);
    }

    public List<Shape> getTerritories() {
        return include;
    }

    @Override
    public boolean isInside(int x, int y) {
        if (include.stream()
                .anyMatch(shape -> shape.isInside(x, y)))
            return !isExcluded(x, y);

        return false;
    }

    @Override
    public boolean isInside(Location loc) {
        if (loc.x < this.min.x
                || loc.x > this.max.x
                || loc.y < this.min.y
                || loc.y > this.max.y
                || loc.z < this.min.z
                || loc.z > this.max.z)
            return false;

        if (include.stream()
                .anyMatch(shape -> shape.isInside(loc)))
            return !isExcluded(loc);

        return false;
    }

    private boolean isExcluded(int x, int y) {
        return exclude.stream()
                .anyMatch(exc -> exc.isInside(x, y));
    }

    private boolean isExcluded(Location loc) {
        return exclude.stream()
                .anyMatch(a -> a.isInside(loc));
    }

    @Override
    public int getXmax() {
        return this.max.x;
    }

    @Override
    public int getXmin() {
        return this.min.x;
    }

    @Override
    public int getYmax() {
        return this.max.y;
    }

    @Override
    public int getYmin() {
        return this.min.y;
    }

    @Override
    public int getZmax() {
        return this.max.z;
    }

    @Override
    public int getZmin() {
        return this.min.z;
    }

    @Override
    public Location getRandomLoc(int geoIndex) {
        return getRandomLoc(this, geoIndex);
    }
}