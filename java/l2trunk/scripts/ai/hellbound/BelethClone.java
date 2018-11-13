package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class BelethClone extends Mystic {
    public BelethClone(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    @Override
    public boolean randomAnimation() {
        return false;
    }

    @Override
    public boolean canSeeInSilentMove(Playable target) {
        return true;
    }

    @Override
    public boolean canSeeInHide(Playable target) {
        return true;
    }

    @Override
    public void addTaskAttack(Creature target) {
    }

}