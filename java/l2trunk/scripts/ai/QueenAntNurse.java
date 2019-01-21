package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Priest;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.MinionInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.npc.model.QueenAntInstance;

public final class QueenAntNurse extends Priest {
    public QueenAntNurse(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = 10000;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return true;

        if (defThink) {
            if (doTask())
                clearTasks();
            return true;
        }

        Creature top_desire_target = getTopDesireTarget();
        if (top_desire_target == null)
            return false;

        if (actor.getDistance(top_desire_target) - top_desire_target.getColRadius() - actor.getColRadius() > 200) {
            moveOrTeleportToLocation(Location.findFrontPosition(top_desire_target, actor, 100, 150));
            return false;
        }

        if (!top_desire_target.isCurrentHpFull() && doTask())
            return createNewTask();

        return false;
    }

    @Override
    public boolean createNewTask() {
        clearTasks();
        NpcInstance actor = getActor();
        Creature top_desire_target = getTopDesireTarget();
        if (actor.isDead() || top_desire_target == null)
            return false;

        if (!top_desire_target.isCurrentHpFull()) {
            Skill skill = Rnd.get(healSkills);
            if (skill.getAOECastRange() < actor.getDistance(top_desire_target))
                moveOrTeleportToLocation(Location.findFrontPosition(top_desire_target, actor, skill.getAOECastRange() - 30, skill.getAOECastRange() - 10));
            addTaskBuff(top_desire_target, skill);
            return true;
        }

        return false;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    private void moveOrTeleportToLocation(Location loc) {
        NpcInstance actor = getActor();
        actor.setRunning();
        if (actor.moveToLocation(loc, 0, true))
            return;
        clientStopMoving();
        _pathfindFails = 0;
        actor.broadcastPacketToOthers(new MagicSkillUse(actor,  2036,  500, 600000));
        ThreadPoolManager.INSTANCE.schedule(new Teleport(loc), 500);
    }

    private Creature getTopDesireTarget() {
        NpcInstance actor = getActor();
        QueenAntInstance queen_ant = (QueenAntInstance) ((MinionInstance) actor).getLeader();
        if (queen_ant == null) {
            return null;
        }
        if (queen_ant.isDead()) {
            return null;
        }
        Creature Larva = queen_ant.getLarva();
        if (Larva != null && Larva.getCurrentHpPercents() < 5)
            return Larva;
        return queen_ant;
    }

    @Override
    public void onIntentionAttack(Creature target) {
    }

    @Override
    public void onEvtClanAttacked(Creature attacked_member, Creature attacker, int damage) {
        if (doTask())
            createNewTask();
    }
}