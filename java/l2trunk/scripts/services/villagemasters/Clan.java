package l2trunk.scripts.services.villagemasters;

import l2trunk.gameserver.scripts.Functions;

public final class Clan extends Functions {
    public void CheckCreateClan() {
        if (npc == null || player == null)
            return;
        String htmltext = "clan-02.htm";
        // Player less 10 levels, and can not create clan
        if (player.getLevel() <= 9)
            htmltext = "clan-06.htm";
            // Player already is a clan by leader and can not newly create clan
        else if (player.isClanLeader())
            htmltext = "clan-07.htm";
            // Player already consists in clan and can not create clan
        else if (player.getClan() != null)
            htmltext = "clan-09.htm";
        npc.showChatWindow(player, "villagemaster/" + htmltext);
    }

    public void CheckDissolveClan() {
        if (npc == null || player == null)
            return;
        String htmltext;
        if (player.isClanLeader())
            htmltext = "clan-04.htm";
        else
            // Player already consists in clan and can not create clan
            if (player.getClan() != null)
                htmltext = "clan-08.htm";
                // Player not in clan and can not dismiss clan
            else
                htmltext = "clan-11.htm";
        npc.showChatWindow(player, "villagemaster/" + htmltext);
    }
}