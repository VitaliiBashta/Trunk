package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _110_ToThePrimevalIsle extends Quest {
    // NPC
    private final int ANTON = 31338;
    private final int MARQUEZ = 32113;

    // QUEST ITEM and REWARD
    private final int ANCIENT_BOOK = 8777;

    public _110_ToThePrimevalIsle() {
        addStartNpc(ANTON);
        addTalkId(MARQUEZ);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("1")) {
            htmltext = "scroll_seller_anton_q0110_05.htm";
            st.setCond(1);
            st.giveItems(ANCIENT_BOOK);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("2") && st.haveQuestItem(ANCIENT_BOOK)) {
            htmltext = "marquez_q0110_05.htm";
            st.playSound(SOUND_FINISH);
            st.giveAdena( 191678);
            st.addExpAndSp(251602, 25242);
            st.takeItems(ANCIENT_BOOK);
            st.finish();
        } else if (event.equals("3")) {
            htmltext = "marquez_q0110_06.htm";
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (id == CREATED)
            if (st.player.getLevel() >= 75)
                htmltext = "scroll_seller_anton_q0110_01.htm";
            else {
                st.exitCurrentQuest();
                htmltext = "scroll_seller_anton_q0110_02.htm";
            }
        else if (npcId == ANTON) {
            if (cond == 1)
                htmltext = "scroll_seller_anton_q0110_07.htm";
        } else if (id == STARTED)
            if (npcId == MARQUEZ && cond == 1)
                if (st.haveQuestItem(ANCIENT_BOOK))
                    htmltext = "marquez_q0110_01.htm";
                else
                    htmltext = "marquez_q0110_07.htm";
        return htmltext;
    }

}