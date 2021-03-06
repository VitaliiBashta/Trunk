package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;

import java.util.stream.IntStream;

public final class _1103_OracleTeleport extends Quest {
    private static final int GLUDIN_DAWN = 31078;
    private static final int GLUDIN_DUSK = 31085;
    private static final int GLUDIO_DAWN = 31079;
    private static final int GLUDIO_DUSK = 31086;
    private static final int DION_DAWN = 31080;
    private static final int DION_DUSK = 31087;
    private static final int GIRAN_DAWN = 31081;
    private static final int GIRAN_DUSK = 31088;
    private static final int OREN_DAWN = 31083;
    private static final int OREN_DUSK = 31090;
    private static final int ADEN_DAWN = 31084;
    private static final int ADEN_DUSK = 31091;
    private static final int HEINE_DAWN = 31082;
    private static final int HEINE_DUSK = 31089;
    private static final int GODDARD_DAWN = 31692;
    private static final int GODDARD_DUSK = 31693;
    private static final int RUNE_DAWN = 31694;
    private static final int RUNE_DUSK = 31695;
    private static final int SCHUTTGART_DAWN = 31997;
    private static final int SCHUTTGART_DUSK = 31998;
    private static final int HV_DAWN = 31168;
    private static final int HV_DUSK = 31169;

    private static final Location DAWN_LOCATION = Location.of(-80157, 111344, -4901);
    private static final Location DUSK_LOCATION = Location.of(-81261, 86531, -5157);

    public _1103_OracleTeleport() {

        addStartNpc(IntStream.rangeClosed(31078, 31091).toArray());

        addStartNpc(31168, 31169, 31170);
        addStartNpc(IntStream.rangeClosed(31692, 31699).toArray());

        addStartNpc(IntStream.rangeClosed(31127, 31142).toArray());
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        Player player = st.player;
        int back = player.getVarInt("FestivalBackCoords");
        if (back == 0)
            back = 1;

        // Dawn Locations

        String htmltext = "Started.htm";
        if (npcId == GLUDIN_DAWN) {
            player.teleToLocation(DAWN_LOCATION);
            player.setVar("id");
            return htmltext;
        }

        if (npcId == GLUDIO_DAWN) {
            player.teleToLocation(DAWN_LOCATION);
            player.setVar("id", 2);
            return htmltext;
        }

        if (npcId == DION_DAWN) {
            player.teleToLocation(DAWN_LOCATION);
            player.setVar("id", 3);
            return htmltext;
        }

        if (npcId == GIRAN_DAWN) {
            player.teleToLocation(DAWN_LOCATION);
            player.setVar("id", 4);
            return htmltext;
        }

        if (npcId == OREN_DAWN) {
            player.teleToLocation(DAWN_LOCATION);
            player.setVar("id", 5);
            return htmltext;
        }

        if (npcId == ADEN_DAWN) {
            player.teleToLocation(DAWN_LOCATION);
            player.setVar("id", 6);
            return htmltext;
        }

        if (npcId == HEINE_DAWN) {
            player.teleToLocation(DAWN_LOCATION);
            player.setVar("id", 7);
            return htmltext;
        }

        if (npcId == GODDARD_DAWN) {
            player.teleToLocation(DAWN_LOCATION);
            player.setVar("id", 8);
            return htmltext;
        }

        if (npcId == RUNE_DAWN) {
            player.teleToLocation(DAWN_LOCATION);
            player.setVar("id", 9);
            return htmltext;
        }

        if (npcId == SCHUTTGART_DAWN) {
            player.teleToLocation(DAWN_LOCATION);
            player.setVar("id", 10);
            return htmltext;
        }

        if (npcId == HV_DAWN) {
            player.teleToLocation(DAWN_LOCATION);
            player.setVar("id", 11);
            return htmltext;
        }


        // Dusk Locations //

        if (npcId == GLUDIN_DUSK) {
            player.teleToLocation(DUSK_LOCATION);
            player.setVar("id");
            return htmltext;
        }

        if (npcId == GLUDIO_DUSK) {
            player.teleToLocation(DUSK_LOCATION);
            player.setVar("id", 2);
            return htmltext;
        }

        if (npcId == DION_DUSK) {
            player.teleToLocation(DUSK_LOCATION);
            player.setVar("id", 3);
            return htmltext;
        }

        if (npcId == GIRAN_DUSK) {
            player.teleToLocation(DUSK_LOCATION);
            player.setVar("id", 4);
            return htmltext;
        }

        if (npcId == OREN_DUSK) {
            player.teleToLocation(DUSK_LOCATION);
            player.setVar("id", 5);
            return htmltext;
        }

        if (npcId == ADEN_DUSK) {
            player.teleToLocation(DUSK_LOCATION);
            player.setVar("id", 6);
            return htmltext;
        }

        if (npcId == HEINE_DUSK) {
            player.teleToLocation(DUSK_LOCATION);
            player.setVar("id", 7);
            return htmltext;
        }

        if (npcId == GODDARD_DUSK) {
            player.teleToLocation(DUSK_LOCATION);
            player.setVar("id", 8);
            return htmltext;
        }

        if (npcId == RUNE_DUSK) {
            player.teleToLocation(DUSK_LOCATION);
            player.setVar("id", 9);
            return htmltext;
        }

        if (npcId == SCHUTTGART_DUSK) {
            player.teleToLocation(DUSK_LOCATION);
            player.setVar("id", 10);
            return htmltext;
        }

        if (npcId == HV_DUSK) {
            player.teleToLocation(DUSK_LOCATION);
            player.setVar("id", 11);
            return htmltext;
        }

        // Oracle of Dusk/Dawn //

        htmltext = "Completed.htm";
        // back to Gludin Village
        if (back == 1) {
            player.teleToLocation(-80826, 149775, -3043);
            return htmltext;
        }

        // back to Gludio Castle Town
        if (back == 2) {
            player.teleToLocation(-12672, 122776, -3116);
            return htmltext;
        }

        // back to Dion Castle Town
        if (back == 3) {
            player.teleToLocation(15670, 142983, -2705);
            return htmltext;
        }

        // back to Giran Castle Town
        if (back == 4) {
            player.teleToLocation(83400, 147943, -3404);
            return htmltext;
        }

        // back to Town of Oren
        if (back == 5) {
            player.teleToLocation(82956, 53162, -1495);
            return htmltext;
        }

        // back to Town of Aden
        if (back == 6) {
            player.teleToLocation(146331, 25762, -2018);
            return htmltext;
        }

        // back to Heine
        if (back == 7) {
            player.teleToLocation(111409, 219364, -3545);
            return htmltext;
        }

        // back to Goddard
        if (back == 8) {
            player.teleToLocation(147928, -55273, -2734);
            return htmltext;
        }

        // back to Rune
        if (back == 9) {
            player.teleToLocation(43799, -47727, -798);
            return htmltext;
        }

        // back to Schuttgart
        if (back == 10) {
            player.teleToLocation(87386, -143246, -1293);
            return htmltext;
        }

        // back to Hunters Village
        if (back == 11) {
            player.teleToLocation(116819, 76994, -2714);
            return htmltext;
        }
        return htmltext;
    }
}