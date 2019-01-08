package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.Arrays;
import java.util.List;

public final class OriginalSinWarden extends Fighter {
    private static final List<Integer> servants1 = List.of(22424, 22425, 22426, 22427, 22428, 22429, 22430);
    private static final List<Integer> servants2 = List.of(22432, 22433, 22434, 22435, 22436, 22437, 22438);
    private static final List<Integer> DarionsFaithfulServants = List.of(22405, 22406, 2240);

    public OriginalSinWarden(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        NpcInstance actor = getActor();
        if (actor.getNpcId() == 22423) spawnServants(servants1);
        if (actor.getNpcId() == 22431) spawnServants(servants2);
    }

    private void spawnServants(List<Integer> servants) {
        servants.forEach(s -> new SimpleSpawner(s)
                .setLoc(Location.findPointToStay(actor, 150, 350))
                .stopRespawn()
                .doSpawn(true));
    }

    @Override
    public void onEvtDead(Creature killer) {
        int rndId = Rnd.get(DarionsFaithfulServants.size() - 1);
        if (Rnd.chance(15)) {
            new SimpleSpawner(DarionsFaithfulServants.get(rndId))
                    .setLoc(Location.findPointToStay(getActor(), 150, 350))
                    .stopRespawn()
                    .doSpawn(true);
        }
        super.onEvtDead(killer);
    }

}