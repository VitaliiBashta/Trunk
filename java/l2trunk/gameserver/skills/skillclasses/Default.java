package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class Default extends Skill {
    private static final Logger LOG = LoggerFactory.getLogger(Default.class);

    public Default(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (activeChar instanceof Player)
            ((Player)activeChar).sendMessage(new CustomMessage("l2trunk.gameserver.skills.skillclasses.Default.NotImplemented").addNumber(id).addString("" + skillType));
        LOG.warn("NOTDONE skill: " + id + ", used by" + activeChar);
        activeChar.sendActionFailed();
    }
}
