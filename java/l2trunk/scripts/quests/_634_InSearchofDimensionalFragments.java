package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.stream.IntStream;

public final class _634_InSearchofDimensionalFragments extends Quest {
    private static final int DIMENSION_FRAGMENT_ID = 7079;

    public _634_InSearchofDimensionalFragments() {
        super(true);

        int[] npcs = IntStream.rangeClosed(31494, 31508).toArray();
            addTalkId(npcs);
            addStartNpc(npcs);

        addKillId(IntStream.rangeClosed(21208, 21256).toArray());
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equalsIgnoreCase(event)) {
            htmltext = "dimension_keeper_1_q0634_03.htm";
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
        } else if ("634_2".equalsIgnoreCase(event)) {
            htmltext = "dimension_keeper_1_q0634_06.htm";
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();
        if (id == CREATED) {
            if (st.player.getLevel() > 20)
                htmltext = "dimension_keeper_1_q0634_01.htm";
            else {
                htmltext = "dimension_keeper_1_q0634_02.htm";
                st.exitCurrentQuest();
            }
        } else if (id == STARTED)
            htmltext = "dimension_keeper_1_q0634_04.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        st.rollAndGive(DIMENSION_FRAGMENT_ID, 2, 60 * Experience.penaltyModifier(st.calculateLevelDiffForDrop(npc.getLevel(), st.player.getLevel()), 9) * npc.getTemplate().rateHp / 4);
    }
}