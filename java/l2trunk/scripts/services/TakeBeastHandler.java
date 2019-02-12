package l2trunk.scripts.services;

import l2trunk.gameserver.scripts.Functions;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;

public final class TakeBeastHandler extends Functions {
    private static final int BEAST_WHIP = 15473;

    public void show() {
        if (npc == null || !npc.isInRange(player, 1000L))
            return;

        String htmltext;
        if (player.getLevel() < 82)
            htmltext = npc.getNpcId() + "-1.htm";
        else if (player.haveItem(BEAST_WHIP))
            htmltext = npc.getNpcId() + "-2.htm";
        else {
            addItem(player, BEAST_WHIP, 1);
            htmltext = npc.getNpcId() + "-3.htm";
        }

        npc.showChatWindow(player, "default/" + htmltext);
    }
}
