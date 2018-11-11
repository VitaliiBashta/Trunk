package l2trunk.scripts.ai.freya;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;

public class IceCastleBreath extends Fighter {
    public IceCastleBreath(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = 6000;
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        Reflection r = getActor().getReflection();
        if (r != null && r.getPlayers() != null) {
            for (Player p : r.getPlayers()) {
                notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 300);
            }
        }
    }

    @Override
    protected void teleportHome() {
        return;
    }
}