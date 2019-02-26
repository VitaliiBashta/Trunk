package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.commons.util.Rnd;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public final class NobleSelector {
    private final List<Node> nodes;
    private Node current = null;
    private int totalWeight = 0;

    public NobleSelector(int initialCapacity) {
        nodes = new ArrayList<>(initialCapacity);
    }

    public final void add(Integer objectId, int points) {
        nodes.add(Node.of(objectId, points));
    }

    public final int size() {
        return nodes.size();
    }

    public final void reset() {
        current = null;
    }

    public final Integer select() {
        final Node result = selectImpl();
        return result != null ? result.value : null;
    }

    private Node selectImpl() {
        if (current == null)
            return init();

        Node n;
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

    private Node init() {
        final int size = nodes.size();
        if (size < 2)
            return null;

        current = nodes.remove(Rnd.get(size));
        totalWeight = 0;
        for (Node n : nodes)
            totalWeight += getWeight(n, current);

        if (size != nodes.size() + 1)
            throw new ConcurrentModificationException();

        return current;
    }

    private int getWeight(Node n, Node base) {
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

    private static class Node {
        private final int value;
        private final int points;
        private int weight = 0;


        private Node(int value, int points) {
            this.value = value;
            this.points = points;
        }
        public static Node of(int objectId, int points)        {
            return new Node(objectId,points);
        }
    }
}