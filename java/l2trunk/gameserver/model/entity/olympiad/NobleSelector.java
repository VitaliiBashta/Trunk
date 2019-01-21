package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.commons.util.Rnd;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public final class NobleSelector<E> {
    private final List<Node<E>> nodes;
    private Node<E> current = null;
    private int totalWeight = 0;

    public NobleSelector(int initialCapacity) {
        nodes = new ArrayList<>(initialCapacity);
    }

    public final void add(E value, int points) {
        nodes.add(new Node<>(value, points));
    }

    public final int size() {
        return nodes.size();
    }

    public final void reset() {
        current = null;
    }

    public final E select() {
        final Node<E> result = selectImpl();
        return result != null ? result.value : null;
    }

    public final int testSelect() {
        final Node<E> result = selectImpl();
        return result != null ? result.points : -1;
    }

    private Node<E> selectImpl() {
        if (current == null)
            return init();

        Node<E> n;
        int random = Rnd.get(totalWeight);
        for (int i = nodes.size(); --i >= 0; ) {
            n = nodes.get(i);
            random -= n.weight;
            if (random < 0)
                if (nodes.remove(i) != n)
                    throw new ConcurrentModificationException();
                else
                    return n;
        }
        return null;
    }

    private Node<E> init() {
        final int size = nodes.size();
        if (size < 2)
            return null;

        current = nodes.remove(Rnd.get(size));
        totalWeight = 0;
        for (Node<E> n : nodes)
            totalWeight += getWeight(n, current);

        if (size != nodes.size() + 1)
            throw new ConcurrentModificationException();

        return current;
    }

    private int getWeight(Node<E> n, Node<E> base) {
        final int delta = Math.abs(n.points - base.points);

        if (delta < 20)
            n.weight = 8;
        else if (delta < 30)
            n.weight = 6;
        else if (delta < 40)
            n.weight = 4;
        else if (delta < 60)
            n.weight = 2;
        else
            n.weight = 1;

        if ((n.points > 50) == (base.points > 50))
            n.weight *= 10;

        return n.weight;
    }

    private class Node<T> {
        private final T value;
        private final int points;
        private int weight = 0;

        Node(T value, int points) {
            this.value = value;
            this.points = points;
        }
    }
}