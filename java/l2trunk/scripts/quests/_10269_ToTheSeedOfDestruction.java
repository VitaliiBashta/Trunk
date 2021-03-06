package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10269_ToTheSeedOfDestruction extends Quest {
    private final static int Keucereus = 32548;
    private final static int Allenos = 32526;

    private final static int Introduction = 13812;

    public _10269_ToTheSeedOfDestruction() {
        addStartNpc(Keucereus);

        addTalkId(Allenos);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("32548-05.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.giveItems(Introduction);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();
        int npcId = npc.getNpcId();
        if (id == COMPLETED)
            if (npcId == Allenos)
                htmltext = "32526-02.htm";
            else
                htmltext = "32548-0a.htm";
        else if (id == CREATED && npcId == Keucereus)
            if (st.player.getLevel() < 75)
                htmltext = "32548-00.htm";
            else
                htmltext = "32548-01.htm";
        else if (id == STARTED && npcId == Keucereus)
            htmltext = "32548-06.htm";
        else if (id == STARTED && npcId == Allenos) {
            htmltext = "32526-01.htm";
            st.giveAdena(29174);
            st.addExpAndSp(176121, 17671);
            st.finish();
            st.playSound(SOUND_FINISH);
        }
        return htmltext;
    }
}