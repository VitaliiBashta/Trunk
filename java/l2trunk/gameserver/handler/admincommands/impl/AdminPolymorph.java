package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.List;

public final class AdminPolymorph implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().CanPolymorph)
            return false;

        GameObject target = activeChar.getTarget();

        switch (comm) {
            case "admin_polyself":
                target = activeChar;
            case "admin_polymorph":
            case "admin_poly":
                if (!(target instanceof Player)) {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
                try {
                    int id = Integer.parseInt(wordList[1]);
                    if (NpcHolder.getTemplate(id) != null) {
                        ((Player) target).setPolyId(id);
                        ((Player) target).broadcastCharInfo();
                    }
                } catch (Exception e) {
                    activeChar.sendMessage("USAGE: //poly id [type:npc|item]");
                    return false;
                }
                break;
            case "admin_unpolyself":
                target = activeChar;
            case "admin_unpolymorph":
            case "admin_unpoly":
                if (target instanceof Player) {
                    ((Player) target).setPolyId(0);
                    ((Player) target).broadcastCharInfo();
                    break;
                } else {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
        }

        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_polyself",
                "admin_polymorph",
                "admin_poly",
                "admin_unpolyself",
                "admin_unpolymorph",
                "admin_unpoly");
    }
}