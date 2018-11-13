package l2trunk.scripts.ai.custom;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class SSQLilimServantMage extends Mystic {
    private boolean _attacked = false;

    public SSQLilimServantMage(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        super.onEvtAttacked(attacker, damage);
        if (Rnd.chance(30) && !_attacked) {
            Functions.npcSay(getActor(), "Who dares enter this place?");
            _attacked = true;
        }
    }

    @Override
    public void onEvtDead(Creature killer) {
        if (Rnd.chance(30))
            Functions.npcSay(getActor(), "Lord Shilen... some day... you will accomplish... this mission...");
        super.onEvtDead(killer);
    }
}