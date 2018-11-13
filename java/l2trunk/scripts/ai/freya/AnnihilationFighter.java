package l2trunk.scripts.ai.freya;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;

public final class AnnihilationFighter extends Fighter {
    public AnnihilationFighter(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        if (Rnd.chance(5))
            NpcUtils.spawnSingle(18839, Location.findPointToStay(getActor(), 40, 120), getActor().getReflection()); // Maguen

        super.onEvtDead(killer);
    }

    @Override
    public boolean canSeeInSilentMove(Playable target) {
        return true;
    }

    @Override
    public boolean canSeeInHide(Playable target) {
        return true;
    }
}