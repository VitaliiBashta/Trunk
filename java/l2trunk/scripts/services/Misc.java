package l2trunk.scripts.services;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.ItemFunctions;

/**
 * @author pchayka
 */
class Misc extends Functions {
    public void assembleAntharasCrystal() {


        Player player = getSelf();
        NpcInstance npc = getNpc();

        if (player == null || npc == null || !NpcInstance.canBypassCheck(player, player.getLastNpc()))
            return;

        if (ItemFunctions.getItemCount(player, 17266) < 1 || ItemFunctions.getItemCount(player, 17267) < 1) {
            show("teleporter/32864-2.htm", player);
            return;
        }
        if (ItemFunctions.removeItem(player, 17266, 1, true, "assembleAntharasCrystal") > 0 && ItemFunctions.removeItem(player, 17267, 1, true, "assembleAntharasCrystal") > 0) {
            ItemFunctions.addItem(player, 17268, 1, true, "assembleAntharasCrystal");
            show("teleporter/32864-3.htm", player);
            return;
        }
    }

}