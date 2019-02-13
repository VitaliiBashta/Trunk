package l2trunk.gameserver.model.actor.instances.player;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class BookMarkList {
    private static final List<ZoneType> FORBIDDEN_ZONES = List.of(
            ZoneType.RESIDENCE,
            ZoneType.ssq_zone,
            ZoneType.battle_zone,
            ZoneType.SIEGE,
            ZoneType.no_restart,
            ZoneType.no_summon);

    private static final Logger LOG = LoggerFactory.getLogger(BookMarkList.class);

    private final Player owner;
    private final List<BookMark> elementData;
    private int capacity;

    public BookMarkList(Player owner, int acapacity) {
        this.owner = owner;
        elementData = new ArrayList<>(acapacity);
        capacity = acapacity;
    }

    private static boolean checkFirstConditions(Player player) {
        if (player.getActiveWeaponFlagAttachment() != null) {
            player.sendPacket(Msg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
            return false;
        }
        if (player.isInOlympiadMode()) {
            player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH);
            return false;
        }
        if (player.getReflection() != ReflectionManager.DEFAULT) {
            player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_IN_AN_INSTANT_ZONE);
            return false;
        }
        if (player.isInDuel()) {
            player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL);
            return false;
        }
        if (player.isInCombat() || player.getPvpFlag() != 0) {
            player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE);
            return false;
        }
        if (player.isOnSiegeField() || player.isInZoneBattle()) {
            player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGE_SCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE);
            return false;
        }
        if (player.isFlying()) {
            player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING);
            return false;
        }
        if (player.isInWater() || player.isInBoat()) {
            player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER);
            return false;
        }

        return true;
    }

    private static boolean checkTeleportConditions(Player player) {
        if (player.isAlikeDead()) {
            player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_DEAD);
            return false;
        }
        if (player.isInStoreMode() || player.isInTrade()) {
            player.sendPacket(Msg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_THE_PRIVATE_SHOPS);
            return false;
        }
        if (player.isInBoat() || player.isParalyzed() || player.isStunned() || player.isSleeping()) {
            player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_IN_A_FLINT_OR_PARALYZED_STATE);
            return false;
        }

        return true;
    }

    private static boolean checkTeleportLocation(Player player, Location loc) {
        return checkTeleportLocation(player, loc.z);
    }

    private static boolean checkTeleportLocation(Player player, int z) {
        if (player == null)
            return false;

        return FORBIDDEN_ZONES.stream()
                .map(player::getZone)
                .peek(zone -> player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA))
                .noneMatch(Objects::nonNull);
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized void setCapacity(int val) {
        capacity = val;
    }

    public void clear() {
        elementData.clear();
    }

    public List<BookMark> getBookMarks() {
        return elementData;
    }

    private synchronized void add(BookMark e) {
        if (elementData.size() >= getCapacity())
            return;
        elementData.add(e);
    }

    public BookMark get(int slot) {
        if (slot < 1 || slot > elementData.size())
            return null;
        return elementData.get(slot - 1);
    }

    public void remove(int slot) {
        if (slot < 1 || slot > elementData.size())
            return;
        elementData.remove(slot - 1);
    }

    public void tryTeleport(int slot) {
        if (!checkFirstConditions(owner) || !checkTeleportConditions(owner))
            return;

        if (slot < 1 || slot > elementData.size())
            return;
        BookMark bookmark = elementData.get(slot - 1);
        if (!checkTeleportLocation(owner, bookmark.loc))
            return;


        if (removeItem(owner, 13016, 1, "BookMarkTeleport") != 1) {
            owner.sendPacket(SystemMsg.YOU_CANNOT_BOOKMARK_THIS_LOCATION_BECAUSE_YOU_DO_NOT_HAVE_A_MY_TELEPORT_FLAG);
            return;
        }

        owner.teleToLocation(bookmark.loc);
    }

    public boolean add(String aname, String aacronym, int aiconId) {
        return owner != null && add(owner.getLoc(), aname, aacronym, aiconId);
    }

    private boolean add(Location loc, String aname, String aacronym, int aiconId) {
        if (!checkFirstConditions(owner) || !checkTeleportLocation(owner, loc))
            return false;

        if (elementData.size() >= getCapacity()) {
            owner.sendPacket(Msg.YOU_HAVE_NO_SPACE_TO_SAVE_THE_TELEPORT_LOCATION);
            return false;
        }

        if (removeItem(owner, 20033, 1, "BookMarkLocationAdd") != 1) {
            owner.sendPacket(Msg.YOU_CANNOT_BOOKMARK_THIS_LOCATION_BECAUSE_YOU_DO_NOT_HAVE_A_MY_TELEPORT_FLAG);
            return false;
        }

        add(new BookMark(loc, aiconId, aname, aacronym));

        return true;
    }

    public void store() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement("DELETE FROM `character_bookmarks` WHERE char_Id=?");
            statement.setInt(1, owner.objectId());
            statement.execute();

            statement = con.prepareStatement("INSERT INTO `character_bookmarks` VALUES(?,?,?,?,?,?,?,?);");
            int slotId = 0;
            for (BookMark bookmark : elementData) {
                statement.setInt(1, owner.objectId());
                statement.setInt(2, ++slotId);
                statement.setString(3, bookmark.getName());
                statement.setString(4, bookmark.getAcronym());
                statement.setInt(5, bookmark.getIcon());
                statement.setInt(6, bookmark.loc.x);
                statement.setInt(7, bookmark.loc.y);
                statement.setInt(8, bookmark.loc.z);
                statement.execute();
            }
        } catch (SQLException e) {
            LOG.error("Error while Inserting BookMarkList! ", e);
        }
    }

    public void restore(Connection con) {
        if (capacity == 0) {
            elementData.clear();
            return;
        }

        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM `character_bookmarks` WHERE `char_Id`=" + owner.objectId() + " ORDER BY `idx` LIMIT " + capacity);
             ResultSet rs = statement.executeQuery()) {
            elementData.clear();
            while (rs.next()) {
                Location loc = new Location(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                add(new BookMark(loc, rs.getInt("icon"), rs.getString("name"), rs.getString("acronym")));
            }
        } catch (SQLException e) {
            LOG.error("Could not restore " + owner + " bookmarks!", e);
        }
    }
}