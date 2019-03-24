package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _637_ThroughOnceMore extends Quest {

    //Npc
    private static final int FLAURON = 32010;
    private static final int MARK = 8067;
    //items
    private final int VISITORSMARK = 8064;
    private final int NECROHEART = 8066;

    public _637_ThroughOnceMore() {
        addStartNpc(FLAURON);

        addKillId(21565, 21566, 21567, 21568);

        addQuestItem(NECROHEART);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("falsepriest_flauron_q0637_04.htm".equals(event)) {
            st.setCond(1);
            st.start();
            st.takeItems(VISITORSMARK, 1);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() > 72 && st.haveQuestItem(VISITORSMARK) && !st.haveQuestItem(MARK))
                htmltext = "falsepriest_flauron_q0637_02.htm";
            else {
                htmltext = "falsepriest_flauron_q0637_01.htm";
                st.exitCurrentQuest();
            }
        } else if (cond == 2 && st.getQuestItemsCount(NECROHEART) == 10) {
            htmltext = "falsepriest_flauron_q0637_05.htm";
            st.takeItems(NECROHEART);
            st.giveItems(MARK);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        } else
            htmltext = "falsepriest_flauron_q0637_04.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        long count = st.getQuestItemsCount(NECROHEART);
        if (st.getCond() == 1 && Rnd.chance(40) && count < 10) {
            st.giveItems(NECROHEART);
            if (count == 9) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }

    @Override
    public void onAbort(QuestState st) {
        st.giveItemIfNotHave(VISITORSMARK);
    }
}