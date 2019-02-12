package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class RainbowChestInstance extends MonsterInstance {
    private static final List<Integer> items = IntStream.rangeClosed(8035, 8055)
            .boxed().collect(Collectors.toList());

    public RainbowChestInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
        if (!(attacker instanceof Player) || attacker.getActiveWeaponInstance() != null || skill != null || isDot)
            return;

        super.reduceCurrentHp(getMaxHp() * 0.2, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
    }

    @Override
    public void onDeath(Creature k) {
        super.onDeath(k);

        Player killer;
        if (k instanceof Playable)
            killer = ((Playable) k).getPlayer();
        else return;
        for (int i = 0; i < 1 + Rnd.get(2); i++) {
            dropItem(killer, Rnd.get(items), 1);
        }
    }
}
