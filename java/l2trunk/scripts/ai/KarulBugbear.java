package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Ranger;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class KarulBugbear extends Ranger {
    private boolean _firstTimeAttacked = true;

    public KarulBugbear(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        _firstTimeAttacked = true;
        super.onEvtSpawn();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (_firstTimeAttacked) {
            _firstTimeAttacked = false;
            if (Rnd.chance(25))
                Functions.npcSay(actor, "Your rear is practically unguarded!");
        } else if (Rnd.chance(10))
            Functions.npcSay(actor, "Watch your back!");
        super.onEvtAttacked(attacker, damage);
    }
}