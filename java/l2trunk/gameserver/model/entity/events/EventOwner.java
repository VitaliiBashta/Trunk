package l2trunk.gameserver.model.entity.events;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class EventOwner {
    private final Set<GlobalEvent> events = new CopyOnWriteArraySet<>();

    @SuppressWarnings("unchecked")
    public <E extends GlobalEvent> E getEvent(Class<E> eventClass) {
        for (GlobalEvent e : events) {
            if (e.getClass() == eventClass)    // fast hack
                return (E) e;
            if (eventClass.isAssignableFrom(e.getClass()))    //FIXME [VISTALL]    какойто другой способ определить
                return (E) e;
        }

        return null;
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
