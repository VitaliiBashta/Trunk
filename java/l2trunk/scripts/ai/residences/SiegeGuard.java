package l2trunk.scripts.ai.residences;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.npc.model.residences.SiegeGuardInstance;

import java.util.List;

public class SiegeGuard extends Fighter {
    public SiegeGuard(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = 1000;
    }

    @Override
    public SiegeGuardInstance getActor() {
        return (SiegeGuardInstance) super.getActor();
    }

    @Override
    public int getMaxPathfindFails() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxAttackTimeout() {
        return 0;
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
        // Осадные гварды могут видеть игроков в режиме Silent Move с вероятностью 10%
        return !target.isSilentMoving() || Rnd.chance(10);
    }

    @Override
    public boolean checkAggression(Playable target, boolean avoidAttack) {
        NpcInstance actor = getActor();
        if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro())
            return false;
        if (target.isAlikeDead() || target.isInvul())
            return false;

        if (!canSeeInSilentMove(target) || !canSeeInHide(target))
            return false;
        if (target instanceof Player && ((Player) target).isGM() && target.isInvisible())
            return false;
        if (target instanceof Player && !((Player)target).isActive())
            return false;

        AggroList.AggroInfo ai = actor.getAggroList().get(target);
        if (ai != null && ai.hate > 0) {
            if (!target.isInRangeZ(actor.getSpawnedLoc(), MAX_PURSUE_RANGE))
                return false;
        } else if (!target.isInRangeZ(actor.getSpawnedLoc(), 600))
            return false;

        if (!canAttackCharacter(target))
            return false;
        if (!GeoEngine.canSeeTarget(actor, target, false))
            return false;

        if (!avoidAttack) {
            actor.getAggroList().addDamageHate(target, 0, 2);

            if (target instanceof Summon)
                actor.getAggroList().addDamageHate(((Summon)target).owner, 0, 1);

            startRunningTask(AI_TASK_ATTACK_DELAY);
            setIntentionAttack(target);
        }

        return true;
    }

    @Override
    public boolean isGlobalAggro() {
        return true;
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
        SiegeGuardInstance actor = getActor();
        if (actor.isDead())
            return;
        if (target == null || !actor.isAutoAttackable(target))
            return;
        super.onEvtAggression(target, aggro);
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

            if (World.getAroundPlayables(actor)
                    .filter(cha -> checkAggression(cha, true))
                    .filter(cha -> !cha.isDead())
                    .anyMatch(cha -> checkAggression(cha, false)))
                return true;
        }

        Location sloc = actor.getSpawnedLoc();
        // Проверка на расстояние до точки спауна
        if (!actor.isInRange(sloc, 250)) {
            teleportHome();
            return true;
        }

        return false;
    }

    @Override
    public Creature prepareTarget() {
        SiegeGuardInstance actor = getActor();
        if (actor.isDead())
            return null;

        // Новая цель исходя из агрессивности
        List<Playable> hateList = actor.getAggroList().getHateList();
        Creature hated = null;
        for (Playable cha : hateList) {
            //Не подходит, очищаем хейт
            if (!checkTarget(cha, MAX_PURSUE_RANGE)) {
                actor.getAggroList().remove(cha, true);
                continue;
            }
            hated = cha;
            break;
        }

        if (hated != null) {
            setAttackTarget(hated);
            return hated;
        }

        return null;
    }

    @Override
    public boolean canAttackCharacter(Creature target) {
        return getActor().isAutoAttackable(target);
    }
}