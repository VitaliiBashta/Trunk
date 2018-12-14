package l2trunk.scripts.ai.other.PailakaDevilsLegacy;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

public final class PowderKeg extends DefaultAI {
    public PowderKeg(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        actor.setTarget(actor);
        actor.doCast(5714, attacker, true);
        actor.doDie(null);
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    @Override
    public boolean randomAnimation() {
        return false;
    }
}