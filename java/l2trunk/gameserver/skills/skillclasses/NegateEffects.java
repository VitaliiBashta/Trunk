package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.skills.EffectType;
import l2trunk.gameserver.stats.Formulas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NegateEffects extends Skill {
    private final boolean onlyPhysical;
    private final boolean negateDebuffs;
    private final Map<EffectType, Integer> negateEffects = new HashMap<>();
    private final Map<String, Integer> negateStackType = new HashMap<>();

    public NegateEffects(StatsSet set) {
        super(set);

        String[] negateEffectsString = set.getString("negateEffects", "").split(";");
        for (String aNegateEffectsString : negateEffectsString)
            if (!aNegateEffectsString.isEmpty()) {
                String[] entry = aNegateEffectsString.split(":");
                negateEffects.put(Enum.valueOf(EffectType.class, entry[0]), entry.length > 1 ? Integer.decode(entry[1]) : Integer.MAX_VALUE);
            }

        String[] negateStackTypeString = set.getString("negateStackType", "").split(";");
        for (String aNegateStackTypeString : negateStackTypeString)
            if (!aNegateStackTypeString.isEmpty()) {
                String[] entry = aNegateStackTypeString.split(":");
                negateStackType.put(entry[0], entry.length > 1 ? Integer.decode(entry[1]) : Integer.MAX_VALUE);
            }

        onlyPhysical = set.getBool("onlyPhysical", false);
        negateDebuffs = set.getBool("negateDebuffs", true);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets)
            if (target != null) {
                if (negateDebuffs || Formulas.calcSkillSuccess(activeChar, target, this, activateRate)) {
                    negateEffects.forEach((k, v) -> negateEffectAtPower(target, k, v));
                    negateStackType.forEach((k, v) -> negateEffectAtPower(target, k, v));

                    getEffects(activeChar, target, activateRate > 0, false);
                } else {
                    activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addString(target.getName()).addSkillName(id, level));
                }

            }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }

    private void negateEffectAtPower(Creature target, EffectType type, int power) {
        for (Effect e : target.getEffectList().getAllEffects()) {
            if (onlyPhysical && e.skill.isMagic() || !e.skill.isCancelable() || e.skill.isOffensive && !negateDebuffs)
                continue;
            // Если у бафа выше уровень чем у скилла Cancel, то есть шанс, что этот баф не снимется
            if (!e.skill.isOffensive && e.skill.magicLevel > magicLevel && Rnd.chance(e.skill.magicLevel - magicLevel))
                continue;
            if (e.getEffectType() == type && e.getStackOrder() <= power)
                e.exit();
        }
    }

    private void negateEffectAtPower(Creature target, String stackType, int power) {
        target.getEffectList().getAllEffects().stream()
                .filter(e ->
                        // Если у бафа выше уровень чем у скилла Cancel, то есть шанс, что этот баф не снимется
                        (!onlyPhysical || !e.skill.isMagic()) && e.skill.isCancelable())
                .filter(e -> !e.skill.isOffensive || negateDebuffs)
                .filter(e -> e.skill.isOffensive || e.skill.magicLevel <= magicLevel || !Rnd.chance(e.skill.magicLevel - magicLevel))
                .filter(e -> e.checkStackType(stackType))
                .filter(e -> e.getStackOrder() <= power)
                .forEach(Effect::exit);
    }
}