package l2trunk.scripts.npc.model;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;


public final class MushroomInstance extends MonsterInstance {
    private static final int FANTASY_MUSHROOM = 18864;
    private static final int FANTASY_MUSHROOM_SKILL = 6427;

    private static final int RAINBOW_FROG = 18866;
    private static final int RAINBOW_FROG_SKILL = 6429;

    private static final int STICKY_MUSHROOM = 18865;
    private static final int STICKY_MUSHROOM_SKILL = 6428;

    private static final int ENERGY_PLANT = 18868;
    private static final int ENERGY_PLANT_SKILL = 6430;

    private static final int ABYSS_WEED = 18867;

    public MushroomInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean canChampion() {
        return false;
    }

    @Override
    public void reduceCurrentHp(double i, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
        if (isDead())
            return;

        // Даже если убил моба саммон, то эффекты грибов идут хозяину.
        Creature killer = attacker;
        if (killer instanceof Summon)
            killer = ((Summon)killer).owner;

        if (getNpcId() == RAINBOW_FROG) // Этот моб баффает баффом.
        {
            ThreadPoolManager.INSTANCE.schedule(new TaskAfterDead(this, killer, RAINBOW_FROG_SKILL), 3000);
            doDie(killer);
        } else if (getNpcId() == STICKY_MUSHROOM) // Этот моб лечит и с шансом 40% кидает корни.
        {
            ThreadPoolManager.INSTANCE.schedule(new TaskAfterDead(this, killer, STICKY_MUSHROOM_SKILL), 3000);
            doDie(killer);
        } else if (getNpcId() == ENERGY_PLANT) // Этот моб лечит.
        {
            ThreadPoolManager.INSTANCE.schedule(new TaskAfterDead(this, killer, ENERGY_PLANT_SKILL), 3000);
            doDie(killer);
        } else if (getNpcId() == ABYSS_WEED) // TODO: Неизвестно, что он делает.
        {
            doDie(killer);
        } else if (getNpcId() == FANTASY_MUSHROOM) {// Этот моб сзывает всех мобов в окружности и станит их.
            getAroundNpc(700, 300)
                    .filter(o -> o instanceof MonsterInstance)
                    .filter(npc -> npc.getNpcId() >= 22768)
                    .filter(npc -> npc.getNpcId() <= 22774)
                    .forEach(npc -> {
                        npc.setRunning();
                        npc.moveToLocation(Location.findPointToStay(this, 20, 50), 0, true);
                    });
            ThreadPoolManager.INSTANCE.schedule(new TaskAfterDead(this, killer, FANTASY_MUSHROOM_SKILL), 4000);
        }
    }

    public static class TaskAfterDead extends RunnableImpl {
        private final NpcInstance actor;
        private final Creature killer;
        private final Skill skill;

        TaskAfterDead(NpcInstance actor, Creature killer, int skillId) {
            this.actor = actor;
            this.killer = killer;
            skill = SkillTable.INSTANCE.getInfo(skillId);
        }

        @Override
        public void runImpl() {
            if (skill == null)
                return;

            if (actor != null && actor.getNpcId() == FANTASY_MUSHROOM) {
                actor.broadcastPacket(new MagicSkillUse(actor, skill.id, skill.level));
                actor.getAroundNpc(200, 300)
                        .filter(o -> o instanceof MonsterInstance)
                        .filter(npc -> npc.getNpcId() >= 22768)
                        .filter(npc -> npc.getNpcId() <= 22774)
                        .forEach(skill::getEffects);
                actor.doDie(killer);
                return;
            }

            if (killer != null && killer instanceof Player && !killer.isDead()) {
                List<Creature> targets = new ArrayList<>();
                targets.add(killer);
                killer.broadcastPacket(new MagicSkillUse(killer, skill));
                skill.useSkill(killer, targets);
            }
        }
    }
}