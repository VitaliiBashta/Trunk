package l2trunk.gameserver.utils;

import l2trunk.commons.lang.FileUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.quest.Quest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class AddonsConfig {
    private static final Logger log = LoggerFactory.getLogger(AddonsConfig.class);
    private static final Path dir = Config.DATAPACK_ROOT.resolve("config/Addons");
    private static Map<Integer, Integer> SKILL_DURATION_LIST;
    private static Map<Integer, Integer> SKILL_REUSE_LIST;
    private static ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Double> questRewardRates = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Double> questDropRates = new ConcurrentHashMap<>();

    public static void load() {
        if (!Files.exists(dir))
            log.warn("WARNING! " + dir + " not exists! Config not loaded!");
        else {
            properties = new ConcurrentHashMap<>();
            questRewardRates = new ConcurrentHashMap<>();
            questDropRates = new ConcurrentHashMap<>();
            parseFiles(FileUtils.getAllFiles(dir, true, ""));
        }
    }

    public synchronized static void reload() {
        properties = new ConcurrentHashMap<>();
        questRewardRates = new ConcurrentHashMap<>();
        questDropRates = new ConcurrentHashMap<>();
        load();

    }

    private static void parseFiles(List<Path> files) {
        for (Path f : files) {
            try {
                if (Files.isHidden(f))
                    continue;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (f.toString().startsWith("quest_reward_rates")) {
                try (InputStream is = Files.newInputStream(f)) {
                    Properties p = new Properties();
                    p.load(is);
                    loadQuestRewardRates(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (f.toString().startsWith("quest_drop_rates")) {
                try (InputStream is = Files.newInputStream(f)) {
                    Properties p = new Properties();
                    p.load(is);
                    loadQuestDropRates(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (f.toString().endsWith(".ini")) {
                try (InputStream is = Files.newInputStream(f)) {
                    Properties p = new Properties();
                    p.load(is);
                    loadProperties(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void loadQuestRewardRates(Properties p) {
        for (String name : p.stringPropertyNames()) {
            int id;
            try {
                id = Integer.parseInt(name);
            } catch (NumberFormatException nfe) {
                continue;
            }
            if (questRewardRates.get(id) != null) {
                questRewardRates.replace(id, Double.parseDouble(p.getProperty(name).trim()));
                log.info("Duplicate quest Reward id \"" + name + "\"");
            } else if (p.getProperty(name) == null)
                log.info("Null property for quest id " + name);
            else
                questRewardRates.put(id, Double.parseDouble(p.getProperty(name).trim()));
        }
        p.clear();
    }

    private static void loadQuestDropRates(Properties p) {
        for (String name : p.stringPropertyNames()) {
            int id;
            try {
                id = Integer.parseInt(name);
            } catch (NumberFormatException nfe) {
                continue;
            }
            if (questDropRates.get(id) != null) {
                questDropRates.replace(id, Double.parseDouble(p.getProperty(name).trim()));
                log.info("Duplicate quest Drop id \"" + name + "\"");
            } else if (p.getProperty(name) == null)
                log.info("Null property for quest id " + name);
            else
                questDropRates.put(id, Double.parseDouble(p.getProperty(name).trim()));
        }
        p.clear();
    }

    private static void loadProperties(Properties p) {
        for (String name : p.stringPropertyNames()) {
            if (properties.get(name) != null) {
                properties.replace(name, p.getProperty(name).trim());
                log.info("Duplicate properties name \"" + name + "\" replaced with new value.");
            } else if (p.getProperty(name) == null)
                log.info("Null property for key " + name);
            else
                properties.put(name, p.getProperty(name).trim());
        }
        p.clear();
    }

    public static double getQuestRewardRates(Quest q) {
        return questRewardRates.getOrDefault(q.getQuestIntId(), 1.0);
    }

    public static double getQuestDropRates(Quest q) {
        return questDropRates.getOrDefault(q.getQuestIntId(), 1.0);
    }

    private static String get(String name) {
        if (properties.get(name) == null)
            log.warn("AddonsConfig: Null value for key: " + name);
        return properties.get(name);
    }

    public static float getFloat(String name) {
        return getFloat(name, Float.MAX_VALUE);
    }

    private static boolean getBoolean(String name) {
        return getBoolean(name, false);
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

    public static void loadSkillDurationList() {
        if (getBoolean("EnableModifySkillDuration")) {
            String[] propertySplit = get("SkillDurationList").split(";");
            SKILL_DURATION_LIST = new HashMap<>(propertySplit.length);
            for (String skill : propertySplit) {
                String[] skillSplit = skill.split(",");
                if (skillSplit.length != 2)
                    log.warn(concat("[SkillDurationList]: invalid config property -> SkillDurationList \"", skill, "\""));
                else {
                    try {
                        SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
                    } catch (NumberFormatException nfe) {
                        if (!skill.isEmpty()) {
                            log.warn(concat("[SkillDurationList]: invalid config property -> SkillList \"", skillSplit[0], "\"", skillSplit[1]));
                        }
                    }
                }
            }
        }
    }

    public static void loadSkillReuseList() {
        if (getBoolean("EnableModifySkillReuse")) {
            String[] propertySplit = get("SkillReuseList").split(";");
            SKILL_REUSE_LIST = new HashMap<>(propertySplit.length);
            for (String skill : propertySplit) {
                String[] skillSplit = skill.split(",");
                if (skillSplit.length != 2)
                    log.warn(concat("[SkillReuseList]: invalid config property -> SkillReuseList \"", skill, "\""));
                else {
                    try {
                        SKILL_REUSE_LIST.put(Integer.valueOf(skillSplit[0]), Integer.valueOf(skillSplit[1]));
                    } catch (NumberFormatException nfe) {
                        if (!skill.isEmpty())
                            log.warn(concat("[SkillReuseList]: invalid config property -> SkillList \"", skillSplit[0], "\"", skillSplit[1]));
                    }
                }
            }
        }
    }

    private static String concat(final String... strings) {
        final StringBuilder sbString = new StringBuilder(getLength(strings));
        for (final String string : strings) {
            sbString.append(string);
        }
        return sbString.toString();
    }

    private static int getLength(final String[] strings) {
        int length = 0;
        for (final String string : strings) {
            length += string.length();
        }
        return length;
    }
}
