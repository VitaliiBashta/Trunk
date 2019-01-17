package l2trunk.scripts.ai.isle_of_prayer;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcLocation;
import l2trunk.scripts.instances.CrystalCaverns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Kechi extends DefaultAI {
    private static final int GUARD1 = 22309;
    private static final int GUARD2 = 22310;
    private static final int GUARD3 = 22417;
    private static final Location guard_spawn_loc = new Location(153384, 149528, -12136);
    private static final List<NpcLocation> guard_run = List.of(
            new NpcLocation(GUARD1, 153384, 149528, -12136),
            new NpcLocation(GUARD1, 153975, 149823, -12152),
            new NpcLocation(GUARD1, 154364, 149665, -12151),
            new NpcLocation(GUARD1, 153786, 149367, -12151),
            new NpcLocation(GUARD2, 154188, 149825, -12152),
            new NpcLocation(GUARD2, 153945, 149224, -12151),
            new NpcLocation(GUARD3, 154374, 149399, -12152),
            new NpcLocation(GUARD3, 153796, 149646, -12159));
    private final Skill KechiDoubleCutter; // Attack by crossing the sword. Power 2957.
    private final Skill KechiAirBlade; // Strikes the enemy a blow in a distance using sword energy. Critical enabled. Power 1812
    private final Skill Invincible; // Invincible against general attack and skill, buff/de-buff.
    private int stage = 0;

    public Kechi(NpcInstance actor) {
        super(actor);

        Map<Integer, Skill> skills = getActor().getTemplate().getSkills();

        KechiDoubleCutter = skills.get(733);
        KechiAirBlade = skills.get(734);

        Invincible = skills.get(5418);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        CrystalCaverns refl = null;
        if (actor.getReflection() instanceof CrystalCaverns)
            refl = (CrystalCaverns) actor.getReflection();
        if (refl != null)
            refl.notifyKechiAttacked();
        super.onEvtAttacked(attacker, damage);
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

        double actor_hp_precent = actor.getCurrentHpPercents();

        switch (stage) {
            case 0:
                if (actor_hp_precent < 80) {
                    spawnMobs();
                    return true;
                }
                break;
            case 1:
                if (actor_hp_precent < 60) {
                    spawnMobs();
                    return true;
                }
                break;
            case 2:
                if (actor_hp_precent < 40) {
                    spawnMobs();
                    return true;
                }
                break;
            case 3:
                if (actor_hp_precent < 30) {
                    spawnMobs();
                    return true;
                }
                break;
            case 4:
                if (actor_hp_precent < 20) {
                    spawnMobs();
                    return true;
                }
                break;
            case 5:
                if (actor_hp_precent < 10) {
                    spawnMobs();
                    return true;
                }
                break;
            case 6:
                if (actor_hp_precent < 5) {
                    spawnMobs();
                    return true;
                }
                break;
        }

        if (Rnd.chance(5)) {
            addTaskBuff(actor, Invincible);
            return true;
        }

        double distance = actor.getDistance(target);

        if (!actor.isAMuted() && Rnd.chance(75))
            return chooseTaskAndTargets(null, target, distance);

        Map<Skill, Integer> d_skill = new HashMap<>();

        addDesiredSkill(d_skill, target, distance, KechiDoubleCutter);
        addDesiredSkill(d_skill, target, distance, KechiAirBlade);

        int r_skill = selectTopSkill(d_skill);

        return chooseTaskAndTargets(r_skill, target, distance);
    }

    private void spawnMobs() {
        stage++;

        NpcInstance actor = getActor();
        guard_run.forEach(npcLoc -> {
            NpcInstance guard = actor.getReflection().addSpawnWithoutRespawn(npcLoc.npcId, guard_spawn_loc, 0);

            guard.setRunning();
            DefaultAI ai = (DefaultAI) guard.getAI();

            ai.addTaskMove(npcLoc, true);
            // Выбираем случайную цель
            Creature hated = actor.getAggroList().getRandomHated();
            if (hated != null)
                ai.notifyEvent(CtrlEvent.EVT_AGGRESSION, hated, 5000);
        });
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        CrystalCaverns refl = null;
        if (actor.getReflection() instanceof CrystalCaverns)
            refl = (CrystalCaverns) actor.getReflection();
        if (refl != null)
            refl.notifyKechiDead(getActor());
        super.onEvtDead(killer);
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}