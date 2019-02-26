package l2trunk.scripts.services.community;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.ImagesCache;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.instancemanager.RaidBossSpawnManager;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.RadarControl;
import l2trunk.gameserver.network.serverpackets.ShowBoard;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.taskmanager.AutoImageSenderManager;
import l2trunk.gameserver.templates.npc.MinionData;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Util;
import l2trunk.scripts.actions.RewardListInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public final class CommunityBosses implements ScriptFile, ICommunityBoardHandler {
    private static final Logger _log = LoggerFactory.getLogger(CommunityBosses.class);

    private static final int BOSSES_PER_PAGE = 10;

    /**
     * Showing list of bosses in Community Board with their Name, Level, Status and Show Details button
     *
     * @param player guy that will receive list
     * @param sort   index of the sorting type
     * @param page   number of the page(Starting from 0)
     * @param search word in Name of the boss
     */
    private static void sendBossListPage(Player player, SortType sort, int page, String search) {
        String html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "bbs_boss_list.htm", player);

        Map<Integer, StatsSet> allBosses = getSearchedBosses(sort, search);
        Map<Integer, StatsSet> bossesToShow = getBossesToShow(allBosses, page);
        boolean isThereNextPage = allBosses.size() > bossesToShow.size();

        html = getBossListReplacements(html, page, bossesToShow, isThereNextPage);

        html = getNormalReplacements(html, page, sort, search, -1);
        ShowBoard.separateAndSend(html, player);
    }

    /**
     * Replacing %x% words in bbs_bbslink_list.htm file
     *
     * @param html      existing file
     * @param page      number of the page(Starting from 0)
     * @param allBosses Map<BossId, BossStatsSet> of bosses that will be shown
     * @param nextPage  Is the next page?
     * @return ready HTML
     */
    private static String getBossListReplacements(String html, int page, Map<Integer, StatsSet> allBosses, boolean nextPage) {
        String newHtml = html;

        int i = 0;

        for (Entry<Integer, StatsSet> entry : allBosses.entrySet()) {
            StatsSet boss = entry.getValue();
            NpcTemplate temp = NpcHolder.getTemplate(entry.getKey());

            boolean isAlive = isBossAlive(boss);

            newHtml = newHtml.replace("<?name_" + i + "?>", temp.name());
            newHtml = newHtml.replace("<?level_" + i + "?>", String.valueOf(temp.level));
            newHtml = newHtml.replace("<?status_" + i + "?>", isAlive ? "Alive" : getRespawnTime(boss));
            newHtml = newHtml.replace("<?status_color_" + i + "?>", getTextColor(isAlive));
            newHtml = newHtml.replace("<?bp_" + i + "?>", "<button value=\"show\" action=\"bypass _bbsboss_<?sort?>_" + page + "_ <?search?> _" + entry.getKey() + "\" width=40 height=12 back=\"L2UI_CT1.ListCTRL_DF_Title_Down\" fore=\"L2UI_CT1.ListCTRL_DF_Title\">");
            i++;
        }

        for (int j = i; j < BOSSES_PER_PAGE; j++) {
            newHtml = newHtml.replace("<?name_" + j + "?>", "...");
            newHtml = newHtml.replace("<?level_" + j + "?>", "...");
            newHtml = newHtml.replace("<?status_" + j + "?>", "...");
            newHtml = newHtml.replace("<?status_color_" + j + "?>", "FFFFFF");
            newHtml = newHtml.replace("<?bp_" + j + "?>", "...");

        }

        newHtml = newHtml.replace("<?previous?>", page > 0 ? "<button action=\"bypass _bbsbosslist_<?sort?>_" + (page - 1) + "_<?search?>\" width=16 height=16 back=\"L2UI_CH3.shortcut_prev_down\" fore=\"L2UI_CH3.shortcut_prev\">" : "<br>");
        newHtml = newHtml.replace("<?next?>", nextPage && i == BOSSES_PER_PAGE ? "<button action=\"bypass _bbsbosslist_<?sort?>_" + (page + 1) + "_<?search?>\" width=16 height=16 back=\"L2UI_CH3.shortcut_next_down\" fore=\"L2UI_CH3.shortcut_next\">" : "<br>");
        newHtml = newHtml.replace("<?pages?>", String.valueOf(page + 1));

        return newHtml;
    }

    /**
     * Getting all bosses to show(checking only page)
     *
     * @param page number of the page(Starting from 0)
     * @return Bosses
     */
    private static Map<Integer, StatsSet> getBossesToShow(Map<Integer, StatsSet> allBosses, int page) {
        Map<Integer, StatsSet> bossesToShow = new LinkedHashMap<>();
        int i = 0;
        for (Entry<Integer, StatsSet> entry : allBosses.entrySet()) {
            if (i < page * BOSSES_PER_PAGE) {
                i++;
            } else {
                StatsSet boss = entry.getValue();
                NpcTemplate temp = NpcHolder.getTemplate(entry.getKey());
                if (boss != null && temp != null) {
                    i++;
                    bossesToShow.put(entry.getKey(), entry.getValue());
                    if (i > (page * BOSSES_PER_PAGE + BOSSES_PER_PAGE - 1)) {
                        return bossesToShow;
                    }
                }
            }
        }
        return bossesToShow;
    }

    /**
     * Showing detailed info about Boss in Community Board. Including name, occupation, status, stats, image
     *
     * @param player guy that will receive details
     * @param sort   index of the sorting type
     * @param page   number of the page(Starting from 0)
     * @param search word in Name of the boss
     * @param bossId Id of the boss to show
     */
    private static void sendBossDetails(Player player, SortType sort, int page, CharSequence search, int bossId) {
        String html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "bbs_boss_details.htm", player);
        StatsSet bossSet = RaidBossSpawnManager.INSTANCE.getAllBosses().get(bossId);

        if (bossSet == null) {
            ShowBoard.separateAndSend(html, player);
            return;
        }

        NpcTemplate bossTemplate = NpcHolder.getTemplate(bossId);
        NpcInstance bossInstance = getAliveBoss(bossId);

        html = getDetailedBossReplacements(html, bossSet, bossTemplate, bossInstance);
        html = getNormalReplacements(html, page, sort, search, bossId);

        if (!AutoImageSenderManager.isImageAutoSendable(bossId))
            ImagesCache.sendImageToPlayer(player, bossId);

        ShowBoard.separateAndSend(html, player);
    }

    /**
     * Managing buttons that were clicking in Boss Details page
     *
     * @param player      that clicked button
     * @param buttonIndex 1: Showing Location of the boss. 2: Showing Drops
     * @param bossId      Id of the boss that getPlayer was looking into
     */
    private static void manageButtons(Player player, int buttonIndex, int bossId) {
        switch (buttonIndex) {
            case 1://Show Location
                RaidBossSpawnManager.showBossLocation(player, bossId);
                break;
            case 2://Show Drops
                if (Config.ALLOW_DROP_CALCULATOR)
                    RewardListInfo.showInfo(player, NpcHolder.getTemplate(bossId), true, false, 1.0);
                break;
            case 3://Go to Boss
                if (!player.isInZonePeace() || Olympiad.isRegistered(player)) {
                    player.sendMessage("You can do it only in safe zone!");
                    return;
                }
                NpcInstance aliveInstance = getAliveBoss(bossId);
                if (aliveInstance != null)
                    player.teleToLocation(aliveInstance.getLoc());
                else
                    player.sendMessage("Boss isn't alive!");
            case 4://Show Location
                player.sendPacket(new RadarControl(2, 2));
        }
    }

    /**
     * Replacing all %a% words by real Values in Detailed Boss Page
     *
     * @param html         current Html
     * @param bossSet      StatsSet of the boss
     * @param bossTemplate NpcTemplate of the boss
     * @param bossInstance any Instance of the boss(can be null)
     * @return filled HTML
     */
    private static String getDetailedBossReplacements(String html, StatsSet bossSet, NpcTemplate bossTemplate, NpcInstance bossInstance) {
        String newHtml = html;

        boolean isAlive = isBossAlive(bossSet);

        newHtml = newHtml.replace("<?name?>", bossTemplate.name());
        newHtml = newHtml.replace("<?occupation?>", String.valueOf(bossTemplate.level));
        newHtml = newHtml.replace("<?status?>", isAlive ? "Alive" : getRespawnTime(bossSet));
        newHtml = newHtml.replace("<?status_color?>", getTextColor(isAlive));
        newHtml = newHtml.replace("<?minions?>", String.valueOf(getMinionsCount(bossTemplate)));

        newHtml = newHtml.replace("<?currentHp?>", Util.formatAdena((bossInstance != null ? (int) bossInstance.getCurrentHp() : 0)));
        newHtml = newHtml.replace("<?maxHp?>", Util.formatAdena((int) bossTemplate.baseHpMax));
        newHtml = newHtml.replace("<?minions?>", String.valueOf(getMinionsCount(bossTemplate)));

        return newHtml;
    }

    /**
     * Replacing page, sorts, bossId, search
     *
     * @param html   to fill
     * @param page   number
     * @param sort   type
     * @param search word
     * @param bossId If of the boss, set -1 if doesn't matter
     * @return new Html page
     */
    private static String getNormalReplacements(String html, int page, SortType sort, CharSequence search, int bossId) {
        String newHtml = html;
        newHtml = newHtml.replace("<?page?>", String.valueOf(page));
        newHtml = newHtml.replace("<?sort?>", String.valueOf(sort.index));
        newHtml = newHtml.replace("<?bossId?>", String.valueOf(bossId));
        newHtml = newHtml.replace("<?search?>", search);

        for (int i = 1; i <= 6; i++) {
            if (Math.abs(sort.index) == i)
                newHtml = newHtml.replace("<?sort" + i + "?>", String.valueOf(-sort.index));
            else
                newHtml = newHtml.replace("<?sort" + i + "?>", String.valueOf(i));
        }

        return newHtml;
    }

    private static boolean isBossAlive(StatsSet set) {
        return (long) set.getInteger("respawn_delay", 0) < System.currentTimeMillis() / TimeUnit.SECONDS.toMillis(1L);
    }

    private static String getRespawnTime(StatsSet set) {
        if (set.getInteger("respawn_delay", 0) < System.currentTimeMillis() / TimeUnit.SECONDS.toMillis(1L))
            return "isAlive";

        long delay = set.getInteger("respawn_delay", 0) - (System.currentTimeMillis() / TimeUnit.SECONDS.toMillis(1L));

        //System.out.println(delay);
        int hours = (int) (delay / 60 / 60);
        int mins = (int) ((delay - (hours * 60 * 60)) / 60);
        int secs = (int) ((delay - ((hours * 60 * 60) + (mins * 60))));

        String Strhours = hours < 10 ? "0" + hours : "" + hours;
        String Strmins = mins < 10 ? "0" + mins : "" + mins;
        String Strsecs = secs < 10 ? "0" + secs : "" + secs;

        return "<font color=\"b02e31\">" + Strhours + ":" + Strmins + ":" + Strsecs + "</font>";
    }

    private static NpcInstance getAliveBoss(int bossId) {
        return GameObjectsStorage.getAllByNpcId(bossId, true, true)
                .findFirst().orElse(null);
    }

    private static int getMinionsCount(NpcTemplate template) {
        return template.getMinionData().stream()
                .mapToInt(MinionData::getAmount)
                .sum();
    }

    private static String getTextColor(boolean alive) {
        if (alive)
            return "259a30";//"327b39";
        else
            return "b02e31";//"8f3d3f";
    }

    /**
     * Getting List of Bosses that getPlayer is looking for(including sort and search)
     *
     * @param sort   Type of sorting he want to use
     * @param search word that he is looking for
     * @return Map of Bosses
     */
    private static Map<Integer, StatsSet> getSearchedBosses(SortType sort, String search) {
        Map<Integer, StatsSet> result = getBossesMapBySearch(search);

//        for (int id : BOSSES_TO_NOT_SHOW)
//            result.remove(id);

        result = sortResults(result, sort);

        return result;
    }

    /**
     * Getting List of Bosses that getPlayer is looking for(including search)
     *
     * @param search String that boss Name needs to contains(can be Empty)
     * @return MapMap of Bosses
     */
    private static Map<Integer, StatsSet> getBossesMapBySearch(String search) {
        Map<Integer, StatsSet> finalResult = new HashMap<>();
        if (search.isEmpty()) {
            finalResult = RaidBossSpawnManager.INSTANCE.getAllBosses();
        } else {
            for (Entry<Integer, StatsSet> entry : RaidBossSpawnManager.INSTANCE.getAllBosses().entrySet()) {
                NpcTemplate temp = NpcHolder.getTemplate(entry.getKey());
                if (StringUtils.containsIgnoreCase(temp.name(), search))
                    finalResult.put(entry.getKey(), entry.getValue());
            }
        }
        return finalResult;
    }

    /**
     * Sorting results by sort type
     *
     * @param result map to sort
     * @param sort   type
     * @return sorted Map
     */
    private static Map<Integer, StatsSet> sortResults(Map<Integer, StatsSet> result, SortType sort) {
        ValueComparator bvc = new ValueComparator(result, sort);
        Map<Integer, StatsSet> sortedMap = new TreeMap<>(bvc);
        sortedMap.putAll(result);
        return sortedMap;
    }

    /**
     * Getting SortType by index
     *
     * @param i index
     * @return SortType
     */
    private static SortType getSortByIndex(int i) {
        for (SortType type : SortType.values())
            if (type.index == i)
                return type;
        return SortType.NAME_ASC;
    }

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoard: Bosses loaded.");
            CommunityBoardManager.registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED)
            CommunityBoardManager.removeHandler(this);
    }

    @Override
    public List<String> getBypassCommands() {
        return List.of("_bbsmemo", "_bbsbosslist", "_bbsboss");
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {
        StringTokenizer st = new StringTokenizer(bypass, "_");
        String cmd = st.nextToken();
        player.setSessionVar("add_fav", null);

        if ("bbsmemo".equals(cmd) || "bbsbosslist".equals(cmd)) {//_bbsbosslist_sort_page_search
            int sort = Integer.parseInt(st.hasMoreTokens() ? st.nextToken() : "1");
            int page = Integer.parseInt(st.hasMoreTokens() ? st.nextToken() : "0");
            String search = st.hasMoreTokens() ? st.nextToken().trim() : "";

            sendBossListPage(player, getSortByIndex(sort), page, search);
        } else if ("bbsboss".equals(cmd)) {//_bbsboss_sort_page_search_rbId_btn
            int sort = Integer.parseInt(st.hasMoreTokens() ? st.nextToken() : "3");
            int page = Integer.parseInt(st.hasMoreTokens() ? st.nextToken() : "0");
            String search = st.hasMoreTokens() ? st.nextToken().trim() : "";
            int bossId = Integer.parseInt(st.hasMoreTokens() ? st.nextToken() : "25044");
            int buttonClick = Integer.parseInt(st.hasMoreTokens() ? st.nextToken() : "0");

            manageButtons(player, buttonClick, bossId);

            sendBossDetails(player, getSortByIndex(sort), page, search, bossId);
        }
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }

    private enum SortType {
        NAME_ASC(1),
        NAME_DESC(-1),
        LEVEL_ASC(2),
        LEVEL_DESC(-2),
        STATUS_ASC(3),
        STATUS_DESC(-3);

        final int index;

        SortType(int index) {
            this.index = index;
        }
    }

    /**
     * Comparator of Bosses
     */
    private static class ValueComparator implements Comparator<Integer>, Serializable {
        private final Map<Integer, StatsSet> base;
        private final SortType sortType;

        private ValueComparator(Map<Integer, StatsSet> base, SortType sortType) {
            this.base = base;
            this.sortType = sortType;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            int sortResult = sortById(o1, o2, sortType);
            if (sortResult == 0 && !o1.equals(o2) && Math.abs(sortType.index) != 1)
                sortResult = sortById(o1, o2, SortType.NAME_ASC);
            return sortResult;
        }

        /**
         * Comparing a and buffPrice but sorting
         *
         * @param a       first variable
         * @param b       second variable
         * @param sorting type of sorting
         * @return result of comparing
         */
        private int sortById(Integer a, Integer b, SortType sorting) {
            NpcTemplate temp1 = NpcHolder.getTemplate(a);
            NpcTemplate temp2 = NpcHolder.getTemplate(b);
            StatsSet set1 = base.get(a);
            StatsSet set2 = base.get(b);
            switch (sorting) {
                case NAME_ASC:
                    return temp1.name.compareTo(temp2.name);
                case NAME_DESC:
                    return temp2.name.compareTo(temp1.name);
                case LEVEL_ASC:
                    return Integer.compare(temp1.level, temp2.level);
                case LEVEL_DESC:
                    return Integer.compare(temp2.level, temp1.level);
                case STATUS_ASC:
                    return Integer.compare(set1.getInteger("respawn_delay", 0), set2.getInteger("respawn_delay", 0));
                case STATUS_DESC:
                    return Integer.compare(set2.getInteger("respawn_delay", 0), set1.getInteger("respawn_delay", 0));
            }
            return 0;
        }
    }
}