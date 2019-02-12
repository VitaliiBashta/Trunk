package Elemental.datatables;

import Elemental.managers.OfflineBufferManager;
import Elemental.managers.OfflineBufferManager.BufferData;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.stream.Collectors;

public enum OfflineBuffersTable {
    INSTANCE;
    private static final Logger _log = LoggerFactory.getLogger(OfflineBuffersTable.class);

    public void restoreOfflineBuffers() {
        _log.info(getClass().getSimpleName() + ": Loading offline buffers...");

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement st = con.prepareStatement("SELECT * FROM character_offline_buffers WHERE charId > 0");
             ResultSet rs = st.executeQuery()) {
            int nBuffers = 0;

            while (rs.next()) {
                Player player = null;

                try {
                    // Restore character and set it in offline mode
                    player = Player.restore(rs.getInt("charId"));
                    if (player == null)
                        continue;

                    player.setIsOnline(true);
                    player.updateOnlineStatus();

                    player.spawnMe();

                    if (player.getClan() != null && player.getClan().getAnyMember(player.objectId()) != null)
                        player.getClan().getAnyMember(player.objectId()).setPlayerInstance(player, false);

                    // Create the buffer data
                    final BufferData buffer = new BufferData(player, rs.getString("title"), rs.getInt("price"));

                    // Get all the buffs from the db
                    try (PreparedStatement stm_items = con.prepareStatement("SELECT * FROM character_offline_buffer_buffs WHERE charId = ?")) {
                        stm_items.setInt(1, player.objectId());
                        try (ResultSet skills = stm_items.executeQuery()) {
                            if (skills.next()) {
                                final String[] skillIds = skills.getString("skillIds").split(",");
                                for (String skillId : skillIds) {
                                    final Skill skill = player.getKnownSkill(Integer.parseInt(skillId));
                                    if (skill == null)
                                        continue;

                                    buffer.buffs.put(skill.id, skill);
                                }
                            }
                        }
                    }

                    // Add the buffer data to the manager
                    OfflineBufferManager.INSTANCE.getBuffStores().put(player.objectId(), buffer);

                    // Sit the player, put it on store and and change the colors and titles
                    player.sitDown(null);

                    player.setVisibleTitleColor(Config.BUFF_STORE_TITLE_COLOR);
                    player.setVisibleTitle(buffer.saleTitle);
                    player.setVisibleNameColor(Config.BUFF_STORE_OFFLINE_NAME_COLOR);

                    player.setPrivateStoreType(Player.STORE_PRIVATE_BUFF);

                    player.broadcastUserInfo(true);
                    nBuffers++;

                } catch (Exception e) {
                    _log.warn(getClass().getSimpleName() + ": Error loading buffer: " + player, e);
                    if (player != null) {
                        player.deleteMe();
                    }
                }
            }

            _log.info(getClass().getSimpleName() + ": Loaded: " + nBuffers + " offline buffer(s)");
        } catch (Exception e) {
            _log.warn(getClass().getSimpleName() + ": Error while loading offline buffer: ", e);
        }
    }

    public synchronized void onLogin(Player trader) {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            // Remove the buffer from the manager
            OfflineBufferManager.INSTANCE.getBuffStores().remove(trader.objectId());

            // Borramos el buff store
            try (PreparedStatement st = con.prepareStatement("DELETE FROM character_offline_buffers WHERE charId=?")) {
                st.setInt(1, trader.objectId());
                st.executeUpdate();
            }

            // Borramos tambien sus buffs
            try (PreparedStatement st = con.prepareStatement("DELETE FROM character_offline_buffer_buffs WHERE charId=?")) {
                st.setInt(1, trader.objectId());
                st.executeUpdate();
            }
        } catch (Exception e) {
            _log.warn(getClass().getSimpleName() + ": Error while removing offline buffer: " + e, e);
        }
    }

    public synchronized void onLogout(Player trader) {
        final BufferData buffer = OfflineBufferManager.INSTANCE.getBuffStores().get(trader.objectId());
        if (buffer == null)
            return;

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            // Guardamos primero el offline buffer
            try (PreparedStatement st = con.prepareStatement("REPLACE INTO character_offline_buffers VALUES (?,?,?)")) {
                st.setInt(1, trader.objectId());
                st.setInt(2, buffer.buffPrice);
                st.setString(3, buffer.saleTitle);
                st.executeUpdate();
            }

            try (PreparedStatement st = con.prepareStatement("REPLACE INTO character_offline_buffer_buffs VALUES (?,?)")) {
                st.setInt(1, trader.objectId());
                st.setString(2, joinAllSkillsToString(buffer.buffs.values()));
                st.executeUpdate();
            }
        } catch (SQLException e) {
            _log.warn(getClass().getSimpleName() + ": Error while saving offline buffer: " + e, e);
        }
    }

    private String joinAllSkillsToString(Collection<Skill> skills) {
        if (skills.isEmpty())
            return "";

        return skills.stream()
                .map(s -> s.id + "")
                .collect(Collectors.joining(","));
    }
}
