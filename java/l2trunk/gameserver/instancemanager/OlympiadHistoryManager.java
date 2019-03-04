package l2trunk.gameserver.instancemanager;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.dao.OlympiadHistoryDAO;
import l2trunk.gameserver.data.StringHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.entity.olympiad.OlympiadHistory;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.utils.HtmlUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


public enum OlympiadHistoryManager {
    INSTANCE;
    private final Map<Integer, List<OlympiadHistory>> _historyNew = new HashMap<>();
    private final Map<Integer, List<OlympiadHistory>> _historyOld = new HashMap<>();

    public void init() {
        Map<Boolean, List<OlympiadHistory>> historyList = OlympiadHistoryDAO.getInstance().select();
        for (Map.Entry<Boolean, List<OlympiadHistory>> entry : historyList.entrySet())
            for (OlympiadHistory history : entry.getValue())
                addHistory(entry.getKey(), history);
    }

    public void switchData() {
        _historyOld.clear();

        _historyOld.putAll(_historyNew);

        _historyNew.clear();

        OlympiadHistoryDAO.getInstance().switchData();
    }

    public void saveHistory(OlympiadHistory history) {
        addHistory(false, history);

        OlympiadHistoryDAO.getInstance().insert(history);
    }

    private void addHistory(boolean old, OlympiadHistory history) {
        Map<Integer, List<OlympiadHistory>> map = old ? _historyOld : _historyNew;

        addHistory0(map, history.objectId1, history);
        addHistory0(map, history.objectId2, history);
    }

    private void addHistory0(Map<Integer, List<OlympiadHistory>> map, int objectId, OlympiadHistory history) {
        List<OlympiadHistory> historySet = map.get(objectId);
        if (historySet == null)
            map.put(objectId, historySet = new CopyOnWriteArrayList<>());

        historySet.add(history);
    }

    public void showHistory(Player player, int targetClassId, int page) {
        final int perpage = 15;

        Map.Entry<Integer, StatsSet> entry = Hero.INSTANCE.getHeroStats(targetClassId);
        if (entry == null)
            return;

        List<OlympiadHistory> historyList = _historyOld.get(entry.getKey());
        if (historyList == null)
            historyList = Collections.emptyList();

        NpcHtmlMessage html = new NpcHtmlMessage(player, null);
        html.setFile("olympiad/monument_hero_info.htm");
        html.replace("%title%", StringHolder.INSTANCE.getNotNull("hero.history"));

        int allStatWinner = 0;
        int allStatLoss = 0;
        int allStatTie = 0;
        for (OlympiadHistory h : historyList) {
            if (h.gameStatus == 0)
                allStatTie++;
            else {
                int team = entry.getKey() == h.objectId1 ? 1 : 2;
                if (h.gameStatus == team)
                    allStatWinner++;
                else
                    allStatLoss++;
            }
        }
        html.replace("%wins%", allStatWinner);
        html.replace("%ties%", allStatTie);
        html.replace("%losses%", allStatLoss);

        int min = perpage * (page - 1);
        int max = perpage * page;

        int currentWinner = 0;
        int currentLoss = 0;
        int currentTie = 0;

        final StringBuilder b = new StringBuilder(500);

        for (int i = 0; i < historyList.size(); i++) {
            OlympiadHistory history = historyList.get(i);
            if (history.gameStatus == 0)
                currentTie++;
            else {
                int team = entry.getKey() == history.objectId1 ? 1 : 2;
                if (history.gameStatus == team)
                    currentWinner++;
                else
                    currentLoss++;
            }

            if (i < min)
                continue;

            if (i >= max)
                break;

            b.append("<tr><td>");
            b.append(history.toString(player, entry.getKey(), currentWinner, currentLoss, currentTie));
            b.append("</td></tr");
        }

        if (min > 0) {
            html.replace("%buttprev%", HtmlUtils.PREV_BUTTON);
            html.replace("%prev_bypass%", "_match?class=" + targetClassId + "&page=" + (page - 1));
        } else
            html.replace("%buttprev%", StringUtils.EMPTY);

        if (historyList.size() > max) {
            html.replace("%buttnext%", HtmlUtils.NEXT_BUTTON);
            html.replace("%next_bypass%", "_match?class=" + targetClassId + "&page=" + (page + 1));
        } else
            html.replace("%buttnext%", "");

        html.replace("%list%", b);

        player.sendPacket(html);
    }
}
