package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _618_IntoTheFlame extends Quest {
    //NPCs
    private static final int KLEIN = 31540;
    private static final int HILDA = 31271;

    //QUEST ITEMS
    private static final int VACUALITE_ORE = 7265;
    private static final int VACUALITE = 7266;
    private static final int FLOATING_STONE = 7267;

    //CHANCE
    private static final int CHANCE_FOR_QUEST_ITEMS = 50;

    public _618_IntoTheFlame() {
        addStartNpc(KLEIN);
        addTalkId(HILDA);
        addKillId(21274, 21275, 21276, 21278,21282, 21283, 21284, 21286,21290, 21291, 21292, 21294);
        addQuestItem(VACUALITE_ORE,VACUALITE,FLOATING_STONE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int cond = st.getCond();
        if ("watcher_valakas_klein_q0618_0104.htm".equalsIgnoreCase(event) && cond == 0) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("watcher_valakas_klein_q0618_0401.htm".equalsIgnoreCase(event))
            if (st.haveQuestItem(VACUALITE)  && cond == 4) {
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
                st.giveItems(FLOATING_STONE);
            } else
                htmltext = "watcher_valakas_klein_q0618_0104.htm";
        else if ("blacksmith_hilda_q0618_0201.htm".equalsIgnoreCase(event) && cond == 1) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("blacksmith_hilda_q0618_0301.htm".equalsIgnoreCase(event))
            if (cond == 3 && st.getQuestItemsCount(VACUALITE_ORE) == 50) {
                st.takeItems(VACUALITE_ORE);
                st.giveItems(VACUALITE);
                st.setCond(4);
                st.playSound(SOUND_MIDDLE);
            } else
                htmltext = "blacksmith_hilda_q0618_0203.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == KLEIN) {
            if (cond == 0) {
                if (st.player.getLevel() < 60) {
                    htmltext = "watcher_valakas_klein_q0618_0103.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "watcher_valakas_klein_q0618_0101.htm";
            } else if (cond == 4 && st.getQuestItemsCount(VACUALITE) > 0)
                htmltext = "watcher_valakas_klein_q0618_0301.htm";
            else
                htmltext = "watcher_valakas_klein_q0618_0104.htm";
        } else if (npcId == HILDA)
            if (cond == 1)
                htmltext = "blacksmith_hilda_q0618_0101.htm";
            else if (cond == 3 && st.getQuestItemsCount(VACUALITE_ORE) >= 50)
                htmltext = "blacksmith_hilda_q0618_0202.htm";
            else if (cond == 4)
                htmltext = "blacksmith_hilda_q0618_0303.htm";
            else
                htmltext = "blacksmith_hilda_q0618_0203.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        long count = st.getQuestItemsCount(VACUALITE_ORE);
        if (Rnd.chance(CHANCE_FOR_QUEST_ITEMS) && count < 50) {
            st.giveItems(VACUALITE_ORE);
            if (count == 49) {
                st.setCond(3);
                st.playSound(SOUND_MIDDLE);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }
}