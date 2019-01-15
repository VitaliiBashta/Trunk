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
        Player player = getSelf();

        if (!NpcInstance.canBypassCheck(player, player.getLastNpc()))
            return;

        player.setVar("backCoords", player.getLoc().toXYZString(), -1);
        player.teleToLocation(Rnd.get(POINTS));
    }

    public void fromFantasyIsle() {
        Player player = getSelf();
        NpcInstance npc = getNpc();
        if (player == null || npc == null)
            return;

        if (!NpcInstance.canBypassCheck(player, npc))
            return;

        String var = player.getVar("backCoords");
        if (var == null || var.equals("")) {
            teleOut();
            return;
        }
        player.teleToLocation(Location.parseLoc(var));
    }

    private void teleOut() {
        Player player = getSelf();
        NpcInstance npc = getNpc();
        if (npc == null || !npc.isInRange(player, 1000L))
            return;

        player.teleToLocation(-44316, -113136, -80); //Orc Village
        show("I don't know from where you came here, but I can teleport you the nearest town.", player, npc);
    }
}