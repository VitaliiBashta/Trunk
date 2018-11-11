package l2trunk.gameserver.utils;

import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.dao.CharacterDAO;
import l2trunk.gameserver.instancemanager.CursedWeaponsManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;

public final class AdminFunctions {
    final static Location JAIL_SPAWN = new Location(-114648, -249384, -2984);

    private AdminFunctions() {
    }

    public static boolean kick(String player, String reason) {
        Player plyr = World.getPlayer(player);
        if (plyr == null)
            return false;

        return kick(plyr, reason);
    }


    private static boolean kick(Player player, String reason) {
        if (Config.ALLOW_CURSED_WEAPONS && Config.DROP_CURSED_WEAPONS_ON_KICK)
            if (player.isCursedWeaponEquipped()) {
                player.setPvpFlag(0);
                CursedWeaponsManager.getInstance().dropPlayer(player);
            }

        player.kick();

        return true;
    }

    public static String banChat(Player adminChar, String adminName, String charName, int val, String reason) {
        Player player = World.getPlayer(charName);

        if (player != null)
            charName = player.getName();
        else if (CharacterDAO.getInstance().getObjectIdByName(charName) == 0)
            return "Player " + charName + " not found.";

        if ((adminName == null || adminName.isEmpty()) && adminChar != null)
            adminName = adminChar.getName();

        if (reason == null || reason.isEmpty())
            reason = "Unknown"; // if no args, then "Unknown" default.

        String result, announce = null;
        if (val == 0) //unban
        {
            if (adminChar != null && !adminChar.getPlayerAccess().CanUnBanChat)
                return "You have no right to withdraw the ban chat.";
            Log.add(adminName + " lifted ban chat Player " + charName + ".", "banchat", adminChar);
            result = "You removed the ban chat Player " + charName + ".";
        } else if (val < 0) {
            if (adminChar != null && adminChar.getPlayerAccess().BanChatMaxValue > 0)
                return "You can ban for no more than " + adminChar.getPlayerAccess().BanChatMaxValue + " minute.";
            Log.add(adminName + " Chat banned Player " + charName + " for an indefinite period, the reason: " + reason + ".", "banchat", adminChar);
            result = "You are banned from chat Player " + charName + " for an indefinite period.";
        } else {
            if (adminChar != null && !adminChar.getPlayerAccess().CanUnBanChat && (player == null || player.getNoChannel() != 0))
                return "You may not change the ban time.";
            if (adminChar != null && adminChar.getPlayerAccess().BanChatMaxValue != -1 && val > adminChar.getPlayerAccess().BanChatMaxValue)
                return "You can ban for no more than " + adminChar.getPlayerAccess().BanChatMaxValue + " minute.";
            Log.add(adminName + " Chat banned Player " + charName + " on " + val + " minute, reasons: " + reason + ".", "banchat", adminChar);
            result = "You are banned from chat Player " + charName + " on " + val + " minute.";
        }

        if (player != null)
            updateNoChannel(player, val, reason);
        else
            AutoBan.ChatBan(charName, val, reason, adminName);

        if (announce != null)
            Announcements.shout(adminChar, announce, ChatType.CRITICAL_ANNOUNCE);

        return result;
    }

    private static void updateNoChannel(Player player, int time, String reason) {
        player.updateNoChannel(time * 60000);
        if (time == 0)
            player.sendMessage(new CustomMessage("common.ChatUnBanned", player));
        else if (time > 0) {
            if (reason == null || reason.isEmpty())
                player.sendMessage(new CustomMessage("common.ChatBanned", player).addNumber(time));
            else
                player.sendMessage(new CustomMessage("common.ChatBannedWithReason", player).addNumber(time).addString(reason));
        } else if (reason == null || reason.isEmpty())
            player.sendMessage(new CustomMessage("common.ChatBannedPermanently", player));
        else
            player.sendMessage(new CustomMessage("common.ChatBannedPermanentlyWithReason", player).addString(reason));
    }
}
