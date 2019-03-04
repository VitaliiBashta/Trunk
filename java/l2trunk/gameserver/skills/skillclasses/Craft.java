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
        dwarven = set.isSet("isDwarven");
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (player.isInStoreMode() || player.isProcessingRequest())
            return false;

        return super.checkCondition(player, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, Creature targets) {
        activeChar.sendPacket(new RecipeBookItemList((Player) activeChar, dwarven));
    }
}
