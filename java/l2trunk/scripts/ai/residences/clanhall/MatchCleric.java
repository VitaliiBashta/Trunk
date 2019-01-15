package l2trunk.scripts.ai.residences.clanhall;

import l2trunk.gameserver.model.instances.NpcInstance;

public final class MatchCleric extends MatchFighter {
    private static final int HEAL = 4056;

    public MatchCleric(NpcInstance actor) {
        super(actor);
    }

    public void heal() {
        NpcInstance actor = getActor();
        addTaskCast(actor, HEAL, 6);
        doTask();
    }
}
