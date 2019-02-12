package l2trunk.scripts.services;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.ItemFunctions;


public final class Misc extends Functions {
    public void assembleAntharasCrystal() {

        if (player == null || npc == null || !NpcInstance.canBypassCheck(player, player.getLastNpc()))
            return;

        if (!player.haveItem(17266) || !player.haveItem(17267)) {
            show("teleporter/32864-2.htm", player);
            return;
        }
        if (ItemFunctions.removeItem(player, 17266, 1, "assembleAntharasCrystal") > 0 && ItemFunctions.removeItem(player, 17267, 1, "assembleAntharasCrystal") > 0) {
            ItemFunctions.addItem(player, 17268, 1, "assembleAntharasCrystal");
            show("teleporter/32864-3.htm", player);
        }
    }

}