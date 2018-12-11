package l2trunk.gameserver;

import l2trunk.commons.configuration.ExProperties;
import l2trunk.commons.lang.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ServicesConfig {
    private static final Logger _log = LoggerFactory.getLogger(ServicesConfig.class);

    private static final ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<>();

    private static void load() {
        Path _content = Config.DATAPACK_ROOT.resolve("config/services");
        if (!Files.exists(_content)) {
            _log.warn(_content + " not exists! Config not loaded!");
        } else {
            loadFile(FileUtils.getAllFiles(_content, true, ""));
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
        try {
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
        } catch (IOException e) {
            _log.error("Error loading config : " + file.toString() + '!', e);
        }
    }

//    public static String get(String name, String defaultValue) {
//        String val;
//        try {
//            val = properties.get(name);
//        } catch (Exception e) {
//            val = defaultValue;
//        }
//        return val;
//    }

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
        } catch (Exception e) {
            val = defaultValue;
        }
        return val;
    }

    public static long get(String name, long defaultValue) {
        long val;
        try {
            val = Long.parseLong(properties.get(name));
        } catch (Exception e) {
            val = defaultValue;
        }
        return val;
    }

    public static double get(String name, double defaultValue) {
        double val;
        try {
            val = Double.parseDouble(properties.get(name));
        } catch (Exception e) {
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

    public static boolean[] get(String name, boolean[] defaultValue) {
        boolean[] val;

        String[] values = get(name, new String[0]);
        val = new boolean[values.length];
        for (int i = 0; i < val.length; i++) {
            val[i] = ExProperties.parseBoolean(values[i]);
        }
        return val;
    }

    public static int[] get(String name, int[] defaultValue) {
        int[] val;

        String[] values = get(name, new String[0]);
        val = new int[values.length];
        for (int i = 0; i < val.length; i++) {
            val[i] = Integer.parseInt(values[i]);
        }
        return val;
    }

    public static long[] get(String name, long[] defaultValue) {
        long[] val;

        String[] values = get(name, new String[0]);
        val = new long[values.length];
        for (int i = 0; i < val.length; i++) {
            val[i] = Long.parseLong(values[i]);
        }
        return val;
    }

    public static double[] get(String name, double[] defaultValue) {
        double[] val;

        String[] values = get(name, new String[0]);
        val = new double[values.length];
        for (int i = 0; i < val.length; i++) {
            val[i] = Double.parseDouble(values[i]);
        }
        return val;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> get(String name, HashMap<String, String> defaultValue) {
        @SuppressWarnings("rawtypes")
        Map values = defaultValue;

        String[] propertySplit = get(name, new String[0]);
        for (String element : propertySplit) {
            String[] vals = element.split(":");
            if (vals.length != 2) {
                continue;
            }
            try {
                values.put(vals[0], vals[1]);
            } catch (Exception e) {
                //
            }
        }
        return values;
    }

    public static Map<Integer, Integer> getII(String name, HashMap<Integer, Integer> defaultValue) {
        Map<Integer, Integer> values = new HashMap<>(defaultValue);

        String[] propertySplit = get(name, new String[0]);
        for (String element : propertySplit) {
            String[] vals = element.split(":");
            if (vals.length != 2) {
                continue;
            }
            try {
                values.put(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]));
            } catch (NumberFormatException e) {
                //
            }
        }
        return values;
    }

    public static Map<Integer, Double> getID(String name, HashMap<Integer, Double> defaultValue) {
        Map<Integer, Double> values = new HashMap<>(defaultValue);

        String[] propertySplit = get(name, new String[0]);
        for (String element : propertySplit) {
            String[] vals = element.split(":");
            if (vals.length != 2) {
                continue;
            }
            try {
                values.put(Integer.parseInt(vals[0]), Double.parseDouble(vals[1]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    public static Map<Integer, Long> getIL(String name, HashMap<Integer, Long> defaultValue) {
        Map<Integer, Long> values = new HashMap<>(defaultValue);

        String[] propertySplit = get(name, new String[0]);
        for (String element : propertySplit) {
            String[] vals = element.split(":");
            if (vals.length != 2) {
                continue;
            }
            try {
                values.put(Integer.parseInt(vals[0]), Long.parseLong(vals[1]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return values;
    }
}