package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _461_RumbleInTheBase extends Quest{
    private static final int Stan = 30200;
    private static final int ShoesStringofSelMahum = 16382;
    private static final int ShinySalmon = 15503;
    private static final List<Integer> SelMahums = List.of(22786, 22787, 22788);
    private static final int SelChef = 18908;

    public _461_RumbleInTheBase() {
        addStartNpc(Stan);
        addQuestItem(ShoesStringofSelMahum, ShinySalmon);
        addKillId(SelMahums);
        addKillId(SelChef);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("stan_q461_03.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npc.getNpcId() == Stan) {
            switch (st.getState()) {
                case CREATED:
                    if (st.player.getLevel() >= 82 && st.player.isQuestCompleted(_252_GoodSmell.class)) {
                        if (st.isNowAvailable())
                            htmltext = "stan_q461_01.htm";
                        else
                            htmltext = "stan_q461_00a.htm";
                    } else
                        htmltext = "stan_q461_00.htm";
                    break;
                case STARTED:
                    if (cond == 1)
                        htmltext = "stan_q461_04.htm";
                    else if (cond == 2) {
                        htmltext = "stan_q461_05.htm";
                        st.takeItems(ShoesStringofSelMahum);
                        st.takeItems(ShinySalmon);
                        st.addExpAndSp(224784, 342528);
                        st.complete();
                        st.playSound(SOUND_FINISH);
                        st.exitCurrentQuest(this);
                    }
                    break;
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        int npcId = npc.getNpcId();
        if (cond == 1) {
            if (st.getQuestItemsCount(ShoesStringofSelMahum) < 10 && st.getQuestItemsCount(ShinySalmon) < 5) {
                if (st.getQuestItemsCount(ShoesStringofSelMahum) < 10 && SelMahums.contains(npcId))
                    st.rollAndGive(ShoesStringofSelMahum, 1, 20);
                if (st.getQuestItemsCount(ShinySalmon) < 5 && npcId == SelChef)
                    st.rollAndGive(ShinySalmon, 1, 10);
            } else
                st.setCond(2);
        }
    }
}