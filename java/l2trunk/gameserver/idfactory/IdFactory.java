package l2trunk.gameserver.idfactory;

import l2trunk.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class IdFactory {
    static final int FIRST_OID = 0x10000000;
    private static final Map<String,String> EXTRACT_OBJ_ID_TABLES = Map.of(
            "characters", "obj_id",
            "items", "object_id",
            "clan_data", "clan_id",
            "ally_data", "ally_id",
            "pets", "objId",
            "couples", "id");
    private static final int LAST_OID = 0x7FFFFFFF;
    static final int FREE_OBJECT_ID_SIZE = LAST_OID - FIRST_OID;
    private static final Logger LOG = LoggerFactory.getLogger(IdFactory.class);
    boolean initialized;

    IdFactory() {
        resetOnlineStatus();
        cleanUpDB();
    }

    public static IdFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private void resetOnlineStatus() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement st = con.prepareStatement("UPDATE characters SET online = 0")) {
            st.executeUpdate("UPDATE characters SET online = 0");
            LOG.info("IdFactory: Clear characters online status.");
        } catch (SQLException e) {
            LOG.error("Error while reseting Online Status! ", e);
        }
    }

    private void cleanUpDB() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement st = con.createStatement()) {
            long cleanupStart = System.currentTimeMillis();
            int cleanCount = 0;


            cleanCount += st.executeUpdate("DELETE FROM account_bonus WHERE account_bonus.account NOT IN (SELECT account_name FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM account_variables WHERE account_variables.account_name NOT IN (SELECT account_name FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM bbs_memo WHERE bbs_memo.account_name NOT IN (SELECT account_name FROM characters);");

            cleanCount += st.executeUpdate("DELETE FROM character_blocklist WHERE character_blocklist.obj_Id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_bookmarks WHERE character_bookmarks.char_Id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_effects_save WHERE character_effects_save.object_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_friends WHERE character_friends.char_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_group_reuse WHERE character_group_reuse.object_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_hennas WHERE character_hennas.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_instances WHERE character_instances.obj_Id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_macroses WHERE character_macroses.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_minigame_score WHERE character_minigame_score.object_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_post_friends WHERE character_post_friends.object_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_premium_items WHERE character_premium_items.charId NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_quests WHERE character_quests.char_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_recipebook WHERE character_recipebook.char_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_shortcuts WHERE character_shortcuts.object_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_skills WHERE character_skills.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_skills_save WHERE character_skills_save.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_subclasses WHERE character_subclasses.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_variables WHERE character_variables.obj_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM siege_players WHERE siege_players.object_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM seven_signs WHERE seven_signs.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM raidboss_points WHERE raidboss_points.owner_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM olympiad_nobles WHERE olympiad_nobles.char_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM bans WHERE bans.obj_Id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM bbs_favorites WHERE bbs_favorites.object_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM bbs_mail WHERE bbs_mail.to_object_id NOT IN (SELECT obj_Id FROM characters);");

            cleanCount += st.executeUpdate("DELETE FROM couples WHERE couples.player1Id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM couples WHERE couples.player2Id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM cursed_weapons WHERE cursed_weapons.player_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM dominion_rewards WHERE dominion_rewards.object_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM event_data WHERE event_data.charId NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM heroes WHERE heroes.char_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM heroes_diary WHERE heroes_diary.charId NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM item_auction_bid WHERE item_auction_bid.playerObjId NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM olympiad_nobles WHERE olympiad_nobles.char_id NOT IN (SELECT obj_Id FROM characters);");

            cleanCount += st.executeUpdate("DELETE FROM fishing_championship WHERE fishing_championship.PlayerName NOT IN (SELECT char_name FROM characters);");

            cleanCount += st.executeUpdate("DELETE FROM items WHERE items.loc != 'MAIL' AND items.owner_id NOT IN (SELECT obj_Id FROM characters) AND items.owner_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += st.executeUpdate("DELETE FROM items_delayed WHERE items_delayed.owner_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM pets WHERE pets.item_obj_id NOT IN (SELECT object_id FROM items);");

            cleanCount += st.executeUpdate("DELETE FROM clan_data WHERE clan_data.clan_id NOT IN (SELECT clanid FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM clan_subpledges WHERE clan_subpledges.type = 0 AND clan_subpledges.leader_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM clan_data WHERE clan_data.clan_id NOT IN (SELECT clan_id FROM clan_subpledges WHERE clan_subpledges.type = 0);");
            cleanCount += st.executeUpdate("DELETE FROM clan_subpledges WHERE clan_subpledges.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += st.executeUpdate("DELETE FROM clan_subpledges_skills WHERE clan_subpledges_skills.clan_id NOT IN (SELECT clan_id FROM clan_subpledges);");
            cleanCount += st.executeUpdate("DELETE FROM ally_data WHERE ally_data.leader_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += st.executeUpdate("DELETE FROM ally_data WHERE ally_data.ally_id NOT IN (SELECT ally_id FROM clan_data);");
            cleanCount += st.executeUpdate("DELETE FROM bbs_clannotice WHERE bbs_clannotice.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += st.executeUpdate("DELETE FROM clan_privs WHERE clan_privs.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += st.executeUpdate("DELETE FROM clan_skills WHERE clan_skills.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += st.executeUpdate("DELETE FROM clan_subpledges WHERE clan_subpledges.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += st.executeUpdate("DELETE FROM clan_wars WHERE clan_wars.clan1 NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += st.executeUpdate("DELETE FROM clan_wars WHERE clan_wars.clan2 NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += st.executeUpdate("DELETE FROM siege_players WHERE siege_players.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += st.executeUpdate("DELETE FROM siege_clans WHERE siege_clans.clan_id NOT IN (SELECT clan_id FROM clan_data);");

            cleanCount += st.executeUpdate("DELETE FROM character_mail WHERE character_mail.char_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM character_mail WHERE character_mail.message_id NOT IN (SELECT message_id FROM mail);");
            cleanCount += st.executeUpdate("DELETE FROM mail WHERE mail.message_id NOT IN (SELECT message_id FROM character_mail);");
            cleanCount += st.executeUpdate("DELETE FROM mail_attachments WHERE mail_attachments.message_id NOT IN (SELECT message_id FROM mail);");

            st.executeUpdate("UPDATE clan_data SET ally_id = '0' WHERE clan_data.ally_id NOT IN (SELECT ally_id FROM ally_data);");
            st.executeUpdate("UPDATE dominion SET lord_object_id = '0' WHERE dominion.lord_object_id NOT IN (SELECT obj_Id FROM characters);");
            st.executeUpdate("UPDATE clan_subpledges SET leader_id=0 WHERE leader_id > 0 AND clan_subpledges.leader_id NOT IN (SELECT obj_Id FROM characters);");
            st.executeUpdate("UPDATE castle SET tax_percent = '0' WHERE castle.id NOT IN (SELECT hasCastle FROM clan_data);");
            st.executeUpdate("UPDATE characters SET clanid = '0', title = '', pledge_type = '0', pledge_rank = '0', lvl_joined_academy = '0', apprentice = '0' WHERE characters.clanid > 0 AND characters.clanid NOT IN (SELECT clan_id FROM clan_data);");
            st.executeUpdate("UPDATE items SET loc = 'WAREHOUSE' WHERE loc = 'MAIL' AND items.object_id NOT IN (SELECT item_id FROM mail_attachments);");

            LOG.info("IdFactory: Cleaned " + cleanCount + " elements from database in " + (System.currentTimeMillis() - cleanupStart) / 1000 + "sec.");
        } catch (SQLException e) {
            LOG.error("", e);
        }
    }

    List<Integer> extractUsedObjectIDTable() throws SQLException {
        List<Integer> objectIds = new ArrayList<>();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement st = con.createStatement()) {
            EXTRACT_OBJ_ID_TABLES.forEach((k, v) -> {
                try {
                    ResultSet rs = st.executeQuery("SELECT " + v + " FROM " + k);
                    int size = objectIds.size();
                    while (rs.next())
                        objectIds.add(rs.getInt(1));

                    size = objectIds.size() - size;
                    if (size > 0)
                        LOG.info("IdFactory: Extracted " + size + " used id's from " + k);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        Collections.sort(objectIds);

        LOG.info("IdFactory: Extracted total " + objectIds.size() + " used id's.");

        return objectIds;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public abstract int getNextId();

    public void releaseId(int id) {
    }

    public abstract int size();

    private static class SingletonHolder {
        static final IdFactory INSTANCE = new BitSetIDFactory();
    }
}