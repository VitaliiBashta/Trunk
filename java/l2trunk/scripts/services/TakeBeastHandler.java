package l2trunk.scripts.services;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class TakeBeastHandler extends Functions {
    private final int BEAST_WHIP = 15473;

    public void show() {
        Player player = getSelf();
        NpcInstance npc = getNpc();

        if (npc == null || !npc.isInRange(player, 1000L))
            return;

        String htmltext;
        if (player.getLevel() < 82)
            htmltext = npc.getNpcId() + "-1.htm";
        else if (Functions.getItemCount(player, BEAST_WHIP) > 0)
            htmltext = npc.getNpcId() + "-2.htm";
        else {
            Functions.addItem(player, BEAST_WHIP, 1, "TakeBeastHandler");
            htmltext = npc.getNpcId() + "-3.htm";
        }

        npc.showChatWindow(player, "default/" + htmltext);
    }
}
