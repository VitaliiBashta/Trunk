package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class OutpostCaptain extends Fighter {
    private boolean _attacked = false;

    public OutpostCaptain(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        if (attacker == null || attacker.getPlayer() == null)
            return;

        World.getAroundNpc(getActor(), 3000, 2000)
                .filter(minion -> minion.getNpcId() == 22358 || minion.getNpcId() == 22357)
                .forEach(minion ->
                        minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000));

        if (!_attacked) {
            Functions.npcSay(getActor(), "Fool, you and your friends will die! Attack!");
            _attacked = true;
        }
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

}