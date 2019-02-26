package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _167_DwarvenKinship extends Quest {
    //NPC
    private static final int Carlon = 30350;
    private static final int Haprock = 30255;
    private static final int Norman = 30210;
    //Quest items
    private static final int CarlonsLetter = 1076;
    private static final int NormansLetter = 1106;

    public _167_DwarvenKinship() {
        super(false);

        addStartNpc(Carlon);

        addTalkId(Haprock,Norman);

        addQuestItem(CarlonsLetter, NormansLetter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("30350-04.htm")) {
            st.giveItems(CarlonsLetter);
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
            st.start();
        } else if (event.equalsIgnoreCase("30255-03.htm")) {
            st.takeItems(CarlonsLetter);
            st.giveItems(ADENA_ID, 2000);
            st.giveItems(NormansLetter, 1);
            st.setCond(2);
            st.start();
        } else if (event.equalsIgnoreCase("30255-04.htm")) {
            st.takeItems(CarlonsLetter);
            st.giveItems(ADENA_ID, 2000);
            st.playSound(SOUND_GIVEUP);
            st.finish();
        } else if (event.equalsIgnoreCase("30210-02.htm")) {
            st.takeItems(NormansLetter);
            st.giveItems(ADENA_ID, 20000);
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Carlon) {
            if (cond == 0) {
                if (st.player.getLevel() >= 15)
                    htmltext = "30350-03.htm";
                else {
                    htmltext = "30350-02.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond > 0)
                htmltext = "30350-05.htm";
        } else if (npcId == Haprock) {
            if (cond == 1)
                htmltext = "30255-01.htm";
            else if (cond > 1)
                htmltext = "30255-05.htm";
        } else if (npcId == Norman && cond == 2)
            htmltext = "30210-01.htm";
        return htmltext;
    }
}