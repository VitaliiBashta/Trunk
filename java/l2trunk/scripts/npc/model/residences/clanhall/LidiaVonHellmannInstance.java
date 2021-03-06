package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.npc.model.residences.SiegeGuardInstance;

import java.util.HashMap;
import java.util.Map;

public final class LidiaVonHellmannInstance extends SiegeGuardInstance {
    public LidiaVonHellmannInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onDeath(Creature killer) {
        SiegeEvent siegeEvent = getEvent(SiegeEvent.class);
        if (siegeEvent == null)
            return;

        siegeEvent.processStep(getMostDamagedClan());

        super.onDeath(killer);
    }

    private Clan getMostDamagedClan() {
        ClanHallSiegeEvent siegeEvent = getEvent(ClanHallSiegeEvent.class);

        Player temp = null;

        Map<Player, Integer> damageMap = new HashMap<>();

        for (AggroList.HateInfo info : getAggroList().getPlayableMap().values()) {
            Playable killer = (Playable) info.attacker;
            int damage = info.damage;
            if (killer instanceof Summon)
                temp = ((Summon)killer).owner;
            else if (killer instanceof Player)
                temp = (Player) killer;

            if (temp == null || siegeEvent.getSiegeClan(SiegeEvent.ATTACKERS, temp.getClan()) == null)
                continue;

            if (!damageMap.containsKey(temp))
                damageMap.put(temp, damage);
            else {
                int dmg = damageMap.get(temp) + damage;
                damageMap.put(temp, dmg);
            }
        }

        int mostDamage = 0;
        Player player = null;

        for (Map.Entry<Player, Integer> entry : damageMap.entrySet()) {
            int damage = entry.getValue();
            Player t = entry.getKey();
            if (damage > mostDamage) {
                mostDamage = damage;
                player = t;
            }
        }

        return player == null ? null : player.getClan();
    }

    @Override
    public boolean isEffectImmune() {
        return true;
    }
}
