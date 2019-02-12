package l2trunk.scripts.npc.model.residences.fortress.peace;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class ArcherCaptionInstance extends NpcInstance {
    public ArcherCaptionInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        showChatWindow(player, "residence2/fortress/fortress_archer.htm");
    }
}
