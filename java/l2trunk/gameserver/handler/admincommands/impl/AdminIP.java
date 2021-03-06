package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;

import java.util.List;

public final class AdminIP implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().CanBan)
            return false;

        switch (comm) {
            case "admin_charip":
                if (wordList.length != 2) {
                    activeChar.sendMessage("Command syntax: //charip <char_name>");
                    activeChar.sendMessage(" Gets character's IP.");
                    break;
                }

                Player pl = World.getPlayer(wordList[1]);

                if (pl == null) {
                    activeChar.sendMessage("Character " + wordList[1] + " not found.");
                    break;
                }

                String ip_adr = pl.getIP();
                if (ip_adr.equalsIgnoreCase("<not connected>")) {
                    activeChar.sendMessage("Character " + wordList[1] + " not found.");
                    break;
                }

                activeChar.sendMessage("Character's IP: " + ip_adr);
                break;
            case "admin_ip":
                Player target;
                if (activeChar.getTarget() instanceof Player)
                    target = (Player) activeChar.getTarget();
                else
                    target = activeChar;

                if ("<not connected>".equalsIgnoreCase(target.getIP())) {
                    activeChar.sendMessage("Target not found.");
                    return false;
                }

                activeChar.sendMessage("IP:" + target.getIP());

                GameObjectsStorage.getAllPlayersStream()
                        .filter(player -> player.getIP().equals(target.getIP()))
                        .forEach(player -> activeChar.sendMessage("Player with same IP:" + player.getName()));
                break;
        }
        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_charip",
                "admin_ip");
    }
}