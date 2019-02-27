package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.tables.PetDataTable;

import java.util.List;

public final class AdminRide implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().Rider)
            return false;

        switch (comm) {
            case "admin_ride":
                if (activeChar.isMounted() || activeChar.getPet() != null) {
                    activeChar.sendMessage("Already Have a Pet or Mounted.");
                    return false;
                }
                if (wordList.length != 2) {
                    activeChar.sendMessage("Incorrect id.");
                    return false;
                }
                activeChar.setMount(Integer.parseInt(wordList[1]), 0, 85);
                break;
            case "admin_ride_wyvern":
            case "admin_wr":
                if (activeChar.isMounted() || activeChar.getPet() != null) {
                    activeChar.sendMessage("Already Have a Pet or Mounted.");
                    return false;
                }
                activeChar.setMount(PetDataTable.WYVERN_ID, 0, 85);
                break;
            case "admin_ride_strider":
            case "admin_sr":
                if (activeChar.isMounted() || activeChar.getPet() != null) {
                    activeChar.sendMessage("Already Have a Pet or Mounted.");
                    return false;
                }
                activeChar.setMount(PetDataTable.STRIDER_WIND_ID, 0, 85);
                break;
            case "admin_unride":
            case "admin_ur":
                activeChar.dismount();
                break;
        }

        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_ride",
                "admin_ride_wyvern",
                "admin_ride_strider",
                "admin_unride",
                "admin_wr",
                "admin_sr",
                "admin_ur");
    }
}