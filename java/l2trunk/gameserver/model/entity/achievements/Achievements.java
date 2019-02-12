package l2trunk.gameserver.model.entity.achievements;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.TutorialCloseHtml;
import l2trunk.gameserver.network.serverpackets.TutorialShowHtml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

public enum Achievements {
    INSTANCE;
    private static final Logger _log = LoggerFactory.getLogger(Achievements.class);
    // id-max
    private final Map<Integer, Integer> achievementMaxLevels = new HashMap<>();
    private final List<AchievementCategory> achievementCategories = new LinkedList<>();

    Achievements() {
        load();
    }

    static int getAchievementLevelSum(Player player, int categoryId) {
        return player.getAchievements(categoryId).values().stream().mapToInt(level -> level).sum();
    }


    public void onBypass(Player player, String bypass, String[] cm) {
        if (bypass.startsWith("_bbs_achievements_cat")) {
            generatePage(player, Integer.parseInt(cm[1]), Integer.parseInt(cm[2]));
        } else if (bypass.equals("_bbs_achievements_close")) {
            player.sendPacket(TutorialCloseHtml.STATIC);
        } else if (bypass.startsWith("_bbs_achievements")) {
            checkAchievementRewards(player);
            generatePage(player);
        } else {
            _log.warn("Invalid achievements bypass: " + bypass);
        }
    }

    private void generatePage(Player player) {
        if (player == null)
            return;

        String achievements = HtmCache.INSTANCE.getNotNull("achievements/Achievements.htm", player);

        StringBuilder ac = new StringBuilder();
        achievementCategories.forEach(cat ->
                ac.append(cat.getHtml(player)));

        achievements = achievements.replace("%categories%", ac.toString());

        player.sendPacket(new TutorialShowHtml(achievements));
    }

    public void generatePage(Player player, int category, int page) {
        if (player == null)
            return;

        String FULL_PAGE = HtmCache.INSTANCE.getNotNull("achievements/inAchievements.htm", player);

        boolean done;
        StringBuilder achievementsNotDone = new StringBuilder();
        StringBuilder achievementsDone = new StringBuilder();
        String html;

        long playerPoints;
        int all = 0;
        int clansvisual = 0;
        int totalpages = (int) (Math.round(player.getAchievements(category).size()) / 5.0 + 1);

        FULL_PAGE = FULL_PAGE.replaceAll("%back%", page == 1 ? "&nbsp;" : "<button value=\"\" action=\"bypass _bbs_achievements_cat " + category + " " + (page - 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\">");
        FULL_PAGE = FULL_PAGE.replaceAll("%more%", totalpages <= page ? "&nbsp;" : "<button value=\"\" action=\"bypass _bbs_achievements_cat " + category + " " + (page + 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\">");

        AchievementCategory cat = achievementCategories.stream().filter(ctg -> ctg.getCategoryId() == category).findAny().orElse(null);
        if (cat == null) {
            _log.warn("Achievements: getCatById - cat - is null, return. for " + player.getName());
            return;
        }

        for (Entry<Integer, Integer> entry : player.getAchievements(category).entrySet()) {
            int aId = entry.getKey();
            int nextLevel = (entry.getValue() + 1) >= getMaxLevel(aId) ? getMaxLevel(aId) : (entry.getValue() + 1);
            Achievement a = getAchievement(aId, Math.max(1, nextLevel));

            if (a == null) {
                _log.warn("Achievements: GetAchievement - a - is null, return. for " + player.getName());
                return;
            }

            playerPoints = player.getCounters().getPoints(a.getType());

            all++;
            if (page == 1 && clansvisual > 5)
                continue;
            if (all > page * 5)
                continue;
            if (all <= (page - 1) * 5)
                continue;

            clansvisual++;

            if (!a.isDone(playerPoints)) {
                done = false;

                String notDoneAchievement = HtmCache.INSTANCE.getNullable("achievements/oneAchievement.htm");

                long needpoints = a.getPointsToComplete();
                long diff = Math.max(0, needpoints - playerPoints);
                long greenbar = 24 * (playerPoints * 100 / needpoints) / 100;
                if (greenbar < 0)
                    greenbar = 0;

                if (greenbar > 24)
                    greenbar = 24;

                notDoneAchievement = notDoneAchievement.replaceFirst("%fame%", "" + a.getFame());
                notDoneAchievement = notDoneAchievement.replaceAll("%bar1%", "" + greenbar);
                notDoneAchievement = notDoneAchievement.replaceAll("%bar2%", "" + (24 - greenbar));

                notDoneAchievement = notDoneAchievement.replaceFirst("%cap1%", greenbar > 0 ? "Gauge_DF_Food_Left" : "Gauge_DF_Exp_bg_Left");
                notDoneAchievement = notDoneAchievement.replaceFirst("%cap2%", "Gauge_DF_Exp_bg_Right");

                notDoneAchievement = notDoneAchievement.replaceFirst("%desc%", a.getDesc().replaceAll("%need%", "" + (diff > 0 ? diff : "no")));

                notDoneAchievement = notDoneAchievement.replaceFirst("%bg%", a.getId() % 2 == 0 ? "090908" : "0f100f");
                notDoneAchievement = notDoneAchievement.replaceFirst("%icon%", a.getIcon());
                notDoneAchievement = notDoneAchievement.replaceFirst("%name%", a.name + (a.getLevel() > 1 ? (" Lv. " + a.getLevel()) : ""));

                html = notDoneAchievement;
            } else {
                done = true;

                String doneAchievement = HtmCache.INSTANCE.getNullable("achievements/oneAchievement.htm");

                doneAchievement = doneAchievement.replaceFirst("%fame%", "" + a.getFame());
                doneAchievement = doneAchievement.replaceAll("%bar1%", "24");
                doneAchievement = doneAchievement.replaceAll("%bar2%", "0");

                doneAchievement = doneAchievement.replaceFirst("%cap1%", "Gauge_DF_Food_Left");
                doneAchievement = doneAchievement.replaceFirst("%cap2%", "Gauge_DF_Food_Right");

                doneAchievement = doneAchievement.replaceFirst("%desc%", "Done.");

                doneAchievement = doneAchievement.replaceFirst("%bg%", a.getId() % 2 == 0 ? "090908" : "0f100f");
                doneAchievement = doneAchievement.replaceFirst("%icon%", a.getIcon());
                doneAchievement = doneAchievement.replaceFirst("%name%", a.name + (a.getLevel() > 1 ? (" Lv. " + a.getLevel()) : ""));

                html = doneAchievement;
            }

            if (clansvisual < 5) {
                for (int d = clansvisual + 1; d != 5; d++) {
                    html = html.replaceAll("%icon" + d + "%", "L2UI_CT1.Inventory_DF_CloakSlot_Disable");
                    html = html.replaceAll("%bar1" + d + "%", "0");
                    html = html.replaceAll("%bar2" + d + "%", "0");
                    html = html.replaceAll("%cap1" + d + "%", "&nbsp;");
                    html = html.replaceAll("%cap2" + d + "%", "&nbsp");
                    html = html.replaceAll("%desc" + d + "%", "&nbsp");
                    html = html.replaceAll("%bg" + d + "%", "0f100f");
                    html = html.replaceAll("%name" + d + "%", "&nbsp");
                }
            }

            if (!done)
                achievementsNotDone.append(html);
            else
                achievementsDone.append(html);
        }


        int greenbar = 0;
        if (getAchievementLevelSum(player, category) > 0) {
            greenbar = 248/*BAR_MAX*/ * (getAchievementLevelSum(player, category) * 100 / cat.getAchievements().size()) / 100;
            greenbar = Math.min(greenbar, 248/*BAR_MAX*/);
        }
        String fp = FULL_PAGE;
        fp = fp.replaceAll("%bar1up%", "" + greenbar);
        fp = fp.replaceAll("%bar2up%", "" + (248 - greenbar));

        fp = fp.replaceFirst("%caps1%", greenbar > 0 ? "Gauge_DF_Large_Food_Left" : "Gauge_DF_Large_Exp_bg_Left");

        fp = fp.replaceFirst("%caps2%", greenbar >= 248 ? "Gauge_DF_Large_Food_Right" : "Gauge_DF_Large_Exp_bg_Right");

        fp = fp.replaceFirst("%achievementsNotDone%", achievementsNotDone.toString());
        fp = fp.replaceFirst("%achievementsDone%", achievementsDone.toString());
        fp = fp.replaceFirst("%catname%", cat.getName());
        fp = fp.replaceFirst("%catDesc%", cat.getDesc());
        fp = fp.replaceFirst("%catIcon%", cat.getIcon());

        player.sendPacket(new TutorialShowHtml(fp));
    }

    private void checkAchievementRewards(Player player) {
        synchronized (player.getAchievements()) {
            for (Entry<Integer, Integer> arco : player.getAchievements().entrySet()) {
                int achievementId = arco.getKey();
                int achievementLevel = arco.getValue();
                if (getMaxLevel(achievementId) <= achievementLevel)
                    continue;

                Achievement nextLevelAchievement;
                do {
                    achievementLevel++;
                    nextLevelAchievement = getAchievement(achievementId, achievementLevel);
                    if (nextLevelAchievement != null && nextLevelAchievement.isDone(player.getCounters().getPoints(nextLevelAchievement.getType())))
                        nextLevelAchievement.reward(player);
                }
                while (nextLevelAchievement != null);
            }
        }
    }

    public int getPointsForThisLevel(int totalPoints, int achievementId, int achievementLevel) {
        if (totalPoints == 0)
            return 0;

        int result = 0;
        for (int i = achievementLevel; i > 0; i--) {
            Achievement a = getAchievement(achievementId, i);
            if (a != null)
                result += a.getPointsToComplete();
        }

        return totalPoints - result;
    }

    public Achievement getAchievement(int achievementId, int achievementLevel) {
        for (AchievementCategory cat : achievementCategories) {
            Optional<Achievement> first = cat.getAchievements().stream()
                    .filter(ach -> ach.getId() == achievementId)
                    .filter(ach -> ach.getLevel() == achievementLevel)
                    .findFirst();
            if (first.isPresent()) return first.get();
        }
        return null;
    }

    public Collection<Integer> getAchievementIds() {
        return achievementMaxLevels.keySet();
    }

    public int getMaxLevel(int id) {
        return achievementMaxLevels.getOrDefault(id, 0);
    }

    private void load() {
        achievementMaxLevels.clear();
        achievementCategories.clear();
        try {
            Path file = Config.CONFIG.resolve("achievements.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            Document doc = factory.newDocumentBuilder().parse(file.toFile());

            for (Node g = doc.getFirstChild(); g != null; g = g.getNextSibling()) {
                for (Node z = g.getFirstChild(); z != null; z = z.getNextSibling()) {
                    if ("categories".equals(z.getNodeName())) {
                        for (Node i = z.getFirstChild(); i != null; i = i.getNextSibling()) {
                            if ("cat".equalsIgnoreCase(i.getNodeName())) {
                                int categoryId = Integer.valueOf(i.getAttributes().getNamedItem("id").getNodeValue());
                                String categoryName = String.valueOf(i.getAttributes().getNamedItem("name").getNodeValue());
                                String categoryIcon = String.valueOf(i.getAttributes().getNamedItem("icon").getNodeValue());
                                String categoryDesc = String.valueOf(i.getAttributes().getNamedItem("desc").getNodeValue());
                                achievementCategories.add(new AchievementCategory(categoryId, categoryName, categoryIcon, categoryDesc));
                            }
                        }
                    } else if ("achievement".equals(z.getNodeName())) {
                        int achievementId = Integer.valueOf(z.getAttributes().getNamedItem("id").getNodeValue());
                        int achievementCategory = Integer.valueOf(z.getAttributes().getNamedItem("cat").getNodeValue());
                        String desc = String.valueOf(z.getAttributes().getNamedItem("desc").getNodeValue());
                        String fieldType = String.valueOf(z.getAttributes().getNamedItem("type").getNodeValue());
                        int achievementMaxLevel = 0;

                        for (Node i = z.getFirstChild(); i != null; i = i.getNextSibling()) {
                            if ("occupation".equalsIgnoreCase(i.getNodeName())) {
                                int level = Integer.valueOf(i.getAttributes().getNamedItem("id").getNodeValue());
                                long pointsToComplete = Long.parseLong(i.getAttributes().getNamedItem("need").getNodeValue());
                                int fame = Integer.valueOf(i.getAttributes().getNamedItem("fame").getNodeValue());
                                String name = String.valueOf(i.getAttributes().getNamedItem("name").getNodeValue());
                                String icon = String.valueOf(i.getAttributes().getNamedItem("icon").getNodeValue());
                                Achievement achievement = new Achievement(achievementId, level, name, achievementCategory, icon, desc, pointsToComplete, fieldType, fame);

                                if (achievementMaxLevel < level)
                                    achievementMaxLevel = level;

                                for (Node o = i.getFirstChild(); o != null; o = o.getNextSibling()) {
                                    if ("reward".equalsIgnoreCase(o.getNodeName())) {
                                        int Itemid = Integer.valueOf(o.getAttributes().getNamedItem("id").getNodeValue());
                                        long Itemcount = Long.parseLong(o.getAttributes().getNamedItem("count").getNodeValue());
                                        achievement.addReward(Itemid, Itemcount);
                                    }
                                }

                                achievementCategories
                                        .stream()
                                        .filter(ctg -> ctg.getCategoryId() == achievementCategory)
                                        .findAny()
                                        .ifPresent(lastCategory -> lastCategory.getAchievements().add(achievement));
                            }
                        }

                        achievementMaxLevels.put(achievementId, achievementMaxLevel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void log() {
        _log.info("Achievement System: Loaded " + achievementCategories.size() + " achievement categories and " + achievementMaxLevels.size() + " achievements.");

    }
}
