package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

import java.util.stream.Stream;

public class ZoneObject implements InitableObject {
    private final String _name;
    private Zone zone;

    public ZoneObject(String name) {
        _name = name;
    }

    @Override
    public void initObject(GlobalEvent e) {
        Reflection r = e.getReflection();

        zone = r.getZone(_name);
    }

    public void setActive(boolean a) {
        zone.setActive(a);
    }

    public void setActive(boolean a, GlobalEvent event) {
        setActive(a);

        //
    }

    public Zone getZone() {
        return zone;
    }

    public Stream<Player> getInsidePlayers() {
        return zone.getInsidePlayers();
    }

    public boolean checkIfInZone(Creature c) {
        return zone.checkIfInZone(c);
    }
}
