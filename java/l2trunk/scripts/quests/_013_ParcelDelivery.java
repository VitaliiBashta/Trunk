package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _013_ParcelDelivery extends Quest {
    private static final int PACKAGE = 7263;

    public _013_ParcelDelivery() {
        super(false);

        addStartNpc(31274);

        addTalkId(31274);
        addTalkId(31539);

        addQuestItem(PACKAGE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("mineral_trader_fundin_q0013_0104.htm")) {
            st.setCond(1);
            st.giveItems(PACKAGE, 1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("warsmith_vulcan_q0013_0201.htm")) {
            st.takeItems(PACKAGE, -1);
            st.giveItems(ADENA_ID, 157834, true);
            st.addExpAndSp(589092, 58794);
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
        if (npcId == 31274) {
            if (cond == 0)
                if (st.player.getLevel() >= 74)
                    htmltext = "mineral_trader_fundin_q0013_0101.htm";
                else {
                    htmltext = "mineral_trader_fundin_q0013_0103.htm";
                    st.exitCurrentQuest(true);
                }
            else if (cond == 1)
                htmltext = "mineral_trader_fundin_q0013_0105.htm";
        } else if (npcId == 31539)
            if (cond == 1 && st.getQuestItemsCount(PACKAGE) == 1)
                htmltext = "warsmith_vulcan_q0013_0101.htm";
        return htmltext;
    }
}