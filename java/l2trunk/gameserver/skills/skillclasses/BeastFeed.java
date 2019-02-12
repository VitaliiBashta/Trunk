package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.FeedableBeastInstance;

import java.util.List;

public final class BeastFeed extends Skill {
    public BeastFeed(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final Creature activeChar, List<Creature> targets) {
        targets.stream().filter(t -> t instanceof FeedableBeastInstance)
                .map(t -> (FeedableBeastInstance) t)
                .forEach(target -> ThreadPoolManager.INSTANCE.execute(() -> target.onSkillUse((Player) activeChar, id)));
    }
}
