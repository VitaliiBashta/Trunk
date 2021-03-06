package l2trunk.gameserver.skills;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.skills.effects.*;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum EffectType {
    // The main effects
    AddSkills(EffectAddSkills.class, null, false),
    AgathionResurrect(EffectAgathionRes.class, null, true),
    Aggression(EffectAggression.class, null, true),
    Betray(EffectBetray.class, null, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    BlessNoblesse(EffectBlessNoblesse.class, null, true),
    BlockStat(EffectBlockStat.class, null, true),
    Buff(EffectBuff.class, null, false),
    Bluff(EffectBluff.class, AbnormalEffect.NULL, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    DebuffImmunity(EffectDebuffImmunity.class, null, true),
    DispelEffects(EffectDispelEffects.class, null, Stats.CANCEL_RESIST, Stats.CANCEL_POWER, true),
    CallSkills(EffectCallSkills.class, null, false),
    CombatPointHealOverTime(EffectCombatPointHealOverTime.class, null, true),
    ConsumeSoulsOverTime(EffectConsumeSoulsOverTime.class, null, true),
    Charge(EffectCharge.class, null, false),
    CharmOfCourage(EffectCharmOfCourage.class, null, true),
    CPDamPercent(EffectCPDamPercent.class, null, true),
    Cubic(EffectCubic.class, null, true),
    DamOverTime(EffectDamOverTime.class, null, false),
    DamOverTimeLethal(EffectDamOverTimeLethal.class, null, false),
    DestroySummon(EffectDestroySummon.class, null, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    Disarm(EffectDisarm.class, null, true),
    Discord(EffectDiscord.class, AbnormalEffect.CONFUSED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    Enervation(EffectEnervation.class, null, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, false),
    FakeDeath(EffectFakeDeath.class, null, true),
    Fear(EffectFear.class, AbnormalEffect.AFFRAID, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    Grow(EffectGrow.class, AbnormalEffect.GROW, false),
    Hate(EffectHate.class, null, false),
    Heal(EffectHeal.class, null, false),
    HealBlock(EffectHealBlock.class, null, true),
    HealCPPercent(EffectHealCPPercent.class, null, true),
    HealOverTime(EffectHealOverTime.class, null, false),
    HealPercent(EffectHealPercent.class, null, false),
    SummonHealPercent(EffectSummonHealPercent.class, null, false),
    HPDamPercent(EffectHPDamPercent.class, null, true),
    IgnoreSkill(EffectBuff.class, null, false),
    Immobilize(EffectImmobilize.class, null, true),
    Interrupt(EffectInterrupt.class, null, true),
    Invulnerable(EffectInvulnerable.class, null, false),
    Invisible(EffectInvisible.class, null, false),
    LockInventory(EffectLockInventory.class, null, false),
    CurseOfLifeFlow(EffectCurseOfLifeFlow.class, null, true),
    LDManaDamOverTime(EffectLDManaDamOverTime.class, null, true),
    ManaDamOverTime(EffectManaDamOverTime.class, null, true),
    ManaHeal(EffectManaHeal.class, null, false),
    ManaHealOverTime(EffectManaHealOverTime.class, null, false),
    ManaHealPercent(EffectManaHealPercent.class, null, false),
    SummonManaHealPercent(EffectSummonManaHealPercent.class, null, false),
    Meditation(EffectMeditation.class, null, false),
    MPDamPercent(EffectMPDamPercent.class, null, true),
    Mute(EffectMute.class, AbnormalEffect.MUTED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    MuteAll(EffectMuteAll.class, AbnormalEffect.MUTED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    MuteAttack(EffectMuteAttack.class, AbnormalEffect.MUTED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    MutePhisycal(EffectMutePhisycal.class, AbnormalEffect.MUTED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    NegateEffects(EffectNegateEffects.class, null, false),
    NegateMusic(EffectNegateMusic.class, null, false),
    Paralyze(EffectParalyze.class, AbnormalEffect.HOLD_1, Stats.PARALYZE_RESIST, Stats.PARALYZE_POWER, true),
    Petrification(EffectPetrification.class, AbnormalEffect.HOLD_2, Stats.PARALYZE_RESIST, Stats.PARALYZE_POWER, true),
    RandomHate(EffectRandomHate.class, null, true),
    Relax(EffectRelax.class, null, true),
    RemoveTarget(EffectRemoveTarget.class, null, true),
    Restoration(EffectRestoration.class, null, true),
    RestorationRandom(EffectRestorationRandom.class, null, true),
    Root(EffectRoot.class, AbnormalEffect.ROOT, Stats.ROOT_RESIST, Stats.ROOT_POWER, true),
    Hourglass(EffectHourglass.class, null, true),
    Salvation(EffectSalvation.class, null, true),
    ServitorShare(EffectServitorShare.class, null, false),
    SilentMove(EffectSilentMove.class, AbnormalEffect.STEALTH, true),
    Sleep(EffectSleep.class, AbnormalEffect.SLEEP, Stats.SLEEP_RESIST, Stats.SLEEP_POWER, true),
    Stun(EffectStun.class, AbnormalEffect.STUN, Stats.STUN_RESIST, Stats.STUN_POWER, true),
    Symbol(EffectSymbol.class, null, false),
    Transformation(EffectTransformation.class, null, true),
    UnAggro(EffectUnAggro.class, null, true),
    Vitality(EffectBuff.class, AbnormalEffect.VITALITY, true),
    VitalityMaintenance(EffectBuff.class, AbnormalEffect.VITALITY, true),
    VitalityStop(EffectVitalityStop.class, null, true),
    VitalityDamOverTime(EffectVitalityDamOverTime.class, AbnormalEffect.VITALITY, true),
    // Производные от основных эффектов
    Poison(EffectDamOverTime.class, null, Stats.POISON_RESIST, Stats.POISON_POWER, false),
    PoisonLethal(EffectDamOverTimeLethal.class, null, Stats.POISON_RESIST, Stats.POISON_POWER, false),
    Bleed(EffectDamOverTime.class, null, Stats.BLEED_RESIST, Stats.BLEED_POWER, false),
    Debuff(EffectBuff.class, null, false),
    WatcherGaze(EffectBuff.class, null, false),

    AbsorbDamageToEffector(EffectBuff.class, null, false), // абсорбирует часть дамага к еффектора еффекта
    AbsorbDamageToMp(EffectBuff.class, AbnormalEffect.S_ARCANE_SHIELD, false), // абсорбирует часть дамага в мп
    AbsorbDamageToSummon(EffectLDManaDamOverTime.class, null, true); // абсорбирует часть дамага к сумону

    private final Constructor<? extends Effect> constructor;
    private final AbnormalEffect abnormal;
    private final Stats resistType;
    private final Stats attributeType;
    private final boolean isRaidImmune;

    EffectType(Class<? extends Effect> clazz, AbnormalEffect abnormal, boolean isRaidImmune) {
        this(clazz, abnormal, null, null, isRaidImmune);
    }

    EffectType(Class<? extends Effect> clazz, AbnormalEffect abnormal, Stats resistType, Stats attributeType, boolean isRaidImmune) {
        try {
            constructor = clazz.getConstructor(Env.class, EffectTemplate.class);
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
        this.abnormal = abnormal;
        this.resistType = resistType;
        this.attributeType = attributeType;
        this.isRaidImmune = isRaidImmune;
    }

    public AbnormalEffect getAbnormal() {
        return abnormal;
    }

    public Stats getResistType() {
        return resistType;
    }

    public Stats getAttributeType() {
        return attributeType;
    }

    public boolean isRaidImmune() {
        return isRaidImmune;
    }

    public Effect makeEffect(Env env, EffectTemplate template) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return constructor.newInstance(env, template);
    }
}