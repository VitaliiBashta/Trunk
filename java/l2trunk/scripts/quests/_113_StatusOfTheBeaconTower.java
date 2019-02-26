package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _113_StatusOfTheBeaconTower extends Quest {
    // NPC
    private static final int MOIRA = 31979;
    private static final int TORRANT = 32016;

    // QUEST ITEM
    private static final int BOX = 8086;

    public _113_StatusOfTheBeaconTower() {
        super(false);
        addStartNpc(MOIRA);
        addTalkId(TORRANT);

        addQuestItem(BOX);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("seer_moirase_q0113_0104.htm")) {
            st.setCond(1);
            st.giveItems(BOX, 1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("torant_q0113_0201.htm")) {
            st.giveItems(ADENA_ID, 154800);
            st.addExpAndSp(619300, 44200);
            st.takeItems(BOX, 1);
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (id == COMPLETED)
            htmltext = "completed";
        else if (npcId == MOIRA) {
            if (id == CREATED) {
                if (st.player.getLevel() >= 40)
                    htmltext = "seer_moirase_q0113_0101.htm";
                else {
                    htmltext = "seer_moirase_q0113_0103.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "seer_moirase_q0113_0105.htm";
        } else if (npcId == TORRANT && st.getQuestItemsCount(BOX) == 1)
            htmltext = "torant_q0113_0101.htm";
        return htmltext;
    }
}