package l2trunk.scripts.ai.isle_of_prayer;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class WaterDragonDetractor extends Fighter {
    private static final int SPIRIT_OF_LAKE = 9689;
    private static final int BLUE_CRYSTAL = 9595;

    public WaterDragonDetractor(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        if (killer != null) {
            final Player player = killer.getPlayer();
            if (player != null) {
                final NpcInstance actor = getActor();
                actor.dropItem(player, SPIRIT_OF_LAKE, 1);
                if (Rnd.chance(10))
                    actor.dropItem(player, BLUE_CRYSTAL, 1);
            }
        }
        super.onEvtDead(killer);
    }
}