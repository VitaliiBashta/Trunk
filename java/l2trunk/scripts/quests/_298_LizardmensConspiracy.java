package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.Map;

public final class _298_LizardmensConspiracy extends Quest {
    //	npc
    private final int PRAGA = 30333;
    private final int ROHMER = 30344;
    //mobs
    private final int MAILLE_LIZARDMAN_WARRIOR = 20922;
    private final int MAILLE_LIZARDMAN_SHAMAN = 20923;
    private final int MAILLE_LIZARDMAN_MATRIARCH = 20924;
    //public final int GIANT_ARANEID = 20925; //в клиенте о нем ни слова
    private final int POISON_ARANEID = 20926;
    private final int KING_OF_THE_ARANEID = 20927;
    //items
    private final int REPORT = 7182;
    private final int SHINING_GEM = 7183;
    private final int SHINING_RED_GEM = 7184;
    //MobsTable {MOB_ID, ITEM_ID}
    private final Map<Integer, Integer> MobsTable = Map.of(
            MAILLE_LIZARDMAN_WARRIOR, SHINING_GEM,
            MAILLE_LIZARDMAN_SHAMAN, SHINING_GEM,
            MAILLE_LIZARDMAN_MATRIARCH, SHINING_GEM,
            POISON_ARANEID, SHINING_RED_GEM,
            KING_OF_THE_ARANEID, SHINING_RED_GEM);

    public _298_LizardmensConspiracy() {
        addStartNpc(PRAGA);

        addTalkId(ROHMER);

        addKillId(MobsTable.keySet());

        addQuestItem(REPORT, SHINING_GEM, SHINING_RED_GEM);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("guard_praga_q0298_0104.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.giveItems(REPORT);
            st.playSound(SOUND_ACCEPT);
        } else if ("magister_rohmer_q0298_0201.htm".equalsIgnoreCase(event)) {
            st.takeItems(REPORT);
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("magister_rohmer_q0298_0301.htm".equalsIgnoreCase(event) && st.getQuestItemsCount(SHINING_GEM) + st.getQuestItemsCount(SHINING_RED_GEM) > 99) {
            st.takeAllItems(SHINING_GEM,SHINING_RED_GEM);
            st.addExpAndSp(0, 42000);
            st.exitCurrentQuest();
            st.playSound(SOUND_FINISH);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == PRAGA) {
            if (cond < 1)
                if (st.player.getLevel() < 25) {
                    htmltext = "guard_praga_q0298_0102.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "guard_praga_q0298_0101.htm";
            if (cond == 1)
                htmltext = "guard_praga_q0298_0105.htm";
        } else if (npcId == ROHMER)
            if (cond < 1)
                htmltext = "magister_rohmer_q0298_0202.htm";
            else if (cond == 1)
                htmltext = "magister_rohmer_q0298_0101.htm";
            else if (cond == 2 | st.getQuestItemsCount(SHINING_GEM) + st.getQuestItemsCount(SHINING_RED_GEM) < 100) {
                htmltext = "magister_rohmer_q0298_0204.htm";
                st.setCond(2);
            } else if (cond == 3 && st.getQuestItemsCount(SHINING_GEM) + st.getQuestItemsCount(SHINING_RED_GEM) > 99)
                htmltext = "magister_rohmer_q0298_0203.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int rand = Rnd.get(10);
        if (st.getCond() == 2)
                if (MobsTable.containsKey(npcId)) {
                    Integer item = MobsTable.get(npcId);
                    if (rand < 6 && st.getQuestItemsCount(item) < 50) {
                        if (rand < 2 && item == SHINING_GEM)
                            st.giveItems(item, 2);
                        else
                            st.giveItems(item);
                        if (st.getQuestItemsCount(SHINING_GEM) + st.getQuestItemsCount(SHINING_RED_GEM) > 99) {
                            st.setCond(3);
                            st.playSound(SOUND_MIDDLE);
                        } else
                            st.playSound(SOUND_ITEMGET);
                    }
                }
    }
}