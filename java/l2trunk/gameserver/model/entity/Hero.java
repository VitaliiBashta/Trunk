package l2trunk.gameserver.model.entity;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.lang.Pair;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.StringHolder;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.database.mysql;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.PcInventory;
import l2trunk.gameserver.model.pledge.Alliance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.tables.ClanTable;
import l2trunk.gameserver.utils.HtmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public enum Hero {
    INSTANCE;
    public static final String COUNT = "count";
    public static final String CLAN_NAME = "clan_name";
    public static final String CLAN_CREST = "clan_crest";
    public static final String ALLY_NAME = "ally_name";
    public static final String ALLY_CREST = "ally_crest";
    public static final String ACTIVE = "active";
    private static final String PLAYED = "played";
    private static final Logger LOG = LoggerFactory.getLogger(Hero.class);
    private static final String GET_HEROES = "SELECT * FROM heroes WHERE played = 1";
    private static final String GET_ALL_HEROES = "SELECT * FROM heroes";
    private static Map<Integer, StatsSet> heroes;
    private static Map<Integer, StatsSet> _completeHeroes;
    private static Map<Integer, List<HeroDiary>> _herodiary;
    private static Map<Integer, String> _heroMessage;

    Hero() {
        init();
    }

    private static void HeroSetClanAndAlly(int charId, StatsSet hero) {
        Entry<Clan, Alliance> e = ClanTable.INSTANCE.getClanAndAllianceByCharId(charId);
        hero.set(CLAN_CREST, e.getKey() == null ? 0 : e.getKey().getCrestId());
        hero.set(CLAN_NAME, e.getKey() == null ? "" : e.getKey().getName());
        hero.set(ALLY_CREST, e.getValue() == null ? 0 : e.getValue().getAllyCrestId());
        hero.set(ALLY_NAME, e.getValue() == null ? "" : e.getValue().getAllyName());
    }

    public static void deleteHero(Player player) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("REPLACE INTO heroes (char_id, count, played, active) VALUES (?,?,?,?)")) {

            for (Integer heroId : heroes.keySet()) {
                int id = player.objectId();
                if (id > 0 && heroId != id)
                    continue;
                StatsSet hero = heroes.get(heroId);
                statement.setInt(1, heroId);
                statement.setInt(2, hero.getInteger(COUNT));
                statement.setInt(3, hero.getInteger(PLAYED));
                statement.setInt(4, 0);
                statement.execute();
                if (_completeHeroes != null && !_completeHeroes.containsKey(heroId)) {
                    _completeHeroes.remove(heroId);
                }
            }
        } catch (SQLException e) {
            LOG.warn("Hero System: Couldnt update Heroes", e);
        }
    }

    public static void addSkills(Player player) {
        player.addSkill(395);
        player.addSkill(396);
        player.addSkill(1374);
        player.addSkill(1375);
        player.addSkill(1376);
    }

    public static void removeSkills(Player player) {
        player.removeSkill(395);
        player.removeSkill(396);
        player.removeSkill(1374);
        player.removeSkill(1375);
        player.removeSkill(1376);
    }

    private void init() {
        heroes = new ConcurrentHashMap<>();
        _completeHeroes = new ConcurrentHashMap<>();
        _herodiary = new ConcurrentHashMap<>();
        _heroMessage = new ConcurrentHashMap<>();

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement(GET_HEROES);
            ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                StatsSet hero = new StatsSet();
                int charId = rset.getInt(Olympiad.CHAR_ID);
                hero.set(Olympiad.CHAR_NAME, Olympiad.getNobleName(charId));
                hero.set(Olympiad.CLASS_ID, Olympiad.getNobleClass(charId));
                hero.set(COUNT, rset.getInt(COUNT));
                hero.set(PLAYED, rset.getInt(PLAYED));
                hero.set(ACTIVE, rset.getInt(ACTIVE));
                HeroSetClanAndAlly(charId, hero);
                loadDiary(charId);
                loadMessage(charId);
                heroes.put(charId, hero);
            }
            statement = con.prepareStatement(GET_ALL_HEROES);
            rset = statement.executeQuery();
            while (rset.next()) {
                StatsSet hero = new StatsSet();
                int charId = rset.getInt(Olympiad.CHAR_ID);
                hero.set(Olympiad.CHAR_NAME, Olympiad.getNobleName(charId));
                hero.set(Olympiad.CLASS_ID, Olympiad.getNobleClass(charId));
                hero.set(COUNT, rset.getInt(COUNT));
                hero.set(PLAYED, rset.getInt(PLAYED));
                hero.set(ACTIVE, rset.getInt(ACTIVE));
                HeroSetClanAndAlly(charId, hero);
                _completeHeroes.put(charId, hero);
            }
        } catch (SQLException e) {
            LOG.warn("Hero System: Couldnt loadFile Heroes", e);
        }

    }

    public Map<Integer, StatsSet> getHeroes() {
        return heroes;
    }

    public synchronized void clearHeroes() {
        mysql.set("UPDATE heroes SET played = 0, active = 0");

        if (!heroes.isEmpty())
            for (StatsSet hero : heroes.values()) {
                if (hero.getInteger(ACTIVE) == 0)
                    continue;

                String name = hero.getString(Olympiad.CHAR_NAME);

                Player player = World.getPlayer(name);

                if (player != null) {
                    PcInventory inventory = player.getInventory();
                    inventory.writeLock();
                    try {
                        for (ItemInstance item : player.getInventory().getItems())
                            if (item.isHeroWeapon())
                                player.getInventory().destroyItem(item, "Clearing Hero Weapon");
                    } finally {
                        inventory.writeUnlock();
                    }

                    player.setHero(false);
                    player.updatePledgeClass();
                    player.broadcastUserInfo(true);
                }
            }

        //Deleting hero weapons from db
        mysql.set("DELETE FROM items WHERE item_id >= 6611 AND item_id <= 6621 OR item_id >= 9388 AND item_id <= 9390");

        heroes.clear();
        _herodiary.clear();
    }

    public synchronized boolean computeNewHeroes(List<StatsSet> newHeroes) {
        if (newHeroes.size() == 0)
            return true;

        Map<Integer, StatsSet> heroes = new ConcurrentHashMap<>();
        boolean error = false;

        for (StatsSet hero : newHeroes) {
            int charId = hero.getInteger(Olympiad.CHAR_ID);

            if (_completeHeroes != null && _completeHeroes.containsKey(charId)) {
                StatsSet oldHero = _completeHeroes.get(charId);
                int count = oldHero.getInteger(COUNT);
                oldHero.set(COUNT, count + 1);
                oldHero.set(PLAYED, 1);
                oldHero.set(ACTIVE, 0);

                heroes.put(charId, oldHero);
            } else {
                StatsSet newHero = new StatsSet();
                newHero.set(Olympiad.CHAR_NAME, hero.getString(Olympiad.CHAR_NAME));
                newHero.set(Olympiad.CLASS_ID, hero.getInteger(Olympiad.CLASS_ID));
                newHero.set(COUNT, 1);
                newHero.set(PLAYED, 1);
                newHero.set(ACTIVE, 0);

                heroes.put(charId, newHero);
            }

            addHeroDiary(charId, HeroDiary.ACTION_HERO_GAINED, 0);
            loadDiary(charId);
        }

        Hero.heroes.putAll(heroes);
        heroes.clear();

        updateHeroes(0);

        return error;
    }

    private void updateHeroes(int id) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("REPLACE INTO heroes (char_id, count, played, active) VALUES (?,?,?,?)")) {

            for (Integer heroId : heroes.keySet()) {
                if (id > 0 && heroId != id)  //if (id > 0 && heroId != id) //Here maybe not normal with intValue
                    continue;
                StatsSet hero = heroes.get(heroId);
                statement.setInt(1, heroId); // statement.setInt(1, heroId);
                statement.setInt(2, hero.getInteger(COUNT));
                statement.setInt(3, hero.getInteger(PLAYED));
                statement.setInt(4, hero.getInteger(ACTIVE));
                statement.execute();
                if (_completeHeroes != null && !_completeHeroes.containsKey(heroId)) {
                    HeroSetClanAndAlly(heroId, hero); //HeroSetClanAndAlly(heroId, hero);
                    _completeHeroes.put(heroId, hero);
                }
            }
        } catch (SQLException e) {
            LOG.error("Hero System: Couldnt update Heroes", e);
        }
    }

    public boolean isHero(int id) {
        if (heroes == null || heroes.isEmpty())
            return false;
        return heroes.containsKey(id) && heroes.get(id).getInteger(ACTIVE) == 1;
    }

    public boolean isInactiveHero(int id) {
        if (heroes == null || heroes.isEmpty())
            return false;
        return heroes.containsKey(id) && heroes.get(id).getInteger(ACTIVE) == 0;
    }

    public void activateHero(Player player) {
        StatsSet hero = heroes.get(player.objectId());
        hero.set(ACTIVE, 1);
        heroes.remove(player.objectId());
        heroes.put(player.objectId(), hero);

        if (player.getBaseClassId() == player.getActiveClassId())
            addSkills(player);

        player.setHero(true);
        player.updatePledgeClass();
        player.broadcastPacket(new SocialAction(player.objectId(), SocialAction.GIVE_HERO));
        if (player.getClan() != null && player.getClan().getLevel() >= 5) {
            player.getClan().incReputation(1000, true, "Hero:activateHero:" + player);
            player.getClan().broadcastToOtherOnlineMembers(new SystemMessage(SystemMessage.CLAN_MEMBER_S1_WAS_NAMED_A_HERO_2S_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addString(player.getName()).addNumber(Math.round(1000 * Config.RATE_CLAN_REP_SCORE)), player);
        }
        player.broadcastUserInfo(true);
        updateHeroes(player.objectId());

        player.getCounters().timesHero++;
    }

    private void loadDiary(int charId) {
        List<HeroDiary> diary = new ArrayList<>();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM  heroes_diary WHERE charId=? ORDER BY time ASC")) {
            statement.setInt(1, charId);
            ResultSet rset = statement.executeQuery();

            while (rset.next()) {
                long time = rset.getLong("time");
                int action = rset.getInt("action");
                int param = rset.getInt("param");

                HeroDiary d = new HeroDiary(action, time, param);
                diary.add(d);
            }

            _herodiary.put(charId, diary);

            if (Config.DEBUG)
                LOG.info("Hero System: Loaded " + diary.size() + " diary entries for Hero(object id: #" + charId + ")");
        } catch (SQLException e) {
            LOG.warn("Hero System: Couldnt loadFile Hero Diary for CharId: " + charId, e);
        }
    }

    public void showHeroDiary(Player activeChar, int heroclass, int charid, int page) {
        final int perpage = 10;

        List<HeroDiary> mainlist = _herodiary.get(charid);

        if (mainlist != null) {
            NpcHtmlMessage html = new NpcHtmlMessage(activeChar, null);
            html.setFile("olympiad/monument_hero_info.htm");
            html.replace("%title%", StringHolder.INSTANCE.getNotNull("hero.diary"));
            html.replace("%heroname%", Olympiad.getNobleName(charid));
            html.replace("%message%", _heroMessage.get(charid));

            List<HeroDiary> list = new ArrayList<>(mainlist);

            Collections.reverse(list);

            boolean color = true;
            final StringBuilder fList = new StringBuilder(500);
            int counter = 0;
            int breakat = 0;
            for (int i = (page - 1) * perpage; i < list.size(); i++) {
                breakat = i;
                HeroDiary diary = list.get(i);
                Pair<String, String> entry = diary.toString(activeChar);

                fList.append("<tr><td>");
                if (color)
                    fList.append("<table width=270 bgcolor=\"131210\">");
                else
                    fList.append("<table width=270>");
                fList.append("<tr><td width=270><font color=\"LEVEL\">").append(entry.getKey()).append("</font></td></tr>");
                fList.append("<tr><td width=270>" + entry.getValue() + "</td></tr>");
                fList.append("<tr><td>&nbsp;</td></tr></table>");
                fList.append("</td></tr>");
                color = !color;
                counter++;
                if (counter >= perpage)
                    break;
            }

            if (breakat < list.size() - 1) {
                html.replace("%buttprev%", HtmlUtils.PREV_BUTTON);
                html.replace("%prev_bypass%", "_diary?class=" + heroclass + "&page=" + (page + 1));
            } else
                html.replace("%buttprev%", "");

            if (page > 1) {
                html.replace("%buttnext%", HtmlUtils.NEXT_BUTTON);
                html.replace("%next_bypass%", "_diary?class=" + heroclass + "&page=" + (page - 1));
            } else
                html.replace("%buttnext%", "");

            html.replace("%list%", fList);

            activeChar.sendPacket(html);
        }
    }

    public void addHeroDiary(int playerId, int id, int param) {
        insertHeroDiary(playerId, id, param);

        List<HeroDiary> list = _herodiary.get(playerId);
        if (list != null)
            list.add(new HeroDiary(id, System.currentTimeMillis(), param));
    }

    private void insertHeroDiary(int charId, int action, int param) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("INSERT INTO heroes_diary (charId, time, action, param) values(?,?,?,?)")) {
            statement.setInt(1, charId);
            statement.setLong(2, System.currentTimeMillis());
            statement.setInt(3, action);
            statement.setInt(4, param);
            statement.execute();
        } catch (SQLException e) {
            LOG.error("SQL exception while saving DiaryData.", e);
        }
    }

    private void loadMessage(int charId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT message FROM heroes WHERE char_id=?")) {
            statement.setInt(1, charId);
            ResultSet rset = statement.executeQuery();
            rset.next();
            _heroMessage.put(charId, rset.getString("message"));
        } catch (SQLException e) {
            LOG.error("Hero System: Couldnt loadFile Hero Message for CharId: " + charId, e);
        }
    }

    public void setHeroMessage(int charId, String message) {
        _heroMessage.put(charId, message);
    }

    private void saveHeroMessage(int charId) {
        if (_heroMessage.get(charId) == null)
            return;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE heroes SET message=? WHERE char_id=?;")) {
            statement.setString(1, _heroMessage.get(charId));
            statement.setInt(2, charId);
            statement.execute();
        } catch (SQLException e) {
            LOG.error("SQL exception while saving HeroMessage.", e);
        }
    }

    public void shutdown() {
        _heroMessage.keySet().forEach(this::saveHeroMessage);
    }

    public int getHeroByClass(int classid) {
        if (!heroes.isEmpty())
            for (Integer heroId : heroes.keySet()) {
                StatsSet hero = heroes.get(heroId);
                if (hero.getInteger(Olympiad.CLASS_ID) == classid)
                    return heroId;
            }
        return 0;
    }

    public Map.Entry<Integer, StatsSet> getHeroStats(int classId) {
        if (!heroes.isEmpty()) {
            for (Map.Entry<Integer, StatsSet> entry : heroes.entrySet()) {
                if (entry.getValue().getInteger(Olympiad.CLASS_ID) == classId)
                    return entry;
            }
        }
        return null;
    }

    public void log() {
        LOG.info("Hero System: Loaded " + heroes.size() + " Heroes.");
        LOG.info("Hero System: Loaded " + _completeHeroes.size() + " all time Heroes.");

    }
}