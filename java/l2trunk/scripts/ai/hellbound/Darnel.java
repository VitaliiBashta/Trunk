package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.TrapInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;

import java.util.HashMap;
import java.util.Map;

public final class Darnel extends DefaultAI {
    private class TrapTask extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            if (actor.isDead())
                return;

            // Спавним 10 ловушек
            TrapInstance trap;
            for (int i = 0; i < 10; i++) {
                trap = new TrapInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(13037), actor, trapSkills[Rnd.get(trapSkills.length)], new Location(Rnd.get(151896, 153608), Rnd.get(145032, 146808), -12584));
                trap.spawnMe();
            }
        }
    }

    private final Skill[] trapSkills = new Skill[]{
            SkillTable.getInstance().getInfo(5267, 1),
            SkillTable.getInstance().getInfo(5268, 1),
            SkillTable.getInstance().getInfo(5269, 1),
            SkillTable.getInstance().getInfo(5270, 1)};

    private final Skill Poison;
    private final Skill Paralysis;

    public Darnel(NpcInstance actor) {
        super(actor);

        Map<Integer,Skill> skills = getActor().getTemplate().getSkills();

        Poison = skills.get(4182);
        Paralysis = skills.get(4189);
    }

    @Override
    protected boolean createNewTask() {
        clearTasks();
        Creature target;
        if ((target = prepareTarget()) == null)
            return false;

        NpcInstance actor = getActor();
        if (actor.isDead())
            return false;

        int rnd_per = Rnd.get(100);

        if (rnd_per < 5) {
            actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 5440, 1, 3000, 0));
            ThreadPoolManager.getInstance().schedule(new TrapTask(), 3000);
            return true;
        }

        double distance = actor.getDistance(target);

        if (!actor.isAMuted() && rnd_per < 75)
            return chooseTaskAndTargets(null, target, distance);

        Map<Skill, Integer> d_skill = new HashMap<>();

        addDesiredSkill(d_skill, target, distance, Poison);
        addDesiredSkill(d_skill, target, distance, Paralysis);

        Skill r_skill = selectTopSkill(d_skill);

        return chooseTaskAndTargets(r_skill, target, distance);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}