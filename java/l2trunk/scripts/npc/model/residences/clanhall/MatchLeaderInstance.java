package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class MatchLeaderInstance extends MatchBerserkerInstance {
    public MatchLeaderInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
        if (attacker instanceof Player)
            damage = ((damage / getMaxHp()) / 0.05) * 100;
        else
            damage = ((damage / getMaxHp()) / 0.05) * 10;

        super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
    }
}
