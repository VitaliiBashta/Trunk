package l2trunk.scripts.npc.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.instancemanager.DimensionalRiftManager;
import l2trunk.gameserver.instancemanager.DimensionalRiftManager.DimensionalRiftRoom;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.DelusionChamber;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.Map;

/**
 * @author pchayka
 */

public final class DelustionGatekeeperInstance extends NpcInstance {
    public DelustionGatekeeperInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.startsWith("enterDC")) {
            int izId = Integer.parseInt(command.substring(8));
            int type = izId - 120;
            Map<Integer, DimensionalRiftRoom> rooms = DimensionalRiftManager.getInstance().getRooms(type);
            if (rooms == null) {
                player.sendPacket(Msg.SYSTEM_ERROR);
                return;
            }
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (player.canReenterInstance(izId))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } else if (player.canEnterInstance(izId)) {
                Party party = player.getParty();
                if (party != null)
                    new DelusionChamber(party, type, Rnd.get(1, rooms.size() - 1));
            }
        } else
            super.onBypassFeedback(player, command);
    }
}