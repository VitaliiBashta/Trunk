package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _645_GhostsOfBatur extends Quest {
    //Npc
    private static final int Karuda = 32017;
    //Items
    private static final int CursedBurialItems = 14861;
    //Mobs
    private static final List<Integer> MOBS = List.of(
            22703, 22704, 22705, 22706, 22707);

    public _645_GhostsOfBatur() {
        super(true);

        addStartNpc(Karuda);
        addKillId(MOBS);
        addQuestItem(CursedBurialItems);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("karuda_q0645_0103.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() < 61) {
                htmltext = "karuda_q0645_0102.htm";
                st.exitCurrentQuest(true);
            } else
                htmltext = "karuda_q0645_0101.htm";
        } else {
            if (cond == 2)
                st.setCond(1);

            if (st.getQuestItemsCount(CursedBurialItems) == 0)
                htmltext = "karuda_q0645_0106.htm";
            else
                htmltext = "karuda_q0645_0105.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() > 0)
            if (Rnd.chance(15))
                st.giveItems(CursedBurialItems, 1, true);
    }
}