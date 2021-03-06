package l2trunk.scripts.ai.custom;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class SSQLilimServantFighter extends Fighter {
    private boolean _attacked = false;

    public SSQLilimServantFighter(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        if (Rnd.chance(30) && !_attacked) {
            Functions.npcSay(getActor(), Rnd.chance(50) ? "Those who are afraid should get away and those who are brave should fight!" : "This place once belonged to Lord Shilen.");
            _attacked = true;
        }
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        if (Rnd.chance(30))
            Functions.npcSay(getActor(), Rnd.chance(50) ? "Why are you getting in our way?" : "Shilen... our Shilen!");
        super.onEvtDead(killer);
    }
}