package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.stream.IntStream;

public final class _627_HeartInSearchOfPower extends Quest {
    //NPC
    private static final int M_NECROMANCER = 31518;
    private static final int ENFEUX = 31519;

    //ITEMS
    private static final int SEAL_OF_LIGHT = 7170;
    private static final int GEM_OF_SUBMISSION = 7171;
    private static final int GEM_OF_SAINTS = 7172;

    //REWARDS
    private static final int MOLD_HARDENER = 4041;
    private static final int ENRIA = 4042;
    private static final int ASOFE = 4043;
    private static final int THONS = 4044;

    public _627_HeartInSearchOfPower() {
        super(true);

        addStartNpc(31518);

        addTalkId(31519);

        addKillId(IntStream.rangeClosed(21520, 21541).toArray());

        addQuestItem(GEM_OF_SUBMISSION);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "dark_necromancer_q0627_0104.htm":
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                break;
            case "dark_necromancer_q0627_0201.htm":
                st.takeItems(GEM_OF_SUBMISSION, 300);
                st.giveItems(SEAL_OF_LIGHT);
                st.setCond(3);
                break;
            case "enfeux_q0627_0301.htm":
                st.takeItems(SEAL_OF_LIGHT, 1);
                st.giveItems(GEM_OF_SAINTS);
                st.setCond(4);
                break;
            case "dark_necromancer_q0627_0401.htm":
                st.takeItems(GEM_OF_SAINTS, 1);
                break;
            default:
                switch (event) {
                    case "627_11":
                        htmltext = "dark_necromancer_q0627_0402.htm";
                        st.giveAdena( 100000);
                        break;
                    case "627_12":
                        htmltext = "dark_necromancer_q0627_0402.htm";
                        st.giveItems(ASOFE, 13, true);
                        st.giveAdena( 6400);
                        break;
                    case "627_13":
                        htmltext = "dark_necromancer_q0627_0402.htm";
                        st.giveItems(THONS, 13, true);
                        st.giveAdena(6400);
                        break;
                    case "627_14":
                        htmltext = "dark_necromancer_q0627_0402.htm";
                        st.giveItems(ENRIA, 6, true);
                        st.giveAdena(13600);
                        break;
                    case "627_15":
                        htmltext = "dark_necromancer_q0627_0402.htm";
                        st.giveItems(MOLD_HARDENER, 3, true);
                        st.giveAdena(17200);
                        break;
                }
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == M_NECROMANCER) {
            if (cond == 0)
                if (st.player.getLevel() >= 60)
                    htmltext = "dark_necromancer_q0627_0101.htm";
                else {
                    htmltext = "dark_necromancer_q0627_0103.htm";
                    st.exitCurrentQuest();
                }
            else if (cond == 1)
                htmltext = "dark_necromancer_q0627_0106.htm";
            else if (st.getQuestItemsCount(GEM_OF_SUBMISSION) == 300)
                htmltext = "dark_necromancer_q0627_0105.htm";
            else if (st.haveQuestItem(GEM_OF_SAINTS))
                htmltext = "dark_necromancer_q0627_0301.htm";
        } else if (npcId == ENFEUX && st.haveQuestItem(SEAL_OF_LIGHT))
            htmltext = "enfeux_q0627_0201.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        long count = st.getQuestItemsCount(GEM_OF_SUBMISSION);
        if (st.getCond() == 1 && count < 300) {
            st.giveItems(GEM_OF_SUBMISSION);
            if (count == 299) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }
}