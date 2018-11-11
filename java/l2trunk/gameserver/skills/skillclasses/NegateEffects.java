package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.skills.EffectType;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.templates.StatsSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NegateEffects extends Skill {
    private final boolean _onlyPhysical;
    private final boolean _negateDebuffs;
    private final Map<EffectType, Integer> _negateEffects = new HashMap<>();
    private final Map<String, Integer> _negateStackType = new HashMap<>();

    public NegateEffects(StatsSet set) {
        super(set);

        String[] negateEffectsString = set.getString("negateEffects", "").split(";");
        for (String aNegateEffectsString : negateEffectsString)
            if (!aNegateEffectsString.isEmpty()) {
                String[] entry = aNegateEffectsString.split(":");
                _negateEffects.put(Enum.valueOf(EffectType.class, entry[0]), entry.length > 1 ? Integer.decode(entry[1]) : Integer.MAX_VALUE);
            }

        String[] negateStackTypeString = set.getString("negateStackType", "").split(";");
        for (String aNegateStackTypeString : negateStackTypeString)
            if (!aNegateStackTypeString.isEmpty()) {
                String[] entry = aNegateStackTypeString.split(":");
                _negateStackType.put(entry[0], entry.length > 1 ? Integer.decode(entry[1]) : Integer.MAX_VALUE);
            }

        _onlyPhysical = set.getBool("onlyPhysical", false);
        _negateDebuffs = set.getBool("negateDebuffs", true);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets)
            if (target != null) {
                if (!_negateDebuffs && !Formulas.calcSkillSuccess(activeChar, target, this, getActivateRate())) {
                    activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addString(target.getName()).addSkillName(getId(), getLevel()));
                    continue;
                }

                if (!_negateEffects.isEmpty())
                    for (Map.Entry<EffectType, Integer> e : _negateEffects.entrySet())
                        negateEffectAtPower(target, e.getKey(), e.getValue());

                if (!_negateStackType.isEmpty())
                    for (Map.Entry<String, Integer> e : _negateStackType.entrySet())
                        negateEffectAtPower(target, e.getKey(), e.getValue());

                getEffects(activeChar, target, getActivateRate() > 0, false);
            }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }

    private void negateEffectAtPower(Creature target, EffectType type, int power) {
        for (Effect e : target.getEffectList().getAllEffects()) {
            Skill skill = e.getSkill();
            if (_onlyPhysical && skill.isMagic() || !skill.isCancelable() || skill.isOffensive() && !_negateDebuffs)
                continue;
            // Если у бафа выше уровень чем у скилла Cancel, то есть шанс, что этот баф не снимется
            if (!skill.isOffensive() && skill.getMagicLevel() > getMagicLevel() && Rnd.chance(skill.getMagicLevel() - getMagicLevel()))
                continue;
            if (e.getEffectType() == type && e.getStackOrder() <= power)
                e.exit();
        }
    }

    private void negateEffectAtPower(Creature target, String stackType, int power) {
        for (Effect e : target.getEffectList().getAllEffects()) {
            Skill skill = e.getSkill();
            if (_onlyPhysical && skill.isMagic() || !skill.isCancelable() || skill.isOffensive() && !_negateDebuffs)
                continue;
            // Если у бафа выше уровень чем у скилла Cancel, то есть шанс, что этот баф не снимется
            if (!skill.isOffensive() && skill.getMagicLevel() > getMagicLevel() && Rnd.chance(skill.getMagicLevel() - getMagicLevel()))
                continue;
            if (e.checkStackType(stackType) && e.getStackOrder() <= power)
                e.exit();
        }
    }
}