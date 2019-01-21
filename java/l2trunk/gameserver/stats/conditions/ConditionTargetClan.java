package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.stats.Env;

import static l2trunk.commons.lang.NumberUtils.toBoolean;

public final class ConditionTargetClan extends Condition {
    private final boolean test;

    public ConditionTargetClan(String param) {
        test = toBoolean(param);
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature character = env.character;
        Creature target = env.target;
        return character.getPlayer() != null
                && target.getPlayer() != null
                && (character.getPlayer().getClanId() != 0
                && character.getPlayer().getClanId() == target.getPlayer().getClanId() == test
                || character.getPlayer().getParty() != null
                && character.getPlayer().getParty() == target.getPlayer().getParty());
    }
}