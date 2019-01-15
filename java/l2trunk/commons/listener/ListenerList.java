package l2trunk.commons.listener;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

public class ListenerList implements Listener{
    private final Set<Listener> listeners = new CopyOnWriteArraySet<>();

    public Stream<Set<Listener>> getListeners() {
        return Stream.of(listeners);
    }

    public boolean add(Listener listener) {
        return listeners.add(listener);
    }

    public boolean remove(Listener listener) {
        return listeners.remove(listener);
    }

}
