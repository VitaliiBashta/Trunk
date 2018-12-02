package l2trunk.scripts.ai.residences;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.npc.model.residences.SiegeGuardInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SiegeGuard extends Fighter {
    SiegeGuard(NpcInstance actor) {
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
    public boolean checkAggression(Creature target, boolean avoidAttack) {
        NpcInstance actor = getActor();
        if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro())
            return false;
        if (target.isAlikeDead() || target.isInvul())
            return false;

        if (target.isPlayable()) {
            if (!canSeeInSilentMove((Playable) target))
                return false;
            if (!canSeeInHide((Playable) target))
                return false;
            if (target.isPlayer() && ((Player) target).isGM() && target.isInvisible())
                return false;
            if (target.isPlayer() && !target.getPlayer().isActive())
                return false;
            if (actor.isMonster() && target.isInZonePeace())
                return false;
        }

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

            if ((target.isSummon() || target.isPet()))
                actor.getAggroList().addDamageHate(target.getPlayer(), 0, 1);

            startRunningTask(AI_TASK_ATTACK_DELAY);
            setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
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

        if (_def_think) {
            if (doTask())
                clearTasks();
            return true;
        }

        long now = System.currentTimeMillis();
        if (now - _checkAggroTimestamp > Config.AGGRO_CHECK_INTERVAL) {
            _checkAggroTimestamp = now;

            final List<Creature> knowns = World.getAroundCharacters(actor);
            final List<Creature> aggroList = new ArrayList<>();

            for (Creature cha : knowns) {
                if (checkAggression(cha, true))
                    aggroList.add(cha);
            }

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
        List<Creature> hateList = actor.getAggroList().getHateList();
        Creature hated = null;
        for (Creature cha : hateList) {
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