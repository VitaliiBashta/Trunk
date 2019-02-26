package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10277_MutatedKaneusDion extends Quest {
    // NPCs
    private static final int Lucas = 30071;
    private static final int Mirien = 30461;

    // MOBs
    private static final int CrimsonHatuOtis = 18558;
    private static final int SeerFlouros = 18559;

    // items
    private static final int Tissue1 = 13832;
    private static final int Tissue2 = 13833;

    public _10277_MutatedKaneusDion() {
        super(true);
        addStartNpc(Lucas);
        addTalkId(Mirien);
        addKillId(CrimsonHatuOtis, SeerFlouros);
        addQuestItem(Tissue1, Tissue2);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("30071-03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30461-02.htm".equalsIgnoreCase(event)) {
            st.giveItems(57, 120000);
            st.finish();
            st.playSound(SOUND_FINISH);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();
        int cond = st.getCond();
        int npcId = npc.getNpcId();
        if (id == COMPLETED) {
            if (npcId == Lucas)
                htmltext = "30071-0a.htm";
        } else if (id == CREATED && npcId == Lucas) {
            if (st.player.getLevel() >= 28)
                htmltext = "30071-01.htm";
            else
                htmltext = "30071-00.htm";
        } else if (npcId == Lucas) {
            if (cond == 1)
                htmltext = "30071-04.htm";
            else if (cond == 2)
                htmltext = "30071-05.htm";
        } else if (npcId == Mirien)
            if (cond == 1)
                htmltext = "30461-01a.htm";
            else if (cond == 2)
                htmltext = "30461-01.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() == STARTED && st.getCond() == 1) {
            st.giveItems(Tissue1);
            st.giveItems(Tissue2);
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        }
    }
}