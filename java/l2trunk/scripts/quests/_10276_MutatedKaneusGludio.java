package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10276_MutatedKaneusGludio extends Quest {
    // NPCs
    private static final int Bathis = 30332;
    private static final int Rohmer = 30344;

    // MOBs
    private static final int TomlanKamos = 18554;
    private static final int OlAriosh = 18555;

    // items
    private static final int Tissue1 = 13830;
    private static final int Tissue2 = 13831;

    public _10276_MutatedKaneusGludio() {
        super(true);
        addStartNpc(Bathis);
        addTalkId(Rohmer);
        addKillId(TomlanKamos, OlAriosh);
        addQuestItem(Tissue1, Tissue2);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("30332-03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30344-02.htm".equalsIgnoreCase(event)) {
            st.giveItems(57, 60000);
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
            if (npcId == Bathis)
                htmltext = "30332-0a.htm";
        } else if (id == CREATED && npcId == Bathis) {
            if (st.player.getLevel() >= 18)
                htmltext = "30332-01.htm";
            else
                htmltext = "30332-00.htm";
        } else if (npcId == Bathis) {
            if (cond == 1)
                htmltext = "30332-04.htm";
            else if (cond == 2)
                htmltext = "30332-05.htm";
        } else if (npcId == Rohmer)
            if (cond == 1)
                htmltext = "30344-01a.htm";
            else if (cond == 2)
                htmltext = "30344-01.htm";
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