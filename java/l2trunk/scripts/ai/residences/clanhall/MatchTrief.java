package l2trunk.scripts.ai.residences.clanhall;

import l2trunk.gameserver.model.instances.NpcInstance;

public final class MatchTrief extends MatchFighter {
    public MatchTrief(NpcInstance actor) {
        super(actor);
    }

    public void hold() {
        NpcInstance actor = getActor();
        addTaskCast(actor, 4047, 6);
        doTask();
    }
}
