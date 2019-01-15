package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _688_DefeatTheElrokianRaiders extends Quest {
    // NPC's
    private static final int dindin = 32105;

    // MOB's
    private static final int elcroki = 22214;

    // drop chance in %
    private static final int DROP_CHANCE = 50;

    private static final int q_necklace_of_storming_party = 8785;

    public _688_DefeatTheElrokianRaiders() {
        super(false);
        addStartNpc(dindin);
        addTalkId(dindin);
        addKillId(elcroki);
        addQuestItem(q_necklace_of_storming_party);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        long count = st.getQuestItemsCount(q_necklace_of_storming_party);

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
            htmltext = "dindin_q0688_04.htm";
        } else if (event.equalsIgnoreCase("reply_5")) {
            if (count >= 10) {
                st.takeItems(q_necklace_of_storming_party, -1);
                st.giveItems(ADENA_ID, count * 3000);
                htmltext = "dindin_q0688_07.htm";
            }
        } else if (event.equalsIgnoreCase("reply_9")) {
            if (count < 100)
                htmltext = "dindin_q0688_11.htm";
            else if (Rnd.get(1000) < 500) {
                st.takeItems(q_necklace_of_storming_party, 100);
                st.giveItems(ADENA_ID, 450000);
                htmltext = "dindin_q0688_12.htm";
            } else {
                st.takeItems(q_necklace_of_storming_party, 100);
                st.giveItems(ADENA_ID, 150000);
                htmltext = "dindin_q0688_13.htm";
            }
        } else if (event.equalsIgnoreCase("reply_8"))
            htmltext = "dindin_q0688_10.htm";
        else if (event.equalsIgnoreCase("reply_7")) {
            if (count >= 1) {
                st.takeItems(q_necklace_of_storming_party, -1);
                st.giveItems(ADENA_ID, count * 3000);
            }
            htmltext = "dindin_q0688_09.htm";
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        long count = st.getQuestItemsCount(q_necklace_of_storming_party);

        if (cond == 0) {
            if (st.getPlayer().getLevel() >= 75)
                htmltext = "dindin_q0688_01.htm";
            else {
                htmltext = "dindin_q0688_02.htm";
                st.exitCurrentQuest(true);
            }
        } else if (cond == 1)
            if (count >= 1)
                htmltext = "dindin_q0688_05.htm";
            else
                htmltext = "dindin_q0688_06.htm";
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        long count = st.getQuestItemsCount(q_necklace_of_storming_party);

        if (st.getCond() == 1 && count < 100 && Rnd.chance(DROP_CHANCE)) {
            long numItems = (int) Config.RATE_QUESTS_REWARD;
            if (count + numItems > 100)
                numItems = 100 - count;
            if (count + numItems >= 100)
                st.playSound(SOUND_MIDDLE);
            else
                st.playSound(SOUND_ITEMGET);
            st.giveItems(q_necklace_of_storming_party, numItems);
        }
        return null;
    }
}