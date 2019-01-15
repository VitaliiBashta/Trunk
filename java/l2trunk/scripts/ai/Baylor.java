package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.instancemanager.HellboundManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;

import java.util.HashMap;
import java.util.Map;

public final class Baylor extends DefaultAI {
    private static final int GroundStrike = 5227; // Массовая атака, 2500 каст
    private static final int Water_Dragon_Claw = 2360;
    private static final int PresentationBalor2 = 5402; // Прыжок, удар по земле
    private static final int Berserk = 5224; // Increases P. Atk. and P. Def.
    private static final int JumpAttack = 5228; // Массовая атака, 2500 каст
    private static final int StrongPunch = 5229; // Откидывает одиночную цель кулаком, и оглушает, рейндж 600
    private static final int Stun1 = 5230; // Массовое оглушение, 5000 каст
    private static final int Stun2 = 5231; // Массовое оглушение, 3000 каст
    private static final int Stun3 = 5232; // Массовое оглушение, 2000 каст
    //final L2Skill Stun4; // Не работает?
    private final int Invincible = 5225; // Неуязвимость при 30% hp
    private boolean _isUsedInvincible = false;

    private int _claw_count = 0;
    private long _last_claw_time = 0;

    public Baylor(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        ThreadPoolManager.INSTANCE.schedule(() -> getActor().broadcastPacketToOthers(new MagicSkillUse(actor, PresentationBalor2, 4000)), 20000);
        super.onEvtSpawn();
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature caster) {
        NpcInstance actor = getActor();
        if (actor.isDead() || skill == null || caster == null)
            return;

        if (System.currentTimeMillis() - _last_claw_time > 5000)
            _claw_count = 0;

        if (skill.getId() == Water_Dragon_Claw) {
            _claw_count++;
            _last_claw_time = System.currentTimeMillis();
        }

        Player player = caster.getPlayer();
        if (player == null)
            return;

        int count = 1;
        Party party = player.getParty();
        if (party != null)
            count = party.size();

        // Снимаем неуязвимость
        if (_claw_count >= count) {
            _claw_count = 0;
            actor.getEffectList().stopEffect(Invincible);
            Functions.npcSay(actor, NpcString.NO_ONE_IS_GOING_TO_SURVIVE);
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

        if (actor_hp_precent < 30 && !_isUsedInvincible) {
            _isUsedInvincible = true;
            addTaskBuff(actor, Invincible);
            Functions.npcSay(actor, NpcString.DEMON_KING_BELETH_GIVE_ME_THE_POWER_AAAHH);
            return true;
        }

        int rnd_per = Rnd.get(100);
        if (rnd_per < 7 && actor.getEffectList().getEffectsBySkillId(Berserk) == null) {
            addTaskBuff(actor, Berserk);
            Functions.npcSay(actor, NpcString.DEMON_KING_BELETH_GIVE_ME_THE_POWER_AAAHH);
            return true;
        }

        if (rnd_per < 15 || rnd_per < 33 && actor.getEffectList().getEffectsBySkillId(Berserk) != null)
            return chooseTaskAndTargets(StrongPunch, target, distance);

        //if(rnd_per < 5 && target.getEffectList().getEffectsBySkill(Imprison) == null)
        //{
        //	_isUsedInvincible = true;
        //	addTaskCast(target, Imprison);
        //	return true;
        //}

        if (!actor.isAMuted() && rnd_per < 50)
            return chooseTaskAndTargets(null, target, distance);

        Map<Skill, Integer> skills = new HashMap<>();

        addDesiredSkill(skills, target, distance, GroundStrike);
        addDesiredSkill(skills, target, distance, JumpAttack);
        addDesiredSkill(skills, target, distance, StrongPunch);
        addDesiredSkill(skills, target, distance, Stun1);
        addDesiredSkill(skills, target, distance, Stun2);
        addDesiredSkill(skills, target, distance, Stun3);

        int skill = selectTopSkill(skills);
        if (skill != 0 && !SkillTable.INSTANCE.getInfo(skill).isOffensive())
            target = actor;

        return chooseTaskAndTargets(skill, target, distance);
    }

    @Override
    public boolean maybeMoveToHome() {
        return false;
    }

    //Hellbound opening hook
    @Override
    public void onEvtDead(Creature killer) {
        if (HellboundManager.getConfidence() < 1)
            HellboundManager.setConfidence(1);

        super.onEvtDead(killer);
    }

}