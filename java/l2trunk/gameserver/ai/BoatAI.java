package l2trunk.gameserver.ai;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.boat.Boat;

public class BoatAI extends CharacterAI {
    public BoatAI(Creature actor) {
        super(actor);
    }

    @Override
    protected void onEvtArrived() {
        Boat actor = (Boat) getActor();
        if (actor == null)
            return;

        actor.onEvtArrived();
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}
