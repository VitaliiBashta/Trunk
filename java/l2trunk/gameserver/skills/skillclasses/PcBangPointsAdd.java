package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;

import java.util.List;

public class PcBangPointsAdd extends Skill {
    public PcBangPointsAdd(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        int points = (int) power;

        for (Creature target : targets) {
            if (target.isPlayer()) {
                Player player = target.getPlayer();
                player.addPcBangPoints(points, false);
            }
            getEffects(activeChar, target, getActivateRate() > 0, false);
        }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}