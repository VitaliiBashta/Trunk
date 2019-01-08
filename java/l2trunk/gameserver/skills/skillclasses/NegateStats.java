package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NegateStats extends Skill {
    private final List<Stats> negateStats;
    private final boolean negateOffensive;
    private final int negateCount;

    public NegateStats(StatsSet set) {
        super(set);

        String[] negateStats = set.getString("negateStats", "").split(" ");
        this.negateStats = Stream.of(negateStats)
                .filter(stat -> !stat.isEmpty())
                .map(Stats::valueOfXml)
                .collect(Collectors.toList());

        negateOffensive = set.getBool("negateDebuffs", false);
        negateCount = set.getInteger("negateCount", 0);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets)
            if (target != null) {
                if (!negateOffensive && !Formulas.calcSkillSuccess(activeChar, target, this, getActivateRate())) {
                    activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addString(target.getName()).addSkillName(getId(), getLevel()));
                    continue;
                }

                int count = 0;
                List<Effect> effects = target.getEffectList().getAllEffects();
                for (Stats stat : negateStats)
                    for (Effect e : effects) {
                        Skill skill = e.getSkill();
                        // Если у бафа выше уровень чем у скилла Cancel, то есть шанс, что этот баф не снимется
                        if (!skill.isOffensive() && skill.getMagicLevel() > getMagicLevel() && Rnd.chance(skill.getMagicLevel() - getMagicLevel())) {
                            count++;
                            continue;
                        }
                        if (skill.isOffensive() == negateOffensive && containsStat(e, stat) && skill.isCancelable()) {
                            target.sendPacket(new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill().getId(), e.getSkill().getDisplayLevel()));
                            e.exit();
                            count++;
                        }
                        if (negateCount > 0 && count >= negateCount)
                            break;
                    }

                getEffects(activeChar, target, getActivateRate() > 0, false);
            }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }

    private boolean containsStat(Effect e, Stats stat) {
        for (FuncTemplate ft : e.getTemplate().getAttachedFuncs())
            if (ft.stat == stat)
                return true;
        return false;
    }

    @Override
    public boolean isOffensive() {
        return !negateOffensive;
    }

    public List<Stats> getNegateStats() {
        return negateStats;
    }
}