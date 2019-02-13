package l2trunk.gameserver.data;

import l2trunk.gameserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public enum StringHolder {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(StringHolder.class);
    private final Map<String, String> strings = new HashMap<>();

    public String getNullable(String name) {
        return get(name);
    }

    public String getNotNull(String name) {
        String text = get(name);
        if (text == null) {
            text = "Not find string: " + name;
            strings.put(name, text);
        }

        return text;
    }

    private String get(String address) {
        return strings.get(address);
    }

    public void load() {

        Path f = Config.DATAPACK_ROOT.resolve("data/string/strings_en.ini");
        if (!Files.exists(f)) {
            LOG.warn("Not find file: " + f.toAbsolutePath());
            return;
        }

        try (LineNumberReader reader = new LineNumberReader(Files.newBufferedReader(f))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#"))
                    continue;

                StringTokenizer token = new StringTokenizer(line, "=");
                if (token.countTokens() < 2) {
                    LOG.error("Error on line: " + line + "; file: " + f.toAbsolutePath());
                    continue;
                }

                String name = token.nextToken();
                StringBuilder value = new StringBuilder();
                value.append(token.nextToken());
                while (token.hasMoreTokens())
                    value.append('=').append(token.nextToken());

                strings.put(name, value.toString());
            }
        } catch (IOException e) {
            LOG.error("Exception in StringHolder", e);
        }
        LOG.info("loaded " + strings.size());
    }

    public void reload() {
        strings.clear();
        load();
    }
}
