package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.Arrays;
import java.util.List;

public final class EvasGiftBox extends Fighter {
    private static final List<Integer> KISS_OF_EVA = List.of(1073, 3141, 3252);

    private static final int Red_Coral = 9692;
    private static final int Crystal_Fragment = 9693;

    public EvasGiftBox(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        if (killer != null) {
            Player player = killer.getPlayer();
            if (player != null && player.getEffectList().containEffectFromSkills(KISS_OF_EVA))
                actor.dropItem(player, Rnd.chance(50) ? Red_Coral : Crystal_Fragment, 1);
        }
        super.onEvtDead(killer);
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}