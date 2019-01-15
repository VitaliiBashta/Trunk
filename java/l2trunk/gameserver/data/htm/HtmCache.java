package l2trunk.gameserver.data.htm;

import l2trunk.commons.lang.FileUtils;
import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Language;
import l2trunk.gameserver.utils.Strings;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public enum HtmCache {
    INSTANCE;
    public static final int LAZY = 1;
    private static final int DISABLED = 0;
    private static final int ENABLED = 2;

    private static final Logger _log = LoggerFactory.getLogger(HtmCache.class);

    private final Cache[] _cache = new Cache[Language.values().length];

    HtmCache() {
        for (int i = 0; i < _cache.length; i++)
            _cache[i] = CacheManager.getInstance().getCache(getClass().getName() + "." + Language.VALUES[i].name());
    }

    public static HtmCache INSTANCE() {
        return INSTANCE;
    }

    public void reload() {
        //clear();

        switch (Config.HTM_CACHE_MODE) {
            case ENABLED:
                for (Language lang : Language.VALUES) {
                    Path root = Config.DATAPACK_ROOT.resolve("data/html-" + lang.getShortName());
                    if (!Files.exists(root)) {
                        _log.info("HtmCache: Not find html dir for lang: " + lang);
                        continue;
                    }
                    load(lang, root, root.toAbsolutePath() + "/");
                }
                for (int i = 0; i < _cache.length; i++) {
                    Cache c = _cache[i];
                    _log.info(String.format("HtmCache: parsing %d documents; lang: %s.", c.getSize(), Language.VALUES[i]));
                }
                break;
            case LAZY:
                _log.info("HtmCache: lazy cache mode.");
                break;
            case DISABLED:
                _log.info("HtmCache: disabled.");
                break;
        }
    }

    private void load(Language lang, Path f, final String rootPath) {
        if (!Files.exists(f)) {
            _log.info("HtmCache: dir not exists: " + f);
            return;
        }
        List<Path> files = FileUtils.getAllFiles(f, true, ".htm");

        for (Path file : files) {
            putContent(lang, file, rootPath);
        }
    }


    private void putContent(Language lang, Path f, final String rootPath) {
        String content = FileUtils.readFileToString(f);

        String path = f.toAbsolutePath().toString().substring(rootPath.length()).replace("\\", "/").toLowerCase();

        _cache[lang.ordinal()].put(new Element(path, Strings.bbParse(content)));
    }

    public String getNotNull(String fileName, Player player) {
        if (player.isGM())
            Functions.sendDebugMessage(player, "HTML: " + fileName);

        return getNotNull(fileName);
    }

    public String getNotNull(String fileName) {
        String cache = getCache(fileName);

        if (StringUtils.isEmpty(cache))
            cache = "Dialog not found: " + fileName + "; Lang: " + Language.ENGLISH;

        return cache;
    }

    public String getNullable(String fileName) {
        String cache = getCache(fileName);

        if (StringUtils.isEmpty(cache))
            return null;

        return cache;
    }

    private String getCache(String file) {
        if (file == null)
            return null;

        final String fileLower = file.toLowerCase();
        String cache = get(fileLower);

        if (cache == null) {
            switch (Config.HTM_CACHE_MODE) {
                case ENABLED:
                    break;
                case LAZY:
                    cache = loadLazy(file);
                    break;
                case DISABLED:
                    cache = loadDisabled(file);
                    break;
            }
        }

        return cache;
    }

    private String loadDisabled(String file) {
        String cache = null;
        Path f = Config.DATAPACK_ROOT.resolve("data/html-" + Language.ENGLISH.getShortName() + "/" + file);
        if (Files.exists(f)) {
            cache = FileUtils.readFileToString(f);
            cache = Strings.bbParse(cache);
        }
        return cache;
    }

    private String loadLazy(String file) {
        String cache = null;
        Path f = Config.DATAPACK_ROOT.resolve("data/html-" + Language.ENGLISH.getShortName() + "/" + file);
        if (Files.exists(f)) {
            cache = FileUtils.readFileToString(f);
            cache = Strings.bbParse(cache);

            _cache[Language.ENGLISH.ordinal()].put(new Element(file, cache));
        }
        return cache;
    }

    private String get(String f) {
        Element element = _cache[Language.ENGLISH.ordinal()].get(f);

        if (element == null)
            element = _cache[Language.ENGLISH.ordinal()].get(f);

        return element == null ? null : (String) element.getObjectValue();
    }

    public void clear() {
        for (Cache a_cache : _cache) a_cache.removeAll();
    }
}
