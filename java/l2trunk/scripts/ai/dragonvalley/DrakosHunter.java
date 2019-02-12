package l2trunk.scripts.ai.dragonvalley;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.NpcUtils;

public final class DrakosHunter extends Fighter {

    public DrakosHunter(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        if (Rnd.chance(Config.DHUNTER_MS_CHANCE)) {
            NpcInstance actor = getActor();
            for (int i = 0; i < 4; i++) {
                NpcInstance n = NpcUtils.spawnSingle(22823, (actor.getLoc().randomOffset(100)));
                n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
            }
        }
        super.onEvtAttacked(attacker, damage);
    }
}
