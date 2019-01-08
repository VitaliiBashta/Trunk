package l2trunk.scripts.ai.isle_of_prayer;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class DarkWaterDragon extends Fighter {
    private static final int FAFURION = 18482;
    private static final List<Integer> SHADES = List.of(22268, 22269);
    private static final int MOBS_COUNT = 5;
    private static final int RED_CRYSTAL = 9596;
    private int _mobsSpawned = 0;

    public DarkWaterDragon(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (!actor.isDead())
            switch (_mobsSpawned) {
                case 0:
                    _mobsSpawned = 1;
                    spawnShades(attacker);
                    break;
                case 1:
                    if (actor.getCurrentHp() < actor.getMaxHp() / 2) {
                        _mobsSpawned = 2;
                        spawnShades(attacker);
                    }
                    break;
            }

        super.onEvtAttacked(attacker, damage);
    }

    private void spawnShades(Creature attacker) {
        NpcInstance actor = getActor();
        for (int i = 0; i < MOBS_COUNT; i++) {
            SimpleSpawner sp = new SimpleSpawner(Rnd.get(SHADES));
            sp.setLoc(Location.findPointToStay(actor, 100, 120));
            NpcInstance npc = sp.doSpawn(true);
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
        }
    }

    @Override
    public void onEvtDead(Creature killer) {
        _mobsSpawned = 0;
        NpcInstance actor = getActor();
        SimpleSpawner sp = new SimpleSpawner(FAFURION);
        sp.setLoc(Location.findPointToStay(actor, 100, 120));
        sp.doSpawn(true);
        if (killer != null) {
            final Player player = killer.getPlayer();
            if (player != null)
                if (Rnd.chance(77))
                    actor.dropItem(player, RED_CRYSTAL, 1);
        }
        super.onEvtDead(killer);
    }

    @Override
    public boolean randomWalk() {
        return _mobsSpawned == 0;
    }
}