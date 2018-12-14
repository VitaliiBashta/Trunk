package l2trunk.scripts.npc.model;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
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
        if (killer.isPet() || killer.isSummon())
            killer = killer.getPlayer();

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
        } else if (getNpcId() == FANTASY_MUSHROOM) // Этот моб сзывает всех мобов в окружности и станит их.
        {
            List<NpcInstance> around = getAroundNpc(700, 300);
            if (around != null && !around.isEmpty())
                for (NpcInstance npc : around)
                    if (npc.isMonster() && npc.getNpcId() >= 22768 && npc.getNpcId() <= 22774) {
                        npc.setRunning();
                        npc.moveToLocation(Location.findPointToStay(this, 20, 50), 0, true);
                    }
            ThreadPoolManager.INSTANCE.schedule(new TaskAfterDead(this, killer, FANTASY_MUSHROOM_SKILL), 4000);
        }
    }

    public static class TaskAfterDead extends RunnableImpl {
        private final NpcInstance _actor;
        private final Creature _killer;
        private final Skill _skill;

        TaskAfterDead(NpcInstance actor, Creature killer, int skillId) {
            _actor = actor;
            _killer = killer;
            _skill = SkillTable.INSTANCE.getInfo(skillId);
        }

        @Override
        public void runImpl() {
            if (_skill == null)
                return;

            if (_actor != null && _actor.getNpcId() == FANTASY_MUSHROOM) {
                _actor.broadcastPacket(new MagicSkillUse(_actor, _skill.getId(), _skill.getLevel()));
                List<NpcInstance> around = _actor.getAroundNpc(200, 300);
                if (around != null && !around.isEmpty())
                    for (NpcInstance npc : around)
                        if (npc.isMonster() && npc.getNpcId() >= 22768 && npc.getNpcId() <= 22774)
                            _skill.getEffects(npc   );
                _actor.doDie(_killer);
                return;
            }

            if (_killer != null && _killer.isPlayer() && !_killer.isDead()) {
                List<Creature> targets = new ArrayList<>();
                targets.add(_killer);
                _killer.broadcastPacket(new MagicSkillUse(_killer, _skill));
                _skill.useSkill(_killer, targets);
            }
        }
    }
}