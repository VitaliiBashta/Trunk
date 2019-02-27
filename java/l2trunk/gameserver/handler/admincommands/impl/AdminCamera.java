package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.InvisibleType;
import l2trunk.gameserver.network.serverpackets.CameraMode;
import l2trunk.gameserver.network.serverpackets.SpecialCamera;

import java.util.List;

public final class AdminCamera implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {

        if (!activeChar.getPlayerAccess().Menu)
            return false;

        switch (comm) {
            case "admin_freelook": {
                if (fullString.length() > 15)
                    fullString = fullString.substring(15);
                else {
                    activeChar.sendMessage("Usage: //freelook 1 or //freelook 0");
                    return false;
                }

                int mode = Integer.parseInt(fullString);
                if (mode == 1) {
                    activeChar.setInvisibleType(InvisibleType.NORMAL);
                    activeChar.setInvul(true);
                    activeChar.setNoChannel(-1);
                    activeChar.setFlying(true);
                } else {
                    activeChar.setInvisibleType(InvisibleType.NONE);
                    activeChar.setInvul(false);
                    activeChar.setNoChannel(0);
                    activeChar.setFlying(false);
                }
                activeChar.sendPacket(new CameraMode(mode));

                break;
            }
            case "admin_cinematic": {
                int id = Integer.parseInt(wordList[1]);
                int dist = Integer.parseInt(wordList[2]);
                int yaw = Integer.parseInt(wordList[3]);
                int pitch = Integer.parseInt(wordList[4]);
                int time = Integer.parseInt(wordList[5]);
                int duration = Integer.parseInt(wordList[6]);
                activeChar.sendPacket(new SpecialCamera(id, dist, yaw, pitch, time, duration));
                break;
            }
        }
        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_freelook",
                "admin_cinematic");
    }
}