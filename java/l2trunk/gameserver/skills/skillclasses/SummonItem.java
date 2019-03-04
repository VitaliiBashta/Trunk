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
    private final int minCount;
    private final int maxCount;

    public SummonItem(final StatsSet set) {
        super(set);

        itemId = set.getInteger("SummonItemId");
        minId = set.getInteger("SummonMinId");
        maxId = set.getInteger("SummonMaxId", minId);
        minCount = set.getInteger("SummonMinCount");
        int maxCount1 = set.getInteger("SummonMaxCount");
        if (maxCount1 < minCount) maxCount = minCount;
        else maxCount =maxCount1;
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