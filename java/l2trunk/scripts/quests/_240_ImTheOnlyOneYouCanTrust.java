package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _240_ImTheOnlyOneYouCanTrust extends Quest {
    private static final int KINTAIJIN = 32640;

    private static final int SpikedStakato = 22617;
    private static final int CannibalisticStakatoFollower = 22624;
    private static final int CannibalisticStakatoLeader1 = 22625;
    private static final int CannibalisticStakatoLeader2 = 22626;

    private static final int STAKATOFANGS = 14879;

    public _240_ImTheOnlyOneYouCanTrust() {
        addStartNpc(KINTAIJIN);
        addKillId(SpikedStakato, CannibalisticStakatoFollower, CannibalisticStakatoLeader1, CannibalisticStakatoLeader2);
        addQuestItem(STAKATOFANGS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("32640-3.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();
        int cond = st.getCond();
        if (id == COMPLETED)
            htmltext = "32640-10.htm";
        else if (id == CREATED) {
            if (st.player.getLevel() >= 81)
                htmltext = "32640-1.htm";
            else {
                htmltext = "32640-0.htm";
                st.exitCurrentQuest();
            }
        } else if (cond == 1)
            htmltext = "32640-8.htm";
        else if (cond == 2) {
            st.addExpAndSp(589542, 36800);
            st.finish();
            st.playSound(SOUND_FINISH);
            htmltext = "32640-9.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            st.giveItemIfNotHave(STAKATOFANGS, 25);
            if (st.haveQuestItem(STAKATOFANGS, 25)) {
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            }
        }
    }
}