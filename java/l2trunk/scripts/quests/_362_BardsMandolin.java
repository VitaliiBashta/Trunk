package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _362_BardsMandolin extends Quest {
    //NPC
    private static final int SWAN = 30957;
    private static final int NANARIN = 30956;
    private static final int GALION = 30958;
    private static final int WOODROW = 30837;
    //items
    private static final int SWANS_FLUTE = 4316;
    private static final int SWANS_LETTER = 4317;
    private static final int Musical_Score__Theme_of_Journey = 4410;

    public _362_BardsMandolin() {
        addStartNpc(SWAN);
        addTalkId(NANARIN,GALION,WOODROW);
        addQuestItem(SWANS_FLUTE,SWANS_LETTER);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        if (st.getState() == CREATED) {
            if (npcId != SWAN)
                return htmltext;
            st.setCond(0);
        }

        int cond = st.getCond();
        if (npcId == SWAN) {
            if (cond == 0)
                htmltext = "30957_1.htm";
            else if (cond == 3 && st.getQuestItemsCount(SWANS_FLUTE) > 0 && st.getQuestItemsCount(SWANS_LETTER) == 0) {
                htmltext = "30957_3.htm";
                st.setCond(4);
                st.giveItems(SWANS_LETTER);
            } else if (cond == 4 && st.haveAllQuestItems(SWANS_FLUTE,SWANS_LETTER))
                htmltext = "30957_6.htm";
            else if (cond == 5)
                htmltext = "30957_4.htm";
        } else if (npcId == WOODROW && cond == 1) {
            htmltext = "30837_1.htm";
            st.setCond(2);
        } else if (npcId == GALION && cond == 2) {
            htmltext = "30958_1.htm";
            st.setCond(3);
            st.giveItems(SWANS_FLUTE);
            st.playSound(SOUND_ITEMGET);
        } else if (npcId == NANARIN && cond == 4 && st.haveAllQuestItems(SWANS_FLUTE,SWANS_LETTER)) {
            htmltext = "30956_1.htm";
            st.takeAllItems(SWANS_FLUTE,SWANS_LETTER);
            st.setCond(5);
        }
        return htmltext;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        int cond = st.getCond();
        if ("30957_2.htm".equalsIgnoreCase(event) && state == CREATED && cond == 0) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30957_5.htm".equalsIgnoreCase(event) && state == STARTED && cond == 5) {
            st.giveAdena( 10000);
            st.giveItems(Musical_Score__Theme_of_Journey);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

}
