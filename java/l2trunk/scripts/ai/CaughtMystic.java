package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class CaughtMystic extends Mystic {
    private static final int TIME_TO_LIVE = 60000;
    private final long TIME_TO_DIE = System.currentTimeMillis() + TIME_TO_LIVE;

    public CaughtMystic(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        if (Rnd.chance(75))
            Functions.npcSayCustomMessage(getActor(), "scripts.ai.CaughtMob.spawn");
    }

    @Override
    public void onEvtDead(Creature killer) {
        if (Rnd.chance(75))
            Functions.npcSayCustomMessage(getActor(), "scripts.ai.CaughtMob.death");

        super.onEvtDead(killer);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (System.currentTimeMillis() >= TIME_TO_DIE) {
            actor.deleteMe();
            return false;
        }
        return super.thinkActive();
    }
}
