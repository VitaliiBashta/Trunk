package l2trunk.scripts.handler.bypass;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.handler.bypass.BypassHandler;
import l2trunk.gameserver.handler.bypass.IBypassHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class TeleToFantasyIsle implements ScriptFile, IBypassHandler {
    private static final List<Location> POINTS = List.of(
                    new Location(-60695, -56896, -2032),
                    new Location(-59716, -55920, -2032),
                    new Location(-58752, -56896, -2032),
                    new Location(-59716, -57864, -2032));

    @Override
    public String getBypasses() {
        return "teleToFantasyIsle";
    }

    @Override
    public void onBypassFeedback(NpcInstance npc, Player player, String command) {
        player.teleToLocation(Rnd.get(POINTS));
    }

    @Override
    public void onLoad() {
        BypassHandler.getInstance().registerBypass(this);
    }

}
