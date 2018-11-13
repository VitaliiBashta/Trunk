package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class Furnace extends Fighter {
    private static final Logger LOG = LoggerFactory.getLogger(Furnace.class);
    private static final long NextAtack = 10L * 1000L; // 10 seconds supposedly TODO
    private long _lastAttackTime = 0;

    private static final int[] Magic_Power = {22800, 22800, 22800, 22800, 22800,
            22800, 22800, 22800, 22800, 22800,
            22800, 22800, 22800, 22800, 22798,
            22798, 22799, 22799};
    private static final int[] Protection = {22798, 22798, 22798, 22798, 22798,
            22798, 22798, 22798, 22798, 22798,
            22798, 22798, 22798, 22798, 22800,
            22800, 22799, 22799};
    private static final int[] Fighting_Spirit = {22799, 22799, 22799, 22799, 22799,
            22799, 22799, 22799, 22799, 22799,
            22799, 22799, 22799, 22799, 22800,
            22800, 22798, 22798};
    private static final int[] Balance = {22800, 22800, 22800, 22800, 22800, 22800,
            22798, 22798, 22798, 22798, 22798, 22798,
            22799, 22799, 22799, 22799, 22799, 22799,};

    private Furnace(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (_lastAttackTime + NextAtack < System.currentTimeMillis() && actor.getTitle() != null) {
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
            } else {

            }

        }
        super.onEvtAttacked(attacker, damage);
    }

    private void spawnMob(int[] mob) {
        for (int npcId : mob) {
            NpcInstance actor = getActor();
            SimpleSpawner spawn;
            try {
                spawn = new SimpleSpawner(NpcHolder.getInstance().getTemplate(npcId));
                spawn.setLoc(Location.coordsRandomize(actor.getLoc(), 50, 200));
                spawn.doSpawn(true);
            } catch (RuntimeException e) {
                LOG.error("Error while Spawning Furnace", e);
            }
        }
    }

    private void unSpawnMob() {
        NpcInstance actor = getActor();
        for (NpcInstance npc : World.getAroundNpc(actor, 500, 100)) {
            if (npc.getNpcId() == 22799 || npc.getNpcId() == 22798 || npc.getNpcId() == 22800) {
                npc.decayMe();
            }
        }
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null || actor.isDead())
            return true;

        if (_lastAttackTime != 0 && _lastAttackTime + NextAtack < System.currentTimeMillis()) {

            if (actor.getTitle() == "Furnace of Magic Power") {
                changestate(actor, 2);
            } else if (actor.getTitle() == "Furnace of Fighting Spirit") {
                changestate(actor, 2);
            } else if (actor.getTitle() == "Furnace of Protection") {
                changestate(actor, 2);
            } else if (actor.getTitle() == "Furnace of Balance") {
                changestate(actor, 2);
            } else {
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