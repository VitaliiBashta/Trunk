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
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.instances.CrystalCaverns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Darnel extends DefaultAI {
    private static final int Poison = 4182;
    private static final int Paralysis = 4189;
    private final List<Integer> trapSkills = List.of(5267, 5268, 5269, 5270);

    public Darnel(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean createNewTask() {
        clearTasks();
        Creature target;
        if ((target = prepareTarget()) == null)
            return false;

        NpcInstance actor = getActor();
        if (actor.isDead())
            return false;

        int rnd_per = Rnd.get(100);

        if (rnd_per < 5) {
            actor.broadcastPacketToOthers(new MagicSkillUse(actor, 5440, 3000));
            ThreadPoolManager.INSTANCE.schedule(new TrapTask(), 3000);
            return true;
        }

        double distance = actor.getDistance(target);

        if (!actor.isAMuted() && rnd_per < 75)
            return chooseTaskAndTargets(null, target, distance);

        Map<Skill, Integer> d_skill = new HashMap<>();

        addDesiredSkill(d_skill, target, distance, Poison, 10);
        addDesiredSkill(d_skill, target, distance, Paralysis, 10);

        int r_skill = selectTopSkill(d_skill);

        return chooseTaskAndTargets(r_skill, target, distance);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        CrystalCaverns refl = null;
        if (actor.getReflection() instanceof CrystalCaverns)
            refl = (CrystalCaverns) actor.getReflection();
        if (refl != null)
            refl.notifyDarnelAttacked();
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        CrystalCaverns refl = null;
        if (actor.getReflection() instanceof CrystalCaverns)
            refl = (CrystalCaverns) actor.getReflection();
        if (refl != null)
            refl.notifyDarnelDead(getActor());
        super.onEvtDead(killer);
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    private class TrapTask extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            if (actor.isDead())
                return;

            // Спавним 10 ловушек
            TrapInstance trap;
            for (int i = 0; i < 10; i++) {
                trap = new TrapInstance(IdFactory.getInstance().getNextId(),
                        NpcHolder.getTemplate(13037), actor, Rnd.get(trapSkills), new Location(Rnd.get(151896, 153608), Rnd.get(145032, 146808), -12584));
                trap.spawnMe();
            }
        }
    }
}