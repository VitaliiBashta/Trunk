package l2trunk.scripts.services.community;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.Functions;

/**
 * info_folder = папка для категорий страниц - String
 * info_page = имя файла, название страницы - String
 * Вызов всплывающего окна у НПС - [scripts_services.ShowInfo:show info_folder info_page|Имя папки, Имя страницы]
 * Вызов всплывающего окна из комьюнити - [bypass _bbsscripts:services.community.ShowInfo:show info_folder info_page|Имя папки, Имя страницы]
 **/

public final class ShowInfo extends Functions {
    public void show(String[] param) {
        String info_folder;
        String info_page;

        if (player == null)
            return;

        if (param.length != 2) {
            String html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "wiki/error_page.htm", player);
            show(html, player);
            return;
        }

        info_folder = String.valueOf(param[0]);
        info_page = String.valueOf(param[1]);

        String html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "wiki/" + info_folder + "/" + info_page + ".htm", player);
        show(html, player);
    }

}