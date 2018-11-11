package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.instances.NpcInstance;

public class BelethClone extends Mystic {
    public BelethClone(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean randomAnimation() {
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
        return;
    }

}