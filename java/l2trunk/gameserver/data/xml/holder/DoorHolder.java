package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.commons.lang.FileUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.templates.DoorTemplate;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public final class DoorHolder extends AbstractHolder {
    private static final DoorHolder _instance = new DoorHolder();

    private final Map<Integer, DoorTemplate> doors = new HashMap<>();

    public static DoorHolder getInstance() {
        return _instance;
    }

    public void addTemplate(DoorTemplate door) {
        doors.put(door.getNpcId(), door);
    }

    public DoorTemplate getTemplate(int doorId) {
        return doors.get(doorId);
    }

    public Map<Integer, DoorTemplate> getDoors() {
        return doors;
    }

    @Override
    public int size() {
        return doors.size();
    }

    @Override
    public void clear() {
        doors.clear();
    }

    public static void main(String[] args) {
        Path dir = Paths.get("c:/projects/Trunk/data/doors/");
        Collection<Path> files = FileUtils.getAllFiles(dir, true, ".xml");
        StringBuilder builder = new StringBuilder();
        for (Path file :files) {
            builder.append(FileUtils.readFileToString(file));

        }

        try (BufferedWriter writer = Files.newBufferedWriter(dir.resolve("doors.xml")))
        {
            writer.write(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
