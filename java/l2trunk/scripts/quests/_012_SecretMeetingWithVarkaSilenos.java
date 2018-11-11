package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

public class _012_SecretMeetingWithVarkaSilenos extends Quest implements ScriptFile {
    private final int CADMON = 31296;
    private final int HELMUT = 31258;
    private final int NARAN_ASHANUK = 31378;

    private final int MUNITIONS_BOX = 7232;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _012_SecretMeetingWithVarkaSilenos() {
        super(false);

        addStartNpc(CADMON);

        addTalkId(HELMUT);
        addTalkId(NARAN_ASHANUK);

        addQuestItem(MUNITIONS_BOX);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("guard_cadmon_q0012_0104.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("trader_helmut_q0012_0201.htm")) {
            st.giveItems(MUNITIONS_BOX, 1);
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("herald_naran_q0012_0301.htm")) {
            st.takeItems(MUNITIONS_BOX, 1);
            st.addExpAndSp(233125, 18142);
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
        if (npcId == CADMON) {
            if (cond == 0) {
                if (st.getPlayer().getLevel() >= 74)
                    htmltext = "guard_cadmon_q0012_0101.htm";
                else {
                    htmltext = "guard_cadmon_q0012_0103.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1)
                htmltext = "guard_cadmon_q0012_0105.htm";
        } else if (npcId == HELMUT) {
            if (cond == 1)
                htmltext = "trader_helmut_q0012_0101.htm";
            else if (cond == 2)
                htmltext = "trader_helmut_q0012_0202.htm";
        } else if (npcId == NARAN_ASHANUK)
            if (cond == 2 && st.getQuestItemsCount(MUNITIONS_BOX) > 0)
                htmltext = "herald_naran_q0012_0201.htm";
        return htmltext;
    }
}
