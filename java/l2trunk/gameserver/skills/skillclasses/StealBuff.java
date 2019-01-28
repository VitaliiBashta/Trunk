package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.GameObjectTasks;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.skills.EffectType;
import l2trunk.gameserver.skills.effects.EffectDispelEffects;
import l2trunk.gameserver.skills.effects.EffectTemplate;
import l2trunk.gameserver.stats.Env;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StealBuff extends Skill {
    private final int stealCount;
    private final int stealChance;

    public StealBuff(StatsSet set) {
        super(set);
        stealCount = set.getInteger("stealCount", 1);
        stealChance = set.getInteger("stealChance", 100);
    }

    private static List<Effect> createEffectList(Creature target) {
        final List<Effect> musicList = new ArrayList<>();
        final List<Effect> buffList = new ArrayList<>();

        target.getEffectList().getAllEffects().stream()
                .filter(StealBuff::canBeStolen)
                .forEach(e -> {
                    if (e.getSkill().isMusic())
                        musicList.add(e);
                    else
                        buffList.add(e);
                });

        // Alexander - Instead of puttin all the songs/dances before the buffs, we put 1 song 1 buff, alternated so the steal is better
        Collections.reverse(musicList);
        Collections.reverse(buffList);
        final List<Effect> effectList = new ArrayList<>();
        for (int i = 0; i < Math.max(musicList.size(), buffList.size()); i++) {
            if (musicList.size() > i)
                effectList.add(musicList.get(i));
            if (buffList.size() > i)
                effectList.add(buffList.get(i));
        }

        return effectList;
    }

    private static boolean canBeStolen(Effect e) {
        if (e == null)
            return false;
        if (!e.isInUse())
            return false;
        if (!e.isCancelable())
            return false;
        if (e.getSkill().isToggle())
            return false;
        if (e.getSkill().isPassive())
            return false;
        if (e.getSkill().isOffensive)
            return false;
        if (e.getEffectType() == EffectType.Vitality || e.getEffectType() == EffectType.VitalityMaintenance)
            return false;
        return !e.getTemplate().applyOnCaster;
    }

    private static Effect cloneEffect(Creature cha, Effect eff) {
        Skill skill = eff.getSkill();

        for (EffectTemplate et : skill.getEffectTemplates()) {
            Effect effect = et.getEffect(new Env(cha, cha, skill));
            if (effect != null) {
                effect.setCount(eff.getCount());
                effect.setPeriod(eff.getCount() == 1 ? eff.getPeriod() - eff.getTime() : eff.getPeriod());
                return effect;
            }
        }
        return null;
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if ((target == null) || !target.isPlayer()) {
            activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets) {
            if (target == null || !target.isPlayer())
                continue;

            List<Effect> effectList = createEffectList(target);
            if (effectList.isEmpty())
                continue;

            final List<Skill> oldEff = new ArrayList<>();
            final List<Integer> timeLeft = new ArrayList<>();
            effectList = effectList.subList(0, Math.min(effectList.size(), stealCount * 2));

            int count = 0;
            for (Effect effect : effectList) {
                if (effect == null)
                    continue;

                // We estimate the success of the cancel on this effect
                if (!EffectDispelEffects.calcCancelSuccess(activeChar, target, effect, this, stealChance))
                    continue;

                Effect stolenEffect = cloneEffect(activeChar, effect);
                if (stolenEffect != null) {
                    activeChar.getEffectList().addEffect(stolenEffect);
                }

                if (Config.ALT_AFTER_CANCEL_RETURN_SKILLS_TIME > 0) {
                    oldEff.add(effect.getSkill());
                    timeLeft.add(effect.getTimeLeft());
                }
                effect.exit();
                target.sendPacket(new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(effect.getSkill().id, effect.getSkill().level));
                count++;

                if (stealCount > 0 && count >= stealCount)
                    break;
            }
            if (!oldEff.isEmpty()) {
                ThreadPoolManager.INSTANCE.schedule(new GameObjectTasks.ReturnTask(target, oldEff, timeLeft), Config.ALT_AFTER_CANCEL_RETURN_SKILLS_TIME * 1000);
            }
            getEffects(activeChar, target, getActivateRate() > 0, false);
        }
        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}