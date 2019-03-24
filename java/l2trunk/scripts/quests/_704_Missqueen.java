package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _704_Missqueen extends Quest {
    //items
    private static final int item_1 = 7832;
    private static final int item_2 = 7833;
    //Npc
    private final int m_q = 31760;

    public _704_Missqueen() {
        addStartNpc(m_q);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = "noquest";
        if (event.equals("31760-02.htm")) {
            if (st.getCond() == 0 && st.player.getLevel() <= 20 && st.player.getLevel() >= 6 && st.player.getPkKills() == 0) {
                st.giveItems(item_1);
                st.setCond(1);
                htmltext = "c_1.htm";
                st.playSound(SOUND_ACCEPT);
            } else
                htmltext = "fail-01.htm";
        } else if (event.equals("31760-03.htm"))
            if (!st.isSet("m_scond")  && st.player.getLevel() <= 25 && st.player.getLevel() >= 20 && st.player.getPkKills() == 0) {
                st.giveItems(item_2);
                st.set("m_scond");
                htmltext = "c_2.htm";
                st.playSound(SOUND_ACCEPT);
            } else
                htmltext = "fail-02.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (npc.getNpcId() == m_q)
            return  "31760-01.htm";
        return "noquest";
    }
}