package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _011_SecretMeetingWithKetraOrcs extends Quest {
    private final int CADMON = 31296;
    private final int LEON = 31256;
    private final int WAHKAN = 31371;

    private final int MUNITIONS_BOX = 7231;

    public _011_SecretMeetingWithKetraOrcs() {
        addStartNpc(CADMON);

        addTalkId(LEON,WAHKAN);

        addQuestItem(MUNITIONS_BOX);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("guard_cadmon_q0011_0104.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("trader_leon_q0011_0201.htm".equalsIgnoreCase(event)) {
            st.giveItems(MUNITIONS_BOX);
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("herald_wakan_q0011_0301.htm".equalsIgnoreCase(event)) {
            st.takeItems(MUNITIONS_BOX);
            st.addExpAndSp(82045, 6047);
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
                    htmltext = "guard_cadmon_q0011_0101.htm";
                else {
                    htmltext = "guard_cadmon_q0011_0103.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "guard_cadmon_q0011_0105.htm";
        } else if (npcId == LEON) {
            if (cond == 1)
                htmltext = "trader_leon_q0011_0101.htm";
            else if (cond == 2)
                htmltext = "trader_leon_q0011_0202.htm";
        } else if (npcId == WAHKAN)
            if (cond == 2 && st.haveQuestItem(MUNITIONS_BOX) )
                htmltext = "herald_wakan_q0011_0201.htm";
        return htmltext;
    }
}
