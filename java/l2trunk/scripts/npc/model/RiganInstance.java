package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class RiganInstance extends NpcInstance {
    public RiganInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        String fileName = "custom/" + getNpcId()
                + (val > 0 ? "-" + val : "") + ".htm";
        player.sendPacket(new NpcHtmlMessage(player, this, fileName, val));
    }
}
