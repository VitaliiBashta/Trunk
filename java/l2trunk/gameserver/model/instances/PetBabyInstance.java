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
    private static final Skill Pet_Haste = SkillTable.INSTANCE.getInfo(5186, 2);
    private static final Skill Pet_Vampiric_Rage = SkillTable.INSTANCE.getInfo(5187, 4);
    private static final int Pet_Regeneration = 5188; // 1-3
    private static final Skill Pet_Blessed_Body = SkillTable.INSTANCE.getInfo(5189, 6);
    private static final Skill Pet_Blessed_Soul = SkillTable.INSTANCE.getInfo(5190, 6);
    private static final Skill Pet_Guidance = SkillTable.INSTANCE.getInfo(5191, 3);
    private static final Skill Pet_Wind_Walk = SkillTable.INSTANCE.getInfo(5192, 2);
    private static final Skill Pet_Acumen = SkillTable.INSTANCE.getInfo(5193, 3);
    private static final Skill Pet_Empower = SkillTable.INSTANCE.getInfo(5194, 3);
    private static final Skill Pet_Concentration = SkillTable.INSTANCE.getInfo(5201, 6);
    private static final Skill Pet_Might = SkillTable.INSTANCE.getInfo(5586, 3);
    private static final Skill Pet_Shield = SkillTable.INSTANCE.getInfo(5587, 3);
    private static final Skill Pet_Focus = SkillTable.INSTANCE.getInfo(5588, 3);
    private static final Skill Pet_Death_Whisper = SkillTable.INSTANCE.getInfo(5589, 3);
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
    private static final List<List<Skill>> TOY_KNIGHT_BUFFS = List.of(
            List.of(Pet_Focus, Pet_Death_Whisper),
            List.of(
                    Pet_Focus,
                    Pet_Death_Whisper,
                    Pet_Shield,
                    Pet_Wind_Walk),
            List.of(
                    Pet_Focus,
                    Pet_Death_Whisper,
                    Pet_Shield,
                    Pet_Wind_Walk,
                    Pet_Vampiric_Rage,
                    Pet_Haste),
            List.of(
                    Pet_Focus,
                    Pet_Death_Whisper,
                    Pet_Shield,
                    Pet_Wind_Walk,
                    Pet_Vampiric_Rage,
                    Pet_Haste,
                    Pet_Might,
                    Pet_Blessed_Body));
    private static final List<List<Skill>> WHITE_WEASEL_BUFFS = List.of(
            List.of(Pet_Blessed_Body, Pet_Wind_Walk),
            List.of(
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Death_Whisper,
                    Pet_Shield),
            List.of(
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Death_Whisper,
                    Pet_Shield,
                    Pet_Vampiric_Rage,
                    Pet_Focus),
            List.of(
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Death_Whisper,
                    Pet_Shield,
                    Pet_Vampiric_Rage,
                    Pet_Focus,
                    Pet_Haste));
    //TODO: Array not offu. (Correct if there infa)
    private static final List<List<Skill>> TURTLE_ASCETIC_BUFFS = List.of(
            List.of(Pet_Blessed_Body, Pet_Blessed_Soul),
            List.of(
                    Pet_Blessed_Body,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Pet_Wind_Walk),
            List.of(
                    Pet_Blessed_Body,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Pet_Wind_Walk,
                    Pet_Armor_Maintenance,
                    Pet_Weapon_Maintenance),
            List.of(
                    Pet_Blessed_Body,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Pet_Wind_Walk,
                    Pet_Armor_Maintenance,
                    Pet_Weapon_Maintenance));
    private static final List<List<Skill>> COUGAR_BUFFS = List.of(
            List.of(Pet_Empower, Pet_Might),
            List.of(
                    Pet_Empower,
                    Pet_Might,
                    Pet_Shield,
                    Pet_Blessed_Body),
            List.of(
                    Pet_Empower,
                    Pet_Might,
                    Pet_Shield,
                    Pet_Blessed_Body,
                    Pet_Acumen,
                    Pet_Haste),
            List.of(
                    Pet_Empower,
                    Pet_Might,
                    Pet_Shield,
                    Pet_Blessed_Body,
                    Pet_Acumen,
                    Pet_Haste,
                    Pet_Vampiric_Rage,
                    Pet_Focus));
    private static final List<List<Skill>> BUFFALO_BUFFS = List.of(
            List.of(Pet_Might, Pet_Blessed_Body),
            List.of(
                    Pet_Might,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Guidance),
            List.of(
                    Pet_Might,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Guidance,
                    Pet_Vampiric_Rage,
                    Pet_Haste),
            List.of(
                    Pet_Might,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Guidance,
                    Pet_Vampiric_Rage,
                    Pet_Haste,
                    Pet_Focus,
                    Pet_Death_Whisper));
    private static final List<List<Skill>> KOOKABURRA_BUFFS = List.of(
            List.of(Pet_Empower, Pet_Blessed_Soul),
            List.of(
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Blessed_Body,
                    Pet_Shield),
            List.of(
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Acumen,
                    Pet_Concentration),
            List.of(
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Acumen,
                    Pet_Concentration));
    private static final List<List<Skill>> FAIRY_PRINCESS_BUFFS = List.of(
            List.of(Pet_Empower, Pet_Blessed_Soul),
            List.of(
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Blessed_Body,
                    Pet_Shield),
            List.of(
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Acumen,
                    Pet_Concentration),
            List.of(
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Acumen,
                    Pet_Concentration));
    private static final List<List<Skill>> ROSE_DESELOPH_BUFFS = List.of(
            List.of(
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus),
            List.of(
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus,
                    Pet_Wind_Walk,
                    Pet_Blessed_Body,
                    Pet_Shield),
            List.of(
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus,
                    Pet_Wind_Walk,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Death_Whisper,
                    Pet_Vampiric_Rage),
            List.of(
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus,
                    Pet_Wind_Walk,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Death_Whisper,
                    Pet_Vampiric_Rage));
    private static final List<List<Skill>> ROSE_HYUM_BUFFS = List.of(
            List.of(Pet_Empower, Pet_Blessed_Soul),
            List.of(
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Wind_Walk,
                    Pet_Shield),
            List.of(
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Wind_Walk,
                    Pet_Shield,
                    Pet_Acumen),
            List.of(
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Wind_Walk,
                    Pet_Shield,
                    Pet_Acumen));
    private static final List<List<Skill>> ROSE_REKANG_BUFFS = List.of(
            List.of(Pet_Blessed_Body, Pet_Wind_Walk),
            List.of(
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Blessed_Soul,
                    Pet_Shield),
            List.of(
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Armor_Maintenance,
                    Weapon_Maintenance),
            List.of(
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Armor_Maintenance,
                    Weapon_Maintenance));
    private static final List<List<Skill>> ROSE_LILIAS_BUFFS = List.of(
            List.of(
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus),
            List.of(
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus),
            List.of(
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus,
                    Pet_Wind_Walk,
                    Pet_Blessed_Body,
                    Pet_Shield),
            List.of(
                    Pet_Might,
                    Pet_Haste,
                    Pet_Focus,
                    Pet_Wind_Walk,
                    Pet_Blessed_Body,
                    Pet_Shield,
                    Pet_Death_Whisper,
                    Pet_Vampiric_Rage));
    private static final List<List<Skill>> ROSE_LAPHAM_BUFFS = List.of(
            List.of(Pet_Empower, Pet_Blessed_Soul),
            List.of(
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Wind_Walk,
                    Pet_Shield),
            List.of(
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Wind_Walk,
                    Pet_Shield,
                    Pet_Acumen),
            List.of(
                    Pet_Empower,
                    Pet_Blessed_Soul,
                    Pet_Wind_Walk,
                    Pet_Shield,
                    Pet_Acumen));
    private static final List<List<Skill>> ROSE_MAPHUM_BUFFS = List.of(
            List.of(Pet_Blessed_Body, Pet_Wind_Walk),
            List.of(
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Blessed_Soul,
                    Pet_Shield),
            List.of(
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Armor_Maintenance,
                    Weapon_Maintenance),
            List.of(
                    Pet_Blessed_Body,
                    Pet_Wind_Walk,
                    Pet_Blessed_Soul,
                    Pet_Shield,
                    Armor_Maintenance,
                    Weapon_Maintenance));
    private static final List<List<Skill>> IMPROVED_ROSE_DESELOPH_BUFFS = List.of(
            List.of(Improved_Condition, Improved_Movement),
            List.of(
                    Improved_Condition,
                    Improved_Movement,
                    Improved_Combat,
                    Chant_of_Blood_Awakening),
            List.of(
                    Improved_Condition,
                    Improved_Movement,
                    Improved_Combat,
                    Chant_of_Blood_Awakening,
                    Improved_Critical_Attack),
            List.of(
                    Improved_Condition,
                    Improved_Movement,
                    Improved_Combat,
                    Chant_of_Blood_Awakening,
                    Improved_Critical_Attack));
    private static final List<List<Skill>> IMPROVED_ROSE_HYUM_BUFFS = List.of(
            List.of(Pet_Acumen, Improved_Condition),
            List.of(
                    Pet_Acumen,
                    Improved_Condition,
                    Improved_Combat),
            List.of(
                    Pet_Acumen,
                    Improved_Condition,
                    Improved_Combat,
                    Improved_Movement),
            List.of(
                    Pet_Acumen,
                    Improved_Condition,
                    Improved_Combat,
                    Improved_Movement,
                    Improved_Magic));
    private static final List<List<Skill>> IMPROVED_ROSE_REKANG_BUFFS = List.of(
            List.of(Improved_Combat, Improved_Condition),
            List.of(
                    Improved_Combat,
                    Improved_Condition,
                    Improved_Movement),
            List.of(
                    Improved_Combat,
                    Improved_Condition,
                    Improved_Movement,
                    Armor_Maintenance),
            List.of(
                    Improved_Combat,
                    Improved_Condition,
                    Improved_Movement,
                    Armor_Maintenance,
                    Weapon_Maintenance));
    private static final List<List<Skill>> IMPROVED_ROSE_LILIAS_BUFFS = List.of(
            List.of(Improved_Combat, Improved_Condition),
            List.of(
                    Improved_Combat,
                    Improved_Condition,
                    Chant_of_Blood_Awakening),
            List.of(
                    Improved_Combat,
                    Improved_Condition,
                    Chant_of_Blood_Awakening,
                    Improved_Movement),
            List.of(
                    Improved_Combat,
                    Improved_Condition,
                    Chant_of_Blood_Awakening,
                    Improved_Movement,
                    Improved_Critical_Attack));
    private static final List<List<Skill>> IMPROVED_ROSE_LAPHAM_BUFFS = List.of(
            List.of(Pet_Acumen, Improved_Condition),
            List.of(
                    Pet_Acumen,
                    Improved_Condition,
                    Improved_Combat),
            List.of(
                    Pet_Acumen,
                    Improved_Condition,
                    Improved_Combat,
                    Improved_Movement),
            List.of(
                    Pet_Acumen,
                    Improved_Condition,
                    Improved_Combat,
                    Improved_Movement,
                    Improved_Magic));
    private static final List<List<Skill>> IMPROVED_ROSE_MAPHUM_BUFFS = List.of(
            List.of(Improved_Combat, Improved_Condition),
            List.of(
                    Improved_Combat,
                    Improved_Condition,
                    Improved_Movement),
            List.of(
                    Improved_Combat,
                    Improved_Condition,
                    Improved_Movement,
                    Armor_Maintenance,
                    Weapon_Maintenance),
            List.of(
                    Improved_Combat,
                    Improved_Condition,
                    Improved_Movement,
                    Armor_Maintenance,
                    Weapon_Maintenance));
    private Future<?> actionTask;
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
                return COUGAR_BUFFS.get(getBuffLevel());
            case PetDataTable.IMPROVED_BABY_BUFFALO_ID:
                return BUFFALO_BUFFS.get(getBuffLevel());
            case PetDataTable.IMPROVED_BABY_KOOKABURRA_ID:
                return KOOKABURRA_BUFFS.get(getBuffLevel());
            case PetDataTable.FAIRY_PRINCESS_ID:
                return FAIRY_PRINCESS_BUFFS.get(getBuffLevel());
            case PetDataTable.SPIRIT_SHAMAN_ID:
                return FAIRY_PRINCESS_BUFFS.get(getBuffLevel());
            case PetDataTable.TOY_KNIGHT_ID:
                return TOY_KNIGHT_BUFFS.get(getBuffLevel());
            case PetDataTable.SUPER_KAT_THE_CAT_Z_ID:
                return TOY_KNIGHT_BUFFS.get(getBuffLevel());
            case PetDataTable.TURTLE_ASCETIC_ID:
                return TURTLE_ASCETIC_BUFFS.get(getBuffLevel());
            case PetDataTable.SUPER_MEW_THE_CAT_Z_ID:
                return TURTLE_ASCETIC_BUFFS.get(getBuffLevel());
            case PetDataTable.WHITE_WEASEL_ID:
                return WHITE_WEASEL_BUFFS.get(getBuffLevel());
            case PetDataTable.ROSE_DESELOPH_ID:
                return ROSE_DESELOPH_BUFFS.get(getBuffLevel());
            case PetDataTable.ROSE_HYUM_ID:
                return ROSE_HYUM_BUFFS.get(getBuffLevel());
            case PetDataTable.ROSE_REKANG_ID:
                return ROSE_REKANG_BUFFS.get(getBuffLevel());
            case PetDataTable.ROSE_LILIAS_ID:
                return ROSE_LILIAS_BUFFS.get(getBuffLevel());
            case PetDataTable.ROSE_LAPHAM_ID:
                return ROSE_LAPHAM_BUFFS.get(getBuffLevel());
            case PetDataTable.ROSE_MAPHUM_ID:
                return ROSE_MAPHUM_BUFFS.get(getBuffLevel());
            case PetDataTable.IMPROVED_ROSE_DESELOPH_ID:
                return IMPROVED_ROSE_DESELOPH_BUFFS.get(getBuffLevel());
            case PetDataTable.IMPROVED_ROSE_HYUM_ID:
                return IMPROVED_ROSE_HYUM_BUFFS.get(getBuffLevel());
            case PetDataTable.IMPROVED_ROSE_REKANG_ID:
                return IMPROVED_ROSE_REKANG_BUFFS.get(getBuffLevel());
            case PetDataTable.IMPROVED_ROSE_LILIAS_ID:
                return IMPROVED_ROSE_LILIAS_BUFFS.get(getBuffLevel());
            case PetDataTable.IMPROVED_ROSE_LAPHAM_ID:
                return IMPROVED_ROSE_LAPHAM_BUFFS.get(getBuffLevel());
            case PetDataTable.IMPROVED_ROSE_MAPHUM_ID:
                return IMPROVED_ROSE_MAPHUM_BUFFS.get(getBuffLevel());

            default:
                return List.of();
        }
    }

    private Skill onActionTask() {
        try {
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

                    if (skill != null && skill.checkCondition(PetBabyInstance.this.owner, owner, false, !isFollowMode(), true)) {
                        setTarget(owner);
                        getAI().cast(skill, owner, false, !isFollowMode());
                        return skill;
                    }
                }

                if (!improved || owner.getEffectList().getEffectsCountForSkill(5771) > 0)
                    return null;

                for (Skill buff : getBuffs()) {
                    if (getCurrentMp() < buff.mpConsume2)
                        continue;

                    if (owner.getEffectList().getAllEffects().stream().anyMatch(ef -> checkEffect(ef, buff)))
                        continue;

                    if (buff.checkCondition(PetBabyInstance.this.owner, owner, false, !isFollowMode(), true)) {
                        setTarget(owner);
                        getAI().cast(buff, owner, false, !isFollowMode());
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
        if (ef.getStackOrder() < skill.getEffectTemplates().get(0).stackOrder) // old weaker
            return false;
        if (ef.getTimeLeft() > 10) // old is not weaker and more ends - waiting
            return true;
        if (ef.getNext() != null) // old but not weaker ends - check that there is recursion scheduler
            return checkEffect(ef.getNext(), skill);
        return false;
    }

    private synchronized void stopBuffTask() {
        if (actionTask != null) {
            actionTask.cancel(false);
            actionTask = null;
        }
    }

    public synchronized void startBuffTask() {
        if (actionTask != null)
            stopBuffTask();

        if (actionTask == null && !isDead())
            actionTask = ThreadPoolManager.INSTANCE.schedule(new ActionTask(), 5000);
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
            actionTask = ThreadPoolManager.INSTANCE.schedule(new ActionTask(), skill == null ? 1000 : skill.hitTime * 333 / Math.max(getMAtkSpd(), 1) - 100);
        }
    }
}