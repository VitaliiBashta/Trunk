package l2trunk.gameserver.utils;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.model.Player;

public final class BbsUtil {
    private BbsUtil() {
    }

    public static String htmlAll(String htm, Player player) {
        String html_all = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "setBlock/allpages.htm", player);
        String html_menu = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "setBlock/menu.htm", player);
        String html_copy = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "setBlock/copyright.htm", player);
        html_all = html_all.replace("%main_menu%", html_menu);
        html_all = html_all.replace("%body_page%", htm);
        html_all = html_all.replace("%copyright%", html_copy);
        html_all = html_all.replace("%copyrightsym%", "©");
        return html_all;
    }

    public static String htmlNotAll(String htm, Player player) {
        String html_all = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "setBlock/allpages.htm", player);
        html_all = html_all.replace("%body_page%", htm);
        return html_all;
    }

    public static String htmlBuff(String htm, Player player) {
        String html_option = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "pages/buffer/setBlock/option.htm", player);
        htm = htm.replace("%main_optons%", html_option);
        htm = htmlAll(htm, player);
        return htm;
    }
}