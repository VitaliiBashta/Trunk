package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.scripts.bosses.BaiumManager;

import java.util.HashMap;
import java.util.Map;

public final class Baium extends DefaultAI {
    private boolean _firstTimeAttacked = true;

    // Боевые скилы байума
    private static final int baium_normal_attack = 4127;
    private static final int energy_wave = 4128;
    private static final int earth_quake = 4129;
    private static final int thunderbolt = 4130;
    private static final int group_hold = 4131;

    public Baium(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        BaiumManager.setLastAttackTime();

        if (_firstTimeAttacked) {
            _firstTimeAttacked = false;
            NpcInstance actor = getActor();
            if (attacker == null)
                return;
            if (attacker.isPlayer() && attacker.getPet() != null)
                attacker.getPet().doDie(actor);
            else if ((attacker.isSummon() || attacker.isPet()) && attacker.getPlayer() != null)
                attacker.getPlayer().doDie(actor);
            attacker.doDie(actor);
        }

        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public boolean createNewTask() {
        NpcInstance actor = getActor();
        if (actor == null)
            return true;

        if (!BaiumManager.getZone().checkIfInZone(actor)) {
            teleportHome();
            return false;
        }

        clearTasks();

        Creature target;
        if ((target = prepareTarget()) == null)
            return false;

        if (!BaiumManager.getZone().checkIfInZone(target)) {
            actor.getAggroList().remove(target, false);
            return false;
        }

        // Шансы использования скилов
        int s_energy_wave = 20;
        int s_earth_quake = 20;
        int s_group_hold = actor.getCurrentHpPercents() > 50 ? 0 : 20;
        int s_thunderbolt = actor.getCurrentHpPercents() > 25 ? 0 : 20;

        int r_skill = 0;

        if (actor.isMovementDisabled()) // Если в руте, то использовать массовый скилл дальнего боя
            r_skill = thunderbolt;
        else if (!Rnd.chance(100 - s_thunderbolt - s_group_hold - s_energy_wave - s_earth_quake)) // Выбираем скилл атаки
        {
            Map<Skill, Integer> d_skill = new HashMap<>(); //TODO class field ?
            double distance = actor.getDistance(target);

            addDesiredSkill(d_skill, target, distance, energy_wave);
            addDesiredSkill(d_skill, target, distance, earth_quake);
            if (s_group_hold > 0)
                addDesiredSkill(d_skill, target, distance, group_hold);
            if (s_thunderbolt > 0)
                addDesiredSkill(d_skill, target, distance, thunderbolt);
            r_skill = selectTopSkill(d_skill);
        }

        // Использовать скилл если можно, иначе атаковать скилом baium_normal_attack
        if (r_skill == 0)
            r_skill = baium_normal_attack;
        else if (SkillTable.INSTANCE.getInfo(r_skill).targetType == Skill.SkillTargetType.TARGET_SELF)
            target = actor;

        // Добавить новое задание
        addTaskCast(target, r_skill);
        return true;
    }

    @Override
    public boolean maybeMoveToHome() {
        NpcInstance actor = getActor();
        if (actor != null && !BaiumManager.getZone().checkIfInZone(actor))
            teleportHome();
        return false;
    }

    @Override
    public void onEvtDead(Creature killer) {
        _firstTimeAttacked = true;
        super.onEvtDead(killer);
    }
}