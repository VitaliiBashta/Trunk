package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.Privilege;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.HtmlUtils;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.List;
import java.util.Map;

public final class AuctionedDoormanInstance extends NpcInstance {
    private final List<Integer> doors;
    private final boolean elite;

    public AuctionedDoormanInstance(int objectId, NpcTemplate template) {
        super(objectId, template);

        doors = template.getAiParams().getIntegerList("doors", List.of());
        elite = template.getAiParams().isSet("elite");
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        ClanHall clanHall = getClanHall();
        if ("openDoors".equalsIgnoreCase(command)) {
            if (player.hasPrivilege(Privilege.CH_ENTER_EXIT) && player.getClan().getHasHideout() == clanHall.getId()) {
                doors.forEach(d -> ReflectionUtils.getDoor(d).openMe());
                showChatWindow(player, "residence2/clanhall/agitafterdooropen.htm");
            } else
                showChatWindow(player, "residence2/clanhall/noAuthority.htm");
        } else if ("closeDoors".equalsIgnoreCase(command)) {
            if (player.hasPrivilege(Privilege.CH_ENTER_EXIT) && player.getClan().getHasHideout() == clanHall.getId()) {
                doors.forEach(d -> ReflectionUtils.getDoor(d).closeMe());
                showChatWindow(player, "residence2/clanhall/agitafterdoorclose.htm");
            } else
                showChatWindow(player, "residence2/clanhall/noAuthority.htm");
        } else if ("banish".equalsIgnoreCase(command)) {
            if (player.hasPrivilege(Privilege.CH_DISMISS)) {
                clanHall.banishForeigner();
                showChatWindow(player, "residence2/clanhall/agitafterbanish.htm");
            } else
                showChatWindow(player, "residence2/clanhall/noAuthority.htm");
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        ClanHall clanHall = getClanHall();
        if (clanHall != null) {
            Clan playerClan = player.getClan();
            if (playerClan != null && playerClan.getHasHideout() == clanHall.getId())
                showChatWindow(player, elite ? "residence2/clanhall/WyvernAgitJanitorHi.htm" : "residence2/clanhall/AgitJanitorHi.htm", Map.of("%owner%", playerClan.getName()));
            else {
                if (playerClan != null && playerClan.getCastle() > 0) {
                    Castle castle = ResidenceHolder.getCastle(playerClan.getCastle());
                    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                    html.setFile("merchant/territorystatus.htm");
                    html.replace("%npcname%", getName());
                    html.replace("%castlename%", HtmlUtils.htmlResidenceName(castle.getId()));
                    html.replace("%taxpercent%", castle.getTaxPercent());
                    html.replace("%clanname%", playerClan.getName());
                    html.replace("%clanleadername%", playerClan.getLeaderName());
                    player.sendPacket(html);
                } else
                    showChatWindow(player, "residence2/clanhall/noAgitInfo.htm");
            }
        } else
            showChatWindow(player, "residence2/clanhall/noAgitInfo.htm");
    }
}
