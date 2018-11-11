package l2trunk.scripts.ai.seedofinfinity;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.scripts.instances.HeartInfinityAttack;

public class Ekimus extends Mystic {
    private long delayTimer = 0;

    public Ekimus(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        for (NpcInstance npc : actor.getReflection().getAllByNpcId(29151, true))
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    protected void thinkAttack() {
        if (delayTimer + 5000 < System.currentTimeMillis()) {
            delayTimer = System.currentTimeMillis();
            if (getActor().getReflection().getInstancedZoneId() == 121)
                ((HeartInfinityAttack) getActor().getReflection()).notifyEkimusAttack();
        }
        super.thinkAttack();
    }

    @Override
    protected boolean thinkActive() {
        if (delayTimer + 5000 < System.currentTimeMillis()) {
            delayTimer = System.currentTimeMillis();
            if (getActor().getReflection().getInstancedZoneId() == 121)
                ((HeartInfinityAttack) getActor().getReflection()).notifyEkimusIdle();
        }
        return super.thinkActive();
    }
}