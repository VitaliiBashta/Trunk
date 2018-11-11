package l2trunk.gameserver.geodata;

import l2trunk.commons.geometry.Shape;

public interface GeoCollision {
    Shape getShape();

    byte[][] getGeoAround();

    void setGeoAround(byte[][] geo);

    boolean isConcrete();
}
