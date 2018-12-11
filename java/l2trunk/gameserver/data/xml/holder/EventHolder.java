package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

import java.util.Map;
import java.util.TreeMap;

public final class EventHolder {
    private static final Map<Integer, GlobalEvent> EVENTS = new TreeMap<>();
    private EventHolder() {
    }

    public static void addEvent(EventType type, GlobalEvent event) {
        EVENTS.put(type.step() + event.getId(), event);

    }

    @SuppressWarnings("unchecked")
    public static <E extends GlobalEvent> E getEvent(EventType type, int id) {
        return (E) EVENTS.get(type.step() + id);
    }

    public static void findEvent(Player player) {
        EVENTS.values().stream()
                .filter(event -> event.isParticle(player))
                .forEach(player::addEvent);
    }

    public static void callInit() {
        EVENTS.values().forEach(GlobalEvent::initEvent);
    }

    public static int size() {
        return EVENTS.size();
    }

    public static void clear() {
        EVENTS.clear();
    }
}
