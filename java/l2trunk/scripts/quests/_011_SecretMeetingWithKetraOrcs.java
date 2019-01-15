package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

public final class _011_SecretMeetingWithKetraOrcs extends Quest {
    private final int CADMON = 31296;
    private final int LEON = 31256;
    private final int WAHKAN = 31371;

    private final int MUNITIONS_BOX = 7231;

    public _011_SecretMeetingWithKetraOrcs() {
        super(false);

        addStartNpc(CADMON);

        addTalkId(LEON);
        addTalkId(WAHKAN);

        addQuestItem(MUNITIONS_BOX);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("guard_cadmon_q0011_0104.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("trader_leon_q0011_0201.htm")) {
            st.giveItems(MUNITIONS_BOX, 1);
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("herald_wakan_q0011_0301.htm")) {
            st.takeItems(MUNITIONS_BOX, 1);
            st.addExpAndSp(82045, 6047);
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
                    htmltext = "guard_cadmon_q0011_0101.htm";
                else {
                    htmltext = "guard_cadmon_q0011_0103.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1)
                htmltext = "guard_cadmon_q0011_0105.htm";
        } else if (npcId == LEON) {
            if (cond == 1)
                htmltext = "trader_leon_q0011_0101.htm";
            else if (cond == 2)
                htmltext = "trader_leon_q0011_0202.htm";
        } else if (npcId == WAHKAN)
            if (cond == 2 && st.getQuestItemsCount(MUNITIONS_BOX) > 0)
                htmltext = "herald_wakan_q0011_0201.htm";
        return htmltext;
    }
}
