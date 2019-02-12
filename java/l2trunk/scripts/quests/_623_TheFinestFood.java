package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _623_TheFinestFood extends Quest {
    private static final int HOT_SPRINGS_BUFFALO = 21315;
    private static final int HOT_SPRINGS_FLAVA = 21316;
    private static final int HOT_SPRINGS_ANTELOPE = 21318;
    private static final int LEAF_OF_FLAVA = 7199;
    private static final int BUFFALO_MEAT = 7200;
    private static final int ANTELOPE_HORN = 7201;
    private final int JEREMY = 31521;

    public _623_TheFinestFood() {
        super(true);

        addStartNpc(JEREMY);

        addTalkId(JEREMY);

        addKillId(HOT_SPRINGS_BUFFALO);
        addKillId(HOT_SPRINGS_FLAVA);
        addKillId(HOT_SPRINGS_ANTELOPE);

        addQuestItem(BUFFALO_MEAT);
        addQuestItem(LEAF_OF_FLAVA);
        addQuestItem(ANTELOPE_HORN);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equalsIgnoreCase(event)) {
            htmltext = "jeremy_q0623_0104.htm";
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if ("623_3".equalsIgnoreCase(event)) {
            htmltext = "jeremy_q0623_0201.htm";
            st.takeItems(LEAF_OF_FLAVA);
            st.takeItems(BUFFALO_MEAT);
            st.takeItems(ANTELOPE_HORN);
            st.giveItems(ADENA_ID, 73000);
            st.addExpAndSp(230000, 18250);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        if (id == CREATED)
            st.setCond(0);
        // На случай любых ошибок, если предметы есть - квест все равно пройдется.
        if (summ(st) >= 300)
            st.setCond(2);
        int cond = st.getCond();
        if (npcId == JEREMY)
            if (cond == 0) {
                if (st.player.getLevel() >= 71)
                    htmltext = "jeremy_q0623_0101.htm";
                else {
                    htmltext = "jeremy_q0623_0103.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1 && summ(st) < 300)
                htmltext = "jeremy_q0623_0106.htm";
            else if (cond == 2 && summ(st) >= 300)
                htmltext = "jeremy_q0623_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        int npcId = npc.getNpcId();
        if (cond == 1) // Like off C4 PTS AI (убрали  && Rnd.chance(50))
            if (npcId == HOT_SPRINGS_BUFFALO) {
                giveItem(st, BUFFALO_MEAT);
            } else if (npcId == HOT_SPRINGS_FLAVA) {
                giveItem(st, LEAF_OF_FLAVA);
            } else if (npcId == HOT_SPRINGS_ANTELOPE) {
                giveItem(st, ANTELOPE_HORN);
            }
    }

    private void giveItem(QuestState st, int itemId) {
        if (st.getQuestItemsCount(itemId) < 100) {
            st.giveItems(itemId);
            if (st.getQuestItemsCount(itemId) == 100) {
                if (summ(st) >= 300)
                    st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }

    private long summ(QuestState st) {
        return st.getQuestItemsCount(LEAF_OF_FLAVA) + st.getQuestItemsCount(BUFFALO_MEAT) + st.getQuestItemsCount(ANTELOPE_HORN);
    }
}