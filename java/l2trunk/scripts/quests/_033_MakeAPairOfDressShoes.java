package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _033_MakeAPairOfDressShoes extends Quest {
    private final int LEATHER = 1882;
    private final int THREAD = 1868;
    private final int DRESS_SHOES_BOX = 7113;

    public _033_MakeAPairOfDressShoes() {
        super(false);

        addStartNpc(30838);
        addTalkId(30838);
        addTalkId(30838);
        addTalkId(30164);
        addTalkId(31520);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "30838-1.htm":
                st.setCond(1);
                st.setState(STARTED);
                st.playSound(SOUND_ACCEPT);
                break;
            case "31520-1.htm":
                st.setCond(2);
                break;
            case "30838-3.htm":
                st.setCond(3);
                break;
            case "30838-5.htm":
                if (st.getQuestItemsCount(LEATHER) >= 200 && st.getQuestItemsCount(THREAD) >= 600 && st.getQuestItemsCount(ADENA_ID) >= 200000) {
                    st.takeItems(LEATHER, 200);
                    st.takeItems(THREAD, 600);
                    st.takeItems(ADENA_ID, 200000);
                    st.setCond(4);
                } else
                    htmltext = "You don't have enough materials";
                break;
            case "30164-1.htm":
                if (st.getQuestItemsCount(ADENA_ID) >= 300000) {
                    st.takeItems(ADENA_ID, 300000);
                    st.setCond(5);
                } else
                    htmltext = "30164-havent.htm";
                break;
            case "30838-7.htm":
                st.giveItems(DRESS_SHOES_BOX, 1);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 30838) {
            if (cond == 0 && st.getQuestItemsCount(DRESS_SHOES_BOX) == 0) {
                if (st.player.getLevel() >= 60) {
                    QuestState fwear = st.player.getQuestState(_037_PleaseMakeMeFormalWear.class);
                    if (fwear != null && fwear.getCond() == 7)
                        htmltext = "30838-0.htm";
                    else
                        st.exitCurrentQuest(true);
                } else
                    htmltext = "30838-00.htm";
            } else if (cond == 1)
                htmltext = "30838-1.htm";
            else if (cond == 2)
                htmltext = "30838-2.htm";
            else if (cond == 3 && st.getQuestItemsCount(LEATHER) >= 200 && st.getQuestItemsCount(THREAD) >= 600 && st.getQuestItemsCount(ADENA_ID) >= 200000)
                htmltext = "30838-4.htm";
            else if (cond == 3 && (st.getQuestItemsCount(LEATHER) < 200 || st.getQuestItemsCount(THREAD) < 600 || st.getQuestItemsCount(ADENA_ID) < 200000))
                htmltext = "30838-4r.htm";
            else if (cond == 4)
                htmltext = "30838-5r.htm";
            else if (cond == 5)
                htmltext = "30838-6.htm";
        } else if (npcId == 31520) {
            if (cond == 1)
                htmltext = "31520-0.htm";
            else if (cond == 2)
                htmltext = "31520-1r.htm";
        } else if (npcId == 30164)
            if (cond == 4)
                htmltext = "30164-0.htm";
            else if (cond == 5)
                htmltext = "30164-2.htm";
        return htmltext;
    }
}