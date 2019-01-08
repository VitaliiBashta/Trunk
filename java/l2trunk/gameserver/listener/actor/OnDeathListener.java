package l2trunk.gameserver.listener.actor;

import l2trunk.gameserver.listener.CharListener;
import l2trunk.gameserver.model.Creature;

@FunctionalInterface
public interface OnDeathListener extends CharListener {
    void onDeath(Creature actor, Creature killer);
}
