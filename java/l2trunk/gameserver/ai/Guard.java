package l2trunk.gameserver.ai;

import l2trunk.gameserver.model.AggroList.AggroInfo;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

public class Guard extends Fighter {
    protected Guard(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean canAttackCharacter(Creature target) {
        NpcInstance actor = getActor();
        if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
            AggroInfo ai = actor.getAggroList().get(target);
            return ai != null && ai.hate > 0;
        }
        return target.isMonster() || target.isPlayable();
    }

    @Override
    public boolean checkAggression(Creature target, boolean avoidAttack) {
        NpcInstance actor = getActor();
        if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro())
            return false;

        if (target.isPlayable()) {
            if (target.getKarma() == 0 || (actor.getParameter("evilGuard", false) && target.getPvpFlag() > 0))
                return false;
        }
        if (target.isMonster()) {
            //if (!((MonsterInstance)target).isAggressive())
            return false;
        }

        return super.checkAggression(target, avoidAttack);
    }

    @Override
    public int getMaxAttackTimeout() {
        return 0;
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}