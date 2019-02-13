package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.MonsterInstance;

import java.util.List;
import java.util.Objects;

public final class ShiftAggression extends Skill {
    public ShiftAggression(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (! (activeChar instanceof Player))
            return;

        Player player = (Player) activeChar;
        targets.stream()
                .filter(target -> target instanceof MonsterInstance)
                .map(target -> (MonsterInstance) target)
                .forEach(monster ->
                        World.getAroundNpc(player, skillRadius, skillRadius)
                                .filter(npc -> npc.getAggroList().get(player) != null)
                                .forEach(npc -> {
                                    npc.getAggroList().addDamageHate(monster, 0, npc.getAggroList().get(player).hate);
                                    npc.getAggroList().remove(player, true);
                                }));


        if (isSSPossible())
            player.unChargeShots(isMagic());
    }
}
