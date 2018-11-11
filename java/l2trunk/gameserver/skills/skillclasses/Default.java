package l2trunk.gameserver.skills.skillclasses;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Default extends Skill {
    private static final Logger _log = LoggerFactory.getLogger(Default.class);

    public Default(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (activeChar.isPlayer())
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.skills.skillclasses.Default.NotImplemented", (Player) activeChar).addNumber(getId()).addString("" + getSkillType()));
        _log.warn("NOTDONE skill: " + getId() + ", used by" + activeChar);
        activeChar.sendActionFailed();
    }
}