package l2trunk.scripts.npc.model.residences.castle;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class VenomTeleportCubicInstance extends NpcInstance {
    private static final List<Location> LOCS = List.of(
                    new Location(11913, -48851, -1088),
            new Location(11918, -49447, -1088));

    public VenomTeleportCubicInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;
        player.teleToLocation(Rnd.get(LOCS));
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "residence2/castle/teleport_cube_benom001.htm");
    }
}
