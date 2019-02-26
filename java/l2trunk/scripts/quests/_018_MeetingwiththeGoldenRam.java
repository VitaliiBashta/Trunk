package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _018_MeetingwiththeGoldenRam extends Quest {
    private static final int SUPPLY_BOX = 7245;

    public _018_MeetingwiththeGoldenRam() {
        super(false);

        addStartNpc(31314);

        addTalkId(31315,31555);

        addQuestItem(SUPPLY_BOX);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        switch (event) {
            case "warehouse_chief_donal_q0018_0104.htm":
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                break;
            case "freighter_daisy_q0018_0201.htm":
                st.setCond(2);
                st.giveItems(SUPPLY_BOX, 1);
                st.playSound(SOUND_ACCEPT);
                break;
            case "supplier_abercrombie_q0018_0301.htm":
                st.takeItems(SUPPLY_BOX, -1);
                st.addExpAndSp(126668, 11731);
                st.giveItems(ADENA_ID, 40000);
                st.playSound(SOUND_FINISH);
                st.finish();
                break;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 31314) {
            if (cond == 0)
                if (st.player.getLevel() >= 66)
                    htmltext = "warehouse_chief_donal_q0018_0101.htm";
                else {
                    htmltext = "warehouse_chief_donal_q0018_0103.htm";
                    st.exitCurrentQuest();
                }
            else if (cond == 1)
                htmltext = "warehouse_chief_donal_q0018_0105.htm";
        } else if (npcId == 31315) {
            if (cond == 1)
                htmltext = "freighter_daisy_q0018_0101.htm";
            else if (cond == 2)
                htmltext = "freighter_daisy_q0018_0202.htm";
        } else if (npcId == 31555)
            if (cond == 2 && st.getQuestItemsCount(SUPPLY_BOX) == 1)
                htmltext = "supplier_abercrombie_q0018_0201.htm";
        return htmltext;
    }
}