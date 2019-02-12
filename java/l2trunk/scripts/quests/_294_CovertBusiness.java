package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _294_CovertBusiness extends Quest {
    private static final int BatFang = 1491;
    private static final int RingOfRaccoon = 1508;

    private static final int BarbedBat = 20370;
    private static final int BladeBat = 20480;

    private static final int Keef = 30534;

    public _294_CovertBusiness() {
        super(false);

        addStartNpc(Keef);
        addTalkId(Keef);

        addKillId(BarbedBat);
        addKillId(BladeBat);

        addQuestItem(BatFang);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("elder_keef_q0294_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int id = st.getState();

        if (id == CREATED) {
            if (st.player.getRace() != Race.dwarf) {
                htmltext = "elder_keef_q0294_00.htm";
                st.exitCurrentQuest(true);
            } else if (st.player.getLevel() >= 10) {
                htmltext = "elder_keef_q0294_02.htm";
                return htmltext;
            } else {
                htmltext = "elder_keef_q0294_01.htm";
                st.exitCurrentQuest(true);
            }
        } else if (st.getQuestItemsCount(BatFang) < 100)
            htmltext = "elder_keef_q0294_04.htm";
        else {
            if (st.getQuestItemsCount(RingOfRaccoon) < 1) {
                st.giveItems(RingOfRaccoon);
                htmltext = "elder_keef_q0294_05.htm";
            } else {
                st.giveItems(ADENA_ID, 2400);
                htmltext = "elder_keef_q0294_06.htm";
            }
            st.addExpAndSp(0, 600);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1)
            st.rollAndGive(BatFang, 1, 2, 100, 100);
    }
}