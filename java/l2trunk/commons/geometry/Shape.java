package l2trunk.commons.geometry;

import l2trunk.gameserver.utils.Location;

public interface Shape {
    boolean isInside(int x, int y);

    boolean isInside(Location loc);

    int getXmax();

    int getXmin();

    int getYmax();

    int getYmin();

    int getZmax();

    int getZmin();
}
