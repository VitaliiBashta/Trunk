package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _702_ATrapForRevenge extends Quest {
    // NPC's
    private static final int PLENOS = 32563;
    private static final int TENIUS = 32555;
    // ITEMS
    private static final int DRAKES_FLESH = 13877;
    private static final int LEONARD = 9628;
    private static final int ADAMANTINE = 9629;
    private static final int ORICHALCUM = 9630;
    // MOB's
    private static final int DRAK = 22612;
    private static final int MUTATED_DRAKE_WING = 22611;

    public _702_ATrapForRevenge() {
        super(true);

        addStartNpc(PLENOS);
        addTalkId(PLENOS);
        addTalkId(TENIUS);
        addKillId(DRAK);
        addKillId(MUTATED_DRAKE_WING);
        addQuestItem(DRAKES_FLESH);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        String htmltext = event;

        if (event.equals("take") && cond == 0) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
            htmltext = "plenos_q702_2.htm";
        } else if ("took_mission".equals(event) && cond == 1) {
            st.setCond(2);
            htmltext = "tenius_q702_3.htm";
            st.playSound(SOUND_MIDDLE);
        } else if ("hand_over".equals(event) && cond == 2) {
            int rand = Rnd.get(1, 3);
            htmltext = "tenius_q702_6.htm";
            st.takeItems(DRAKES_FLESH, -1);
            if (rand == 1)
                st.giveItems(LEONARD, 3);
            else if (rand == 2)
                st.giveItems(ADAMANTINE, 3);
            else if (rand == 3)
                st.giveItems(ORICHALCUM, 3);

            st.giveItems(ADENA_ID, 157200);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == PLENOS) {
            if (cond == 0) {
                if (st.player.getLevel() >= 78) {
                    if (st.player.isQuestCompleted(_10273_GoodDayToFly.class))
                        htmltext = "plenos_q702_1.htm";
                    else
                        htmltext = "plenos_q702_1a.htm";
                } else {
                    htmltext = "plenos_q702_1b.htm";
                    st.exitCurrentQuest(true);
                }
            } else
                htmltext = "plenos_q702_1c.htm";

        } else if (npcId == TENIUS)
            if (cond == 1)
                htmltext = "tenius_q702_1.htm";
            else if (cond == 2 && st.getQuestItemsCount(DRAKES_FLESH) < 100)
                htmltext = "tenius_q702_4.htm";
            else if (cond == 2 && st.getQuestItemsCount(DRAKES_FLESH) >= 100)
                htmltext = "tenius_q702_5.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 2 && (npcId == DRAK || npcId == MUTATED_DRAKE_WING) && st.getQuestItemsCount(DRAKES_FLESH) <= 100) {
            st.giveItems(DRAKES_FLESH);
            st.playSound(SOUND_ITEMGET);
        }
    }
}