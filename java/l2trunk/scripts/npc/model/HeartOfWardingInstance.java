package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.bosses.AntharasManager;

/**
 * @author pchayka
 */

public final class HeartOfWardingInstance extends NpcInstance {
    public HeartOfWardingInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.equalsIgnoreCase("enter_lair")) {
            AntharasManager.enterTheLair(player);
            return;
        } else
            super.onBypassFeedback(player, command);
    }
}