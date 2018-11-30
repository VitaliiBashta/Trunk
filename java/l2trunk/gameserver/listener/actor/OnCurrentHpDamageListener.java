package l2trunk.gameserver.listener.actor;

import l2trunk.gameserver.listener.CharListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;

public interface OnCurrentHpDamageListener extends CharListener {
    void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill);
}
