package l2trunk.scripts.npc.model.residences.castle;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.SiegeToggleNpcObject;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class CastleMassTeleporterInstance extends NpcInstance {
    private final Location _teleportLoc;
    private ScheduledFuture<?> _teleportTask = null;

    public CastleMassTeleporterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        _teleportLoc = Location.of(template.getAiParams().getString("teleport_loc"));
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (_teleportTask != null) {
            showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm", Map.of("%teleportIn%", getSecondsToTP()));
            return;
        }

        _teleportTask = ThreadPoolManager.INSTANCE.schedule(new TeleportTask(), isAllTowersDead() ? 480000L : 30000L);

        showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm", Map.of("%teleportIn%", getSecondsToTP()));
    }

    @Override
    public void showChatWindow(Player player, int val) {
        if (_teleportTask != null)
            showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm", Map.of("%teleportIn%", getSecondsToTP()));
        else {
            if (isAllTowersDead())
                showChatWindow(player, "residence2/castle/gludio_mass_teleporter002.htm");
            else
                showChatWindow(player, "residence2/castle/gludio_mass_teleporter001.htm");
        }
    }

    /**
     * @return Number of Seconds to next teleportation into the castle
     */
    private String getSecondsToTP() {
        if (_teleportTask == null) {
            return isAllTowersDead() ? "480" : "30";
        }
        return String.valueOf(_teleportTask.getDelay(TimeUnit.SECONDS));
    }

    private boolean isAllTowersDead() {
        SiegeEvent siegeEvent = getEvent(SiegeEvent.class);
        if (siegeEvent == null || !siegeEvent.isInProgress())
            return false;

        List<SiegeToggleNpcObject> towers = siegeEvent.getObjects(CastleSiegeEvent.CONTROL_TOWERS);
        return towers.stream().noneMatch(SiegeToggleNpcObject::isAlive);
    }

    private class TeleportTask extends RunnableImpl {
        @Override
        public void runImpl() {
            Functions.npcShout(CastleMassTeleporterInstance.this, NpcString.THE_DEFENDERS_OF_S1_CASTLE_WILL_BE_TELEPORTED_TO_THE_INNER_CASTLE, "#" + getCastle().getNpcStringName().getId());

            World.getAroundPlayers(CastleMassTeleporterInstance.this, 200, 50).forEach(p ->
                    p.teleToLocation(Location.findPointToStay(_teleportLoc, 10, 100, p.getGeoIndex())));

            _teleportTask = null;
        }
    }
}
