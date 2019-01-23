package l2trunk.gameserver.model.actor.recorder;

import l2trunk.gameserver.model.instances.NpcInstance;

public class NpcStatsChangeRecorder extends CharStatsChangeRecorder<NpcInstance> {

    public NpcStatsChangeRecorder(NpcInstance actor) {
        super(actor);
    }


    protected void onSendChanges() {
        super.onSendChanges();
        if ((this.changes & 0x1) == 1)
            this.activeChar.broadcastCharInfo();
    }

}