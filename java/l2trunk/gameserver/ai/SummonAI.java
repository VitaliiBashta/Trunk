package l2trunk.gameserver.ai;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Summon;

public final class SummonAI extends PlayableAI {
    public SummonAI(Summon actor) {
        super(actor);
    }

    @Override
    public void thinkActive() {
        Summon actor = getActor();

        clearNextAction();
        if (actor.isDepressed()) {
            setAttackTarget(actor.owner);
            changeIntention(CtrlIntention.AI_INTENTION_ATTACK, actor.owner, null);
            thinkAttack(true);
        } else if (actor.isFollowMode()) {
            changeIntention(CtrlIntention.AI_INTENTION_FOLLOW, actor.owner, Config.FOLLOW_RANGE);
            thinkFollow();
        }

        super.thinkActive();
    }

    @Override
    public void thinkAttack(boolean checkRange) {
        Summon actor = getActor();

        if (actor.isDepressed())
            setAttackTarget(actor.owner);

        super.thinkAttack(checkRange);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        Summon actor = getActor();
        if (attacker != null && actor.owner.isDead() && !actor.isDepressed())
            Attack(attacker, false, false);
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public Summon getActor() {
        return (Summon) super.getActor();
    }
}