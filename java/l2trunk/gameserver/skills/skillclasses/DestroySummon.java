package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Formulas;

import java.util.List;

public final class DestroySummon extends Skill {
    public DestroySummon(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets)
            if (target != null) {

                if (activateRate() > 0 && !Formulas.calcSkillSuccess(activeChar, target, this, activateRate())) {
                    activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addString(target.getName()).addSkillName(id, level));
                    continue;
                }

                if (target.isSummon()) {
                    ((Summon) target).saveEffects();
                    ((Summon) target).unSummon();
                    getEffects(activeChar, target, activateRate() > 0, false);
                }
            }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}