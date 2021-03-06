package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _012_SecretMeetingWithVarkaSilenos extends Quest {
    private final int CADMON = 31296;
    private final int HELMUT = 31258;
    private final int NARAN_ASHANUK = 31378;

    private final int MUNITIONS_BOX = 7232;

    public _012_SecretMeetingWithVarkaSilenos() {
        addStartNpc(CADMON);

        addTalkId(HELMUT,NARAN_ASHANUK);

        addQuestItem(MUNITIONS_BOX);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("guard_cadmon_q0012_0104.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("trader_helmut_q0012_0201.htm".equalsIgnoreCase(event)) {
            st.giveItems(MUNITIONS_BOX);
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("herald_naran_q0012_0301.htm".equalsIgnoreCase(event)) {
            st.takeItems(MUNITIONS_BOX);
            st.addExpAndSp(233125, 18142);
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == CADMON) {
            if (cond == 0) {
                if (st.player.getLevel() >= 74)
                    htmltext = "guard_cadmon_q0012_0101.htm";
                else {
                    htmltext = "guard_cadmon_q0012_0103.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "guard_cadmon_q0012_0105.htm";
        } else if (npcId == HELMUT) {
            if (cond == 1)
                htmltext = "trader_helmut_q0012_0101.htm";
            else if (cond == 2)
                htmltext = "trader_helmut_q0012_0202.htm";
        } else if (npcId == NARAN_ASHANUK)
            if (cond == 2 && st.haveQuestItem(MUNITIONS_BOX))
                htmltext = "herald_naran_q0012_0201.htm";
        return htmltext;
    }
}
