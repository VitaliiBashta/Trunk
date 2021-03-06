package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Ranger;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class DeluLizardmanSpecialAgent extends Ranger {
    private boolean _firstTimeAttacked = true;

    public DeluLizardmanSpecialAgent(NpcInstance actor) {
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
                Functions.npcSay(actor, "How dare you interrupt our fight! Hey guys, help!");
        } else if (Rnd.chance(10))
            Functions.npcSay(actor, "Hey! Were having a duel here!");
        super.onEvtAttacked(attacker, damage);
    }
}