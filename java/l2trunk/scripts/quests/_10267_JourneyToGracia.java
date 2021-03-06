package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10267_JourneyToGracia extends Quest {
    private final static int Orven = 30857;
    private final static int Keucereus = 32548;
    private final static int Papiku = 32564;

    private final static int Letter = 13810;

    public _10267_JourneyToGracia() {
        addStartNpc(Orven);

        addTalkId(Keucereus,Papiku);

        addQuestItem(Letter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("30857-06.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.giveItems(Letter);
        } else if ("32564-02.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("32548-02.htm".equalsIgnoreCase(event)) {
            st.giveAdena( 92500);
            st.takeItems(Letter);
            st.addExpAndSp(75480, 7570);
            st.unset("cond");
            st.finish();
            st.playSound(SOUND_FINISH);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (id == COMPLETED) {
            if (npcId == Keucereus)
                htmltext = "32548-03.htm";
            else if (npcId == Orven)
                htmltext = "30857-0a.htm";
        } else if (id == CREATED) {
            if (npcId == Orven)
                if (st.player.getLevel() < 75)
                    htmltext = "30857-00.htm";
                else
                    htmltext = "30857-01.htm";
        } else if (id == STARTED)
            if (npcId == Orven)
                htmltext = "30857-07.htm";
            else if (npcId == Papiku) {
                if (cond == 1)
                    htmltext = "32564-01.htm";
                else
                    htmltext = "32564-03.htm";
            } else if (npcId == Keucereus && cond == 2)
                htmltext = "32548-01.htm";
        return htmltext;
    }
}