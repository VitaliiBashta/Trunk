package l2trunk.scripts.services.villagemasters;

import l2trunk.gameserver.scripts.Functions;

public final class Ally extends Functions {
    public void CheckCreateAlly() {
        if (npc == null || player == null)
            return;
        String htmltext = "ally-01.htm";
        if (player.isClanLeader())
            htmltext = "ally-02.htm";
        npc.showChatWindow(player, "villagemaster/" + htmltext);
    }

    public void CheckDissolveAlly() {
        if (npc == null || player == null)
            return;
        String htmltext = "ally-01.htm";
        if (player.isAllyLeader())
            htmltext = "ally-03.htm";
        npc.showChatWindow(player, "villagemaster/" + htmltext);
    }
}