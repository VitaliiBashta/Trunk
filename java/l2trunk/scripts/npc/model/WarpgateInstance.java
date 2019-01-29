package l2trunk.scripts.npc.model;

import l2trunk.gameserver.instancemanager.HellboundManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.quests._130_PathToHellbound;
import l2trunk.scripts.quests._133_ThatsBloodyHot;

public final class WarpgateInstance extends NpcInstance {

    public WarpgateInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.startsWith("enter_hellbound")) {
            if (HellboundManager.getHellboundLevel() != 0 && (player.isQuestCompleted(_130_PathToHellbound.class) || player.isQuestCompleted(_133_ThatsBloodyHot.class))) {
                player.teleToLocation(-11272, 236464, -3248);
            } else if (HellboundManager.getConfidence() < 1 && (player.isQuestCompleted(_130_PathToHellbound.class))) {
                HellboundManager.setConfidence(1);
                player.teleToLocation(-11272, 236464, -3248);

            } else {
                showChatWindow(player, "default/32318-1.htm");
            }
        } else
            super.onBypassFeedback(player, command);
    }
}