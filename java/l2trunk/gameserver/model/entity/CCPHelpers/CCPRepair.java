package l2trunk.gameserver.model.entity.CCPHelpers;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.dao.ItemsDAO;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.ItemInstance.ItemLocation;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.scripts.Functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CCPRepair {
    private static final Logger _log = LoggerFactory.getLogger(CCPRepair.class);

    public static boolean repairChar(Player activeChar, String target) {
        if (!target.isEmpty()) {
            if (activeChar.getName().equalsIgnoreCase(target)) {
                activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.YouCantRepairYourself"));
                return false;
            }

            int objId = 0;

            for (Map.Entry<Integer, String> e : activeChar.getAccountChars().entrySet()) {
                if (e.getValue().equalsIgnoreCase(target)) {
                    objId = e.getKey();
                    break;
                }
            }

            if (objId == 0) {
                activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.YouCanRepairOnlyOnSameAccount"));
                return false;
            } else if (World.getPlayer(objId) != null) {
                activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.CharIsOnline"));
                return false;
            }

            PreparedStatement statement;
            ResultSet rs;
            try (Connection con = DatabaseFactory.getInstance().getConnection()) {
                statement = con.prepareStatement("SELECT karma FROM characters WHERE obj_Id=?");
                statement.setInt(1, objId);
                statement.execute();
                rs = statement.getResultSet();

                int karma;
                rs.next();

                karma = rs.getInt("karma");


                if (karma > 0) {
                    statement = con.prepareStatement("UPDATE characters SET x=17144, y=170156, z=-3502 WHERE obj_Id=?");
                    statement.setInt(1, objId);
                    statement.execute();
                } else {
                    statement = con.prepareStatement("UPDATE characters SET x=0, y=0, z=0 WHERE obj_Id=?");
                    statement.setInt(1, objId);
                    statement.execute();

                    ItemsDAO.INSTANCE.getItemsByOwnerIdAndLoc(objId, ItemLocation.PAPERDOLL)
                            .filter(ItemInstance::isEquipped)
                            .forEach(item -> {
                                item.setEquipped(false);
                                item.setJdbcState(JdbcEntityState.UPDATED);
                                item.update();
                            });
                }

                statement = con.prepareStatement("DELETE FROM character_variables WHERE obj_id=? AND type='user-var' AND name='reflection'");
                statement.setInt(1, objId);
                statement.execute();

                activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.RepairDone"));
                return true;
            } catch (SQLException e) {
                _log.error("Error while repairing Char", e);
                return false;
            }
        } else {
            activeChar.sendMessage(".repair <name>");
            return false;
        }
    }

    public static String getCharsOnAccount(String myCharName, String accountName) {
        List<String> chars = new ArrayList<>();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters WHERE account_name=?")) {
            statement.setString(1, accountName);

            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    chars.add(rset.getString("char_name"));
                }
            }
        } catch (SQLException e) {
            _log.error("Error while getting Chars on Account", e);
        }

        StringBuilder resultBuilder = new StringBuilder();
        for (String charName : chars)
            if (!charName.equalsIgnoreCase(myCharName))
                resultBuilder.append(charName).append(';');

        if (resultBuilder.length() == 0)
            return "";

        return resultBuilder.substring(0, resultBuilder.length() - 1);
    }
}
