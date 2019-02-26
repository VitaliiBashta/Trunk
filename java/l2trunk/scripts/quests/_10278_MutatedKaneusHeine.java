package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10278_MutatedKaneusHeine extends Quest {
    // NPCs
    private static final int Gosta = 30916;
    private static final int Minevia = 30907;

    // MOBs
    private static final int BladeOtis = 18562;
    private static final int WeirdBunei = 18564;

    // items
    private static final int Tissue1 = 13834;
    private static final int Tissue2 = 13835;

    public _10278_MutatedKaneusHeine() {
        super(true);
        addStartNpc(Gosta);
        addTalkId(Minevia);
        addKillId(BladeOtis, WeirdBunei);
        addQuestItem(Tissue1, Tissue2);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("30916-03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30907-02.htm".equalsIgnoreCase(event)) {
            st.giveItems(57, 180000);
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
            if (npcId == Gosta)
                htmltext = "30916-0a.htm";
        } else if (id == CREATED && npcId == Gosta) {
            if (st.player.getLevel() >= 38)
                htmltext = "30916-01.htm";
            else
                htmltext = "30916-00.htm";
        } else if (npcId == Gosta) {
            if (cond == 1)
                htmltext = "30916-04.htm";
            else if (cond == 2)
                htmltext = "30916-05.htm";
        } else if (npcId == Minevia)
            if (cond == 1)
                htmltext = "30907-01a.htm";
            else if (cond == 2)
                htmltext = "30907-01.htm";
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