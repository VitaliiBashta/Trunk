package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.FeedableBeastInstance;
import l2trunk.gameserver.templates.StatsSet;

import java.util.List;

public class BeastFeed extends Skill {
    public BeastFeed(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final Creature activeChar, List<Creature> targets) {
        for (final Creature target : targets) {
            ThreadPoolManager.INSTANCE().execute(new RunnableImpl() {
                @Override
                public void runImpl() {
                    if (target instanceof FeedableBeastInstance)
                        ((FeedableBeastInstance) target).onSkillUse((Player) activeChar, id);
                }
            });
        }
    }
}
