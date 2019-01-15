package l2trunk.gameserver.model.entity.achievements;

import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.model.Player;

import java.util.ArrayList;
import java.util.List;

final class AchievementCategory {
    private static final int BAR_MAX = 24;
    private final int _categoryId;
    //private String _html;
    private final String _name;
    private final String _icon;
    private final String _desc;
    private final List<Achievement> _achievements = new ArrayList<>();

    public AchievementCategory(int categoryId, String categoryName, String categoryIcon, String categoryDesc) {
        _categoryId = categoryId;
        _name = categoryName;
        _icon = categoryIcon;
        _desc = categoryDesc;
    }

    public String getHtml(Player player) {
        return getHtml(Achievements.getAchievementLevelSum(player, getCategoryId()));
    }

    private String getHtml(int totalPlayerLevel) {
        int greenbar = 0;

        if (totalPlayerLevel > 0) {
            greenbar = BAR_MAX * (totalPlayerLevel * 100 / _achievements.size()) / 100;
            greenbar = Math.min(greenbar, BAR_MAX);
        }

        String temp = HtmCache.INSTANCE.getNullable("achievements/AchievementsCat.htm");

        temp = temp.replaceFirst("%bg%", getCategoryId() % 2 == 0 ? "090908" : "0f100f");
        temp = temp.replaceFirst("%desc%", getDesc());
        temp = temp.replaceFirst("%icon%", getIcon());
        temp = temp.replaceFirst("%name%", getName());
        temp = temp.replaceFirst("%id%", "" + getCategoryId());

        temp = temp.replaceFirst("%caps1%", greenbar > 0 ? "Gauge_DF_Food_Left" : "Gauge_DF_Exp_bg_Left");
        temp = temp.replaceFirst("%caps2%", greenbar >= 24 ? "Gauge_DF_Food_Right" : "Gauge_DF_Exp_bg_Right");

        temp = temp.replaceAll("%bar1%", "" + greenbar);
        temp = temp.replaceAll("%bar2%", "" + (24 - greenbar));
        return temp;
    }

    public int getCategoryId() {
        return _categoryId;
    }

    public List<Achievement> getAchievements() {
        return _achievements;
    }

    public String getDesc() {
        return _desc;
    }

    public String getIcon() {
        return _icon;
    }

    public String getName() {
        return _name;
    }
}
