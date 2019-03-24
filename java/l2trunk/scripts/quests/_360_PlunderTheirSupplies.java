package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _360_PlunderTheirSupplies extends Quest {
    //NPC
    private static final int COLEMAN = 30873;

    //MOBS
    private static final int TAIK_SEEKER = 20666;
    private static final int TAIK_LEADER = 20669;

    //QUEST ITEMS
    private static final int SUPPLY_ITEM = 5872;
    private static final int SUSPICIOUS_DOCUMENT = 5871;
    private static final int RECIPE_OF_SUPPLY = 5870;

    //DROP CHANCES
    private static final int ITEM_DROP_SEEKER = 50;
    private static final int ITEM_DROP_LEADER = 65;
    private static final int DOCUMENT_DROP = 5;

    public _360_PlunderTheirSupplies() {
        addStartNpc(COLEMAN);

        addKillId(TAIK_SEEKER,TAIK_LEADER);

        addQuestItem(SUPPLY_ITEM,SUSPICIOUS_DOCUMENT,RECIPE_OF_SUPPLY);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("guard_coleman_q0360_04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("guard_coleman_q0360_10.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int id = st.getState();
        long docs = st.getQuestItemsCount(RECIPE_OF_SUPPLY);
        long supplies = st.getQuestItemsCount(SUPPLY_ITEM);
        if (id != STARTED) {
            if (st.player.getLevel() >= 52)
                htmltext = "guard_coleman_q0360_02.htm";
            else
                htmltext = "guard_coleman_q0360_01.htm";
        } else if (docs > 0 || supplies > 0) {
            long reward = 6000 + supplies * 100 + docs * 6000;
            st.takeItems(SUPPLY_ITEM);
            st.takeItems(RECIPE_OF_SUPPLY);
            st.giveAdena(reward);
            htmltext = "guard_coleman_q0360_08.htm";
        } else
            htmltext = "guard_coleman_q0360_05.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == TAIK_SEEKER && Rnd.chance(ITEM_DROP_SEEKER) || npcId == TAIK_LEADER && Rnd.chance(ITEM_DROP_LEADER)) {
            st.giveItems(SUPPLY_ITEM);
            st.playSound(SOUND_ITEMGET);
        }
        if (Rnd.chance(DOCUMENT_DROP)) {
            if (st.getQuestItemsCount(SUSPICIOUS_DOCUMENT) < 4)
                st.giveItems(SUSPICIOUS_DOCUMENT);
            else {
                st.takeItems(SUSPICIOUS_DOCUMENT);
                st.giveItems(RECIPE_OF_SUPPLY);
            }
            st.playSound(SOUND_ITEMGET);
        }
    }
}