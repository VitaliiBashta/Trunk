package l2trunk.gameserver.utils;

import l2trunk.commons.configuration.ExProperties;
import l2trunk.commons.lang.FileUtils;
import static l2trunk.commons.lang.NumberUtils.*;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.quest.Quest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public final class AddonsConfig {
    private static final Logger log = LoggerFactory.getLogger(AddonsConfig.class);
    private static final Path dir = Config.DATAPACK_ROOT.resolve("config/Addons");
    private static Map<String, String> properties = new ConcurrentHashMap<>();
    private static Map<Integer, Double> questRewardRates = new ConcurrentHashMap<>();
    private static Map<Integer, Double> questDropRates = new ConcurrentHashMap<>();

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
                    ExProperties p = new ExProperties();
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

    private static void loadQuestRewardRates(ExProperties p) {
        for (String name : p.stringPropertyNames()) {
            int id = toInt(name);
            if (questRewardRates.containsKey(id)) {
                questRewardRates.replace(id, toDouble(p.getProperty(name).trim()));
                log.info("Duplicate quest Reward id \"" + name + "\"");
            } else if (p.getProperty(name) == null)
                log.info("Null property for quest id " + name);
            else
                questRewardRates.put(id, toDouble(p.getProperty(name).trim()));
        }
        p.clear();
    }

    private static void loadQuestDropRates(Properties p) {
        for (String name : p.stringPropertyNames()) {
            int id = toInt(name);
            if (questDropRates.get(id) != null) {
                questDropRates.replace(id, toDouble(p.getProperty(name).trim()));
                log.info("Duplicate quest Drop id \"" + name + "\"");
            } else if (p.getProperty(name) == null)
                log.info("Null property for quest id " + name);
            else
                questDropRates.put(id, toDouble(p.getProperty(name).trim()));
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

}
