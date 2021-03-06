package l2trunk.scripts.services;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

import java.util.ArrayList;
import java.util.List;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class ObtainTalisman extends Functions {
    public void Obtain() {
        if (player == null || npc == null)
            return;

        if (!NpcInstance.canBypassCheck(player, npc))
            return;

        if (!player.isQuestContinuationPossible(false)) {
            player.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
            return;
        }

        if (!player.haveItem(9912, 200)) {
            show("scripts/services/ObtainTalisman-no.htm", player, npc);
            return;
        }

        final List<Integer> talismans = new ArrayList<>();

        // 9914-9965
        for (int i = 9914; i <= 9965; i++)
            if (i != 9923)
                talismans.add(i);
        // 10416-10424
        for (int i = 10416; i <= 10424; i++)
            talismans.add(i);
        // 10518-10519
        for (int i = 10518; i <= 10519; i++)
            talismans.add(i);
        // 10533-10543
        for (int i = 10533; i <= 10543; i++)
            talismans.add(i);

        removeItem(player, 9912, 200, "ObtainTalisman");
        for (int i = 0; i < 20; i++) {
            addItem(player, Rnd.get(talismans), 1);
        }
        show("scripts/services/ObtainTalisman.htm", player, npc);
    }
}