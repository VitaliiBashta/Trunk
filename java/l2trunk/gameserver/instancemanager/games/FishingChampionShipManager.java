package l2trunk.gameserver.instancemanager.games;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public enum FishingChampionShipManager {
    INSTANCE;
    private static final Logger _log = LoggerFactory.getLogger(FishingChampionShipManager.class);

    //    private static final FishingChampionShipManager _instance = new FishingChampionShipManager();
    private final List<String> _playersName = new ArrayList<>();
    private final List<String> _fishLength = new ArrayList<>();
    private final List<String> _winPlayersName = new ArrayList<>();
    private final List<String> _winFishLength = new ArrayList<>();
    private final List<Fisher> _tmpPlayers = new ArrayList<>();
    private final List<Fisher> _winPlayers = new ArrayList<>();
    private long _enddate = 0;
    private double _minFishLength = 0;
    private boolean _needRefresh = true;

    FishingChampionShipManager() {
        restoreData();
        refreshWinResult();
        recalculateMinLength();
        if (_enddate <= System.currentTimeMillis()) {
            _enddate = System.currentTimeMillis();
            new finishChamp().run();
        } else
            ThreadPoolManager.INSTANCE.schedule(new finishChamp(), _enddate - System.currentTimeMillis());
    }

//    public static FishingChampionShipManager INSTANCE() {
//        return _instance;
//    }

    private void setEndOfChamp() {
        Calendar finishtime = Calendar.getInstance();
        finishtime.setTimeInMillis(_enddate);
        finishtime.set(Calendar.MINUTE, 0);
        finishtime.set(Calendar.SECOND, 0);
        finishtime.add(Calendar.DAY_OF_MONTH, 6);
        finishtime.set(Calendar.DAY_OF_WEEK, 3);
        finishtime.set(Calendar.HOUR_OF_DAY, 19);
        _enddate = finishtime.getTimeInMillis();
    }

    private void restoreData() {
        _enddate = ServerVariables.getLong("fishChampionshipEnd", 0);
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT `PlayerName`, `fishLength`, `rewarded` FROM fishing_championship");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                int rewarded = rs.getInt("rewarded");
                if (rewarded == 0) // Текущий участник
                    _tmpPlayers.add(new Fisher(rs.getString("PlayerName"), rs.getDouble("fishLength"), 0));
                if (rewarded > 0) // Победитель прошлой недели
                    _winPlayers.add(new Fisher(rs.getString("PlayerName"), rs.getDouble("fishLength"), rewarded));
            }
        } catch (SQLException e) {
            _log.warn("Exception: can't get fishing championship info: ", e);
        }
    }

    public synchronized void newFish(Player pl, int lureId) {
        if (!Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
            return;
        double p1 = Rnd.get(60, 80);
        if (p1 < 90 && lureId > 8484 && lureId < 8486) {
            int diff = (int) Math.round(90 - p1);
            if (diff > 1)
                p1 += Rnd.get(1, diff);
        }
        double len = Rnd.get(100, 999) / 1000. + p1;
        if (_tmpPlayers.size() < 5) {
            for (Fisher fisher : _tmpPlayers)
                if (fisher.getName().equalsIgnoreCase(pl.getName())) {
                    if (fisher.getLength() < len) {
                        fisher.setLength(len);
                        pl.sendMessage(new CustomMessage("l2trunk.gameserver.instancemanager.games.FishingChampionShipManager.ResultImproveOn"));
                        recalculateMinLength();
                    }
                    return;
                }
            _tmpPlayers.add(new Fisher(pl.getName(), len, 0));
            pl.sendMessage(new CustomMessage("l2trunk.gameserver.instancemanager.games.FishingChampionShipManager.YouInAPrizeList"));
            recalculateMinLength();
        } else if (_minFishLength < len) {
            for (Fisher fisher : _tmpPlayers)
                if (fisher.getName().equalsIgnoreCase(pl.getName())) {
                    if (fisher.getLength() < len) {
                        fisher.setLength(len);
                        pl.sendMessage(new CustomMessage("l2trunk.gameserver.instancemanager.games.FishingChampionShipManager.ResultImproveOn"));
                        recalculateMinLength();
                    }
                    return;
                }
            Fisher minFisher = null;
            double minLen = 99999.;
            for (Fisher fisher : _tmpPlayers)
                if (fisher.getLength() < minLen) {
                    minFisher = fisher;
                    minLen = minFisher.getLength();
                }
            _tmpPlayers.remove(minFisher);
            _tmpPlayers.add(new Fisher(pl.getName(), len, 0));
            pl.sendMessage(new CustomMessage("l2trunk.gameserver.instancemanager.games.FishingChampionShipManager.YouInAPrizeList"));
            recalculateMinLength();
        }
    }

    private void recalculateMinLength() {
        double minLen = 99999.;
        for (Fisher fisher : _tmpPlayers)
            if (fisher.getLength() < minLen)
                minLen = fisher.getLength();
        _minFishLength = minLen;
    }

    private long getTimeRemaining() {
        return (_enddate - System.currentTimeMillis()) / 60000;
    }

    private String getWinnerName(int par) {
        if (_winPlayersName.size() >= par)
            return _winPlayersName.get(par - 1);
        return "—";
    }

    private String getCurrentName(int par) {
        if (_playersName.size() >= par)
            return _playersName.get(par - 1);
        return "—";
    }

    private String getFishLength(int par) {
        if (_winFishLength.size() >= par)
            return _winFishLength.get(par - 1);
        return "0";
    }

    private String getCurrentFishLength(int par) {
        if (_fishLength.size() >= par)
            return _fishLength.get(par - 1);
        return "0";
    }

    public void getReward(Player pl) {
        String filename = "fisherman/championship/getReward.htm";
        NpcHtmlMessage html = new NpcHtmlMessage(pl.objectId());
        html.setFile(filename);
        pl.sendPacket(html);
        for (Fisher fisher : _winPlayers)
            if (fisher._name.equalsIgnoreCase(pl.getName()))
                if (fisher.getRewardType() != 2) {
                    int rewardCnt = 0;
                    for (int x = 0; x < _winPlayersName.size(); x++)
                        if (_winPlayersName.get(x).equalsIgnoreCase(pl.getName()))
                            switch (x) {
                                case 0:
                                    rewardCnt = Config.ALT_FISH_CHAMPIONSHIP_REWARD_1;
                                    break;
                                case 1:
                                    rewardCnt = Config.ALT_FISH_CHAMPIONSHIP_REWARD_2;
                                    break;
                                case 2:
                                    rewardCnt = Config.ALT_FISH_CHAMPIONSHIP_REWARD_3;
                                    break;
                                case 3:
                                    rewardCnt = Config.ALT_FISH_CHAMPIONSHIP_REWARD_4;
                                    break;
                                case 4:
                                    rewardCnt = Config.ALT_FISH_CHAMPIONSHIP_REWARD_5;
                                    break;
                            }
                    fisher.setRewardType(2);
                    if (rewardCnt > 0) {
                        pl.sendPacket(SystemMessage2.obtainItems(Config.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM, rewardCnt, 0));
                        pl.getInventory().addItem(Config.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM, rewardCnt, "Fishing Championship");
                        pl.sendItemList(false);
                    }
                }
    }

    public void showMidResult(Player pl) {
        if (_needRefresh) {
            refreshResult();
            ThreadPoolManager.INSTANCE.schedule(new needRefresh(), 60000);
        }
        NpcHtmlMessage html = new NpcHtmlMessage(pl.objectId());
        String filename = "fisherman/championship/MidResult.htm";
        html.setFile(filename);
        StringBuilder strBuilder = new StringBuilder();
        for (int x = 1; x <= 5; x++) {
            strBuilder.append("<tr><td width=70 align=center>").append(x).append(" Position:</td>");
            strBuilder.append("<td width=110 align=center>").append(getCurrentName(x)).append("</td>");
            strBuilder.append("<td width=80 align=center>").append(getCurrentFishLength(x)).append("</td></tr>");
        }
        html.replace("%TABLE%", strBuilder.toString());
        html.replace("%prizeItem%", ItemHolder.getTemplate(Config.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM).getName());
        html.replace("%prizeFirst%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_1));
        html.replace("%prizeTwo%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_2));
        html.replace("%prizeThree%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_3));
        html.replace("%prizeFour%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_4));
        html.replace("%prizeFive%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_5));
        pl.sendPacket(html);
    }

    public void showChampScreen(Player pl, NpcInstance npc) {
        NpcHtmlMessage html = new NpcHtmlMessage(pl.objectId());
        String filename = "fisherman/championship/champScreen.htm";
        html.setFile(filename);
        StringBuilder strBuilder = new StringBuilder();
        for (int x = 1; x <= 5; x++) {
            strBuilder.append("<tr><td width=70 align=center>").append(x).append(" Position:</td>");
            strBuilder.append("<td width=110 align=center>").append(getWinnerName(x)).append("</td>");
            strBuilder.append("<td width=80 align=center>").append(getFishLength(x)).append("</td></tr>");
        }
        html.replace("%TABLE%", strBuilder.toString());
        html.replace("%prizeItem%", ItemHolder.getTemplate(Config.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM).getName());
        html.replace("%prizeFirst%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_1));
        html.replace("%prizeTwo%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_2));
        html.replace("%prizeThree%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_3));
        html.replace("%prizeFour%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_4));
        html.replace("%prizeFive%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_5));
        html.replace("%refresh%", String.valueOf(getTimeRemaining()));
        html.replace("%objectId%", String.valueOf(npc.objectId()));
        pl.sendPacket(html);
    }

    public void shutdown() {
        ServerVariables.set("fishChampionshipEnd", _enddate);
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement("DELETE FROM fishing_championship");
            statement.execute();
            statement.close();

            for (Fisher fisher : _winPlayers) {
                statement = con.prepareStatement("INSERT INTO fishing_championship(PlayerName,fishLength,rewarded) VALUES (?,?,?)");
                statement.setString(1, fisher.getName());
                statement.setDouble(2, fisher.getLength());
                statement.setInt(3, fisher.getRewardType());
                statement.execute();
                statement.close();
            }
            for (Fisher fisher : _tmpPlayers) {
                statement = con.prepareStatement("INSERT INTO fishing_championship(PlayerName,fishLength,rewarded) VALUES (?,?,?)");
                statement.setString(1, fisher.getName());
                statement.setDouble(2, fisher.getLength());
                statement.setInt(3, 0);
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            _log.warn("Exception: can't update player vitality: ", e);
        }
    }

    private synchronized void refreshResult() {
        _needRefresh = false;
        _playersName.clear();
        _fishLength.clear();
        Fisher fisher1 = null;
        Fisher fisher2 = null;
        for (int x = 0; x <= _tmpPlayers.size() - 1; x++)
            for (int y = 0; y <= _tmpPlayers.size() - 2; y++) {
                fisher1 = _tmpPlayers.get(y);
                fisher2 = _tmpPlayers.get(y + 1);
                if (fisher1.getLength() < fisher2.getLength()) {
                    _tmpPlayers.set(y, fisher2);
                    _tmpPlayers.set(y + 1, fisher1);
                }
            }
        for (int x = 0; x <= _tmpPlayers.size() - 1; x++) {
            _playersName.add(_tmpPlayers.get(x)._name);
            _fishLength.add(String.valueOf(_tmpPlayers.get(x).getLength()));
        }
    }

    private void refreshWinResult() {
        _winPlayersName.clear();
        _winFishLength.clear();
        Fisher fisher1 = null;
        Fisher fisher2 = null;
        for (int x = 0; x <= _winPlayers.size() - 1; x++)
            for (int y = 0; y <= _winPlayers.size() - 2; y++) {
                fisher1 = _winPlayers.get(y);
                fisher2 = _winPlayers.get(y + 1);
                if (fisher1.getLength() < fisher2.getLength()) {
                    _winPlayers.set(y, fisher2);
                    _winPlayers.set(y + 1, fisher1);
                }
            }
        for (int x = 0; x <= _winPlayers.size() - 1; x++) {
            _winPlayersName.add(_winPlayers.get(x)._name);
            _winFishLength.add(String.valueOf(_winPlayers.get(x).getLength()));
        }
    }

    private class finishChamp extends RunnableImpl {
        @Override
        public void runImpl() {
            _winPlayers.clear();
            for (Fisher fisher : _tmpPlayers) {
                fisher.setRewardType(1);
                _winPlayers.add(fisher);
            }
            _tmpPlayers.clear();
            refreshWinResult();
            setEndOfChamp();
            shutdown();
            _log.info("Fishing Championship Manager : start new event period.");
            ThreadPoolManager.INSTANCE.schedule(new finishChamp(), _enddate - System.currentTimeMillis());
        }
    }

    private class needRefresh extends RunnableImpl {
        @Override
        public void runImpl() {
            _needRefresh = true;
        }
    }

    private class Fisher {
        private double _length = 0.;
        private String _name;
        private int _reward = 0;

        Fisher(String name, double length, int rewardType) {
            setName(name);
            setLength(length);
            setRewardType(rewardType);
        }

        String getName() {
            return _name;
        }

        void setName(String value) {
            _name = value;
        }

        int getRewardType() {
            return _reward;
        }

        void setRewardType(int value) {
            _reward = value;
        }

        double getLength() {
            return _length;
        }

        void setLength(double value) {
            _length = value;
        }
    }
}