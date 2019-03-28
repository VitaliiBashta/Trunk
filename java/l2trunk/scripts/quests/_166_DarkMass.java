package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _166_DarkMass extends Quest {
    private final int UNDRES_LETTER_ID = 1088;
    private final int CEREMONIAL_DAGGER_ID = 1089;
    private final int DREVIANT_WINE_ID = 1090;
    private final int GARMIELS_SCRIPTURE_ID = 1091;

    public _166_DarkMass() {
        addStartNpc(30130);
        addTalkId(30135, 30139, 30143);
        addQuestItem(CEREMONIAL_DAGGER_ID, DREVIANT_WINE_ID, GARMIELS_SCRIPTURE_ID, UNDRES_LETTER_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("1")) {
            htmltext = "30130-04.htm";
            st.giveItems(UNDRES_LETTER_ID);
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {

        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int cond = st.getCond();

        if (npcId == 30130) {
            if (id == CREATED) {
                if (st.player.getRace() != Race.darkelf && st.player.getRace() != Race.human)
                    htmltext = "30130-00.htm";
                else if (st.player.getLevel() >= 2) {
                    htmltext = "30130-03.htm";
                    return htmltext;
                } else {
                    htmltext = "30130-02.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "30130-05.htm";
            else if (cond == 2) {
                htmltext = "30130-06.htm";
                st.takeAllItems(UNDRES_LETTER_ID,CEREMONIAL_DAGGER_ID,DREVIANT_WINE_ID,GARMIELS_SCRIPTURE_ID);
                st.giveAdena(2966);
                st.player.addExpAndSp(5672, 446);
                if (st.player.getClassId().occupation() == 0)
                    st.player.sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide."));
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        } else if (npcId == 30135) {
            if (cond == 1 ) {
                giveItem(st, CEREMONIAL_DAGGER_ID);
                htmltext = "30135-01.htm";
            } else
                htmltext = "30135-02.htm";
        } else if (npcId == 30139) {
            if (cond == 1 ) {
                giveItem(st, DREVIANT_WINE_ID);
                htmltext = "30139-01.htm";
            } else
                htmltext = "30139-02.htm";
        } else if (npcId == 30143)
            if (cond == 1 ) {
                giveItem(st, GARMIELS_SCRIPTURE_ID);
                htmltext = "30143-01.htm";
            } else
                htmltext = "30143-02.htm";
        return htmltext;
    }

    private void giveItem(QuestState st, int item) {
        st.giveItemIfNotHave(item);
        if (st.haveAllQuestItems(CEREMONIAL_DAGGER_ID,DREVIANT_WINE_ID,GARMIELS_SCRIPTURE_ID))
            st.setCond(2);
    }
}