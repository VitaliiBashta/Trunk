package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _370_AnElderSowsSeeds extends Quest {
    //npc
    private static final int CASIAN = 30612;
    //mobs
    private static final List<Integer> MOBS = List.of(
            20082, 20084, 20086, 20089, 20090);
    //items
    private static final int SPB_PAGE = 5916;
    //Collection Kranvel's Spellbooks
    private static final List<Integer> CHAPTERS = List.of(
            5917, 5918, 5919, 5920);

    public _370_AnElderSowsSeeds() {
        addStartNpc(CASIAN);

        addKillId(MOBS);

        addQuestItem(SPB_PAGE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if ("30612-1.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30612-6.htm".equalsIgnoreCase(event)) {
            long mincount = CHAPTERS.stream().min(Integer::compareTo).orElse(0);
            if (mincount > 0) {
                CHAPTERS.forEach(itemId -> st.takeItems(itemId, mincount));

                st.giveAdena(3600 * mincount);
                htmltext = "30612-8.htm";
            } else
                htmltext = "30612-4.htm";
        } else if ("30612-9.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";

        int cond = st.getCond();

        if (st.getState() == CREATED) {
            if (st.player.getLevel() < 28) {
                htmltext = "30612-0a.htm";
                st.exitCurrentQuest();
            } else
                htmltext = "30612-0.htm";
        } else if (cond == 1)
            htmltext = "30612-4.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED)
            return;

        if (Rnd.chance(Math.min((int) (15 * st.getRateQuestsReward()), 100))) {
            st.giveItems(SPB_PAGE);
            st.playSound(SOUND_ITEMGET);
        }
    }
}