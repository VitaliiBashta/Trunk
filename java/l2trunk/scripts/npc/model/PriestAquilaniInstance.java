package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.quests._10288_SecretMission;

public final class PriestAquilaniInstance extends NpcInstance {

    public PriestAquilaniInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        if (player.isQuestCompleted(_10288_SecretMission.class) ) {
            player.sendPacket(new NpcHtmlMessage(player, this, "default/32780-1.htm", val));
        } else {
            player.sendPacket(new NpcHtmlMessage(player, this, "default/32780.htm", val));
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("teleport".equalsIgnoreCase(command)) {
            player.teleToLocation(new Location(118833, -80589, -2688));
        } else
            super.onBypassFeedback(player, command);
    }
}