package l2trunk.scripts.events.TheFlowOfTheHorror;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncMul;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class GilmoreAI extends Fighter {
    private static final List<Location> points_stage1 = List.of(
            Location.of(73195, 118483, -3722),
            Location.of(73535, 117945, -3754),
            Location.of(73446, 117334, -3752),
            Location.of(72847, 117311, -3711),
            Location.of(72296, 117720, -3694),
            Location.of(72463, 118401, -3694),
            Location.of(72912, 117895, -3723));

    private static final Location points_stage2 = Location.of(73615, 117629, -3765);

    private static final List<String> text_stage1 = List.of("Text1", "Text2", "Text3", "Text4", "Text5", "Text6", "Text7");

    private static final List<String> text_stage2 = List.of("Are you ready? ", " Let's begin, there is not a moment to lose!");

    private long wait_timeout = 0;
    private boolean wait = false;
    private int step_stage2 = 1;

    public GilmoreAI(NpcInstance actor) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 250;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null || actor.isDead())
            return true;

        if (defThink) {
            doTask();
            return true;
        }

        if (System.currentTimeMillis() > wait_timeout) {
            if (!wait)
                switch (TheFlowOfTheHorror.getStage()) {
                    case 1:
                        if (Rnd.chance(30)) {
                            Functions.npcSay(actor, Rnd.get(text_stage1));
                            wait_timeout = System.currentTimeMillis() + 10000;
                            wait = true;
                            return true;
                        }
                        break;
                    case 2:
                        switch (step_stage2) {
                            case 1:
                                Functions.npcSay(actor, text_stage2.get(0));
                                wait_timeout = System.currentTimeMillis() + 10000;
                                wait = true;
                                return true;
                            case 2:
                                break;
                        }
                        break;
                }

            wait_timeout = 0;
            wait = false;

            actor.setRunning();

            switch (TheFlowOfTheHorror.getStage()) {
                case 1:
                    addTaskMove(Rnd.get(points_stage1), true);
                    doTask();
                    return true;
                case 2:
                    switch (step_stage2) {
                        case 1:
                            Functions.npcSay(actor, text_stage2.get(1));
                            addTaskMove(points_stage2, true);
                            doTask();
                            step_stage2 = 2;
                            return true;
                        case 2:
                            actor.setHeading(0);
                            actor.stopMove();
                            actor.broadcastPacketToOthers(new MagicSkillUse(actor, 454, 3000));
                            step_stage2 = 3;
                            return true;
                        case 3:
                            actor.addStatFunc(new FuncMul(Stats.MAGIC_ATTACK_SPEED, 0x40, actor, 5));
                            actor.addStatFunc(new FuncMul(Stats.MAGIC_DAMAGE, 0x40, actor, 10));
                            actor.addStatFunc(new FuncMul(Stats.PHYSICAL_DAMAGE, 0x40, actor, 10));
                            actor.addStatFunc(new FuncMul(Stats.RUN_SPEED, 0x40, actor, 3));
                            actor.addSkill(1467);
                            actor.sendChanges();
                            step_stage2 = 4;
                            return true;
                        case 4:
                            setIntention(CtrlIntention.AI_INTENTION_ATTACK);
                            return true;
                        case 10:
                            actor.removeStatsOwner(this);
                            step_stage2 = 11;
                            return true;
                    }
            }
        }

        return false;
    }

    @Override
    public boolean createNewTask() {
        clearTasks();
        NpcInstance actor = getActor();
        if (actor == null)
            return true;

        World.getAroundNpc(actor, 1000, 200)
                .filter(npc -> npc.getNpcId() == 20235)
                .filter(npc -> Rnd.chance(10))
                .map(npc -> (MonsterInstance) npc)
                .findFirst().ifPresent(monster -> {
            if (Rnd.chance(20))
                addTaskCast(monster, 1467);
            else
                addTaskAttack(monster);
        });
        return true;
    }
}