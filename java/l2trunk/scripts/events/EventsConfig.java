package l2trunk.scripts.events;

import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public final class EventsConfig extends Functions implements ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(EventsConfig.class);

    private static final String dir = "./config/events";
    private static ConcurrentHashMap<String, String> properties;
    
    public static final List<Location> EVENT_MANAGERS = List.of(
            new Location(81921, 148921, -3467, 16384),
            new Location(146405, 28360, -2269, 49648),
            new Location(19319, 144919, -3103, 31135),
            new Location(-82805, 149890, -3129, 16384),
            new Location(-12347, 122549, -3104, 16384),
            new Location(110642, 220165, -3655, 61898),
            new Location(116619, 75463, -2721, 20881),
            new Location(85513, 16014, -3668, 23681),
            new Location(81999, 53793, -1496, 61621),
            new Location(148159, -55484, -2734, 44315),
            new Location(44185, -48502, -797, 27479),
            new Location(86899, -143229, -1293, 8192));

    public static final List<Location> CTREES = List.of(
            new Location(81961, 148921, -3467, 0),
            new Location(146445, 28360, -2269, 0),
            new Location(19319, 144959, -3103, 0),
            new Location(-82845, 149890, -3129, 0),
            new Location(-12387, 122549, -3104, 0),
            new Location(110602, 220165, -3655, 0),
            new Location(116659, 75463, -2721, 0),
            new Location(85553, 16014, -3668, 0),
            new Location(81999, 53743, -1496, 0),
            new Location(148199, -55484, -2734, 0),
            new Location(44185, -48542, -797, 0),
            new Location(86859, -143229, -1293, 0));

    public static final List<Location> EVENT_MANAGERS_coffer_march8 = List.of(
            new Location(-14823, 123567, -3143, 8192), // Gludio
            new Location(-83159, 150914, -3155, 49152), // Gludin
            new Location(18600, 145971, -3095, 40960), // Dion
            new Location(82158, 148609, -3493, 60), // Giran
            new Location(110992, 218753, -3568, 0), // Hiene
            new Location(116339, 75424, -2738, 0), // Hunter Village
            new Location(81140, 55218, -1551, 32768), // Oren
            new Location(147148, 27401, -2231, 2300), // Aden
            new Location(43532, -46807, -823, 31471), // Rune
            new Location(87765, -141947, -1367, 6500), // Schuttgart
            new Location(147154, -55527, -2807, 61300)); // Goddard

    public static final List<Location> EVENT_MANAGERS_harvest_meleons = List.of(
            new Location(81921, 148921, -3467, 16384),
            new Location(146405, 28360, -2269, 49648),
            new Location(19319, 144919, -3103, 31135),
            new Location(-82805, 149890, -3129, 33202),
            new Location(-12347, 122549, -3104, 32603),
            new Location(110642, 220165, -3655, 61898),
            new Location(116619, 75463, -2721, 20881),
            new Location(85513, 16014, -3668, 23681),
            new Location(81999, 53793, -1496, 61621),
            new Location(148159, -55484, -2734, 44315),
            new Location(44185, -48502, -797, 27479),
            new Location(86899, -143229, -1293, 22021));


    @Override
    public void onLoad() {
        properties = new ConcurrentHashMap<>();
        LoadConfig();
        _log.info("Loaded Service: EventsConfig");
    }

    @Override
    public void onReload() {
        onLoad();
    }

    private static void LoadConfig() {
        File files = new File(dir);
        if (!files.exists())
            _log.warn("WARNING! " + dir + " not exists! Config not loaded!");
        else
            parseFiles(files.listFiles());
    }

    private static void parseFiles(File[] files) {
        for (File f : files) {
            if (f.isHidden())
                continue;
            if (f.isDirectory() && !f.getName().contains("defaults"))
                parseFiles(f.listFiles());
            else if (f.getName().endsWith(".ini")) {
                try {
                    InputStream is = new FileInputStream(f);
                    Properties p = new Properties();
                    p.load(is);
                    loadProperties(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void loadProperties(Properties p) {
        for (String name : p.stringPropertyNames()) {
            if (properties.get(name) != null) {
                properties.replace(name, p.getProperty(name).trim());
                _log.info("Duplicate properties name \"" + name + "\" replaced with new value.");
            } else if (p.getProperty(name) == null)
                _log.info("Null property for key " + name);
            else
                properties.put(name, p.getProperty(name).trim());
        }
        p.clear();
    }

    public static String get(String name) {
        if (properties.get(name) == null)
            _log.warn("ConfigSystem: Null value for key: " + name);
        return properties.get(name);
    }

    public static int getInt(String name) {
        return getInt(name, Integer.MAX_VALUE);
    }

    public static int[] getIntArray(String name) {
        return getIntArray(name, new int[0]);
    }

    public static int getIntHex(String name) {
        return getIntHex(name, Integer.decode("0xFFFFFF"));
    }

    public static byte getByte(String name) {
        return getByte(name, Byte.MAX_VALUE);
    }

    public static long getLong(String name) {
        return getLong(name, Long.MAX_VALUE);
    }

    public static double getDouble(String name) {
        return getDouble(name, Double.MAX_VALUE);
    }

    private static String get(String name, String def) {
        return get(name) == null ? def : get(name);
    }

    private static float getFloat(String name, float def) {
        return Float.parseFloat(get(name, String.valueOf(def)));
    }

    private static boolean getBoolean(String name, boolean def) {
        return Boolean.parseBoolean(get(name, String.valueOf(def)));
    }

    private static int getInt(String name, int def) {
        return Integer.parseInt(get(name, String.valueOf(def)));
    }

    private static int[] getIntArray(String name, int[] def) {
        return get(name, null) == null ? def : Util.parseCommaSeparatedIntegerArray(get(name, null));
    }

    private static int getIntHex(String name, int def) {
        if (!get(name, String.valueOf(def)).trim().startsWith("0x"))
            return Integer.decode("0x" + get(name, String.valueOf(def)));
        else
            return Integer.decode(get(name, String.valueOf(def)));
    }

    private static byte getByte(String name, byte def) {
        return Byte.parseByte(get(name, String.valueOf(def)));
    }

    private static double getDouble(String name, double def) {
        return Double.parseDouble(get(name, String.valueOf(def)));
    }

    private static long getLong(String name, long def) {
        return Long.parseLong(get(name, String.valueOf(def)));
    }

    private static void set(String name, String param) {
        properties.replace(name, param);
    }

    public static void set(String name, Object obj) {
        set(name, String.valueOf(obj));
    }
}
