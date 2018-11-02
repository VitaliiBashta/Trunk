package l2f.gameserver.skills.skillclasses;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.FeedableBeastInstance;
import l2f.gameserver.templates.StatsSet;

import java.util.List;

public class BeastFeed extends Skill {
    public BeastFeed(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final Creature activeChar, List<Creature> targets) {
        for (final Creature target : targets) {
            ThreadPoolManager.getInstance().execute(new RunnableImpl() {
                @Override
                public void runImpl() {
                    if (target instanceof FeedableBeastInstance)
                        ((FeedableBeastInstance) target).onSkillUse((Player) activeChar, _id);
                }
            });
        }
    }
}
