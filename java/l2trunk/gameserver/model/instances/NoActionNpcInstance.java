package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.npc.NpcTemplate;

@Deprecated
public class NoActionNpcInstance extends NpcInstance {
    public NoActionNpcInstance(final int objectID, final NpcTemplate template) {
        super(objectID, template);
    }

    @Override
    public void onAction(final Player player, final boolean dontMove) {
        player.sendActionFailed();
    }
}
