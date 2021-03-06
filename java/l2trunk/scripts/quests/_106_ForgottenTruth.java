package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _106_ForgottenTruth extends Quest {
    private static final int ELDRITCH_DAGGER = 989;
    private static final int ELDRITCH_STAFF = 2373;
    private final int ONYX_TALISMAN1 = 984;
    private final int ONYX_TALISMAN2 = 985;
    private final int ANCIENT_SCROLL = 986;
    private final int ANCIENT_CLAY_TABLET = 987;
    private final int KARTAS_TRANSLATION = 988;

    public _106_ForgottenTruth() {
        addStartNpc(30358);
        addTalkId(30133);

        addKillId(27070);

        addQuestItem(KARTAS_TRANSLATION, ONYX_TALISMAN1, ONYX_TALISMAN2, ANCIENT_SCROLL, ANCIENT_CLAY_TABLET);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equals("tetrarch_thifiell_q0106_05.htm")) {
            st.giveItems(ONYX_TALISMAN1);
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == 30358) {
            if (cond == 0) {
                if (st.player.getRace() != Race.darkelf) {
                    htmltext = "tetrarch_thifiell_q0106_00.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() >= 10)
                    htmltext = "tetrarch_thifiell_q0106_03.htm";
                else {
                    htmltext = "tetrarch_thifiell_q0106_02.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond > 0 && st.haveAnyQuestItems(ONYX_TALISMAN1,ONYX_TALISMAN2) && !st.haveQuestItem(KARTAS_TRANSLATION))
                htmltext = "tetrarch_thifiell_q0106_06.htm";
            else if (cond == 4 && st.haveQuestItem(KARTAS_TRANSLATION) ) {
                htmltext = "tetrarch_thifiell_q0106_07.htm";
                st.takeItems(KARTAS_TRANSLATION);

                if (st.player.getClassId().isMage())
                    st.giveItems(ELDRITCH_STAFF);
                else
                    st.giveItems(ELDRITCH_DAGGER);

                st.giveAdena( 10266);
                st.player.addExpAndSp(24195, 2074);

                if (st.player.getClassId().occupation() == 0) {
                    st.player.setVar("p1q3"); // flag for helper
                    st.player.sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide."));
                    st.giveItems(1060, 100); // healing potion
                    for (int item = 4412; item <= 4417; item++)
                        st.giveItems(item, 10); // echo cry
                    if (st.player.getClassId().isMage()) {
                        st.playTutorialVoice("tutorial_voice_027");
                        st.giveItems(5790, 3000); // newbie sps
                    } else {
                        st.playTutorialVoice("tutorial_voice_026");
                        st.giveItems(5789, 6000); // newbie ss
                    }
                }

                st.finish();
                st.playSound(SOUND_FINISH);
            }
        } else if (npcId == 30133)
            if (cond == 1 && st.haveQuestItem(ONYX_TALISMAN1)) {
                htmltext = "karta_q0106_01.htm";
                st.takeItems(ONYX_TALISMAN1);
                st.giveItems(ONYX_TALISMAN2);
                st.setCond(2);
            } else if (cond == 2 && st.getQuestItemsCount(ONYX_TALISMAN2) > 0 && (st.getQuestItemsCount(ANCIENT_SCROLL) == 0 || st.getQuestItemsCount(ANCIENT_CLAY_TABLET) == 0))
                htmltext = "karta_q0106_02.htm";
            else if (cond == 3 && st.haveAllQuestItems(ANCIENT_SCROLL,ANCIENT_CLAY_TABLET)) {
                htmltext = "karta_q0106_03.htm";
                st.takeAllItems(ONYX_TALISMAN2,ANCIENT_SCROLL, ANCIENT_CLAY_TABLET);
                st.giveItems(KARTAS_TRANSLATION);
                st.setCond(4);
            } else if (cond == 4 && st.haveQuestItem(KARTAS_TRANSLATION))
                htmltext = "karta_q0106_04.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == 27070)
            if (st.getCond() == 2 && st.haveQuestItem(ONYX_TALISMAN2))
                if (Rnd.chance(20))
                    st.giveItemIfNotHave(ANCIENT_SCROLL);
                else if (Rnd.chance(10)) {
                    st.giveItemIfNotHave(ANCIENT_CLAY_TABLET);
                }
        st.playSound(SOUND_MIDDLE);
        if (st.haveAllQuestItems(ANCIENT_SCROLL, ANCIENT_CLAY_TABLET))
            st.setCond(3);
    }
}