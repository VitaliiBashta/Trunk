package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.scripts.bosses.ValakasManager;

import java.util.HashMap;
import java.util.Map;

public final class Valakas extends Fighter {
    // Self skills
    private static final int s_lava_skin = 4680;
    private static final int s_fear = 4689;
    private static final int s_defence_down = 5864;
    private static final int s_berserk = 5865;
    // Offensive damage skills
    private static final int s_tremple_left = 4681;
    private static final int s_tremple_right = 4682;
    private static final int s_tail_stomp_a = 4685;
    private static final int s_tail_lash = 4688;
    private static final int s_meteor = 4690;
    private static final int s_breath_low = 4683;
    private static final int s_breath_high = 4684;
    // Offensive percentage skills
    private static final int s_destroy_body = 5860;
    private static final int s_destroy_soul = 5861;
    private static final int s_destroy_body2 = 5862;
    private static final int s_destroy_soul2 = 5863;
    final int s_regen = 4691;
    // Timer reuses
    private final long defenceDownReuse = 120000L;
    // Timers
    private long defenceDownTimer = Long.MAX_VALUE;
    // Vars
    private double _rangedAttacksIndex, _counterAttackIndex, _attacksIndex;
    private int _hpStage = 0;
    private int DAMAGE_COUNTER = 0;

    public Valakas(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {

        NpcInstance actor = getActor();
        if (DAMAGE_COUNTER == 0)
            actor.getAI().startAITask();

        ValakasManager.setLastAttackTime();
        ValakasManager.getZone().getInsidePlayables()
                .forEach(p -> notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 1));

        if (damage > 100) {
            if (attacker.getDistance(actor) > 400)
                _rangedAttacksIndex += damage / 1000D;
            else
                _counterAttackIndex += damage / 1000D;
        }
        _attacksIndex += damage / 1000D;
        DAMAGE_COUNTER += damage;

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

        double distance = actor.getDistance(target);

        // Buffs and stats
        double chp = actor.getCurrentHpPercents();
        if (_hpStage == 0) {
            actor.altOnMagicUseTimer(actor, 4691);
            _hpStage = 1;
        } else if (chp < 80 && _hpStage == 1) {
            actor.altOnMagicUseTimer(actor, 4691, 2);
            defenceDownTimer = System.currentTimeMillis();
            _hpStage = 2;
        } else if (chp < 50 && _hpStage == 2) {
            actor.altOnMagicUseTimer(actor, 4691, 3);
            _hpStage = 3;
        } else if (chp < 30 && _hpStage == 3) {
            actor.altOnMagicUseTimer(actor, 4691, 4);
            _hpStage = 4;
        } else if (chp < 10 && _hpStage == 4) {
            actor.altOnMagicUseTimer(actor, 4691, 5);
            _hpStage = 5;
        }


        // Tactical Movements
        if (_counterAttackIndex > 2000) {
            ValakasManager.broadcastScreenMessage(NpcString.VALAKAS_HEIGHTENED_BY_COUNTERATTACKS);
            _counterAttackIndex = 0;
            return chooseTaskAndTargets(s_berserk, actor, 0);
        } else if (_rangedAttacksIndex > 2000) {
            if (Rnd.chance(60)) {
                Creature randomHated = actor.getAggroList().getRandomHated();
                if (randomHated != null) {
                    setAttackTarget(randomHated);
                    actor.startConfused();
                    ThreadPoolManager.INSTANCE.schedule(() -> {
                        getActor().stopConfused();
                        madnessTask = null;
                    }, 20000L);
                }
                ValakasManager.broadcastScreenMessage(NpcString.VALAKAS_RANGED_ATTACKS_ENRAGED_TARGET_FREE);
                _rangedAttacksIndex = 0;
            } else {
                ValakasManager.broadcastScreenMessage(NpcString.VALAKAS_RANGED_ATTACKS_PROVOKED);
                _rangedAttacksIndex = 0;
                return chooseTaskAndTargets(s_berserk, actor, 0);
            }
        } else if (_attacksIndex > 3000) {
            ValakasManager.broadcastScreenMessage(NpcString.VALAKAS_PDEF_ISM_DECREACED_SLICED_DASH);
            _attacksIndex = 0;
            return chooseTaskAndTargets(s_defence_down, actor, 0);
        } else if (defenceDownTimer < System.currentTimeMillis()) {
            ValakasManager.broadcastScreenMessage(NpcString.VALAKAS_FINDS_YOU_ATTACKS_ANNOYING_SILENCE);
            defenceDownTimer = System.currentTimeMillis() + defenceDownReuse + Rnd.get(60) * 1000L;
            return chooseTaskAndTargets(s_fear, target, distance);
        }

        // Basic Attack
        if (Rnd.chance(50))
            return chooseTaskAndTargets(Rnd.chance(50) ? s_tremple_left : s_tremple_right, target, distance);

        // Stage based skill attacks
        Map<Skill, Integer> d_skill = new HashMap<>();
        switch (_hpStage) {
            case 1:
                addDesiredSkill(d_skill, target, distance, s_breath_low);
                addDesiredSkill(d_skill, target, distance, s_tail_stomp_a);
                addDesiredSkill(d_skill, target, distance, s_meteor);
                addDesiredSkill(d_skill, target, distance, s_fear);
                break;
            case 2:
            case 3:
                addDesiredSkill(d_skill, target, distance, s_breath_low);
                addDesiredSkill(d_skill, target, distance, s_tail_stomp_a);
                addDesiredSkill(d_skill, target, distance, s_breath_high);
                addDesiredSkill(d_skill, target, distance, s_tail_lash);
                addDesiredSkill(d_skill, target, distance, s_destroy_body);
                addDesiredSkill(d_skill, target, distance, s_destroy_soul);
                addDesiredSkill(d_skill, target, distance, s_meteor);
                addDesiredSkill(d_skill, target, distance, s_fear);
                break;
            case 4:
            case 5:
                addDesiredSkill(d_skill, target, distance, s_breath_low);
                addDesiredSkill(d_skill, target, distance, s_tail_stomp_a);
                addDesiredSkill(d_skill, target, distance, s_breath_high);
                addDesiredSkill(d_skill, target, distance, s_tail_lash);
                addDesiredSkill(d_skill, target, distance, s_destroy_body);
                addDesiredSkill(d_skill, target, distance, s_destroy_soul);
                addDesiredSkill(d_skill, target, distance, s_meteor);
                addDesiredSkill(d_skill, target, distance, s_fear);
                addDesiredSkill(d_skill, target, distance, Rnd.chance(60) ? s_destroy_soul2 : s_destroy_body);
                break;
        }

        int r_skill = selectTopSkill(d_skill);
        if (r_skill != 0 && !SkillTable.INSTANCE.getInfo(r_skill).isOffensive)
            target = actor;

        return chooseTaskAndTargets(r_skill, target, distance);
    }

    @Override
    public void thinkAttack() {
        NpcInstance actor = getActor();
        // Lava buff
        if (actor.isInZone(Zone.ZoneType.poison))
            if (actor.getEffectList() != null && actor.getEffectList().getEffectsBySkillId(s_lava_skin) == null)
                actor.altOnMagicUseTimer(actor, s_lava_skin);
        super.thinkAttack();
    }

}