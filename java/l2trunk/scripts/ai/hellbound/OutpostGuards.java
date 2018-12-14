package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;

public class OutpostGuards extends Fighter {
    public OutpostGuards(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }
}