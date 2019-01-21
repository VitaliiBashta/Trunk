package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class Anais extends Fighter {

    public Anais(NpcInstance actor) {
        super(actor);
        this.AI_TASK_ATTACK_DELAY = 1000;
        this.AI_TASK_ACTIVE_DELAY = 1000;
    }

    @Override
    public boolean canSeeInSilentMove(Playable target) {
        return (!target.isSilentMoving()) || (Rnd.chance(10));
    }
}