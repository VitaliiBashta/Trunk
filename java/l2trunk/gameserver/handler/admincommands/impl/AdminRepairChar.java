package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public final class AdminRepairChar implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (activeChar.getPlayerAccess() == null || !activeChar.getPlayerAccess().CanEditChar)
            return false;

        if (wordList.length != 2)
            return false;

        PreparedStatement statement;
        ResultSet rset;
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            statement = con.prepareStatement("UPDATE characters SET x=-84318, y=244579, z=-3730 WHERE char_name=?");
            statement.setString(1, wordList[1]);
            statement.execute();
            statement = con.prepareStatement("SELECT obj_id FROM characters where char_name=?");
            statement.setString(1, wordList[1]);
            rset = statement.executeQuery();
            int objId = 0;
            if (rset.next())
                objId = rset.getInt(1);

            if (objId == 0)
                return false;

            // con = L2DatabaseFactory.INSTANCE().getConnection();
            statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=?");
            statement.setInt(1, objId);
            statement.execute();
            // con = L2DatabaseFactory.INSTANCE().getConnection();
            statement = con.prepareStatement("UPDATE items SET loc='INVENTORY' WHERE owner_id=? AND loc!='WAREHOUSE'");
            statement.setInt(1, objId);
            statement.execute();

            // con = L2DatabaseFactory.INSTANCE().getConnection();
            statement = con.prepareStatement("DELETE FROM character_variables WHERE obj_id=? AND `type`='user-var' AND `name`='reflection' LIMIT 1");
            statement.setInt(1, objId);
            statement.execute();
        } catch (SQLException ignored) {
        }

        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_restore",
                "admin_repair");
    }
}