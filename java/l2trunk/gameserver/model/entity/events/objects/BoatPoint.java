package l2trunk.gameserver.model.entity.events.objects;


import l2trunk.gameserver.utils.Location;
import org.dom4j.Element;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class BoatPoint extends Location {
    private final int _fuel;
    private int _speed1;
    private int _speed2;
    private boolean _teleport;

    private BoatPoint(int x, int y, int z, int h, int speed1, int speed2, int fuel, boolean teleport) {
        super(x, y, z, h);
        _speed1 = speed1;
        _speed2 = speed2;
        _fuel = fuel;
        _teleport = teleport;
    }

    public static BoatPoint parse(Element element) {
        int speed1 = element.attributeValue("speed1") == null ? 0 : toInt(element.attributeValue("speed1"));
        int speed2 = element.attributeValue("speed2") == null ? 0 : toInt(element.attributeValue("speed2"));
        int x = toInt(element.attributeValue("x"));
        int y = toInt(element.attributeValue("y"));
        int z = toInt(element.attributeValue("z"));
        int h = element.attributeValue("h") == null ? 0 : toInt(element.attributeValue("h"));
        int fuel = element.attributeValue("fuel") == null ? 0 : toInt(element.attributeValue("fuel"));
        boolean teleport = Boolean.parseBoolean(element.attributeValue("teleport"));
        return new BoatPoint(x, y, z, h, speed1, speed2, fuel, teleport);
    }

    public int getSpeed1() {
        return _speed1;
    }

    public void setSpeed1(int speed1) {
        _speed1 = speed1;
    }

    public int getSpeed2() {
        return _speed2;
    }

    public void setSpeed2(int speed2) {
        _speed2 = speed2;
    }

    public int getFuel() {
        return _fuel;
    }

    public boolean isTeleport() {
        return _teleport;
    }

    public void setTeleport(boolean teleport) {
        _teleport = teleport;
    }
}
