package l2trunk.gameserver.data;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.utils.Language;

import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public final class StringHolder extends AbstractHolder {
    private static final StringHolder _instance = new StringHolder();
    private final Map<Language, Map<String, String>> _strings = new HashMap<>();

    private StringHolder() {
    }

    public static StringHolder getInstance() {
        return _instance;
    }

    public String getNullable(Player player, String name) {
        Language lang = player == null ? Language.ENGLISH : player.getLanguage();
        return get(lang, name);
    }

    public String getNotNull(Player player, String name) {
        Language lang = player == null ? Language.ENGLISH : player.getLanguage();

        String text = get(lang, name);
        if (text == null && player != null) {
            text = "Not find string: " + name + "; for lang: " + lang;
            _strings.get(lang).put(name, text);
        }

        return text;
    }

    private String get(Language lang, String address) {
        Map<String, String> strings = _strings.get(lang);

        return strings.get(address);
    }

    public void load() {
        for (Language lang : Language.VALUES) {
            _strings.put(lang, new HashMap<>());

            Path f = Config.DATAPACK_ROOT.resolve("data/string/strings_" + lang.getShortName() + ".ini");
            if (!Files.exists(f)) {
                warn("Not find file: " + f.toAbsolutePath());
                continue;
            }

            try (LineNumberReader reader = new LineNumberReader(Files.newBufferedReader(f))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#"))
                        continue;

                    StringTokenizer token = new StringTokenizer(line, "=");
                    if (token.countTokens() < 2) {
                        error("Error on line: " + line + "; file: " + f.toAbsolutePath());
                        continue;
                    }

                    String name = token.nextToken();
                    StringBuilder value = new StringBuilder();
                    value.append(token.nextToken());
                    while (token.hasMoreTokens())
                        value.append('=').append(token.nextToken());

                    Map<String, String> strings = _strings.get(lang);

                    strings.put(name, value.toString());
                }
            } catch (IOException e) {
                error("Exception in StringHolder", e);
            }
        }

        log();
    }

    public void reload() {
        clear();
        load();
    }

    @Override
    public void log() {
        for (Map.Entry<Language, Map<String, String>> entry : _strings.entrySet())
            info("load strings: " + entry.getValue().size() + " for lang: " + entry.getKey());
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {
        _strings.clear();
    }
}
