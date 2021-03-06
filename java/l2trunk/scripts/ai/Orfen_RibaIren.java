package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class Orfen_RibaIren extends Fighter {
    private static final int Orfen_id = 29014;

    public Orfen_RibaIren(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean createNewTask() {
        return defaultNewTask();
    }

    @Override
    public void onEvtClanAttacked(Creature attacked_member, Creature attacker, int damage) {
        super.onEvtClanAttacked(attacked_member, attacker, damage);
        NpcInstance actor = getActor();
        if (healSkills.size() == 0)
            return;
        if (attacked_member.isDead() || actor.isDead() || attacked_member.getCurrentHpPercents() > 50)
            return;

        int heal_chance = 0;
        if (attacked_member.getNpcId() == actor.getNpcId())
            heal_chance = attacked_member.objectId() == actor.objectId() ? 100 : 0;
        else
            heal_chance = attacked_member.getNpcId() == Orfen_id ? 90 : 10;

        if (Rnd.chance(heal_chance) && canUseSkill(healSkills.get(0), attacked_member, -1))
            addTaskAttack(attacked_member, healSkills.get(0).id, healSkills.get(0).level);
    }
}