package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class CabaleBufferInstance extends NpcInstance {
    public CabaleBufferInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
    }

    @Override
    public void showChatWindow(Player player, String filename) {
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
    }
}