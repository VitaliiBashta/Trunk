package l2trunk.scripts.npc.model.residences;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.npc.NpcTemplate;


public final class TeleportSiegeGuardInstance extends SiegeGuardInstance {
    public TeleportSiegeGuardInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        canBypassCheck(player, this);

    }
}
