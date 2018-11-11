package l2trunk.gameserver.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HtmPropHolder {
    private static final Logger LOG = LoggerFactory.getLogger(HtmPropHolder.class);

    private static final String[] HTM_FILE_ENDINGS = {".htm", ".html"};
    private static final String USUAL_PROP_NAME_ADDON = ".prop";
    private static final String STARTING_PATH = "data/html-en/";
    private static final char LINE_SEPARATOR = '\n';
    private static final String START_BOUNDARY = "{";
    private static final String FINISH_BOUNDARY = "}";
    private final Map<String, HtmPropList> lists;

    private HtmPropHolder() {
        this.lists = new ConcurrentHashMap<>();
    }

    public static HtmPropList getList(String filePath) {
        String additionalEnding = "";
        for (int endingIndex = -1; endingIndex < HTM_FILE_ENDINGS.length; endingIndex++) {
            if (endingIndex >= 0) {
                additionalEnding = HTM_FILE_ENDINGS[endingIndex];
            }
            HtmPropList list = getInstance().justGetList("data/html-en/" + filePath + additionalEnding);
            if (list == null) {
                list = getInstance().loadFile("data/html-en/" + filePath + additionalEnding);
            }
            if (list != null) {
                return list;
            }
        }
        return null;
    }

    private static HtmPropHolder getInstance() {
        return HtmPropHolderHolder.instance;
    }

    private HtmPropList justGetList(String filePath) {
        return this.lists.get(filePath);
    }

    private HtmPropList loadFile(String filePath) {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            return null;
        }
        List<HtmProp> list = new ArrayList<>();
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
                        HtmProp prop = new HtmProp(keyWord, text.toString());
                        list.add(prop);

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
            HtmPropList propList = new HtmPropList(list);
            this.lists.put(filePath, propList);
            return propList;
        }
        return null;
    }

    private static class HtmPropHolderHolder {
        private static final HtmPropHolder instance = new HtmPropHolder();
    }
}