package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;

import java.util.List;
import java.util.Objects;

public final class Effect extends Skill {
    public Effect(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        getEffects(activeChar, target);
    }
}