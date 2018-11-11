package l2trunk.scripts.services;

import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.quests._240_ImTheOnlyOneYouCanTrust;

class TeleToStakatoNest extends Functions {
    private final static Location[] teleports = {
            new Location(80456, -52322, -5640),
            new Location(88718, -46214, -4640),
            new Location(87464, -54221, -5120),
            new Location(80848, -49426, -5128),
            new Location(87682, -43291, -4128)};

    public void list() {
        Player player = getSelf();
        NpcInstance npc = getNpc();
        if (player == null || npc == null)
            return;

        QuestState qs = player.getQuestState(_240_ImTheOnlyOneYouCanTrust.class);
        if (qs == null || !qs.isCompleted()) {
            show("scripts/services/TeleToStakatoNest-no.htm", player);
            return;
        }

        show("scripts/services/TeleToStakatoNest.htm", player);
    }

    public void teleTo(String[] args) {
        Player player = getSelf();
        NpcInstance npc = getNpc();
        if (player == null || npc == null || !npc.isInRange(player, 1000L))
            return;
        if (args.length != 1)
            return;

        Location loc = teleports[Integer.parseInt(args[0]) - 1];
        Party party = player.getParty();
        if (party == null)
            player.teleToLocation(loc);
        else
            for (Player member : party.getMembers())
                if (member != null && member.isInRange(player, 1000))
                    member.teleToLocation(loc);
    }
}