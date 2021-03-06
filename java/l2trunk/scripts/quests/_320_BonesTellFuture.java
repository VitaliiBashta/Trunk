package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _320_BonesTellFuture extends Quest {
    //item
    private final int BONE_FRAGMENT = 809;

    public _320_BonesTellFuture() {
        addStartNpc(30359);

        addKillId(20517,20518);

        addQuestItem(BONE_FRAGMENT);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("tetrarch_kaitar_q0320_04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getRace() != Race.darkelf) {
                htmltext = "tetrarch_kaitar_q0320_00.htm";
                st.exitCurrentQuest();
            } else if (st.player.getLevel() >= 10)
                htmltext = "tetrarch_kaitar_q0320_03.htm";
            else {
                htmltext = "tetrarch_kaitar_q0320_02.htm";
                st.exitCurrentQuest();
            }
        } else if (st.getQuestItemsCount(BONE_FRAGMENT) < 10)
            htmltext = "tetrarch_kaitar_q0320_05.htm";
        else {
            htmltext = "tetrarch_kaitar_q0320_06.htm";
            st.giveItems(ADENA_ID, 8470);
            st.takeItems(BONE_FRAGMENT);
            st.exitCurrentQuest();
            st.unset("cond");
            st.playSound(SOUND_FINISH);
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        st.rollAndGive(BONE_FRAGMENT, 1, 1, 10, 10);
        if (st.haveQuestItem(BONE_FRAGMENT, 10))
            st.setCond(2);
        st.start();
    }
}
