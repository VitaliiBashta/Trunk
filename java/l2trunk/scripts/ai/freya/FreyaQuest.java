package l2trunk.scripts.ai.freya;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class FreyaQuest extends Fighter {
    public FreyaQuest(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = Integer.MAX_VALUE;
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        Reflection r = getActor().getReflection();
        r.getPlayers().forEach(p ->
            this.notifyEvent(CtrlEvent.EVT_ATTACKED, p, 300));

        Functions.npcSayCustomMessage(getActor(), "scripts.ai.freya.FreyaQuest.onEvtSpawn");
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    @Override
    public void returnHome(boolean clearAggro, boolean teleport) {
    }
}