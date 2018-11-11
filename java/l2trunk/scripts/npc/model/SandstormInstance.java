package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

/**
 * Данный инстанс используется NPC Sandstorm в локации Hellbound
 *
 * @author SYS
 */
public class SandstormInstance extends NpcInstance {
    public SandstormInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
    }

    @Override
    public void onAction(Player player, boolean shift) {
        player.sendActionFailed();
    }
}