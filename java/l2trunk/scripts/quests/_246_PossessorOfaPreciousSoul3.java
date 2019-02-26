package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.stream.IntStream;

public final class _246_PossessorOfaPreciousSoul3 extends Quest {
    private static final int CARADINES_LETTER_2_PART = 7678;
    private static final int RING_OF_GODDESS_WATERBINDER = 7591;
    private static final int NECKLACE_OF_GODDESS_EVERGREEN = 7592;
    private static final int STAFF_OF_GODDESS_RAIN_SONG = 7593;
    private static final int CARADINES_LETTER = 7679;
    private static final int RELIC_BOX = 7594;
    private static final int STAFF_OF_GODDES = 21725;

    public _246_PossessorOfaPreciousSoul3() {
        super(true);

        addStartNpc(31740);

        addTalkId(31741, 30721);

        addKillId(21541, 21544, 25325);
        addKillId(IntStream.rangeClosed(21535, 21540).toArray());

        addQuestItem(RING_OF_GODDESS_WATERBINDER,
                NECKLACE_OF_GODDESS_EVERGREEN,
                STAFF_OF_GODDESS_RAIN_SONG,
                STAFF_OF_GODDES);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        switch (event) {
            case "caradine_q0246_0104.htm":
                st.setCond(1);
                st.takeItems(CARADINES_LETTER_2_PART, 1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                break;
            case "ossian_q0246_0201.htm":
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
                break;
            case "ossian_q0246_0401.htm":
                st.takeItems(RING_OF_GODDESS_WATERBINDER, 1);
                st.takeItems(NECKLACE_OF_GODDESS_EVERGREEN, 1);
                st.takeItems(STAFF_OF_GODDESS_RAIN_SONG, 1);
                st.setCond(6);
                st.giveItems(RELIC_BOX, 1);
                st.playSound(SOUND_MIDDLE);
                break;
            case "magister_ladd_q0246_0501.htm":
                st.takeItems(RELIC_BOX, 1);
                st.giveItems(CARADINES_LETTER);
                st.addExpAndSp(719843, 0);
                st.unset("cond");
                st.finish();
                break;
            case "ossian_q0246_0301rb.htm":
                st.setCond(4);
                st.playSound(SOUND_MIDDLE);
                st.unset("staff_select");
                break;
            case "ossian_q0246_0301mb.htm":
                st.setCond(4);
                st.playSound(SOUND_MIDDLE);
                st.set("staff_select");
                break;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (!st.player.isSubClassActive())
            return "Subclass only!";

        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 31740) {
            if (cond == 0) {
                QuestState previous = st.player.getQuestState(_242_PossessorOfaPreciousSoul2.class);
                if (previous != null && previous.getState() == COMPLETED && st.player.getLevel() >= 65)
                    htmltext = "caradine_q0246_0101.htm";
                else {
                    htmltext = "caradine_q0246_0102.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "caradine_q0246_0105.htm";
        } else if (npcId == 31741) {
            if (cond == 1)
                htmltext = "ossian_q0246_0101.htm";
            else if ((cond == 2 || cond == 3) && (st.getQuestItemsCount(RING_OF_GODDESS_WATERBINDER) < 1 || st.getQuestItemsCount(NECKLACE_OF_GODDESS_EVERGREEN) < 1))
                htmltext = "ossian_q0246_0203.htm";
            else if (cond == 3 && st.haveAllQuestItems(RING_OF_GODDESS_WATERBINDER, NECKLACE_OF_GODDESS_EVERGREEN))
                htmltext = "ossian_q0246_0202.htm";
            else if (cond == 4)
                htmltext = "ossian_q0246_0301.htm";
            else if (cond == 5 && st.getQuestItemsCount(STAFF_OF_GODDESS_RAIN_SONG) < 1)
                htmltext = "ossian_q0246_0402.htm";
            else if (cond == 5 && st.haveAllQuestItems(RING_OF_GODDESS_WATERBINDER, NECKLACE_OF_GODDESS_EVERGREEN, STAFF_OF_GODDESS_RAIN_SONG))
                htmltext = "ossian_q0246_0303.htm";
            else if (cond == 6)
                htmltext = "ossian_q0246_0403.htm";
        } else if (npcId == 30721) {
            if (cond == 6 && st.haveQuestItem(RELIC_BOX))
                htmltext = "magister_ladd_q0246_0401.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (!st.player.isSubClassActive())
            return;

        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 2) {
            if (Rnd.chance(80)) {
                if (npcId == 21541 && st.getQuestItemsCount(RING_OF_GODDESS_WATERBINDER) == 0) {
                    st.giveItems(RING_OF_GODDESS_WATERBINDER);
                    if (st.haveAllQuestItems(RING_OF_GODDESS_WATERBINDER, NECKLACE_OF_GODDESS_EVERGREEN)) {
                        st.setCond(3);
                        st.playSound(SOUND_MIDDLE);
                    } else
                        st.playSound(SOUND_ITEMGET);
                } else if (npcId == 21544 && st.getQuestItemsCount(NECKLACE_OF_GODDESS_EVERGREEN) == 0) {
                    st.giveItems(NECKLACE_OF_GODDESS_EVERGREEN);
                    if (st.haveAllQuestItems(RING_OF_GODDESS_WATERBINDER, NECKLACE_OF_GODDESS_EVERGREEN)) {
                        st.setCond(3);
                        st.playSound(SOUND_MIDDLE);
                    } else
                        st.playSound(SOUND_ITEMGET);
                }
            }
        } else if (cond == 4) {
            if (npcId == 25325 && !st.isSet("staff_select") && !st.haveQuestItem(STAFF_OF_GODDESS_RAIN_SONG)) {
                st.giveItems(STAFF_OF_GODDESS_RAIN_SONG);
                st.setCond(5);
                st.playSound(SOUND_MIDDLE);
            } else if (npcId >= 21535 && npcId <= 21540 && st.isSet("staff_select") && st.getQuestItemsCount(STAFF_OF_GODDES) < 100) {
                st.giveItems(STAFF_OF_GODDES);
                if (st.haveQuestItem(STAFF_OF_GODDES, 100)) {
                    st.takeItems(STAFF_OF_GODDES);
                    st.giveItems(STAFF_OF_GODDESS_RAIN_SONG);
                    st.setCond(5);
                    st.playSound(SOUND_MIDDLE);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        }
    }
}