package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _410_PathToPalusKnight extends Quest {
    //npc
    private final int VIRGIL = 30329;
    private final int KALINTA = 30422;
    //mobs
    private final int POISON_SPIDER = 20038;
    private final int ARACHNID_TRACKER = 20043;
    private final int LYCANTHROPE = 20049;
    //items
    private final int PALLUS_TALISMAN_ID = 1237;
    private final int LYCANTHROPE_SKULL_ID = 1238;
    private final int VIRGILS_LETTER_ID = 1239;
    private final int MORTE_TALISMAN_ID = 1240;
    private final int PREDATOR_CARAPACE_ID = 1241;
    private final int TRIMDEN_SILK_ID = 1242;
    private final int COFFIN_ETERNAL_REST_ID = 1243;
    private final int GAZE_OF_ABYSS_ID = 1244;

    public _410_PathToPalusKnight() {
        super(false);

        addStartNpc(VIRGIL);

        addTalkId(KALINTA);

        addKillId(POISON_SPIDER);
        addKillId(ARACHNID_TRACKER);
        addKillId(LYCANTHROPE);

        addQuestItem(PALLUS_TALISMAN_ID,
                VIRGILS_LETTER_ID,
                COFFIN_ETERNAL_REST_ID,
                MORTE_TALISMAN_ID,
                PREDATOR_CARAPACE_ID,
                TRIMDEN_SILK_ID,
                LYCANTHROPE_SKULL_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("1")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
            htmltext = "master_virgil_q0410_06.htm";
            st.giveItems(PALLUS_TALISMAN_ID, 1);
        } else if (event.equalsIgnoreCase("410_1")) {
            if (st.player.getLevel() >= 18 && st.player.getClassId().id == 0x1f && st.getQuestItemsCount(GAZE_OF_ABYSS_ID) == 0)
                htmltext = "master_virgil_q0410_05.htm";
            else if (st.player.getClassId().id != 0x1f) {
                if (st.player.getClassId().id == 0x20)
                    htmltext = "master_virgil_q0410_02a.htm";
                else
                    htmltext = "master_virgil_q0410_03.htm";
            } else if (st.player.getLevel() < 18)
                htmltext = "master_virgil_q0410_02.htm";
            else if (st.getQuestItemsCount(GAZE_OF_ABYSS_ID) == 1)
                htmltext = "master_virgil_q0410_04.htm";
        } else if (event.equalsIgnoreCase("30329_2")) {
            htmltext = "master_virgil_q0410_10.htm";
            st.takeItems(PALLUS_TALISMAN_ID, -1);
            st.takeItems(LYCANTHROPE_SKULL_ID, -1);
            st.giveItems(VIRGILS_LETTER_ID, 1);
            st.setCond(3);
        } else if (event.equalsIgnoreCase("30422_1")) {
            htmltext = "kalinta_q0410_02.htm";
            st.takeItems(VIRGILS_LETTER_ID, -1);
            st.giveItems(MORTE_TALISMAN_ID);
            st.setCond(4);
        } else if (event.equalsIgnoreCase("30422_2")) {
            htmltext = "kalinta_q0410_06.htm";
            st.takeItems(MORTE_TALISMAN_ID, -1);
            st.takeItems(TRIMDEN_SILK_ID, -1);
            st.takeItems(PREDATOR_CARAPACE_ID, -1);
            st.giveItems(COFFIN_ETERNAL_REST_ID, 1);
            st.setCond(6);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == VIRGIL) {
            if (cond < 1)
                htmltext = "master_virgil_q0410_01.htm";
            else if (st.getQuestItemsCount(PALLUS_TALISMAN_ID) > 0) {
                if (st.getQuestItemsCount(LYCANTHROPE_SKULL_ID) < 1)
                    htmltext = "master_virgil_q0410_07.htm";
                else if (st.getQuestItemsCount(LYCANTHROPE_SKULL_ID) > 0 && st.getQuestItemsCount(LYCANTHROPE_SKULL_ID) < 13)
                    htmltext = "master_virgil_q0410_08.htm";
                else if (st.haveQuestItem(LYCANTHROPE_SKULL_ID, 12))
                    htmltext = "master_virgil_q0410_09.htm";
            } else if (st.haveQuestItem(COFFIN_ETERNAL_REST_ID)) {
                htmltext = "master_virgil_q0410_11.htm";
                st.takeItems(COFFIN_ETERNAL_REST_ID);
                if (st.player.getClassId().occupation() == 0) {
                    st.giveItems(GAZE_OF_ABYSS_ID, 1);
                    if (!st.player.isVarSet("prof1")) {
                        st.player.setVar("prof1", 1);
                        st.addExpAndSp(228064, 16455);
                        st.giveItems(ADENA_ID, 81900);
                    }
                }
                st.exitCurrentQuest(true);
                st.playSound(SOUND_FINISH);
            } else if (st.getQuestItemsCount(MORTE_TALISMAN_ID) > 0 | st.getQuestItemsCount(VIRGILS_LETTER_ID) > 0)
                htmltext = "master_virgil_q0410_12.htm";
        } else if (npcId == KALINTA && cond > 0)
            if (st.getQuestItemsCount(VIRGILS_LETTER_ID) > 0)
                htmltext = "kalinta_q0410_01.htm";
            else if (st.getQuestItemsCount(MORTE_TALISMAN_ID) > 0)
                if (st.getQuestItemsCount(TRIMDEN_SILK_ID) < 1 && st.getQuestItemsCount(PREDATOR_CARAPACE_ID) < 1)
                    htmltext = "kalinta_q0410_03.htm";
                else if (st.getQuestItemsCount(TRIMDEN_SILK_ID) < 1 | st.getQuestItemsCount(PREDATOR_CARAPACE_ID) < 1)
                    htmltext = "kalinta_q0410_04.htm";
                else if (st.getQuestItemsCount(TRIMDEN_SILK_ID) > 4 && st.getQuestItemsCount(PREDATOR_CARAPACE_ID) > 0)
                    htmltext = "kalinta_q0410_05.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == LYCANTHROPE) {
            if (cond == 1 && st.getQuestItemsCount(PALLUS_TALISMAN_ID) > 0 && st.getQuestItemsCount(LYCANTHROPE_SKULL_ID) < 13) {
                st.giveItems(LYCANTHROPE_SKULL_ID);
                if (st.getQuestItemsCount(LYCANTHROPE_SKULL_ID) > 12) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(2);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == POISON_SPIDER) {
            if (cond == 4 && st.getQuestItemsCount(MORTE_TALISMAN_ID) > 0 && st.getQuestItemsCount(PREDATOR_CARAPACE_ID) < 1) {
                st.giveItems(PREDATOR_CARAPACE_ID);
                st.playSound(SOUND_MIDDLE);
                if (st.getQuestItemsCount(TRIMDEN_SILK_ID) > 4)
                    st.setCond(5);
            }
        } else if (npcId == ARACHNID_TRACKER)
            if (cond == 4 && st.getQuestItemsCount(MORTE_TALISMAN_ID) > 0 && st.getQuestItemsCount(TRIMDEN_SILK_ID) < 5) {
                st.giveItems(TRIMDEN_SILK_ID);
                if (st.getQuestItemsCount(TRIMDEN_SILK_ID) > 4) {
                    st.playSound(SOUND_MIDDLE);
                    if (st.getQuestItemsCount(PREDATOR_CARAPACE_ID) > 0)
                        st.setCond(5);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
    }
}