package l2trunk.gameserver.model.entity.events;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class EventOwner {
    private final Set<GlobalEvent> events = new CopyOnWriteArraySet<>();

    public <E extends GlobalEvent> E getEvent(Class<E> eventClass) {
        return  events.stream()
        .filter(e -> eventClass.isAssignableFrom(e.getClass()))
        .map(eventClass::cast)
        .findFirst().orElse(null);
    }

    public void addEvent(GlobalEvent event) {
        events.add(event);
    }

    public void removeEvent(GlobalEvent event) {
        events.remove(event);
    }

    public Set<GlobalEvent> getEvents() {
        return events;
    }
}
