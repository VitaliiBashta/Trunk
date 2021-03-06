package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.AdminFunctions;
import l2trunk.gameserver.utils.Location;

import java.util.List;
import java.util.StringTokenizer;

public final class AdminMenu implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().Menu)
            return false;

        if (fullString.startsWith("admin_teleport_character_to_menu")) {
            String[] data = fullString.split(" ");
            if (data.length == 5) {
                String playerName = data[1];
                Player player = World.getPlayer(playerName);
                if (player != null)
                    teleportCharacter(player, new Location(Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4])));
            }
        } else if (fullString.startsWith("admin_recall_char_menu"))
            try {
                String targetName = fullString.substring(23);
                Player player = World.getPlayer(targetName);
                teleportCharacter(player, activeChar.getLoc());
            } catch (StringIndexOutOfBoundsException ignored) {
            }
        else if (fullString.startsWith("admin_goto_char_menu"))
            try {
                String targetName = fullString.substring(21);
                Player player = World.getPlayer(targetName);
                teleportToCharacter(activeChar, player);
            } catch (StringIndexOutOfBoundsException ignored) {
            }
        else if (fullString.equals("admin_kill_menu")) {
            GameObject obj = activeChar.getTarget();
            StringTokenizer st = new StringTokenizer(fullString);
            if (st.countTokens() > 1) {
                st.nextToken();
                String player = st.nextToken();
                Player plyr = World.getPlayer(player);
                if (plyr == null)
                    activeChar.sendMessage("Player " + player + " not found in game.");
                obj = plyr;
            }
            if (obj instanceof Creature) {
                Creature target = (Creature) obj;
                target.reduceCurrentHp(target.getMaxHp() + 1, activeChar, null, true, true, true, false, false, false, true);
            } else
                activeChar.sendPacket(SystemMsg.INVALID_TARGET);
        } else if (fullString.startsWith("admin_kick_menu")) {
            StringTokenizer st = new StringTokenizer(fullString);
            if (st.countTokens() > 1) {
                st.nextToken();
                String player = st.nextToken();
                if (AdminFunctions.kick(player, "kick"))
                    activeChar.sendMessage("Player kicked.");
            }
        }

        activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/charmanage.htm"));
        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_char_manage",
                "admin_teleport_character_to_menu",
                "admin_recall_char_menu",
                "admin_goto_char_menu",
                "admin_kick_menu",
                "admin_kill_menu",
                "admin_ban_menu",
                "admin_unban_menu");
    }

    private void teleportCharacter(Player player, Location loc) {
        if (player != null) {
            player.sendMessage("Admin is teleporting you.");
            player.teleToLocation(loc);
        }
    }

    private void teleportToCharacter(Player activeChar, GameObject target) {
        Player player;
        if (target instanceof Player)
            player = (Player) target;
        else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        if (player.objectId() == activeChar.objectId())
            activeChar.sendMessage("You cannot getPlayer teleport.");
        else {
            activeChar.teleToLocation(player.getLoc());
            activeChar.sendMessage("You have teleported to character " + player.getName() + ".");
        }
    }

}