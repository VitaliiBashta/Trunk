package l2trunk.gameserver.geodata;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.commons.text.StrTable;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.utils.Location;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public final class PathFindBuffers {
    private final static int MIN_MAP_SIZE = 1 << 6;
    private final static int STEP_MAP_SIZE = 1 << 5;
    private final static int MAX_MAP_SIZE = 1 << 9;

    private static final Map<Integer, PathFindBuffer[]> buffers = new HashMap<>();
    private static final List<Integer> sizes;
    private static final Lock lock = new ReentrantLock();

    static {
        Map<Integer, Integer> config = new HashMap<>();
        String[] k;
        for (String e : Config.PATHFIND_BUFFERS.split(";"))
            if (!e.isEmpty() && (k = e.split("x")).length == 2)
                config.put(Integer.valueOf(k[1]), Integer.valueOf(k[0]));

        for (Map.Entry<Integer, Integer> itr : config.entrySet()) {
            int size = itr.getKey();
            int count = itr.getValue();

            PathFindBuffer[] buff = new PathFindBuffer[count];
            for (int i = 0; i < count; i++)
                buff[i] = new PathFindBuffer(size);

            buffers.put(size, buff);
        }

        sizes = new ArrayList<>(config.keySet());
        Collections.sort(sizes);
    }

    private synchronized static PathFindBuffer create(int mapSize) {
        PathFindBuffer buffer;
        PathFindBuffer[] buff = buffers.get(mapSize);
        if (buff != null)
            buff = ArrayUtils.add(buff, buffer = new PathFindBuffer(mapSize));
        else {
            buff = new PathFindBuffer[]{buffer = new PathFindBuffer(mapSize)};
            sizes.add(mapSize);
            Collections.sort(sizes);
        }
        buffers.put(mapSize, buff);
        buffer.inUse = true;
        return buffer;
    }

    private static PathFindBuffer get(int mapSize) {
        lock.lock();
        try {
            PathFindBuffer[] buff = buffers.get(mapSize);
            for (PathFindBuffer buffer : buff)
                if (!buffer.inUse) {
                    buffer.inUse = true;
                    return buffer;
                }
            return null;
        } finally {
            lock.unlock();
        }
    }

    static PathFindBuffer alloc(int mapSize) {
        if (mapSize > MAX_MAP_SIZE)
            return null;
        mapSize += STEP_MAP_SIZE;
        if (mapSize < MIN_MAP_SIZE)
            mapSize = MIN_MAP_SIZE;

        PathFindBuffer buffer = null;
        for (int size : sizes)
            if (size >= mapSize) {
                mapSize = size;
                buffer = get(mapSize);
                break;
            }

        //Не найден свободный буффер, или буфферов под такой размер нет
        if (buffer == null) {
            for (int size = MIN_MAP_SIZE; size < MAX_MAP_SIZE; size += STEP_MAP_SIZE)
                if (size >= mapSize) {
                    mapSize = size;
                    buffer = create(mapSize);
                    break;
                }
        }

        return buffer;
    }

    public static void recycle(PathFindBuffer buffer) {
        lock.lock();
        try {
            buffer.inUse = false;
        } finally {
            lock.unlock();
        }
    }

    public static StrTable getStats() {
        StrTable table = new StrTable("PathFind Buffers Stats");
        lock.lock();
        try {
            long totalUses = 0, totalPlayable = 0, totalTime = 0;
            int index = 0;
            int count;
            long uses;
            long playable;
            long itrs;
            long success;
            long overtime;
            long time;

            for (int size : sizes) {
                index++;
                count = 0;
                uses = 0;
                playable = 0;
                itrs = 0;
                success = 0;
                overtime = 0;
                time = 0;
                for (PathFindBuffer buff : buffers.get(size)) {
                    count++;
                    uses += buff.totalUses;
                    playable += buff.playableUses;
                    success += buff.successUses;
                    overtime += buff.overtimeUses;
                    time += buff.totalTime / 1000000;
                    itrs += buff.totalItr;
                }

                totalUses += uses;
                totalPlayable += playable;
                totalTime += time;

                table.set(index, "Size", size)
                        .set(index, "Count", count)
                        .set(index, "Uses (success%)", uses + "(" + String.format("%2.2f", (uses > 0) ? success * 100. / uses : 0) + "%)")
                        .set(index, "Uses, playble", playable + "(" + String.format("%2.2f", (uses > 0) ? playable * 100. / uses : 0) + "%)")
                        .set(index, "Uses, overtime", overtime + "(" + String.format("%2.2f", (uses > 0) ? overtime * 100. / uses : 0) + "%)")
                        .set(index, "Iter., avg", (uses > 0) ? itrs / uses : 0)
                        .set(index, "Time, avg (ms)", String.format("%1.3f", (uses > 0) ? (double) time / uses : 0.));
            }

            table.addTitle("Uses, total / playable  : " + totalUses + " / " + totalPlayable)
                    .addTitle("Uses, total time / avg (ms) : " + totalTime + " / " + String.format("%1.3f", totalUses > 0 ? (double) totalTime / totalUses : 0));
        } finally {
            lock.unlock();
        }

        return table;
    }

    public static class PathFindBuffer {
        final int mapSize;
        final GeoNode[][] nodes;
        final Queue<GeoNode> open;
        int offsetX, offsetY;
        boolean inUse;

        //статистика
        long totalUses;
        long successUses;
        long overtimeUses;
        long playableUses;
        long totalTime;
        long totalItr;

        PathFindBuffer(int mapSize) {
            open = new PriorityQueue<>(mapSize);
            this.mapSize = mapSize;
            nodes = new GeoNode[mapSize][mapSize];
            for (int i = 0; i < nodes.length; i++)
                for (int j = 0; j < nodes[i].length; j++)
                    nodes[i][j] = new GeoNode();
        }

        public void free() {
            open.clear();
            for (GeoNode[] node : nodes)
                for (GeoNode aNode : node) aNode.free();
        }
    }

    public static class GeoNode implements Comparable<GeoNode> {
        public final static int NONE = 0;
        final static int OPENED = 1;
        final static int CLOSED = -1;

        public int x, y;
        public int state;
        public GeoNode parent;
        short z, nswe;
        float totalCost, costFromStart, costToEnd;

        GeoNode() {
            nswe = -1;
        }

        public GeoNode set(int x, int y, short z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public boolean isSet() {
            return nswe != -1;
        }

        void free() {
            nswe = -1;
            costFromStart = 0f;
            totalCost = 0f;
            costToEnd = 0f;
            parent = null;
            state = NONE;
        }

        public Location getLoc() {
            return new Location(x, y, z);
        }

        @Override
        public String toString() {
            return "[" + x + "," + y + "," + z + "] f: " + totalCost;
        }

        @Override
        public int compareTo(GeoNode o) {
            return Float.compare(totalCost, o.totalCost);
        }
    }
}