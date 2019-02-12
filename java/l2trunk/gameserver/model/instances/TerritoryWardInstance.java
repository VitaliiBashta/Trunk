package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.TerritoryWardObject;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class TerritoryWardInstance extends NpcInstance {
    private final TerritoryWardObject _territoryWard;

    public TerritoryWardInstance(int objectId, NpcTemplate template, TerritoryWardObject territoryWardObject) {
        super(objectId, template);
        setHasChatWindow(false);
        _territoryWard = territoryWardObject;
    }

    @Override
    public void onDeath(Creature killer) {
        super.onDeath(killer);

        final Player player = killer instanceof Playable ? ((Playable)killer).getPlayer(): null;
        if (player == null)
            return;

        if (_territoryWard.canPickUp(player)) {
            _territoryWard.pickUp(player);
            decayMe();
        }
    }

    @Override
    protected void onDecay() {
        decayMe();

        _spawnAnimation = 2;
    }

    @Override
    public boolean isAttackable(Creature attacker) {
        return isAutoAttackable(attacker);
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        DominionSiegeEvent siegeEvent = getEvent(DominionSiegeEvent.class);
        if (siegeEvent == null)
            return false;
        DominionSiegeEvent siegeEvent2 = attacker.getEvent(DominionSiegeEvent.class);
        if (siegeEvent2 == null)
            return false;
        if (siegeEvent == siegeEvent2)
            return false;
        return siegeEvent2.getResidence().getOwner() == ((Player) attacker).getClan();
    }

    @Override
    public boolean isInvul() {
        return false;
    }

    @Override
    public Clan getClan() {
        return null;
    }
}
