package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public final class Tears extends DefaultAI {
    private static final int Water_Dragon_Scale = 2369;
    private static final int Tears_Copy = 25535;
    private final int Invincible = 5420;
    private final int Freezing = 5238;
    private final List<NpcInstance> spawns = new ArrayList<>();
    private ScheduledFuture<?> spawnTask;
    private ScheduledFuture<?> despawnTask;
    private boolean _isUsedInvincible = false;
    private int _scale_count = 0;
    private long _last_scale_time = 0;

    public Tears(NpcInstance actor) {
        super(actor);

//        Map<Integer, Skill> skills = getActor().getTemplate().getSkills();
//
//        Invincible = skills.get(5420);
//        Freezing = skills.get(5238);
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature caster) {
        NpcInstance actor = getActor();
        if (actor.isDead() || skill == null || caster == null)
            return;

        if (System.currentTimeMillis() - _last_scale_time > 5000)
            _scale_count = 0;

        if (skill.id == Water_Dragon_Scale) {
            _scale_count++;
            _last_scale_time = System.currentTimeMillis();
        }

        Player player = caster.getPlayer();
        if (player == null)
            return;

        int count = 1;
        Party party = player.getParty();
        if (party != null)
            count = party.size();

        // Снимаем неуязвимость
        if (_scale_count >= count) {
            _scale_count = 0;
            actor.getEffectList().stopEffect(Invincible);
        }
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

        double distance = actor.getDistance(target);
        double actor_hp_precent = actor.getCurrentHpPercents();
        int rnd_per = Rnd.get(100);

        if (actor_hp_precent < 15 && !_isUsedInvincible) {
            _isUsedInvincible = true;
            addTaskBuff(actor, Invincible);
            Functions.npcSay(actor, "Prepare for death!");
            return true;
        }

        if (rnd_per < 5 && spawnTask == null && despawnTask == null) {
            actor.broadcastPacketToOthers(new MagicSkillUse(actor,  5441,  3000));
            spawnTask = ThreadPoolManager.INSTANCE.schedule(this::spawnMobs, 3000);
            return true;
        }

        if (!actor.isAMuted() && rnd_per < 75)
            return chooseTaskAndTargets(null, target, distance);

        return chooseTaskAndTargets(Freezing, target, distance);
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    private void spawnMobs() {
        NpcInstance actor = getActor();

        Location pos;
        Creature hated;

        // Спавним 9 копий
        for (int i = 0; i < 9; i++) {
            pos = Location.findPointToStay(new Location(144298, 154420, -11854), 300, 320, actor.getGeoIndex());
            SimpleSpawner sp = (SimpleSpawner) new SimpleSpawner(Tears_Copy)
                    .setLoc(pos)
                    .setReflection(actor.getReflection());
            NpcInstance copy = sp.doSpawn(true);
            spawns.add(copy);

            // Атакуем случайную цель
            hated = actor.getAggroList().getRandomHated();
            if (hated != null)
                copy.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, hated, Rnd.get(1, 100));
        }

        // Прячемся среди них
        pos = Location.findPointToStay(new Location(144298, 154420, -11854), 300, 320, actor.getReflectionId());
        actor.teleToLocation(pos);

        // Атакуем случайную цель
        hated = actor.getAggroList().getRandomHated();
        if (hated != null)
            actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, hated, Rnd.get(1, 100));

        if (despawnTask != null)
            despawnTask.cancel(false);
        despawnTask = ThreadPoolManager.INSTANCE.schedule(this::despawnMobs, 30000);
        spawnTask = null;
    }

    private void despawnMobs() {
        spawns.forEach(GameObject::deleteMe);
        spawns.clear();
        despawnTask = null;
    }

}