package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.RecipeBookItemList;

import java.util.List;

public final class Craft extends Skill {
    private final boolean dwarven;

    public Craft(StatsSet set) {
        super(set);
        dwarven = set.getBool("isDwarven");
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        Player p = (Player) activeChar;
        if (p.isInStoreMode() || p.isProcessingRequest())
            return false;

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        activeChar.sendPacket(new RecipeBookItemList((Player) activeChar, dwarven));
    }
}
