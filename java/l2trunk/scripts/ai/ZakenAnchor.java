package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class ZakenAnchor extends DefaultAI {
    private static final int DayZaken = 29176;
    private static final int UltraDayZaken = 29181;
    private static final int Candle = 32705;

    public ZakenAnchor(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        int i1 = (int) actor.getAroundNpc(1000, 100)
                .filter(npc -> npc.getNpcId() == Candle)
                .filter(npc -> npc.getRightHandItem() == 15302)
                .count();

        if (i1 >= 4) {
            if (actor.getReflection().getInstancedZoneId() == 133) {
                actor.getReflection().addSpawnWithoutRespawn(DayZaken, actor);
                for (int i = 0; i < 4; i++) {
                    actor.getReflection().addSpawnWithoutRespawn(20845, actor, 200);
                    actor.getReflection().addSpawnWithoutRespawn(20847, actor, 200);
                }
                actor.deleteMe();
                return true;
            } else if (actor.getReflection().getInstancedZoneId() == 135) {
                actor.getReflection().getNpcs()
                        .filter(npc -> (npc.getNpcId() == UltraDayZaken))
                        .forEach(npc -> {
                            npc.setInvul(false);
                            npc.teleToLocation(actor.getLoc());
                        });
                //actor.getReflection().addSpawnWithoutRespawn(UltraDayZaken, actor.getTerritory(), 0);
                for (int i = 0; i < 4; i++) {
                    actor.getReflection().addSpawnWithoutRespawn(29184, actor, 300);
                    actor.getReflection().addSpawnWithoutRespawn(29183, actor, 300);
                }
                actor.deleteMe();
                return true;
            }
        }

        return false;
    }
}