package l2trunk.gameserver.listener.actor;

import l2trunk.gameserver.listener.CharListener;
import l2trunk.gameserver.model.Creature;

public interface OnAttackHitListener extends CharListener {
    void onAttackHit(Creature actor, Creature attacker);
}
