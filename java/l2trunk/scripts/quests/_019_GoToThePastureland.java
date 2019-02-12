package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _019_GoToThePastureland extends Quest {
    private final int VLADIMIR = 31302;
    private final int TUNATUN = 31537;

    private final int BEAST_MEAT = 7547;

    public _019_GoToThePastureland() {
        super(false);

        addStartNpc(VLADIMIR);

        addTalkId(VLADIMIR);
        addTalkId(TUNATUN);

        addQuestItem(BEAST_MEAT);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("trader_vladimir_q0019_0104.htm".equals(event)) {
            st.giveItems(BEAST_MEAT);
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        }
        if ("beast_herder_tunatun_q0019_0201.htm".equals(event)) {
            st.takeItems(BEAST_MEAT);
            st.addExpAndSp(136766, 12688);
            st.giveItems(ADENA_ID, 50000);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(false);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == VLADIMIR) {
            if (cond == 0)
                if (st.player.getLevel() >= 63)
                    htmltext = "trader_vladimir_q0019_0101.htm";
                else {
                    htmltext = "trader_vladimir_q0019_0103.htm";
                    st.exitCurrentQuest(true);
                }
            else
                htmltext = "trader_vladimir_q0019_0105.htm";
        } else if (npcId == TUNATUN)
            if (st.getQuestItemsCount(BEAST_MEAT) >= 1)
                htmltext = "beast_herder_tunatun_q0019_0101.htm";
            else {
                htmltext = "beast_herder_tunatun_q0019_0202.htm";
                st.exitCurrentQuest(true);
            }
        return htmltext;
    }
}