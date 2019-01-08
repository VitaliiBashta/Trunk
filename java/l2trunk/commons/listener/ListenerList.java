package l2trunk.commons.listener;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ListenerList {
    private final Set<Listener> listeners = new CopyOnWriteArraySet<>();

    public Collection<Listener> getListeners() {
        return listeners;
    }

    public boolean add(Listener listener) {
        return listeners.add(listener);
    }

    public boolean remove(Listener listener) {
        return listeners.remove(listener);
    }

}
