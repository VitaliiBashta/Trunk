package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public class TriolsMirrorInstance extends NpcInstance {
    public TriolsMirrorInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        if (getNpcId() == 32040)
            player.teleToLocation(-12766, -35840, -10856); //to pagan
        else if (getNpcId() == 32039)
            player.teleToLocation(35079, -49758, -760); //from pagan
    }
}