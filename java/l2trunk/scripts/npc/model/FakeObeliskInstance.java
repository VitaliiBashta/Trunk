package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

/**
 * Данный инстанс используется NPC 13193 в локации Seed of Destruction
 *
 * @author SYS
 */
public final class FakeObeliskInstance extends NpcInstance {
    public FakeObeliskInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val) {
    }

    @Override
    public void onAction(Player player, boolean shift) {
        player.sendActionFailed();
    }
}