package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.utils.ItemFunctions;

import java.util.List;

public final class SummonItem extends Skill {
    private final int itemId;
    private final int minId;
    private final int maxId;
    private final long minCount;
    private final long maxCount;

    public SummonItem(final StatsSet set) {
        super(set);

        itemId = set.getInteger("SummonItemId", 0);
        minId = set.getInteger("SummonMinId", 0);
        maxId = set.getInteger("SummonMaxId", minId);
        minCount = set.getLong("SummonMinCount");
        maxCount = set.getLong("SummonMaxCount", minCount);
    }

    @Override
    public void useSkill(final Creature activeChar, final List<Creature> targets) {
        if (activeChar instanceof Player) {
            Player player = (Player) activeChar;
            targets.forEach(target -> {
                int itemId = minId > 0 ? Rnd.get(minId, maxId) : this.itemId;
                long count = Rnd.get(minCount, maxCount);

                ItemFunctions.addItem(player, itemId, count, "SummonItem");
                getEffects(player, target, activateRate > 0, false);
            });
        }
    }
}