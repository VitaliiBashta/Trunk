package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _168_DeliverSupplies extends Quest {
    private final int JENNIES_LETTER_ID = 1153;
    private final int SENTRY_BLADE1_ID = 1154;
    private final int SENTRY_BLADE2_ID = 1155;
    private final int SENTRY_BLADE3_ID = 1156;
    private final int OLD_BRONZE_SWORD_ID = 1157;

    public _168_DeliverSupplies() {
        addStartNpc(30349);
        addTalkId(30355,30357,30360);
        addQuestItem(SENTRY_BLADE1_ID,
                OLD_BRONZE_SWORD_ID,
                JENNIES_LETTER_ID,
                SENTRY_BLADE2_ID,
                SENTRY_BLADE3_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {

        String htmltext = event;
        if ("1".equals(event)) {
            st.unset("id");
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            htmltext = "30349-03.htm";
            st.giveItems(JENNIES_LETTER_ID);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {

        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == 30349 && cond == 0) {
            if (cond < 15) {
                if (st.player.getRace() != Race.darkelf)
                    htmltext = "30349-00.htm";
                else if (st.player.getLevel() >= 3)
                    htmltext = "30349-02.htm";
                else {
                    htmltext = "30349-01.htm";
                    st.exitCurrentQuest();
                }
            } else {
                htmltext = "30349-01.htm";
                st.exitCurrentQuest();
            }
        } else if (npcId == 30349 && cond == 1 && st.getQuestItemsCount(JENNIES_LETTER_ID) > 0)
            htmltext = "30349-04.htm";
        else if (npcId == 30349 && cond == 2 && st.getQuestItemsCount(SENTRY_BLADE1_ID) == 1 && st.getQuestItemsCount(SENTRY_BLADE2_ID) == 1 && st.getQuestItemsCount(SENTRY_BLADE3_ID) == 1) {
            htmltext = "30349-05.htm";
            st.takeItems(SENTRY_BLADE1_ID, 1);
            st.setCond(3);
        } else if (npcId == 30349 && cond == 3 && st.getQuestItemsCount(SENTRY_BLADE1_ID) == 0 && (st.getQuestItemsCount(SENTRY_BLADE2_ID) == 1 || st.getQuestItemsCount(SENTRY_BLADE3_ID) == 1))
            htmltext = "30349-07.htm";
        else if (npcId == 30349 && cond == 4 && st.getQuestItemsCount(OLD_BRONZE_SWORD_ID) == 2) {
            htmltext = "30349-06.htm";
            st.takeItems(OLD_BRONZE_SWORD_ID, 2);
            st.unset("cond");
            st.playSound(SOUND_FINISH);
            st.giveItems(ADENA_ID, 820);
            st.finish();
        } else if (npcId == 30360 && cond == 1 && st.getQuestItemsCount(JENNIES_LETTER_ID) == 1) {
            htmltext = "30360-01.htm";
            st.takeItems(JENNIES_LETTER_ID, 1);
            st.giveItems(SENTRY_BLADE1_ID);
            st.giveItems(SENTRY_BLADE2_ID);
            st.giveItems(SENTRY_BLADE3_ID);
            st.setCond(2);
        } else if (npcId == 30360 && (cond == 2 || cond == 3) && st.getQuestItemsCount(SENTRY_BLADE1_ID) + st.getQuestItemsCount(SENTRY_BLADE2_ID) + st.getQuestItemsCount(SENTRY_BLADE3_ID) > 0)
            htmltext = "30360-02.htm";
        else if (npcId == 30355 && cond == 3 && st.getQuestItemsCount(SENTRY_BLADE2_ID) == 1 && st.getQuestItemsCount(SENTRY_BLADE1_ID) == 0) {
            htmltext = "30355-01.htm";
            st.takeItems(SENTRY_BLADE2_ID, 1);
            st.giveItems(OLD_BRONZE_SWORD_ID);
            if (st.getQuestItemsCount(SENTRY_BLADE3_ID) == 0)
                st.setCond(4);
        } else if (npcId == 30355 && (cond == 4 || cond == 3) && st.getQuestItemsCount(SENTRY_BLADE2_ID) == 0)
            htmltext = "30355-02.htm";
        else if (npcId == 30357 && cond == 3 && st.getQuestItemsCount(SENTRY_BLADE3_ID) == 1 && st.getQuestItemsCount(SENTRY_BLADE1_ID) == 0) {
            htmltext = "30357-01.htm";
            st.takeItems(SENTRY_BLADE3_ID, 1);
            st.giveItems(OLD_BRONZE_SWORD_ID);
            if (st.getQuestItemsCount(SENTRY_BLADE2_ID) == 0)
                st.setCond(4);
        } else if (npcId == 30357 && (cond == 4 || cond == 5) && st.getQuestItemsCount(SENTRY_BLADE3_ID) == 0)
            htmltext = "30357-02.htm";
        return htmltext;
    }
}