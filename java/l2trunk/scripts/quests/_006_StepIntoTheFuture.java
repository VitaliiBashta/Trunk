package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _006_StepIntoTheFuture extends Quest {
    //NPC
    private static final int Roxxy = 30006;
    private static final int Baulro = 30033;
    private static final int Windawood = 30311;
    //Quest Item
    private static final int BaulrosLetter = 7571;
    //Items
    private static final int ScrollOfEscapeGiran = 7126;
    private static final int MarkOfTraveler = 7570;

    public _006_StepIntoTheFuture() {
        super(false);
        addStartNpc(Roxxy);

        addTalkId(Baulro);
        addTalkId(Windawood);

        addQuestItem(BaulrosLetter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("rapunzel_q0006_0104.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("baul_q0006_0201.htm")) {
            st.giveItems(BaulrosLetter);
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("sir_collin_windawood_q0006_0301.htm")) {
            st.takeItems(BaulrosLetter);
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("rapunzel_q0006_0401.htm")) {
            st.giveItems(ScrollOfEscapeGiran);
            st.giveItems(MarkOfTraveler);
            st.unset("cond");
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
        if (npcId == Roxxy) {
            if (cond == 0)
                if (st.player.getRace() == Race.human && st.player.getLevel() >= 3)
                    htmltext = "rapunzel_q0006_0101.htm";
                else {
                    htmltext = "rapunzel_q0006_0102.htm";
                    st.exitCurrentQuest(true);
                }
            else if (cond == 1)
                htmltext = "rapunzel_q0006_0105.htm";
            else if (cond == 3)
                htmltext = "rapunzel_q0006_0301.htm";
        } else if (npcId == Baulro) {
            if (cond == 1)
                htmltext = "baul_q0006_0101.htm";
            else if (cond == 2 && st.getQuestItemsCount(BaulrosLetter) > 0)
                htmltext = "baul_q0006_0202.htm";
        } else if (npcId == Windawood)
            if (cond == 2 && st.getQuestItemsCount(BaulrosLetter) > 0)
                htmltext = "sir_collin_windawood_q0006_0201.htm";
            else if (cond == 2 && st.getQuestItemsCount(BaulrosLetter) == 0)
                htmltext = "sir_collin_windawood_q0006_0302.htm";
            else if (cond == 3)
                htmltext = "sir_collin_windawood_q0006_0303.htm";
        return htmltext;
    }
}
