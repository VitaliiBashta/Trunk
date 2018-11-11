package l2trunk.scripts.services;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

/**
 * Author: Bonux
 **/
class TeleToGracia extends Functions {
    public void tele() {
        Player player = getSelf();
        NpcInstance npc = getNpc();

        if (player != null && npc != null && npc.isInRange(player, 1000L))
            if (player.getLevel() < 75)
                show("teleporter/" + npc.getNpcId() + "-4.htm", player);
            else if (player.getAdena() >= 150000) {
                player.reduceAdena(150000, true, "TeleToGracia");
                player.teleToLocation(-149406, 255247, -80);
            } else
                show("teleporter/" + npc.getNpcId() + "-2.htm", player);
    }
}
