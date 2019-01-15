package l2trunk.scripts.ai.freya;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class IceCaptainKnight extends Fighter {
    public IceCaptainKnight(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = 6000;
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        Reflection r = getActor().getReflection();
        if (r != null && r.getPlayers() != null) {
            r.getPlayers().forEach(p -> notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 300));
        }
    }

    @Override
    public void teleportHome() {
    }
}