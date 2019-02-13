package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class MatchScoutInstance extends CTBBossInstance {
    private long massiveDamage;

    public MatchScoutInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
        if (massiveDamage > System.currentTimeMillis())
            damage = 10000;
        else if (getCurrentHpPercents() > 50) {
            if (attacker instanceof Player)
                damage = ((damage / getMaxHp()) / 0.05) * 100;
            else
                damage = ((damage / getMaxHp()) / 0.05) * 10;
        } else if (getCurrentHpPercents() > 30) {
            if (Rnd.chance(90)) {
                if (attacker instanceof Player)
                    damage = ((damage / getMaxHp()) / 0.05) * 100;
                else
                    damage = ((damage / getMaxHp()) / 0.05) * 10;
            } else
                massiveDamage = System.currentTimeMillis() + 5000L;
        } else
            massiveDamage = System.currentTimeMillis() + 5000L;

        super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
    }
}
