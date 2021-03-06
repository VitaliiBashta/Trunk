package l2trunk.scripts.ai.dragonvalley;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.NpcUtils;

public final class DragonKnight extends Fighter {
    public DragonKnight(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        super.onEvtDead(killer);
        switch (getActor().getNpcId()) {
            case 22844:
                if (Rnd.chance(50)) {
                    NpcInstance n = NpcUtils.spawnSingle(22845, getActor().getLoc());
                    n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
                }
                break;
            case 22845:
                if (Rnd.chance(50)) {
                    NpcInstance n = NpcUtils.spawnSingle(22846, getActor().getLoc());
                    n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
                }
                break;
        }

    }
}