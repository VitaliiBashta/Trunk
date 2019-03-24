package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _122_OminousNews extends Quest {
    private final int MOIRA = 31979;
    private final int KARUDA = 32017;

    public _122_OminousNews() {
        addStartNpc(MOIRA);
        addTalkId(KARUDA);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext;
        int cond = st.getCond();
        htmltext = event;
        if ("seer_moirase_q0122_0104.htm".equalsIgnoreCase(htmltext) && cond == 0) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("karuda_q0122_0201.htm".equalsIgnoreCase(htmltext))
            if (cond == 1) {
                st.giveAdena( 8923);
                st.addExpAndSp(45151, 2310);
                st.playSound(SOUND_FINISH);
                st.finish();
            } else
                htmltext = "noquest";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == MOIRA) {
            if (cond == 0) {
                if (st.player.getLevel() >= 20)
                    htmltext = "seer_moirase_q0122_0101.htm";
                else {
                    htmltext = "seer_moirase_q0122_0103.htm";
                    st.exitCurrentQuest();
                }
            } else
                htmltext = "seer_moirase_q0122_0104.htm";
        } else if (npcId == KARUDA && cond == 1)
            htmltext = "karuda_q0122_0101.htm";
        return htmltext;
    }
}