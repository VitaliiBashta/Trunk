package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.templates.item.ArmorTemplate.ArmorType;

public final class ConditionUsingArmor extends Condition {
    private final ArmorType armor;

    public ConditionUsingArmor(ArmorType armor) {
        this.armor = armor;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.character instanceof Player && ((Player) env.character).isWearingArmor(armor);

    }
}
