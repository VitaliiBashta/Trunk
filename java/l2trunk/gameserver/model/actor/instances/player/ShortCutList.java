package l2trunk.gameserver.model.actor.instances.player;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExAutoSoulShot;
import l2trunk.gameserver.network.serverpackets.ShortCutInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ShortCutList {
    private static final Logger _log = LoggerFactory.getLogger(ShortCutList.class);

    private final Player player;
    private final Map<Integer, ShortCut> shortCuts = new ConcurrentHashMap<>();

    public ShortCutList(Player owner) {
        player = owner;
    }

    public Collection<ShortCut> getAllShortCuts() {
        return shortCuts.values();
    }

    public void registerShortCut(ShortCut shortcut) {
        ShortCut oldShortCut = shortCuts.put(shortcut.getSlot() + 12 * shortcut.getPage(), shortcut);
        registerShortCutInDb(shortcut, oldShortCut);
    }

    private synchronized void registerShortCutInDb(ShortCut shortcut, ShortCut oldShortCut) {
        if (oldShortCut != null)
            deleteShortCutFromDb(oldShortCut);
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("REPLACE INTO character_shortcuts SET object_id=?,slot=?,page=?,type=?,shortcut_id=?,level=?,character_type=?,class_index=?")) {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, shortcut.getSlot());
            statement.setInt(3, shortcut.getPage());
            statement.setInt(4, shortcut.getType());
            statement.setInt(5, shortcut.getId());
            statement.setInt(6, shortcut.getLevel());
            statement.setInt(7, shortcut.getCharacterType());
            statement.setInt(8, player.getActiveClassId());
            statement.execute();
        } catch (SQLException e) {
            _log.error("Could not store shortcuts player:" + player + " slot:" + shortcut.getSlot() + " page:" + shortcut.getPage() + " type:" + shortcut.getType() + " id:" + shortcut.getId() + " level:" + shortcut.getLevel() + " CharType:" + shortcut.getCharacterType(), e);
        }
    }

    private void deleteShortCutFromDb(ShortCut shortcut) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND slot=? AND page=? AND class_index=?")) {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, shortcut.getSlot());
            statement.setInt(3, shortcut.getPage());
            statement.setInt(4, player.getActiveClassId());
            statement.execute();
        } catch (SQLException e) {
            _log.error("Could not delete shortcuts:", e);
        }
    }

    /**
     * Удаляет ярлык с пользовательской панели по номеру страницы и слота.
     */
    public void deleteShortCut(int slot, int page) {
        ShortCut old = shortCuts.remove(slot + page * 12);
        if (old == null)
            return;
        deleteShortCutFromDb(old);
        // When you remove from the panel skill, Offe is sent full init labels
        // Handle the removal of subject labels - client side.
        if (old.getType() == ShortCut.TYPE_SKILL) {
            player.sendPacket(new ShortCutInit(player));
            for (int shotId : player.getAutoSoulShot())
                player.sendPacket(new ExAutoSoulShot(shotId, true));
        }
    }

    /**
     * Удаляет ярлык предмета с пользовательской панели.
     */
    public void deleteShortCutByObjectId(int objectId) {
        for (ShortCut shortcut : shortCuts.values())
            if (shortcut != null && shortcut.getType() == ShortCut.TYPE_ITEM && shortcut.getId() == objectId)
                deleteShortCut(shortcut.getSlot(), shortcut.getPage());
    }

    /**
     * Удаляет ярлык скила с пользовательской панели.
     */
    public void deleteShortCutBySkillId(int skillId) {
        for (ShortCut shortcut : shortCuts.values())
            if (shortcut != null && shortcut.getType() == ShortCut.TYPE_SKILL && shortcut.getId() == skillId)
                deleteShortCut(shortcut.getSlot(), shortcut.getPage());
    }

    public void restore() {
        shortCuts.clear();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT character_type, slot, page, type, shortcut_id, level FROM character_shortcuts WHERE object_id=? AND class_index=?")) {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getActiveClassId());
            ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                int slot = rset.getInt("slot");
                int page = rset.getInt("page");
                int type = rset.getInt("type");
                int id = rset.getInt("shortcut_id");
                int level = rset.getInt("level");
                int character_type = rset.getInt("character_type");

                shortCuts.put(slot + page * 12, new ShortCut(slot, page, type, id, level, character_type));
            }
        } catch (SQLException e) {
            _log.error("could not store shortcuts:", e);
        }
    }
}