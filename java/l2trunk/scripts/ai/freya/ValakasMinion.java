package l2trunk.scripts.ai.freya;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.scripts.bosses.ValakasManager;

public final class ValakasMinion extends Mystic {
    public ValakasMinion(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        ValakasManager.getZone().getInsidePlayers()
                .forEach(p -> notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000));
    }
}