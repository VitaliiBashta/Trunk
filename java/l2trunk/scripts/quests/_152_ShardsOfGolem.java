package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _152_ShardsOfGolem extends Quest {
    private static final int WOODEN_BP = 23;
    private final int HARRYS_RECEIPT1 = 1008;
    private final int HARRYS_RECEIPT2 = 1009;
    private final int GOLEM_SHARD = 1010;
    private final int TOOL_BOX = 1011;

    public _152_ShardsOfGolem() {
        super(false);

        addStartNpc(30035);

        addTalkId(30035);
        addTalkId(30035);
        addTalkId(30283);
        addTalkId(30035);

        addKillId(20016);
        addKillId(20101);

        addQuestItem(HARRYS_RECEIPT1,
                GOLEM_SHARD,
                TOOL_BOX,
                HARRYS_RECEIPT2);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("30035-04.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
            if (st.getQuestItemsCount(HARRYS_RECEIPT1) == 0)
                st.giveItems(HARRYS_RECEIPT1, 1);
        } else if (event.equals("152_2")) {
            st.takeItems(HARRYS_RECEIPT1, -1);
            if (st.getQuestItemsCount(HARRYS_RECEIPT2) == 0) {
                st.giveItems(HARRYS_RECEIPT2, 1);
                st.setCond(2);
            }
            htmltext = "30283-02.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == 30035) {
            if (cond == 0) {
                if (st.player.getLevel() >= 10) {
                    htmltext = "30035-03.htm";
                    return htmltext;
                }
                htmltext = "30035-02.htm";
                st.exitCurrentQuest(true);
            } else if (cond == 1 && st.haveQuestItem(HARRYS_RECEIPT1) )
                htmltext = "30035-05.htm";
            else if (cond == 2 && st.haveQuestItem(HARRYS_RECEIPT2) )
                htmltext = "30035-05.htm";
            else if (cond == 4 && st.haveQuestItem(TOOL_BOX) ) {
                st.takeItems(TOOL_BOX);
                st.takeItems(HARRYS_RECEIPT2);
                st.setCond(0);
                st.playSound(SOUND_FINISH);
                st.giveItems(WOODEN_BP);
                st.addExpAndSp(5000, 0);
                htmltext = "30035-06.htm";
                st.exitCurrentQuest(false);
            }
        } else if (npcId == 30283) {
            if (cond == 1 && st.haveQuestItem(HARRYS_RECEIPT1) )
                htmltext = "30283-01.htm";
            else if (cond == 2 && st.haveQuestItem(HARRYS_RECEIPT2)  && st.getQuestItemsCount(GOLEM_SHARD) < 5)
                htmltext = "30283-03.htm";
            else if (cond == 3 && st.haveQuestItem(HARRYS_RECEIPT2)  && st.getQuestItemsCount(GOLEM_SHARD) == 5) {
                st.takeItems(GOLEM_SHARD);
                if (st.getQuestItemsCount(TOOL_BOX) == 0) {
                    st.giveItems(TOOL_BOX);
                    st.setCond(4);
                }
                htmltext = "30283-04.htm";
            }
        } else if (cond == 4 && st.haveAllQuestItems(HARRYS_RECEIPT2,TOOL_BOX))
            htmltext = "30283-05.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 2 && Rnd.chance(30) && st.getQuestItemsCount(GOLEM_SHARD) < 5) {
            st.giveItems(GOLEM_SHARD);
            if (st.getQuestItemsCount(GOLEM_SHARD) == 5) {
                st.setCond(3);
                st.playSound(SOUND_MIDDLE);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }
}