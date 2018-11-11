package l2trunk.gameserver.listener.actor;

import l2trunk.gameserver.listener.CharListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;

public interface OnMagicHitListener extends CharListener
{
	void onMagicHit(Creature actor, Skill skill, Creature caster);
}
