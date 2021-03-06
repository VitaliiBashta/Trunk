package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.stream.IntStream;

public final class _368_TrespassingIntoTheSacredArea extends Quest {
    //NPCs
    private static final int RESTINA = 30926;
    //items
    private static final int BLADE_STAKATO_FANG = 5881;
    //Chances
    private static final int BLADE_STAKATO_FANG_BASECHANCE = 10;

    public _368_TrespassingIntoTheSacredArea() {
        addStartNpc(RESTINA);
        addKillId(IntStream.rangeClosed(20794, 20797).toArray());
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != RESTINA)
            return htmltext;
        if (st.getState() == CREATED) {
            if (st.player.getLevel() < 36) {
                htmltext = "30926-00.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "30926-01.htm";
                st.setCond(0);
            }
        } else {
            long count = st.getQuestItemsCount(BLADE_STAKATO_FANG);
            if (count > 0) {
                htmltext = "30926-04.htm";
                st.takeItems(BLADE_STAKATO_FANG);
                st.giveAdena( count * 2250);
                st.playSound(SOUND_MIDDLE);
            } else
                htmltext = "30926-03.htm";
        }
        return htmltext;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        if ("30926-02.htm".equalsIgnoreCase(event) && _state == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("30926-05.htm".equalsIgnoreCase(event) && _state == STARTED) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;

        if (Rnd.chance(npc.getNpcId() - 20794 + BLADE_STAKATO_FANG_BASECHANCE)) {
            qs.giveItems(BLADE_STAKATO_FANG);
            qs.playSound(SOUND_ITEMGET);
        }
    }
}
