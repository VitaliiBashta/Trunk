package l2trunk.gameserver.skills.skillclasses;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.templates.StatsSet;

import java.util.List;

public class VitalityHeal extends Skill {
    public VitalityHeal(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        int fullPoints = Config.VITALITY_LEVELS[4];
        double percent = power;

        for (Creature target : targets) {
            if (target.isPlayer()) {
                Player player = target.getPlayer();
                double points = fullPoints / 100 * percent;
                player.addVitality(points);
            }
            getEffects(activeChar, target, getActivateRate() > 0, false);
        }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}