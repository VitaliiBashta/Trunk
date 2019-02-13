package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.Arrays;
import java.util.List;


public final class Furnace extends Fighter {
    private static final long NEXT_ATACK = 10 * 1000L; // 10 seconds supposedly
    private static final List<Integer> Magic_Power =List.of(
            22800, 22800, 22800, 22800, 22800, 22800, 22800, 22800, 22800,
            22800, 22800, 22800, 22800, 22800, 22798, 22798, 22799, 22799);
    private static final List<Integer> Protection =List.of(
            22798, 22798, 22798, 22798, 22798, 22798, 22798, 22798, 22798,
            22798, 22798, 22798, 22798, 22798, 22800, 22800, 22799, 22799);
    private static final List<Integer> Fighting_Spirit =List.of(
            22799, 22799, 22799, 22799, 22799, 22799, 22799, 22799, 22799,
            22799, 22799, 22799, 22799, 22799, 22800, 22800, 22798, 22798);
    private static final List<Integer> Balance =List.of(
            22800, 22800, 22800, 22800, 22800, 22800, 22798, 22798, 22798,
            22798, 22798, 22798, 22799, 22799, 22799, 22799, 22799, 22799);
    private static final List<Integer> allMobs = List.of(22798,22799,22800);
    private long _lastAttackTime = 0;

    public Furnace(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (_lastAttackTime + NEXT_ATACK < System.currentTimeMillis() && actor.getTitle() != null) {
            if ("Furnace of Magic Power".equals(actor.getTitle())) {
                changestate(actor, 1);
                unSpawnMob();
                spawnMob(Magic_Power);
                _lastAttackTime = System.currentTimeMillis();
            } else if ("Furnace of Fighting Spirit".equals(actor.getTitle())) {
                changestate(actor, 1);
                unSpawnMob();
                spawnMob(Fighting_Spirit);
                _lastAttackTime = System.currentTimeMillis();
            } else if ("Furnace of Protection".equals(actor.getTitle())) {
                changestate(actor, 1);
                unSpawnMob();
                spawnMob(Protection);
                _lastAttackTime = System.currentTimeMillis();
            } else if ("Furnace of Balance".equals(actor.getTitle())) {
                changestate(actor, 1);
                unSpawnMob();
                spawnMob(Balance);
                _lastAttackTime = System.currentTimeMillis();
            }

        }
        super.onEvtAttacked(attacker, damage);
    }

    private void spawnMob(List<Integer> mobs) {
        mobs.forEach(id -> new SimpleSpawner(id)
                .setLoc(Location.coordsRandomize(getActor().getLoc(), 50, 200))
                .doSpawn(true));
    }

    private void unSpawnMob() {
        NpcInstance actor = getActor();
        World.getAroundNpc(actor, 500, 100)
                .filter(npc -> allMobs.contains(npc.getNpcId()))
                .forEach(GameObject::decayMe);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null || actor.isDead())
            return true;

        if (_lastAttackTime != 0 && _lastAttackTime + NEXT_ATACK < System.currentTimeMillis()) {

            switch (actor.getTitle()) {
                case "Furnace of Magic Power":
                    changestate(actor, 2);
                    break;
                case "Furnace of Fighting Spirit":
                    changestate(actor, 2);
                    break;
                case "Furnace of Protection":
                    changestate(actor, 2);
                    break;
                case "Furnace of Balance":
                    changestate(actor, 2);
                    break;
                default:
                    return false;
            }
            _lastAttackTime = 0;
        }

        return super.thinkActive();
    }

    private void changestate(NpcInstance actor, int state) {
        actor.setNpcState(state);
    }
}