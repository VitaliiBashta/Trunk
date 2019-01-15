package l2trunk.gameserver.data;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.utils.Language;
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
    private final Map<Language, Map<String, String>> strings = new HashMap<>();

    public String getNullable(String name) {
        return get(Language.ENGLISH, name);
    }

    public String getNotNull(Player player, String name) {
        Language lang =  Language.ENGLISH;

        String text = get(lang, name);
        if (text == null && player != null) {
            text = "Not find string: " + name + "; for lang: " + lang;
            strings.get(lang).put(name, text);
        }

        return text;
    }

    private String get(Language lang, String address) {
        Map<String, String> strings = this.strings.get(lang);

        return strings.get(address);
    }

    public void load() {
        for (Language lang : Language.VALUES) {
            strings.put(lang, new HashMap<>());

            Path f = Config.DATAPACK_ROOT.resolve("data/string/strings_" + lang.getShortName() + ".ini");
            if (!Files.exists(f)) {
                LOG.warn("Not find file: " + f.toAbsolutePath());
                continue;
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

                    Map<String, String> strings = this.strings.get(lang);

                    strings.put(name, value.toString());
                }
            } catch (IOException e) {
                LOG.error("Exception in StringHolder", e);
            }
        }
        strings.forEach((key, value) -> LOG.info("loaded " + value.size() + " for " + key.getShortName() + " language"));
    }

    public void reload() {
        strings.clear();
        load();
    }
}
