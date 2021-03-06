package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.StringTokenizer;

public final class WyvernManagerInstance extends NpcInstance {
    WyvernManagerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        StringTokenizer st = new StringTokenizer(command, " ");
        String actualCommand = st.nextToken();
        boolean condition = validateCondition(player);

        if ("RideHelp".equalsIgnoreCase(actualCommand)) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("wyvern/help_ride.htm");
            html.replace("%npcname%", "Wyvern Manager " + getName());
            player.sendPacket(html);
            player.sendActionFailed();
        }
        if (condition) {
            if ("RideWyvern".equalsIgnoreCase(actualCommand) && player.isClanLeader())
                if (!player.isRiding() || !PetDataTable.isStrider(player.getMountNpcId())) {
                    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                    html.setFile("wyvern/not_ready.htm");
                    html.replace("%npcname%", "Wyvern Manager " + getName());
                    player.sendPacket(html);
                } else if (!player.haveItem(1460, 25)) {
                    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                    html.setFile("wyvern/havenot_cry.htm");
                    html.replace("%npcname%", "Wyvern Manager " + getName());
                    player.sendPacket(html);
                } else if (SevenSigns.INSTANCE.getCurrentPeriod() == 3 && SevenSigns.INSTANCE.getCabalHighestScore() == 3) {
                    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                    html.setFile("wyvern/no_ride_dusk.htm");
                    html.replace("%npcname%", "Wyvern Manager " + getName());
                    player.sendPacket(html);
                } else if (player.getInventory().destroyItemByItemId(1460, 25L, "WyvernManager")) {
                    player.setMount(PetDataTable.WYVERN_ID, player.getMountObjId(), player.getMountLevel());
                    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                    html.setFile("wyvern/after_ride.htm");
                    html.replace("%npcname%", "Wyvern Manager " + getName());
                    player.sendPacket(html);
                }
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        if (!validateCondition(player)) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("wyvern/lord_only.htm");
            html.replace("%npcname%", "Wyvern Manager " + getName());
            player.sendPacket(html);
            player.sendActionFailed();
            return;
        }
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setFile("wyvern/lord_here.htm");
        html.replace("%Char_name%", player.getName());
        html.replace("%npcname%", "Wyvern Manager " + getName());
        player.sendPacket(html);
        player.sendActionFailed();
    }

    private boolean validateCondition(Player player) {
        Residence residence = getCastle();
        if (residence != null && residence.getId() > 0)
            if (player.getClan() != null)
                if (residence.getOwnerId() == player.getClanId() && player.isClanLeader()) // Leader of clan
                    return true; // Owner
        residence = getFortress();
        if (residence != null && residence.getId() > 0)
            if (player.getClan() != null)
                if (residence.getOwnerId() == player.getClanId() && player.isClanLeader()) // Leader of clan
                    return true; // Owner
        residence = getClanHall();
        if (residence != null && residence.getId() > 0)
            // Leader of clan
            // Owner
            if (player.getClan() != null)
                return residence.getOwnerId() == player.getClanId() && player.isClanLeader();
        return false;
    }
}