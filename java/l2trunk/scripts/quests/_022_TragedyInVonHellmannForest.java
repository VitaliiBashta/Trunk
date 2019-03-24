package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;

import java.util.List;
import java.util.stream.IntStream;

public final class _022_TragedyInVonHellmannForest extends Quest {
    private static final int LostSkullOfElf = 7142;
    private static final int CrossOfEinhasad = 7141;
    private static final int SealedReportBox = 7146;
    // ~~~~ Monster list: ~~~~
    private static final List<Integer> Mobs = List.of(
            21547, 21548, 21549, 21550, 21551, 21552, 21553, 21554, 21555, 21556, 21557, 21558,
            21559, 21560, 21561, 21562, 21563, 21564, 21565, 21566, 21567, 21568, 21569, 21570,
            21571, 21572, 21573, 21574, 21575, 21576, 21577, 21578);
    private static NpcInstance GhostOfPriestInstance = null;
    private static NpcInstance SoulOfWellInstance = null;
    // Npc list
    private final int Well = 31527;
    private final int Tifaren = 31334;
    private final int Innocentin = 31328;
    private final int SoulOfWell = 27217;
    private final int GhostOfPriest = 31528;
    private final int GhostOfAdventurer = 31529;
    // ~~~~~~~~ Item list ~~~~~~~~
    private final int ReportBox = 7147;
    private final int LetterOfInnocentin = 7143;
    private final int JewelOfAdventurerRed = 7145;
    private final int JewelOfAdventurerGreen = 7144;

    public _022_TragedyInVonHellmannForest() {
        addStartNpc(Tifaren);

        addTalkId( GhostOfPriest, Innocentin, GhostOfAdventurer, Well);

        addKillId(SoulOfWell);

        addKillId(Mobs);

        addQuestItem(LostSkullOfElf);
    }

    private void spawnGhostOfPriest(QuestState st) {
        GhostOfPriestInstance = NpcUtils.spawnSingle(GhostOfPriest, Location.findPointToStay(st.player, 50, 100));
    }

    private void spawnSoulOfWell(QuestState st) {
        SoulOfWellInstance = NpcUtils.spawnSingle(SoulOfWell, Location.findPointToStay(st.player, 50, 100));
    }

    private void despawnGhostOfPriest() {
        if (GhostOfPriestInstance != null)
            GhostOfPriestInstance.deleteMe();
    }

    private void despawnSoulOfWell() {
        if (SoulOfWellInstance != null)
            SoulOfWellInstance.deleteMe();
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("31334-03.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(3);
            st.takeItems(CrossOfEinhasad);
        } else if ("31334-06.htm".equalsIgnoreCase(event))
            st.setCond(4);
        else if ("31334-09.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.takeItems(LostSkullOfElf);
            despawnGhostOfPriest();
            spawnGhostOfPriest(st);
        } else if ("31528-07.htm".equalsIgnoreCase(event)) {
            despawnGhostOfPriest();
            st.setCond(7);
        } else if ("31328-06.htm".equalsIgnoreCase(event)) {
            st.setCond(8);
            st.giveItems(LetterOfInnocentin);
        } else if ("31529-09.htm".equalsIgnoreCase(event)) {
            st.setCond(9);
            st.takeItems(LetterOfInnocentin);
        } else if ("explore".equalsIgnoreCase(event)) {
            despawnSoulOfWell();
            spawnSoulOfWell(st);
            st.setCond(10);
            st.giveItems(JewelOfAdventurerGreen);
            htmltext = "<html><body>Attack Soul of Well but do not kill while stone will not change colour...</body></html>";
        } else if ("attack_timer".equalsIgnoreCase(event)) {
            despawnSoulOfWell();
            st.giveItems(JewelOfAdventurerRed);
            st.takeItems(JewelOfAdventurerGreen);
            st.setCond(11);
            return null;
        } else if ("31328-08.htm".equalsIgnoreCase(event)) {
            st.startQuestTimer("wait_timer", 600000);
            st.setCond(15);
            st.takeItems(ReportBox, 1);
        } else if ("wait_timer".equalsIgnoreCase(event)) {
            st.setCond(16);
            htmltext = "<html><body>Innocentin wants with you to speak...</body></html>";
        } else if ("31328-16.htm".equalsIgnoreCase(event)) {
            st.startQuestTimer("next_wait_timer", 300000);
            st.setCond(17);
        } else if (event.equalsIgnoreCase("next_wait_timer"))
            st.setCond(18);
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        String htmltext = "noquest";
        if (npcId == Tifaren) {
            if (cond == 0) {
                if (st.player.isQuestCompleted(_021_HiddenTruth.class))
                    htmltext = "31334-01.htm";
                else
                    htmltext = "<html><head><body>You not complite quest Hidden Truth...</body></html>";
            } else if (cond == 3)
                return "31334-04.htm";
            else if (cond == 4)
                htmltext = "31334-06.htm";
            else if (cond == 5) {
                if (st.haveAnyQuestItems(LostSkullOfElf))
                    htmltext = "31334-07.htm";
                else {
                    st.setCond(4);
                    htmltext = "31334-06.htm";
                }
            } else if (cond == 6) {
                despawnGhostOfPriest();
                spawnGhostOfPriest(st);
                htmltext = "31334-09.htm";
            }
        } else if (npcId == GhostOfPriest) {
            if (cond == 6)
                htmltext = "31528-00.htm";
            else if (cond == 7)
                htmltext = "31528-07.htm";
        } else if (npcId == Innocentin) {
            if (cond == 0)
                htmltext = "31328-17.htm";
            if (cond == 7)
                htmltext = "31328-00.htm";
            else if (cond == 8)
                htmltext = "31328-06.htm";
            else if (cond == 14) {
                if (st.haveQuestItem(ReportBox))
                    htmltext = "31328-07.htm";
                else {
                    st.setCond(13);
                    htmltext = "Go away!";
                }
            } else if (cond == 15) {
                if (!st.isRunningQuestTimer("wait_timer"))
                    st.setCond(16);
                htmltext = "31328-09.htm";
            } else if (cond == 16)
                htmltext = "31328-08a.htm";
            else if (cond == 17) {
                if (!st.isRunningQuestTimer("next_wait_timer"))
                    st.setCond(18);
                htmltext = "31328-16a.htm";
            } else if (cond == 18) {
                htmltext = "31328-17.htm";
                st.addExpAndSp(345966, 31578);
                st.finish();
            }
        } else if (npcId == GhostOfAdventurer) {
            if (cond == 8) {
                if (st.haveQuestItem(LetterOfInnocentin))
                    htmltext = "31529-00.htm";
                else
                    htmltext = "You have no Letter of Innocentin! Are they Please returned to High Priest Innocentin...";
            } else if (cond == 9)
                htmltext = "31529-09.htm";
            else if (cond == 11) {
                if (st.haveQuestItem(JewelOfAdventurerRed)) {
                    htmltext = "31529-10.htm";
                    st.takeItems(JewelOfAdventurerRed);
                    st.setCond(12);
                } else {
                    st.setCond(9);
                    htmltext = "31529-09.htm";
                }
            } else if (cond == 13)
                if (st.haveQuestItem(SealedReportBox) ) {
                    htmltext = "31529-11.htm";
                    st.setCond(14);
                    st.takeItems(SealedReportBox);
                    st.giveItems(ReportBox);
                } else {
                    st.setCond(12);
                    htmltext = "31529-10.htm";
                }
        } else if (npcId == Well)
            if (cond == 9)
                htmltext = "31527-00.htm";
            else if (cond == 10) {
                despawnSoulOfWell();
                spawnSoulOfWell(st);
                st.setCond(10);
                st.startQuestTimer("attack_timer", 120000);
                st.takeAllItems(JewelOfAdventurerGreen,JewelOfAdventurerRed);
                st.giveItems(JewelOfAdventurerGreen);
                htmltext = "<html><body>Attack Soul of Well but do not kill while stone will not change colour...</body></html>";
            } else if (cond == 12) {
                htmltext = "31527-01.htm";
                st.setCond(13);
                st.giveItems(SealedReportBox);
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (Mobs.contains(npcId))
            if (cond == 4 && Rnd.chance(99)) {
                st.giveItems(LostSkullOfElf);
                st.playSound(SOUND_MIDDLE);
                st.setCond(5);
            }
        if (npcId == SoulOfWell)
            if (cond == 10) {
                st.setCond(9);
                st.takeAllItems(JewelOfAdventurerGreen,JewelOfAdventurerRed);
                st.cancelQuestTimer("attack_timer");
            }
    }
}