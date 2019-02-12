package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.QuestEventType;

/**
 * @author VISTALL
 * @date 8:44/10.06.2011
 */
public class QuestNotAggroMob extends DefaultAI {
    public QuestNotAggroMob(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return false;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int dam) {
        NpcInstance actor = getActor();
        Player player = attacker.getPlayer();

        if (player != null) {
            player.getQuestsForEvent(actor, QuestEventType.ATTACKED_WITH_QUEST)
                    .forEach(qs -> qs.quest.notifyAttack(actor, qs));
        }
    }

    @Override
    public void onEvtAggression(Creature attacker, int d) {
        //
    }
}
