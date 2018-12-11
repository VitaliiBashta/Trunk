package l2trunk.gameserver.model.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

public final class PetBabyInstance extends PetInstance {
    private static final Logger _log = LoggerFactory.getLogger(PetBabyInstance.class);
    // heal
    private static final int HealTrick = 4717;
    private static final int GreaterHealTrick = 4718;
    private static final int GreaterHeal = 5195;
    private static final int BattleHeal = 5590;
    private static final int Recharge = 5200;
    private static final Skill Pet_Haste = SkillTable.INSTANCE.getInfo(5186,2); 
    private static final Skill Pet_Vampiric_Rage = SkillTable.INSTANCE.getInfo(5187,4); 
    private static final int Pet_Regeneration = 5188; // 1-3
    private static final Skill Pet_Blessed_Body = SkillTable.INSTANCE.getInfo(5189,6); 
    private static final Skill Pet_Blessed_Soul = SkillTable.INSTANCE.getInfo(5190,6); 
    private static final Skill Pet_Guidance = SkillTable.INSTANCE.getInfo(5191,3); 
    private static final Skill Pet_Wind_Walk = SkillTable.INSTANCE.getInfo(5192,2); 
    private static final Skill Pet_Acumen = SkillTable.INSTANCE.getInfo(5193,3); 
    private static final Skill Pet_Empower = SkillTable.INSTANCE.getInfo(5194, 3); 
    private static final Skill Pet_Concentration = SkillTable.INSTANCE.getInfo(5201, 6);
    private static final Skill Pet_Might = SkillTable.INSTANCE.getInfo(5586, 3);
    private static final Skill Pet_Shield = SkillTable.INSTANCE.getInfo(5587,3); 
    private static final Skill Pet_Focus = SkillTable.INSTANCE.getInfo(5588,3);
    private static final Skill Pet_Death_Whisper = SkillTable.INSTANCE.getInfo(5589,3);
    private static final Skill Pet_Armor_Maintenance = SkillTable.INSTANCE.getInfo(5988);
    private static final Skill Pet_Weapon_Maintenance = SkillTable.INSTANCE.getInfo(5987); // 1
    private static final Skill Chant_of_Blood_Awakening = SkillTable.INSTANCE.getInfo(15191);
    private static final Skill Improved_Critical_Attack = SkillTable.INSTANCE.getInfo(1502);
    private static final Skill Improved_Combat = SkillTable.INSTANCE.getInfo(1499);
    private static final Skill Improved_Movement = SkillTable.INSTANCE.getInfo(1504);
    private static final Skill Improved_Condition = SkillTable.INSTANCE.getInfo(1501);
    private static final Skill Improved_Magic = SkillTable.INSTANCE.getInfo(1500);
    private static final Skill Armor_Maintenance = SkillTable.INSTANCE.getInfo(5988);
    private static final Skill Weapon_Maintenance = SkillTable.INSTANCE.getInfo(5987);
    private static final int WindShackle = 5196, Hex = 5197, Slow = 5198, CurseGloom = 5199;
    private static final Skill[][] TOY_KNIGHT_BUFFS = {
            {Pet_Focus, Pet_Death_Whisper},
            {
                    Pet_Focus,
                    Pet_Death_Whisper,
                    Pet_Shield,
                    Pet_Wind_Walk},
            {
                    Pet_Focus,
                    Pet_Death_Whisper,
                    Pet_Shield,
                    Pet_Wind_Walk,
                    Pet_Vampiric_Rage,
                    Pet_Haste},
            {
                    Pet_Focus,
                    Pet_Death_Whisper,
                    Pet_Shield,
                    Pet_Wind_Walk,
                    Pet_Vampiric_Rage,
                    Pet_Haste,
                    Pet_Might,
                    Pet_Blessed_Body}};
    private static final Skill[][] WHITE_WEASEL_BUFFS = {
            {Pet_Blessed_Body, Pet_Wind_Walk},
            {
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Death_Whisper,
                    Pet_Shield},
            {
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Death_Whisper,
                    Pet_Shield,
                    Pet_Vampiric_Rage,
                    Pet_Focus},
            {
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Death_Whisper,
                    Pet_Shield,
                    Pet_Vampiric_Rage,
                    Pet_Focus,
                    Pet_Haste}};
    //TODO: Array not offu. (Correct if there infa)
    private static final Skill[][] TURTLE_ASCETIC_BUFFS = {
            {Pet_Blessed_Body, Pet_Blessed_Soul},
            {
                    Pet_Blessed_Body,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Pet_Wind_Walk},
            {
                    Pet_Blessed_Body,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Pet_Wind_Walk,
                    Pet_Armor_Maintenance,
                    Pet_Weapon_Maintenance},
            {
                    Pet_Blessed_Body,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Pet_Wind_Walk,
                    Pet_Armor_Maintenance,
                    Pet_Weapon_Maintenance}};
    private static final Skill[][] COUGAR_BUFFS = {
            {Pet_Empower, Pet_Might},
            {
                    Pet_Empower,
                    Pet_Might,
                    Pet_Shield,
                    Pet_Blessed_Body},
            {
                    Pet_Empower,
                    Pet_Might,
                    Pet_Shield,
                    Pet_Blessed_Body,
                    Pet_Acumen,
                    Pet_Haste},
            {
                    Pet_Empower,
                    Pet_Might,
                    Pet_Shield,
                    Pet_Blessed_Body,
                    Pet_Acumen,
                    Pet_Haste,
                    Pet_Vampiric_Rage,
                    Pet_Focus}};
    private static final Skill[][] BUFFALO_BUFFS = {
            {Pet_Might, Pet_Blessed_Body},
            {
                    Pet_Might,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Guidance},
            {
                    Pet_Might,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Guidance,
                    Pet_Vampiric_Rage,
                    Pet_Haste},
            {
                    Pet_Might,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Guidance,
                    Pet_Vampiric_Rage,
                    Pet_Haste,
                    Pet_Focus,
                    Pet_Death_Whisper}};
    private static final Skill[][] KOOKABURRA_BUFFS = {
            {Pet_Empower, Pet_Blessed_Soul},
            {
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Blessed_Body,
                    Pet_Shield},
            {
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Acumen,
                    Pet_Concentration},
            {
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Acumen,
                    Pet_Concentration}};
    private static final Skill[][] FAIRY_PRINCESS_BUFFS = {
            {Pet_Empower, Pet_Blessed_Soul},
            {
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Blessed_Body,
                    Pet_Shield},
            {
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Acumen,
                    Pet_Concentration},
            {
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Acumen,
                    Pet_Concentration}};
    private static final Skill[][] ROSE_DESELOPH_BUFFS = {
            {
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus},
            {
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus,
                    Pet_Wind_Walk,
                    Pet_Blessed_Body,
                    Pet_Shield},
            {
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus,
                    Pet_Wind_Walk,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Death_Whisper,
                    Pet_Vampiric_Rage},
            {
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus,
                    Pet_Wind_Walk,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Death_Whisper,
                    Pet_Vampiric_Rage}};
    private static final Skill[][] ROSE_HYUM_BUFFS = {
            {Pet_Empower, Pet_Blessed_Soul},
            {
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Wind_Walk,
                    Pet_Shield},
            {
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Wind_Walk,
                    Pet_Shield,
                    Pet_Acumen},
            {
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Wind_Walk,
                    Pet_Shield,
                    Pet_Acumen}};
    private static final Skill[][] ROSE_REKANG_BUFFS = {
            {Pet_Blessed_Body, Pet_Wind_Walk},
            {
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Blessed_Soul,
                    Pet_Shield},
            {
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Armor_Maintenance,
                    Weapon_Maintenance},
            {
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Armor_Maintenance,
                    Weapon_Maintenance}};
    private static final Skill[][] ROSE_LILIAS_BUFFS = {
            {
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus},
            {
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus},
            {
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus,
                    Pet_Wind_Walk,
                    Pet_Blessed_Body,
                    Pet_Shield},
            {
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus,
                    Pet_Wind_Walk,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Death_Whisper,
                    Pet_Vampiric_Rage}};
    private static final Skill[][] ROSE_LAPHAM_BUFFS = {
            {Pet_Empower, Pet_Blessed_Soul},
            {
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Wind_Walk,
                    Pet_Shield},
            {
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Wind_Walk,
                    Pet_Shield,
                    Pet_Acumen},
            {
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Wind_Walk,
                    Pet_Shield,
                    Pet_Acumen}};
    private static final Skill[][] ROSE_MAPHUM_BUFFS = {
            {Pet_Blessed_Body, Pet_Wind_Walk},
            {
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Blessed_Soul,
                    Pet_Shield},
            {
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Armor_Maintenance,
                    Weapon_Maintenance},
            {
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Armor_Maintenance,
                    Weapon_Maintenance}};
    private static final Skill[][] IMPROVED_ROSE_DESELOPH_BUFFS = {
            {Improved_Condition, Improved_Movement},
            {
                    Improved_Condition,
                    Improved_Movement,
                    Improved_Combat,
                    Chant_of_Blood_Awakening},
            {
                    Improved_Condition,
                    Improved_Movement,
                    Improved_Combat,
                    Chant_of_Blood_Awakening,
                    Improved_Critical_Attack},
            {
                    Improved_Condition,
                    Improved_Movement,
                    Improved_Combat,
                    Chant_of_Blood_Awakening,
                    Improved_Critical_Attack}};
    private static final Skill[][] IMPROVED_ROSE_HYUM_BUFFS = {
            {Pet_Acumen, Improved_Condition},
            {
                    Pet_Acumen,
                    Improved_Condition,
                    Improved_Combat},
            {
                    Pet_Acumen,
                    Improved_Condition,
                    Improved_Combat,
                    Improved_Movement,},
            {
                    Pet_Acumen,
                    Improved_Condition,
                    Improved_Combat,
                    Improved_Movement,
                    Improved_Magic,}};
    private static final Skill[][] IMPROVED_ROSE_REKANG_BUFFS = {
            {Improved_Combat, Improved_Condition},
            {
                    Improved_Combat,
                    Improved_Condition,
                    Improved_Movement},
            {
                    Improved_Combat,
                    Improved_Condition,
                    Improved_Movement,
                    Armor_Maintenance},
            {
                    Improved_Combat,
                    Improved_Condition,
                    Improved_Movement,
                    Armor_Maintenance,
                    Weapon_Maintenance}};
    private static final Skill[][] IMPROVED_ROSE_LILIAS_BUFFS = {
            {Improved_Combat, Improved_Condition},
            {
                    Improved_Combat,
                    Improved_Condition,
                    Chant_of_Blood_Awakening},
            {
                    Improved_Combat,
                    Improved_Condition,
                    Chant_of_Blood_Awakening,
                    Improved_Movement},
            {
                    Improved_Combat,
                    Improved_Condition,
                    Chant_of_Blood_Awakening,
                    Improved_Movement,
                    Improved_Critical_Attack}};
    private static final Skill[][] IMPROVED_ROSE_LAPHAM_BUFFS = {
            {Pet_Acumen, Improved_Condition},
            {
                    Pet_Acumen,
                    Improved_Condition,
                    Improved_Combat},
            {
                    Pet_Acumen,
                    Improved_Condition,
                    Improved_Combat,
                    Improved_Movement},
            {
                    Pet_Acumen,
                    Improved_Condition,
                    Improved_Combat,
                    Improved_Movement,
                    Improved_Magic}};
    private static final Skill[][] IMPROVED_ROSE_MAPHUM_BUFFS = {
            {Improved_Combat, Improved_Condition},
            {
                    Improved_Combat,
                    Improved_Condition,
                    Improved_Movement},
            {
                    Improved_Combat,
                    Improved_Condition,
                    Improved_Movement,
                    Armor_Maintenance,
                    Weapon_Maintenance},
            {
                    Improved_Combat,
                    Improved_Condition,
                    Improved_Movement,
                    Armor_Maintenance,
                    Weapon_Maintenance}};
    private Future<?> _actionTask;
    private boolean _buffEnabled = true;

    public PetBabyInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control, int _currentLevel, long exp) {
        super(objectId, template, owner, control, _currentLevel, exp);
    }

    public PetBabyInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control) {
        super(objectId, template, owner, control);
    }

    private List<Skill> getBuffs() {
        switch (getNpcId()) {
            case PetDataTable.IMPROVED_BABY_COUGAR_ID:
                return Arrays.asList(COUGAR_BUFFS[getBuffLevel()]);
            case PetDataTable.IMPROVED_BABY_BUFFALO_ID:
                return Arrays.asList(BUFFALO_BUFFS[getBuffLevel()]);
            case PetDataTable.IMPROVED_BABY_KOOKABURRA_ID:
                return Arrays.asList(KOOKABURRA_BUFFS[getBuffLevel()]);
            case PetDataTable.FAIRY_PRINCESS_ID:
                return Arrays.asList(FAIRY_PRINCESS_BUFFS[getBuffLevel()]);
            case PetDataTable.SPIRIT_SHAMAN_ID:
                return Arrays.asList(FAIRY_PRINCESS_BUFFS[getBuffLevel()]);
            case PetDataTable.TOY_KNIGHT_ID:
                return Arrays.asList(TOY_KNIGHT_BUFFS[getBuffLevel()]);
            case PetDataTable.SUPER_KAT_THE_CAT_Z_ID:
                return Arrays.asList(TOY_KNIGHT_BUFFS[getBuffLevel()]);
            case PetDataTable.TURTLE_ASCETIC_ID:
                return Arrays.asList(TURTLE_ASCETIC_BUFFS[getBuffLevel()]);
            case PetDataTable.SUPER_MEW_THE_CAT_Z_ID:
                return Arrays.asList(TURTLE_ASCETIC_BUFFS[getBuffLevel()]);
            case PetDataTable.WHITE_WEASEL_ID:
                return Arrays.asList(WHITE_WEASEL_BUFFS[getBuffLevel()]);
            case PetDataTable.ROSE_DESELOPH_ID:
                return Arrays.asList(ROSE_DESELOPH_BUFFS[getBuffLevel()]);
            case PetDataTable.ROSE_HYUM_ID:
                return Arrays.asList(ROSE_HYUM_BUFFS[getBuffLevel()]);
            case PetDataTable.ROSE_REKANG_ID:
                return Arrays.asList(ROSE_REKANG_BUFFS[getBuffLevel()]);
            case PetDataTable.ROSE_LILIAS_ID:
                return Arrays.asList(ROSE_LILIAS_BUFFS[getBuffLevel()]);
            case PetDataTable.ROSE_LAPHAM_ID:
                return Arrays.asList(ROSE_LAPHAM_BUFFS[getBuffLevel()]);
            case PetDataTable.ROSE_MAPHUM_ID:
                return Arrays.asList(ROSE_MAPHUM_BUFFS[getBuffLevel()]);
            case PetDataTable.IMPROVED_ROSE_DESELOPH_ID:
                return Arrays.asList(IMPROVED_ROSE_DESELOPH_BUFFS[getBuffLevel()]);
            case PetDataTable.IMPROVED_ROSE_HYUM_ID:
                return Arrays.asList(IMPROVED_ROSE_HYUM_BUFFS[getBuffLevel()]);
            case PetDataTable.IMPROVED_ROSE_REKANG_ID:
                return Arrays.asList(IMPROVED_ROSE_REKANG_BUFFS[getBuffLevel()]);
            case PetDataTable.IMPROVED_ROSE_LILIAS_ID:
                return Arrays.asList(IMPROVED_ROSE_LILIAS_BUFFS[getBuffLevel()]);
            case PetDataTable.IMPROVED_ROSE_LAPHAM_ID:
                return Arrays.asList(IMPROVED_ROSE_LAPHAM_BUFFS[getBuffLevel()]);
            case PetDataTable.IMPROVED_ROSE_MAPHUM_ID:
                return Arrays.asList(IMPROVED_ROSE_MAPHUM_BUFFS[getBuffLevel()]);

            default:
                return Collections.emptyList();
        }
    }

    private Skill onActionTask() {
        try {
            Player owner = getPlayer();
            if (!owner.isDead() && !owner.isInvul() && !isCastingNow()) {
                if (getEffectList().getEffectsCountForSkill(5753) > 0) // Awakening
                    return null;

                if (getEffectList().getEffectsCountForSkill(5771) > 0) // Buff Control
                    return null;

                boolean improved = PetDataTable.isImprovedBabyPet(getNpcId());
                Skill skill = null;

                if (!Config.ALT_PET_HEAL_BATTLE_ONLY || owner.isInCombat()) {
                    // check treatment
                    double curHp = owner.getCurrentHpPercents();
                    if (curHp < 90 && Rnd.chance((100 - curHp) / 3))
                        if (curHp < 33) // an emergency, a strong healing
                            skill = SkillTable.INSTANCE.getInfo(improved ? BattleHeal : GreaterHealTrick, getHealLevel());
                        else if (getNpcId() != PetDataTable.IMPROVED_BABY_KOOKABURRA_ID)
                            skill = SkillTable.INSTANCE.getInfo(improved ? GreaterHeal : HealTrick, getHealLevel());

                    // check RECHARGER
                    if (skill == null && (getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID || getNpcId() == PetDataTable.FAIRY_PRINCESS_ID)) // Речардж для Kookaburra и Принцесы Феи, но в комбат моде
                    {
                        double curMp = owner.getCurrentMpPercents();
                        if (curMp < 66 && Rnd.chance((100 - curMp) / 2))
                            skill = SkillTable.INSTANCE.getInfo(Recharge, getRechargeLevel());
                    }

                    if (skill != null && skill.checkCondition(PetBabyInstance.this, owner, false, !isFollowMode(), true)) {
                        setTarget(owner);
                        getAI().Cast(skill, owner, false, !isFollowMode());
                        return skill;
                    }
                }

                if (!improved || owner.getEffectList().getEffectsCountForSkill(5771) > 0)
                    return null;

                outer:
                for (Skill buff : getBuffs()) {
                    if (getCurrentMp() < buff.getMpConsume2())
                        continue;

                    for (Effect ef : owner.getEffectList().getAllEffects())
                        if (checkEffect(ef, buff))
                            continue outer;

                    if (buff.checkCondition(PetBabyInstance.this, owner, false, !isFollowMode(), true)) {
                        setTarget(owner);
                        getAI().Cast(buff, owner, false, !isFollowMode());
                        return buff;
                    }
                    return null;
                }
            }
        } catch (RuntimeException e) {
            _log.warn("Pet [#" + getNpcId() + "] a buff task error has occurred: " + e);
            _log.error("", e);
        }
        return null;
    }

    /**
     * Returns true if the effect is to already have the skill and re-apply it is not necessary
     */
    private boolean checkEffect(Effect ef, Skill skill) {
        if (ef == null || !ef.isInUse() || !EffectList.checkStackType(ef.getTemplate(), skill.getEffectTemplates().get(0))) // no such skill
            return false;
        if (ef.getStackOrder() < skill.getEffectTemplates().get(0)._stackOrder) // old weaker
            return false;
        if (ef.getTimeLeft() > 10) // old is not weaker and more ends - waiting
            return true;
        if (ef.getNext() != null) // old but not weaker ends - check that there is recursion scheduler
            return checkEffect(ef.getNext(), skill);
        return false;
    }

    private synchronized void stopBuffTask() {
        if (_actionTask != null) {
            _actionTask.cancel(false);
            _actionTask = null;
        }
    }

    public synchronized void startBuffTask() {
        if (_actionTask != null)
            stopBuffTask();

        if (_actionTask == null && !isDead())
            _actionTask = ThreadPoolManager.INSTANCE.schedule(new ActionTask(), 5000);
    }

    public boolean isBuffEnabled() {
        return _buffEnabled;
    }

    public void triggerBuff() {
        _buffEnabled = !_buffEnabled;
    }

    @Override
    protected void onDeath(Creature killer) {
        stopBuffTask();
        super.onDeath(killer);
    }

    @Override
    public void doRevive() {
        super.doRevive();
        startBuffTask();
    }

    @Override
    public void unSummon() {
        stopBuffTask();
        super.unSummon();
    }

    private int getHealLevel() {
        return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 12), 1), 12);
    }

    private int getRechargeLevel() {
        return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 8), 1), 8);
    }

    private int getBuffLevel() {
        if (getNpcId() == PetDataTable.FAIRY_PRINCESS_ID)
            return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 3), 0), 3);
        return Math.min(Math.max((getLevel() - 55) / 5, 0), 3);
    }

    @Override
    public int getSoulshotConsumeCount() {
        return 1;
    }

    @Override
    public int getSpiritshotConsumeCount() {
        return 1;
    }

    class ActionTask extends RunnableImpl {
        @Override
        public void runImpl() {
            Skill skill = onActionTask();
            _actionTask = ThreadPoolManager.INSTANCE.schedule(new ActionTask(), skill == null ? 1000 : skill.getHitTime() * 333 / Math.max(getMAtkSpd(), 1) - 100);
        }
    }
}