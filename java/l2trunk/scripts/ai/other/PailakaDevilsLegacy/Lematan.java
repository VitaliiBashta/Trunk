package l2trunk.scripts.ai.other.PailakaDevilsLegacy;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.Arrays;
import java.util.List;

public final class Lematan extends Fighter {
    private static final int LEMATAN_FOLLOWER = 18634;
    private static final List<Location> _position = List.of(
           Location.of(84840, -208488, -3336),
           Location.of(85160, -208488, -3336),
           Location.of(84696, -208744, -3336),
           Location.of(85264, -208744, -3336),
           Location.of(84840, -209000, -3336),
           Location.of(85160, -209000, -3336));
    private boolean _teleported = false;

    public Lematan(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean maybeMoveToHome() {
        if (getActor().isInRange(getActor().getSpawnedLoc(), 10000L))
            return true;
        return true;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();

        if (actor.getCurrentHp() < (actor.getMaxHp() / 2) && !_teleported) {
            // мы на корабле но у нас половина ХП и мы не телепортнулись
            actor.setSpawnedLoc(Location.of(84984, -208744, -3336));
            actor.teleToLocation(Location.of(84984, -208744, -3336));
            attacker.teleToLocation(Location.of(85128, -208744, -3336));

            _teleported = true;

            _position.forEach(loc -> new SimpleSpawner(LEMATAN_FOLLOWER)
                    .setLoc(loc)
                    .setAmount(1)
                    .setRespawnDelay(30)
                    .setReflection(actor.getReflection())
                    .init());
            super.onEvtAttacked(attacker, damage);
        }
    }
}