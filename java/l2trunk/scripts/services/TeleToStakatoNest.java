package l2trunk.scripts.services;

import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.quests._240_ImTheOnlyOneYouCanTrust;

import java.util.Objects;

public final class TeleToStakatoNest extends Functions {
    private final static Location[] teleports = {
            new Location(80456, -52322, -5640),
            new Location(88718, -46214, -4640),
            new Location(87464, -54221, -5120),
            new Location(80848, -49426, -5128),
            new Location(87682, -43291, -4128)};

    public void list() {
        if (player == null || npc == null)
            return;

        if (!player.isQuestCompleted(_240_ImTheOnlyOneYouCanTrust.class)) {
            show("scripts/services/TeleToStakatoNest-no.htm", player);
            return;
        }

        show("scripts/services/TeleToStakatoNest.htm", player);
    }

    public void teleTo(String[] args) {
        if (npc == null || !npc.isInRange(player, 1000L))
            return;
        if (args.length != 1)
            return;

        Location loc = teleports[Integer.parseInt(args[0]) - 1];
        Party party = player.getParty();
        if (party == null)
            player.teleToLocation(loc);
        else
            party.getMembersStream()
                    .filter(Objects::nonNull)
                    .filter(member -> member.isInRange(player, 1000))
                    .forEach(member -> member.teleToLocation(loc));
    }
}