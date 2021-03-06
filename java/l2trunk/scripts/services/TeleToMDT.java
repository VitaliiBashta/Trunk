package l2trunk.scripts.services;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

public final class TeleToMDT extends Functions {
    public void toMDT() {
        if (player == null || npc == null)
            return;

        if (!NpcInstance.canBypassCheck(player, npc))
            return;

        player.setVar("backCoords", player.getLoc().toXYZString());
        player.teleToLocation(12661, 181687, -3560);
    }

    public void fromMDT() {
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
        if (player == null || npc == null)
            return;
        player.teleToLocation(12902, 181011, -3563);
        show("I don't know from where you came here, but I can teleport you the another border side.", player, npc);
    }
}