package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;
import java.util.Map;

public final class _312_TakeAdvantageOfTheCrisis extends Quest {
    private static final int FILAUR = 30535;
    private static final int MINERAL_FRAGMENT = 14875;
    private static final int DROP_CHANCE = 40;
    private static final List<Integer> MINE_MOBS = List.of(
            22678, 22679, 22680, 22681, 22682, 22683, 22684, 22685, 22686, 22687, 22688, 22689, 22690);

    private static final Map<Integer, Integer> rewardcount = Map.of(
            9487, 366,
            9488, 229,
            9489, 183,
            9490, 122,
            9491, 122,
            9497, 129,
            9625, 667,
            9626, 1000,
            9628, 24,
            9630, 36);

    public _312_TakeAdvantageOfTheCrisis() {
        super(false);

        addStartNpc(FILAUR);
        addKillId(MINE_MOBS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("30535-06.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("30535-09.htm")) {
            st.exitCurrentQuest(true);
            st.playSound(SOUND_FINISH);
        } else {
            int id = Integer.parseInt(event);
            if (id > 0) {
                int count = 0;
                if (rewardcount.containsKey(id))
                    count = rewardcount.get(id);
                if (count > 0) {
                    if (st.getQuestItemsCount(MINERAL_FRAGMENT) >= count) {
                        st.giveItems(id, 1);
                        st.takeItems(MINERAL_FRAGMENT, count);
                        st.playSound(SOUND_MIDDLE);
                        return "30535-16.htm";
                    }
                    return "30535-15.htm";
                }
            }
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (npcId == FILAUR)
            if (cond == 0) {
                if (st.player.getLevel() >= 80)
                    htmltext = "30535-01.htm";
                else {
                    htmltext = "30535-00.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (id == STARTED)
                if (st.haveAnyQuestItems(MINERAL_FRAGMENT))
                    htmltext = "30535-10.htm";
                else
                    htmltext = "30535-07.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && MINE_MOBS.contains(npc.getNpcId()))
            if (Rnd.chance(DROP_CHANCE)) {
                st.giveItems(MINERAL_FRAGMENT, (int) Config.RATE_QUESTS_REWARD);
                st.playSound(SOUND_ITEMGET);
            }
    }
}