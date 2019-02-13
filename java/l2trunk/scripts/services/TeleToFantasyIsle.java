package l2trunk.scripts.services;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class TeleToFantasyIsle extends Functions {
    public static final List<Location> POINTS = List.of(
            new Location(-60695, -56896, -2032),
            new Location(-59716, -55920, -2032),
            new Location(-58752, -56896, -2032),
            new Location(-59716, -57864, -2032));

    public void toFantasyIsle() {
        if (!NpcInstance.canBypassCheck(player, player.getLastNpc()))
            return;

        player.setVar("backCoords", player.getLoc().toXYZString());
        player.teleToLocation(Rnd.get(POINTS));
    }

    public void fromFantasyIsle() {
        if (player == null || npc == null)
            return;

        if (!NpcInstance.canBypassCheck(player, npc))
            return;

        String var = player.getVar("backCoords");
        if (var == null || var.equals("")) {
            teleOut();
            return;
        }
        player.teleToLocation(Location.of(var));
    }

    private void teleOut() {
        if (npc == null || !npc.isInRange(player, 1000L))
            return;

        player.teleToLocation(-44316, -113136, -80); //Orc Village
        show("I don't know from where you came here, but I can teleport you the nearest town.", player, npc);
    }
}