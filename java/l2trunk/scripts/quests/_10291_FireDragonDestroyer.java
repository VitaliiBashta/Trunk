package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10291_FireDragonDestroyer extends Quest {
    private static final int Klein = 31540;
    private static final int PoorNecklace = 15524;
    private static final int ValorNecklace = 15525;
    private static final int Valakas = 29028;

    public _10291_FireDragonDestroyer() {
        super(PARTY_ALL);
        addStartNpc(Klein);
        addQuestItem(PoorNecklace, ValorNecklace);
        addKillId(Valakas);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("klein_q10291_04.htm".equalsIgnoreCase(event)) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
            st.giveItems(PoorNecklace);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Klein) {
            if (cond == 0) {
                if (st.player.getLevel() >= 83 && st.haveQuestItem(7267))
                    htmltext = "klein_q10291_01.htm";
                else if (st.getQuestItemsCount(7267) < 1)
                    htmltext = "klein_q10291_00a.htm";
                else
                    htmltext = "klein_q10291_00.htm";
            } else if (cond == 1)
                htmltext = "klein_q10291_05.htm";
            else if (cond == 2) {
                if (st.haveQuestItem(ValorNecklace) ) {
                    htmltext = "klein_q10291_07.htm";
                    st.takeItems(ValorNecklace);
                    st.giveItems(8567);
                    st.giveItems(ADENA_ID, 126549);
                    st.addExpAndSp(717291, 77397);
                    st.playSound(SOUND_FINISH);
                    st.setState(COMPLETED);
                    st.exitCurrentQuest(false);
                } else
                    htmltext = "klein_q10291_06.htm";
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (cond == 1 && npcId == Valakas) {
            st.takeItems(PoorNecklace);
            st.giveItems(ValorNecklace);
            st.setCond(2);
        }
    }
}