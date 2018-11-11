package l2trunk.gameserver.listener.actor;

import l2trunk.gameserver.listener.CharListener;
import l2trunk.gameserver.model.Creature;

public interface OnAttackListener extends CharListener
{
	void onAttack(Creature actor, Creature target);
}
