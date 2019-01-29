package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.utils.ItemFunctions;

import java.util.List;
import java.util.Objects;

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
        if (!activeChar.isPlayable())
            return;
        targets.stream()
                .filter(Objects::nonNull)
                .forEach(target -> {
                    int itemId = minId > 0 ? Rnd.get(minId, maxId) : this.itemId;
                    long count = Rnd.get(minCount, maxCount);

                    ItemFunctions.addItem((Playable) activeChar, itemId, count, true, "SummonItem");
                    getEffects(activeChar, target, activateRate > 0, false);
                });
    }
}