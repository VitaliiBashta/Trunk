package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

public class TullyWorkShopTeleporterInstance extends NpcInstance {
    public TullyWorkShopTeleporterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (!player.isInParty()) {
            showChatWindow(player, "default/32753-1.htm");
            return;
        }
        if (!player.isPartyLeader()) {
            showChatWindow(player, "default/32753-2.htm");
            return;
        }
        if (!rangeCheck(player)) {
            showChatWindow(player, "default/32753-2.htm");
            return;
        }

        if (command.equalsIgnoreCase("01_up")) {
            player.getParty().teleport(new Location(-12700, 273340, -13600));
        } else if (command.equalsIgnoreCase("02_up")) {
            player.getParty().teleport(new Location(-13246, 275740, -11936));
        } else if (command.equalsIgnoreCase("02_down")) {
            player.getParty().teleport(new Location(-12894, 273900, -15296));
        } else if (command.equalsIgnoreCase("03_up")) {
            player.getParty().teleport(new Location(-12798, 273458, -10496));
        } else if (command.equalsIgnoreCase("03_down")) {
            player.getParty().teleport(new Location(-12718, 273490, -13600));
        } else if (command.equalsIgnoreCase("04_up")) {
            player.getParty().teleport(new Location(-13500, 275912, -9032));
        } else if (command.equalsIgnoreCase("04_down")) {
            player.getParty().teleport(new Location(-13246, 275740, -11936));
        } else
            super.onBypassFeedback(player, command);
    }

    private boolean rangeCheck(Player pl) {
        return pl.getParty().getMembers().stream()
        .allMatch(m -> pl.isInRange(m, 400));
    }
}