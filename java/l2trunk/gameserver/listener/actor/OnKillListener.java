package l2trunk.gameserver.listener.actor;

import l2trunk.gameserver.listener.CharListener;
import l2trunk.gameserver.model.Creature;

public interface OnKillListener extends CharListener {
    void onKill(Creature actor, Creature victim);

    boolean ignorePetOrSummon();
}
