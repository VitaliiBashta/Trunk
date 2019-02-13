package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;

import java.util.List;

public final class VitalityHeal extends Skill {
    public VitalityHeal(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        int fullPoints = Config.VITALITY_LEVELS.get(4);
        double percent = power;

        for (Creature target : targets) {
            if (target instanceof Player) {
                ((Player)target).addVitality(fullPoints / 100. * percent);
            }
            getEffects(activeChar, target, activateRate > 0, false);
        }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}