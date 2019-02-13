package l2trunk.gameserver.skills.effects;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;

import java.util.List;
import java.util.stream.Collectors;

public final class EffectDiscord extends Effect {
    public EffectDiscord(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        int skilldiff = effected.getLevel() - skill.magicLevel;
        int lvldiff = effected.getLevel() - effector.getLevel();
        if (skilldiff > 10 || skilldiff > 5 && Rnd.chance(30) || Rnd.chance(Math.abs(lvldiff) * 2))
            return false;

        boolean multitargets = skill.isAoE();

        if (!(effected instanceof MonsterInstance)) {
            if (!multitargets)
                effector.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        if (effected.isFearImmune() || effected.isRaid()) {
            if (!multitargets)
                effector.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        if (effected.isInZonePeace()) {
            if (!multitargets)
                effector.sendPacket(SystemMsg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        effected.startConfused();

        onActionTime();
    }

    @Override
    public void onExit() {
        super.onExit();

        if (!effected.stopConfused()) {
            effected.abortAttack(true, true);
            effected.abortCast(true, true);
            effected.stopMove();
            effected.getAI().setAttackTarget(null);
            effected.setWalking();
            effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }

    @Override
    protected boolean onActionTime() {
        List<Creature> targetList = effected.getAroundCharacters(900, 200)
                .filter(c ->c instanceof NpcInstance)
                .filter(c -> c != effected)
                .collect(Collectors.toList());

        // if there is no target, exit function
        if (targetList.isEmpty())
            return true;

        // Choosing randomly a new target
        Creature target = Rnd.get(targetList);

        // Attacking the target
        effected.setRunning();
        effected.getAI().Attack(target, true, false);

        return false;
    }
}