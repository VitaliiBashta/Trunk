package l2trunk.gameserver.instancemanager;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.CursedWeapon;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

public enum CursedWeaponsManager {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(CursedWeaponsManager.class);
    private static final int CURSEDWEAPONS_MAINTENANCE_INTERVAL = 60 * 60 * 1000; // 60 min in millisec
    private static final String SQL_DELETE_CW = "DELETE FROM cursed_weapons WHERE item_id = ?";
    private static final String SQL_DELETE_CW_FROM_PLAYER_INVENTORY = "DELETE FROM items WHERE owner_id=? AND item_id=?";
    private static final String SQL_DELETE_ALL_CW_SKILLS = "DELETE FROM character_skills WHERE skill_id=?";
    private static final String SQL_DELETE_CW_FROM_SKILLS1 = "DELETE FROM character_skills WHERE char_obj_id=? AND skill_id=?";
    private final Map<Integer, CursedWeapon> cursedWeaponsMap;
    private ScheduledFuture<?> _removeTask;

    CursedWeaponsManager() {
        cursedWeaponsMap = new HashMap<>();

        if (!Config.ALLOW_CURSED_WEAPONS)
            return;

        load();
        restore();
        checkConditions();

        cancelTask();
        _removeTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new RemoveTask(), CURSEDWEAPONS_MAINTENANCE_INTERVAL, CURSEDWEAPONS_MAINTENANCE_INTERVAL);

    }

    private void load() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);

            Path file = Config.DATAPACK_ROOT.resolve("data/cursed_weapons.xml");
            if (!Files.exists(file))
                return;

            Document doc = factory.newDocumentBuilder().parse(Files.newInputStream(file));

            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
                if ("list".equalsIgnoreCase(n.getNodeName()))
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
                        if ("item".equalsIgnoreCase(d.getNodeName())) {
                            NamedNodeMap attrs = d.getAttributes();
                            int id = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                            int skillId = Integer.parseInt(attrs.getNamedItem("skillId").getNodeValue());
                            String name = "Unknown cursed weapon";
                            if (attrs.getNamedItem("name") != null)
                                name = attrs.getNamedItem("name").getNodeValue();
                            else if (ItemHolder.getTemplate(id) != null)
                                name = ItemHolder.getTemplate(id).getName();

                            if (id == 0)
                                continue;

                            CursedWeapon cw = new CursedWeapon(id, skillId, name);
                            for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
                                if ("dropRate".equalsIgnoreCase(cd.getNodeName()))
                                    cw.setDropRate(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
                                else if ("duration".equalsIgnoreCase(cd.getNodeName())) {
                                    attrs = cd.getAttributes();
                                    cw.setDurationMin(Integer.parseInt(attrs.getNamedItem("min").getNodeValue()));
                                    cw.setDurationMax(Integer.parseInt(attrs.getNamedItem("max").getNodeValue()));
                                } else if ("durationLost".equalsIgnoreCase(cd.getNodeName()))
                                    cw.setDurationLost(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
                                else if ("disapearChance".equalsIgnoreCase(cd.getNodeName()))
                                    cw.setDisapearChance(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
                                else if ("stageKills".equalsIgnoreCase(cd.getNodeName()))
                                    cw.setStageKills(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
                                else if ("transformationId".equalsIgnoreCase(cd.getNodeName()))
                                    cw.setTransformationId(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
                                else if ("transformationTemplateId".equalsIgnoreCase(cd.getNodeName()))
                                    cw.setTransformationTemplateId(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
                                else if ("transformationName".equalsIgnoreCase(cd.getNodeName()))
                                    cw.setTransformationName(cd.getAttributes().getNamedItem("val").getNodeValue());

                            // Store cursed weapon
                            cursedWeaponsMap.put(id, cw);
                        }

//            cursedWeapons = cursedWeaponsMap.values();
        } catch (DOMException | NumberFormatException | ParserConfigurationException | SAXException e) {
            LOG.error("CursedWeaponsManager: Error parsing cursed_weapons file. ", e);
        } catch (IOException e) {
            LOG.error("CursedWeaponsManager: IOException parsing cursed_weapons file. ", e);
        }
    }

    private void restore() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM cursed_weapons");
             ResultSet rset = statement.executeQuery()) {

            while (rset.next()) {
                int itemId = rset.getInt("item_id");
                CursedWeapon cw = cursedWeaponsMap.get(itemId);
                if (cw != null) {
                    cw.setPlayerId(rset.getInt("player_id"));
                    cw.setPlayerKarma(rset.getInt("player_karma"));
                    cw.setPlayerPkKills(rset.getInt("player_pkkills"));
                    cw.setNbKills(rset.getInt("nb_kills"));
                    cw.setLoc(new Location(rset.getInt("x"), rset.getInt("y"), rset.getInt("z")));
                    cw.setEndTime(rset.getLong("end_time") * 1000L);

                    if (!cw.reActivate())
                        endOfLife(cw);
                } else {
                    removeFromDb(itemId);
                    LOG.warn("CursedWeaponsManager: Unknown cursed weapon " + itemId + ", deleted");
                }
            }
        } catch (SQLException e) {
            LOG.warn("CursedWeaponsManager: Could not restore cursed_weapons data: ", e);
        }
    }

    private void checkConditions() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement1 = con.prepareStatement(SQL_DELETE_ALL_CW_SKILLS);
             PreparedStatement statement2 = con.prepareStatement("SELECT owner_id FROM items WHERE item_id=?")) {

            for (CursedWeapon cw : cursedWeaponsMap.values()) {
                int itemId = cw.getItemId();
                // Do an item check to be sure that the cursed weapon and/or skill isn't hold by someone
                int skillId = cw.getSkillId();
                boolean foundedInItems = false;

                // Delete all cursed weapons skills (we don`t care about same skill on multiply weapons, when player back, skill will appears again)
                statement1.setInt(1, skillId);
                statement1.executeUpdate();

                statement2.setInt(1, itemId);
                ResultSet rset = statement2.executeQuery();

                while (rset.next()) {
                    // A player has the cursed weapon in his inventory ...
                    int playerId = rset.getInt("owner_id");

                    if (!foundedInItems) {
                        if (playerId != cw.getPlayerId() || cw.getPlayerId() == 0) {
                            emptyPlayerCursedWeapon(playerId, itemId, cw);
                            LOG.info("CursedWeaponsManager[254]: Player " + playerId + " owns the cursed weapon " +
                                    itemId + " but he shouldn't.");
                        } else
                            foundedInItems = true;
                    } else {
                        emptyPlayerCursedWeapon(playerId, itemId, cw);
                        LOG.info("CursedWeaponsManager[262]: Player " + playerId + " owns the cursed weapon " +
                                itemId + " but he shouldn't.");
                    }
                }

                if (!foundedInItems && cw.getPlayerId() != 0) {
                    removeFromDb(cw.getItemId());

                    LOG.info("CursedWeaponsManager: Unownered weapon, removing from table...");
                }
            }
        } catch (SQLException e) {
            LOG.warn("CursedWeaponsManager: Could not check cursed_weapons data: ", e);
        }
    }

    private void emptyPlayerCursedWeapon(int playerId, int itemId, CursedWeapon cw) {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            // Delete the item
            PreparedStatement statement = con.prepareStatement(SQL_DELETE_CW_FROM_PLAYER_INVENTORY);
            statement.setInt(1, playerId);
            statement.setInt(2, itemId);
            statement.executeUpdate();
            statement = con.prepareStatement("UPDATE characters SET karma=?, pkkills=? WHERE obj_id=?");
            statement.setInt(1, cw.getPlayerKarma());
            statement.setInt(2, cw.getPlayerPkKills());
            statement.setInt(3, playerId);
            if (statement.executeUpdate() != 1)
                LOG.warn("Error while updating karma & pkkills for userId " + cw.getPlayerId());
            // clean up the cursedweapons table.
            removeFromDb(itemId);
        } catch (SQLException e) {
            LOG.error("Error while deleting Player Cursed Weapon! ", e);
        }
    }

    private void removeFromDb(int itemId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SQL_DELETE_CW)) {
            statement.setInt(1, itemId);
            statement.executeUpdate();

            if (getCursedWeapon(itemId) != null)
                getCursedWeapon(itemId).initWeapon();
        } catch (SQLException e) {
            LOG.error("CursedWeaponsManager: Failed to remove data: ", e);
        }
    }

    private void cancelTask() {
        if (_removeTask != null) {
            _removeTask.cancel(false);
            _removeTask = null;
        }
    }

    public void endOfLife(CursedWeapon cw) {
        if (cw.isActivated()) {
            Player player = cw.getOnlineOwner();
            if (player != null) {
                // Remove from player
                LOG.info("CursedWeaponsManager: " + cw.getName() + " being removed online from " + player + ".");

                player.abortAttack(true, true);

                player.setKarma(cw.getPlayerKarma());
                player.setPkKills(cw.getPlayerPkKills());
                player.setCursedWeaponEquippedId(0);
                player.setTransformation(0);
                player.setTransformationName(null);
                player.removeSkill(cw.getSkillId(), false);
                player.getInventory().destroyItemByItemId(cw.getItemId(), 1L, "CursedWeapon");
                player.broadcastCharInfo();
            } else {
                // Remove from Db
                LOG.info("CursedWeaponsManager: " + cw.getName() + " being removed offline.");
                try (Connection con = DatabaseFactory.getInstance().getConnection()) {
                    PreparedStatement statement = con.prepareStatement(SQL_DELETE_CW_FROM_PLAYER_INVENTORY);
                    statement.setInt(1, cw.getPlayerId());
                    statement.setInt(2, cw.getItemId());
                    statement.executeUpdate();

                    // Delete the skill
                    statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND skill_id=?");
                    statement.setInt(1, cw.getPlayerId());
                    statement.setInt(2, cw.getSkillId());
                    statement.executeUpdate();

                    // Restore the karma
                    statement = con.prepareStatement("UPDATE characters SET karma=?, pkkills=? WHERE obj_Id=?");
                    statement.setInt(1, cw.getPlayerKarma());
                    statement.setInt(2, cw.getPlayerPkKills());
                    statement.setInt(3, cw.getPlayerId());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    LOG.warn("CursedWeaponsManager: Could not delete : ", e);
                }
            }
        } else // either this cursed weapon is in the inventory of someone who has another cursed weapon equipped,
            // OR this cursed weapon is on the ground.
            if (cw.getPlayer() != null && cw.getPlayer().getInventory().getItemByItemId(cw.getItemId()) != null) {
                Player player = cw.getPlayer();
                if (!cw.getPlayer().getInventory().destroyItemByItemId(cw.getItemId(), 1, "CursedWeapon"))
                    LOG.info("CursedWeaponsManager[453]: Error! Cursed weapon not found!!!");

                player.sendChanges();
                player.broadcastUserInfo(true);
            }
            // is dropped on the ground
            else if (cw.getItem() != null) {
                cw.getItem().deleteMe();
                cw.getItem().delete();
                LOG.info("CursedWeaponsManager: " + cw.getName() + " item has been removed from World.");
            }

        cw.initWeapon();
        removeFromDb(cw.getItemId());

        announce(new SystemMessage(SystemMessage.S1_HAS_DISAPPEARED_CW).addString(cw.getName()));
    }

    private synchronized void saveData(CursedWeapon cw) {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            // Delete previous datas
            PreparedStatement statement = con.prepareStatement(SQL_DELETE_CW);
            statement.setInt(1, cw.getItemId());
            statement.executeUpdate();
            if (cw.isActive()) {
                statement = con.prepareStatement("REPLACE INTO cursed_weapons (item_id, player_id, player_karma, player_pkkills, nb_kills, x, y, z, end_time) VALUES (?,?,?,?,?,?,?,?,?)");
                statement.setInt(1, cw.getItemId());
                statement.setInt(2, cw.getPlayerId());
                statement.setInt(3, cw.getPlayerKarma());
                statement.setInt(4, cw.getPlayerPkKills());
                statement.setInt(5, cw.getNbKills());
                statement.setInt(6, cw.getLoc().x);
                statement.setInt(7, cw.getLoc().y);
                statement.setInt(8, cw.getLoc().z);
                statement.setLong(9, cw.getEndTime() / 1000);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOG.error("CursedWeapon: Failed to save data: ", e);
        }
    }

    public void saveData() {
        for (CursedWeapon cw : cursedWeaponsMap.values())
            saveData(cw);
    }

    /**
     * вызывается, когда проклятое оружие оказывается в инвентаре игрока
     */
    public void checkPlayer(Player player, ItemInstance item) {
        if (player == null || item == null || player.isInOlympiadMode())
            return;

        CursedWeapon cw = cursedWeaponsMap.get(item.getItemId());
        if (cw == null)
            return;

        if (player.getObjectId() == cw.getPlayerId() || cw.getPlayerId() == 0 || cw.isDropped()) {
            activate(player, item);
            showUsageTime(player, cw);
        } else {
            // wtf? how you get it?
            LOG.warn("CursedWeaponsManager: " + player + " tried to obtain " + item + " in wrong way");
            player.getInventory().destroyItem(item, item.getCount(), "CursedWeapon");
        }
    }

    private void activate(Player player, ItemInstance item) {
        if (player == null || player.isInOlympiadMode())
            return;
        CursedWeapon cw = cursedWeaponsMap.get(item.getItemId());
        if (cw == null)
            return;

        if (player.isCursedWeaponEquipped()) // cannot own 2 cursed swords
        {
            if (player.getCursedWeaponEquippedId() != item.getItemId()) {
                CursedWeapon cw2 = cursedWeaponsMap.get(player.getCursedWeaponEquippedId());
                cw2.setNbKills(cw2.getStageKills() - 1);
                cw2.increaseKills();
            }

            // erase the newly obtained cursed weapon
            endOfLife(cw);
            player.getInventory().destroyItem(item, 1, "CursedWeapon");
        } else if (cw.getTimeLeft() > 0) {
            cw.activate(player, item);
            announce(new SystemMessage(SystemMessage.THE_OWNER_OF_S2_HAS_APPEARED_IN_THE_S1_REGION).addZoneName(player.getLoc()).addString(cw.getName()));
            player.sendChanges();
            player.broadcastUserInfo(true);
            saveData(cw);
        } else {
            endOfLife(cw);
            player.getInventory().destroyItem(item, 1, "CursedWeapon");
        }
    }

    public void doLogout(Player player) {
        cursedWeaponsMap.values().stream()
                .filter(cw -> player.getInventory().getItemByItemId(cw.getItemId()) != null)
                .forEach(cw -> {
                    cw.setPlayer(null);
                    cw.setItem(null);
                });
    }

    /**
     * drop from L2NpcInstance killed by L2Player
     */
    public void dropAttackable(NpcInstance attackable, Player killer) {
        if (killer.isInOlympiadMode() || killer.isCursedWeaponEquipped() || cursedWeaponsMap.size() == 0 || killer.getReflection() != ReflectionManager.DEFAULT)
            return;

        synchronized (cursedWeaponsMap.values()) {
            List<CursedWeapon> cursedWeapons = new ArrayList<>();
            for (CursedWeapon cw : this.cursedWeaponsMap.values()) {
                if (cw.isActive())
                    continue;
                cursedWeapons.add(cw);
            }

            if (cursedWeapons.size() > 0) {
                CursedWeapon cw = Rnd.get(cursedWeapons);
                if (Rnd.get(100000000) <= cw.getDropRate())
                    cw.create(attackable, killer);
            }
        }
    }

    /**
     * Выпадение оружия из владельца, или исчезновение с определенной вероятностью.
     * Вызывается при смерти игрока.
     */
    public void dropPlayer(Player player) {
        CursedWeapon cw = cursedWeaponsMap.get(player.getCursedWeaponEquippedId());
        if (cw == null)
            return;

        if (cw.dropIt(player)) {
            saveData(cw);
            announce(new SystemMessage(SystemMessage.S2_WAS_DROPPED_IN_THE_S1_REGION).addZoneName(player.getLoc()).addItemName(cw.getItemId()));
        } else
            endOfLife(cw);
    }

    public void increaseKills(int itemId) {
        CursedWeapon cw = cursedWeaponsMap.get(itemId);
        if (cw != null) {
            cw.increaseKills();
            saveData(cw);
        }
    }

    public int getLevel(int itemId) {
        CursedWeapon cw = cursedWeaponsMap.get(itemId);
        return cw != null ? cw.getLevel() : 0;
    }

    private void announce(SystemMessage sm) {
        GameObjectsStorage.getAllPlayersStream().forEach(player -> player.sendPacket(sm));
    }

    public void showUsageTime(Player player, int itemId) {
        CursedWeapon cw = cursedWeaponsMap.get(itemId);
        if (cw != null)
            showUsageTime(player, cw);
    }

    private void showUsageTime(Player player, CursedWeapon cw) {
        SystemMessage sm = new SystemMessage(SystemMessage.S2_MINUTE_OF_USAGE_TIME_ARE_LEFT_FOR_S1);
        sm.addString(cw.getName());
        sm.addNumber(cw.getTimeLeft() / 60000);
        player.sendPacket(sm);
    }

    public boolean isCursed(int itemId) {
        return cursedWeaponsMap.containsKey(itemId);
    }

    public Collection<CursedWeapon> getCursedWeapons() {
        return cursedWeaponsMap.values();
    }

    public Set<Integer> getCursedWeaponsIds() {
        return cursedWeaponsMap.keySet();
    }

    public CursedWeapon getCursedWeapon(int itemId) {
        return cursedWeaponsMap.get(itemId);
    }

    public void log() {
        LOG.info("Loaded " + this.cursedWeaponsMap.size() + " cursed weapons");
    }

    private class RemoveTask extends RunnableImpl {
        @Override
        public void runImpl() {
            cursedWeaponsMap.values().stream()
                    .filter(CursedWeapon::isActive)
                    .filter(cw -> cw.getTimeLeft() <= 0)
                    .forEach(CursedWeaponsManager.this::endOfLife);
        }
    }
}