package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _265_ChainsOfSlavery extends Quest {
    // NPC
    private static final int KRISTIN = 30357;

    // MOBS
    private static final int IMP = 20004;
    private static final int IMP_ELDER = 20005;

    // ITEMS
    private static final int IMP_SHACKLES = 1368;

    public _265_ChainsOfSlavery() {
        super(false);
        addStartNpc(KRISTIN);

        addKillId(IMP);
        addKillId(IMP_ELDER);

        addQuestItem(IMP_SHACKLES);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("sentry_krpion_q0265_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("sentry_krpion_q0265_06.htm".equalsIgnoreCase(event))
            st.exitCurrentQuest();
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        if (st.getCond() == 0) {
            if (st.player.getRace() != Race.darkelf) {
                htmltext = "sentry_krpion_q0265_00.htm";
                st.exitCurrentQuest();
            } else if (st.player.getLevel() < 6) {
                htmltext = "sentry_krpion_q0265_01.htm";
                st.exitCurrentQuest();
            } else
                htmltext = "sentry_krpion_q0265_02.htm";
        } else {
            long count = st.getQuestItemsCount(IMP_SHACKLES);
            if (count > 0)
                st.giveAdena(13 * count + 500);
            st.takeItems(IMP_SHACKLES);
            htmltext = "sentry_krpion_q0265_05.htm";

            if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("p1q2")) {
                st.player.setVar("p1q2");
                st.player.sendPacket(new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n                  Go find the Newbie Guide."));
                QuestState qs = st.player.getQuestState(_255_Tutorial.class);
                if (qs != null && qs.getInt("Ex") != 10) {
                    st.showQuestionMark(26);
                    qs.set("Ex", 10);
                    if (st.player.getClassId().isMage()) {
                        st.playTutorialVoice("tutorial_voice_027");
                        st.giveItems(5790, 3000);
                    } else {
                        st.playTutorialVoice("tutorial_voice_026");
                        st.giveItems(5789, 6000);
                    }
                }
            } else
                htmltext = "sentry_krpion_q0265_04.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (st.getCond() == 1 && Rnd.chance(5 + npcId - 20004)) {
            st.giveItems(IMP_SHACKLES);
            st.playSound(SOUND_ITEMGET);
        }
    }
}