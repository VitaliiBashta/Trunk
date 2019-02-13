package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.stats.Env;

public class ConditionTargetRelation extends Condition {
    private final Relation _state;

    public ConditionTargetRelation(Relation state) {
        _state = state;
    }

    public static Relation getRelation(Creature activeChar, Creature aimingTarget) {
        if (activeChar instanceof Playable) {
            if (aimingTarget instanceof MonsterInstance)
                return Relation.Enemy;
            Player player = ((Playable)activeChar).getPlayer();

            if (aimingTarget instanceof Playable) {
                Player target = ((Playable)aimingTarget).getPlayer();

                if (player.getParty() != null && target.getParty() != null && player.getParty() == target.getParty())
                    return Relation.Friend;
                //		if (player.getClan() != null && target.getClan() != null && player.getClan() == target.getClan())
                //		     return Relation.Friend;
                //		if (player.getAlliance() != null && target.getAlliance() != null && player.getAlliance() == target.getAlliance())
                //		     return Relation.Friend;
                if (player.isInOlympiadMode() && player.isOlympiadCompStarted() && player.getOlympiadSide() == target.getOlympiadSide())

                    return Relation.Friend;
                if (player.getTeam() != TeamType.NONE && target.getTeam() != TeamType.NONE && player.getTeam() == target.getTeam())
                    return Relation.Friend;
                if (player.getClanId() != 0 && player.getClanId() == target.getClanId() && !player.isInOlympiadMode())
                    return Relation.Friend;
                if (player.getAllyId() != 0 && player.getAllyId() == target.getAllyId() && !player.isInOlympiadMode())
                    return Relation.Friend;
                if (activeChar.isInZoneBattle())
                    return Relation.Enemy;
                if (activeChar.isInZonePvP())
                    return Relation.Enemy;
                if (activeChar.isInZonePeace())
                    return Relation.Neutral;
                if (player.atMutualWarWith(target))
                    return Relation.Enemy;
                if (target.getKarma() > 0)
                    return Relation.Enemy;
            }
        }
        return Relation.Neutral;
    }

    @Override
    protected boolean testImpl(Env env) {
        return getRelation(env.character, env.target) == _state;
    }

    public enum Relation {
        Neutral,
        Friend,
        Enemy
    }
}