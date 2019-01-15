package l2trunk.scripts.ai.seedofdestruction;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class GreatPowerfulDevice extends DefaultAI {
    private static final List<Integer> MOBS = List.of(22540, // White Dragon Leader
            22546, // Warrior of Light
            22542, // Dragon Steed Troop Magic Leader
            22547, // Dragon Steed Troop Healer
            22538); // Dragon Steed Troop Commander
    private static final Location OBELISK_LOC = new Location(-245825, 217075, -12208);

    public GreatPowerfulDevice(NpcInstance actor) {
        super(actor);
        actor.setBlock(true);
        actor.startDamageBlocked();
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        if (checkAllDestroyed(actor.getNpcId())) {
            // Спаун мобов вокруг обелиска
            for (int i = 0; i < 6; i++)
                MOBS.forEach(mobId ->
                        actor.getReflection().addSpawnWithoutRespawn(mobId, Location.findPointToStay(OBELISK_LOC.clone().setZ(-12224), 600, 1200, actor.getGeoIndex()), 0));
            actor.getReflection().openDoor(12240027);
            actor.getReflection().getNpcs()
                    .filter(n -> n.getNpcId() == 18778)
                    .forEach(Creature::stopDamageBlocked);
        }
        super.onEvtDead(killer);
    }

    private boolean checkAllDestroyed(int mobId) {
        return getActor().getReflection().getNpcs()
                .filter(n -> (n.getNpcId() == mobId))
                .allMatch(Creature::isDead);
    }
}