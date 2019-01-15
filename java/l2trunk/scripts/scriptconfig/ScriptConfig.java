package l2trunk.scripts.scriptconfig;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptConfig extends Functions implements ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(ScriptConfig.class);

    private static final String dir = (Config.DATAPACK_ROOT + "/config/ScripsConfig");
    private static ConcurrentHashMap<String, String> properties;

    @Override
    public void onLoad() {
        properties = new ConcurrentHashMap<>();
        LoadConfig();
        LOG.info("Loaded Service: ScripsConfig");
    }

    @Override
    public void onReload() {
        onLoad();
    }

    private static void LoadConfig() {
        File files = new File(dir);
        if (!files.exists())
            LOG.warn("WARNING! " + dir + " not exists! Config not loaded!");
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
                LOG.info("Duplicate properties name \"" + name + "\" replaced with new value.");
            } else if (p.getProperty(name) == null)
                LOG.info("Null property for key " + name);
            else
                properties.put(name, p.getProperty(name).trim());
        }
        p.clear();
    }

    private static String get(String name) {
        if (properties.get(name) == null)
            LOG.warn("ConfigSystem: Null value for key: " + name);
        return properties.get(name);
    }

    private static int getInt(String name) {
        return getInt(name, Integer.MAX_VALUE);
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

    private static int getInt(String name, int def) {
        return Integer.parseInt(get(name, String.valueOf(def)));
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
