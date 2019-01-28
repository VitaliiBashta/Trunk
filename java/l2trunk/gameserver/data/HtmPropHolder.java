package l2trunk.gameserver.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class HtmPropHolder {
    private static final Logger LOG = LoggerFactory.getLogger(HtmPropHolder.class);
    private static final Map<String, Map<String, String>> lists = new ConcurrentHashMap<>();

    public static Map<String, String> getList(String filePath) {
        Map<String, String> list = lists.get("data/html-en/" + filePath);
        if (list == null) list = loadFile("data/html-en/" + filePath);
        return list;
    }

    private static Map<String, String> loadFile(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) return null;

        Map<String, String> list = new HashMap<>();
        try {
            String keyWord = "";
            boolean started = false;
            StringBuilder text = new StringBuilder();
            for (String line : Files.readAllLines(path)) {
                line = line.replace("\t", "");

                String lineTrim = line.trim();
                if (keyWord.isEmpty()) {
                    if (!lineTrim.isEmpty())
                        keyWord = lineTrim;
                    if (lineTrim.contains("{")) {
                        started = true;
                    }

                } else if (started) {
                    if (lineTrim.contains("}")) {
                        list.put(keyWord, text.toString());

                        keyWord = "";
                        text.setLength(0);
                        started = false;
                    } else if (!lineTrim.isEmpty()) {
                        if (text.length() > 0) {
                            text.append('\n');
                        }
                        text.append(lineTrim);
                    }

                } else if (lineTrim.contains("{")) {
                    started = true;
                }
            }
        } catch (IOException e) {
            LOG.error("Error while loading HtmPropFile. Path: " + filePath + " Error: ", e);
        }

        if (!list.isEmpty()) {
            lists.put(filePath, list);
            return list;
        }
        return null;
    }
}