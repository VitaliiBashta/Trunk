package l2trunk.scripts.ai.residences.clanhall;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;
import java.util.stream.Collectors;

public class MatchFighter extends Fighter {
    public MatchFighter(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isActionsDisabled())
            return true;
        if (defThink) {
            if (doTask())
                clearTasks();
            return true;
        }

        long now = System.currentTimeMillis();
        if (now - _checkAggroTimestamp > Config.AGGRO_CHECK_INTERVAL) {
            _checkAggroTimestamp = now;

            final List<Creature> aggroList = World.getAroundCharacters(actor)
            .filter(cha -> checkAggression(cha, true))
                .collect(Collectors.toList());

            if (!aggroList.isEmpty()) {
                aggroList.sort(_nearestTargetComparator);

                for (Creature cha : aggroList) {
                    if (cha != null && !cha.isDead()) {
                        if (checkAggression(cha, false))
                            return true;
                    }
                }
            }
        }

        return randomWalk();

    }

    @Override
    public boolean checkAggression(Creature target, boolean avoidAttack) {
        CTBBossInstance actor = getActor();

        if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE)
            return false;

        if (target.isAlikeDead() || target.isInvul())
            return false;

        if (!actor.isAttackable(target))
            return false;
        if (!GeoEngine.canSeeTarget(actor, target, false))
            return false;

        if (!avoidAttack) {
            actor.getAggroList().addDamageHate(target, 0, 2);

            if ((target.isSummon() || target.isPet()))
                actor.getAggroList().addDamageHate(target.getPlayer(), 0, 1);

            startRunningTask(AI_TASK_ATTACK_DELAY);
            setIntentionAttack(CtrlIntention.AI_INTENTION_ATTACK, target);
        }

        return true;
    }

    @Override
    public boolean canAttackCharacter(Creature target) {
        NpcInstance actor = getActor();
        return actor.isAttackable(target);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        CTBBossInstance actor = getActor();

        int x = (int) (actor.getX() + 800 * Math.cos(actor.headingToRadians(actor.getHeading() - 32768)));
        int y = (int) (actor.getY() + 800 * Math.sin(actor.headingToRadians(actor.getHeading() - 32768)));

        actor.setSpawnedLoc(new Location(x, y, actor.getZ()));
        addTaskMove(actor.getSpawnedLoc(), true);
        doTask();
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public CTBBossInstance getActor() {
        return (CTBBossInstance) super.getActor();
    }
}
