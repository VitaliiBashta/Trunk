package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _636_TruthBeyond extends Quest {
    //items
    private static final int MARK = 8067;
    private static final int VISITORSMARK = 8064;
    //Npc
    private final int ELIYAH = 31329;
    private final int FLAURON = 32010;

    public _636_TruthBeyond() {
        super(false);

        addStartNpc(ELIYAH);
        addTalkId(FLAURON);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("priest_eliyah_q0636_05.htm".equals(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("falsepriest_flauron_q0636_02.htm".equals(event)) {
            st.playSound(SOUND_FINISH);
            st.giveItems(VISITORSMARK, 1);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == ELIYAH && cond == 0) {
            if (st.getQuestItemsCount(VISITORSMARK) == 0 && st.getQuestItemsCount(MARK) == 0) {
                if (st.player.getLevel() > 72)
                    htmltext = "priest_eliyah_q0636_01.htm";
                else {
                    htmltext = "priest_eliyah_q0636_03.htm";
                    st.exitCurrentQuest();
                }
            } else
                htmltext = "priest_eliyah_q0636_06.htm";
        } else if (npcId == FLAURON)
            if (cond == 1) {
                htmltext = "falsepriest_flauron_q0636_01.htm";
                st.setCond(2);
            } else
                htmltext = "falsepriest_flauron_q0636_03.htm";
        return htmltext;
    }
}