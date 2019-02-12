package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.List;

public final class ConditionTargetRace extends Condition {
    private final int race;
    private static final List<String> races = List.of(
            "<none>", "Undead", "MagicCreatures", "Beasts", "Animals", "Plants",
            "Humanoids", "Spirits", "Angels", "Demons", "Dragons", "Giants", "Bugs",
            "Fairies", "Humans", "Elves", "DarkElves", "Orcs", "Dwarves", "Others",
            "NonLivingBeings", "SiegeWeapons", "DefendingArmy", "Mercenaries", "UnknownCreature", "Kamael");

    public ConditionTargetRace(String race) {

        // Раса определяется уровнем(1-25) скила 4416
        for (int i = 0; i < races.size(); i++)
            if (races.get(i).equalsIgnoreCase(race)) {
                this.race = i;
                return;
            }

        throw new IllegalArgumentException("ConditionTargetRace: Invalid race name: " + race);
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature target = env.target;
        if (target != null)
            if (target.getTemplate() != null)
                if (target instanceof SummonInstance || target instanceof NpcInstance)
                    if (race == ((NpcTemplate) target.getTemplate()).getRace()) return true;
        return false;
    }
}