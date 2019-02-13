package l2trunk.gameserver;

import l2trunk.commons.configuration.ExProperties;
import l2trunk.commons.lang.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ServicesConfig {
    private static final Logger _log = LoggerFactory.getLogger(ServicesConfig.class);

    private static final Map<String, String> properties = new ConcurrentHashMap<>();

    private static void load() {
        Path content = Config.DATAPACK_ROOT.resolve("config/services");
        if (!Files.exists(content)) {
            _log.warn(content + " not exists! Config not loaded!");
        } else {
            loadFile(FileUtils.getAllFiles(content, true, ""));
        }
        _log.info("Config services: Loaded " + properties.size() + " properties.");
    }

    public static void reload() {
        properties.clear();
        load();
    }

    private static void loadFile(List<Path> content) {
        content.stream()
                .filter(file -> (file.toString().endsWith(".ini")) || file.toString().endsWith(".properties"))
                .forEach(ServicesConfig::loadProperties);
    }

    private static void loadProperties(Path file) {
        ExProperties p = new ExProperties();
        p.load(file);
        for (String name : p.stringPropertyNames()) {
            if (properties.get(name) != null) {
                properties.replace(name, p.getProperty(name).trim());
            } else if (p.getProperty(name) == null) {
                _log.info("Null property for key " + name);
            } else {
                properties.put(name, p.getProperty(name).trim());
            }
        }
        p.clear();
    }

    public static boolean get(String name, boolean defaultValue) {
        boolean val;
        try {
            val = ExProperties.parseBoolean(properties.get(name));
        } catch (Exception e) {
            val = defaultValue;
        }
        return val;
    }

    public static int get(String name, int defaultValue) {
        int val;
        try {
            val = Integer.parseInt(properties.get(name));
        } catch (NumberFormatException e) {
            val = defaultValue;
        }
        return val;
    }

    public static long get(String name, long defaultValue) {
        long val;
        try {
            val = Long.parseLong(properties.get(name));
        } catch (NumberFormatException e) {
            val = defaultValue;
        }
        return val;
    }

    public static double get(String name, double defaultValue) {
        double val;
        try {
            val = Double.parseDouble(properties.get(name));
        } catch (NumberFormatException e) {
            val = defaultValue;
        }
        return val;
    }

    private static String[] get(String name, String[] defaultValue) {
        String[] val;
        try {
            val = properties.get(name).split("[\\s,;]+");
        } catch (Exception e) {
            val = defaultValue;
        }
        return val;
    }


    public static int[] get(String name) {
        int[] val;

        String[] values = get(name, new String[0]);
        val = new int[values.length];
        for (int i = 0; i < val.length; i++) {
            val[i] = Integer.parseInt(values[i]);
        }
        return val;
    }


}