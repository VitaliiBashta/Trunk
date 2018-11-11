package l2trunk.gameserver.data.htm;

import l2trunk.commons.crypt.CryptUtil;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class HtmCache {
    private static final int DISABLED = 0;
    public static final int LAZY = 1;
    private static final int ENABLED = 2;

    private static final Logger _log = LoggerFactory.getLogger(HtmCache.class);

    private final static HtmCache _instance = new HtmCache();
    private final Cache[] _cache = new Cache[Language.VALUES.length];

    private HtmCache() {
        for (int i = 0; i < _cache.length; i++)
            _cache[i] = CacheManager.getInstance().getCache(getClass().getName() + "." + Language.VALUES[i].name());
    }

    public static HtmCache getInstance() {
        return _instance;
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


    private String getContent(Path file) throws IOException {
        InputStream stream = CryptUtil.decryptOnDemand(new ByteArrayInputStream(FileUtils.readFileToString(file).getBytes()));
        InputStream _stream = Files.newInputStream(file);
        StringBuilder builder = new StringBuilder();
        if ((byte) _stream.read() == 0x00) {
            byte[] buffer = new byte[1024];
            int num;
            while ((num = stream.read(buffer)) >= 0) {
                String tmp = new String(buffer);
                builder.append(tmp, 0, num);
            }
            return builder.toString();
        }
        return FileUtils.readFileToString(file);
    }

    private void putContent(Language lang, Path f, final String rootPath) {
        String content = FileUtils.readFileToString(f);

        String path = f.toAbsolutePath().toString().substring(rootPath.length()).replace("\\", "/").toLowerCase();

        _cache[lang.ordinal()].put(new Element(path, Strings.bbParse(content)));
    }

    public String getNotNull(String fileName, Player player) {
        Language lang = player == null ? Language.ENGLISH : player.getLanguage();

        if (player.isGM())
            Functions.sendDebugMessage(player, "HTML: " + fileName);

        return getNotNull(fileName, lang);
    }

    public String getNotNull(String fileName, Language lang) {
        String cache = getCache(fileName, lang);

        if (StringUtils.isEmpty(cache))
            cache = "Dialog not found: " + fileName + "; Lang: " + lang;

        return cache;
    }

    public String getNullable(String fileName, Player player) {
        Language lang = player == null ? Language.ENGLISH : player.getLanguage();
        String cache = getCache(fileName, lang);

        if (StringUtils.isEmpty(cache))
            return null;

        return cache;
    }

    private String getCache(String file, Language lang) {
        if (file == null)
            return null;

        final String fileLower = file.toLowerCase();
        String cache = get(lang, fileLower);

        if (cache == null) {
            switch (Config.HTM_CACHE_MODE) {
                case ENABLED:
                    break;
                case LAZY:
                    cache = loadLazy(lang, file);
                    if (cache == null && lang != Language.ENGLISH)
                        cache = loadLazy(Language.ENGLISH, file);
                    break;
                case DISABLED:
                    cache = loadDisabled(lang, file);
                    if (cache == null && lang != Language.ENGLISH)
                        cache = loadDisabled(Language.ENGLISH, file);
                    break;
            }
        }

        return cache;
    }

    private String loadDisabled(Language lang, String file) {
        String cache = null;
        Path f = Config.DATAPACK_ROOT.resolve("data/html-" + lang.getShortName() + "/" + file);
        if (Files.exists(f)) {
            cache = FileUtils.readFileToString(f);
            cache = Strings.bbParse(cache);
        }
        return cache;
    }

    private String loadLazy(Language lang, String file) {
        String cache = null;
        Path f = Config.DATAPACK_ROOT.resolve("data/html-" + lang.getShortName() + "/" + file);
        if (Files.exists(f)) {
            cache = FileUtils.readFileToString(f);
            cache = Strings.bbParse(cache);

            _cache[lang.ordinal()].put(new Element(file, cache));
        }
        return cache;
    }

    private String get(Language lang, String f) {
        Element element = _cache[lang.ordinal()].get(f);

        if (element == null)
            element = _cache[Language.ENGLISH.ordinal()].get(f);

        return element == null ? null : (String) element.getObjectValue();
    }

    public void clear() {
        for (Cache a_cache : _cache) a_cache.removeAll();
    }
}
