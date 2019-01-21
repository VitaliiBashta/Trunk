package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _354_ConquestofAlligatorIsland extends Quest {
    private static final int PIRATES_TREASURE_MAP = 5915;
    private static final int CHANCE = 35;
    private static final int CHANCE2 = 10;
    //mobs
    private static final int CROKIAN_LAD = 20804;
    private static final int DAILAON_LAD = 20805;
    private static final int CROKIAN_LAD_WARRIOR = 20806;
    private static final int FARHITE_LAD = 20807;
    private static final int NOS_LAD = 20808;
    private static final int SWAMP_TRIBE = 20991;
    private static final List<Integer> MOBLIST = List.of(
            CROKIAN_LAD, DAILAON_LAD, CROKIAN_LAD_WARRIOR, FARHITE_LAD, NOS_LAD, SWAMP_TRIBE);    //RANDOM_REWARDS [ITEM_ID, QTY]
    //items
    private final int ALLIGATOR_TOOTH = 5863;
    private final int TORN_MAP_FRAGMENT = 5864;
    private final List<Integer> RANDOM_REWARDS_IDS = List.of(
            736,             //SoE
            1061,            //Healing Potion
            734,             //Haste Potion
            735,             //Alacrity Potion
            1878,            //Braided Hemp
            1875,            //Stone of Purity
            1879,            //Cokes
            1880,            //Steel
            956,             //Enchant Armor D
            955);           //Enchant Weapon D
    private final List<Integer> RANDOM_REWARDS_COUNT = List.of(
            15,            //SoE
            20,            //Healing Potion
            10,            //Haste Potion
            5,            //Alacrity Potion
            25,            //Braided Hemp
            10,            //Stone of Purity
            10,            //Cokes
            10,            //Steel
            1,            //Enchant Armor D
            1);           //Enchant Weapon D

    public _354_ConquestofAlligatorIsland() {
        super(false);

        addStartNpc(30895);

        addKillId(MOBLIST);

        addQuestItem(ALLIGATOR_TOOTH, TORN_MAP_FRAGMENT);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        long amount = st.getQuestItemsCount(ALLIGATOR_TOOTH);
        if (event.equalsIgnoreCase("30895-00a.htm"))
            st.exitCurrentQuest(true);
        else if (event.equalsIgnoreCase("1")) {
            st.setState(STARTED);
            st.setCond(1);
            htmltext = "30895-02.htm";
            st.playSound(SOUND_ACCEPT);
        } else if ("30895-06.htm".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(TORN_MAP_FRAGMENT) > 9)
                htmltext = "30895-07.htm";
        } else if ("30895-05.htm".equalsIgnoreCase(event)) {
            if (amount > 0)
                if (amount > 99) {
                    st.giveItems(ADENA_ID, amount * 300);
                    st.takeItems(ALLIGATOR_TOOTH);
                    st.playSound(SOUND_ITEMGET);
                    int random = Rnd.get(RANDOM_REWARDS_IDS.size());
                    st.giveItems(RANDOM_REWARDS_IDS.get(random), RANDOM_REWARDS_COUNT.get(random));
                    htmltext = "30895-05b.htm";
                } else {
                    st.giveItems(ADENA_ID, amount * 100);
                    st.takeItems(ALLIGATOR_TOOTH);
                    st.playSound(SOUND_ITEMGET);
                    htmltext = "30895-05a.htm";
                }
        } else if ("30895-08.htm".equalsIgnoreCase(event)) {
            st.giveItems(PIRATES_TREASURE_MAP, 1);
            st.takeItems(TORN_MAP_FRAGMENT, -1);
            st.playSound(SOUND_ITEMGET);
        } else if ("30895-09.htm".equalsIgnoreCase(event)) {
            st.exitCurrentQuest(true);
            st.playSound(SOUND_FINISH);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int cond = st.getCond();
        if (cond < 1) {
            if (st.getPlayer().getLevel() < 38)
                htmltext = "30895-00.htm";
            else
                htmltext = "30895-01.htm";
        } else
            htmltext = "30895-03.htm";

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (Rnd.chance(CHANCE)) {
            st.giveItems(ALLIGATOR_TOOTH);
            st.playSound(SOUND_ITEMGET);
        }
        if (Rnd.chance(CHANCE2) && st.getQuestItemsCount(TORN_MAP_FRAGMENT) < 10) {
            st.giveItems(TORN_MAP_FRAGMENT, 1);
            if (st.getQuestItemsCount(TORN_MAP_FRAGMENT) < 10)
                st.playSound(SOUND_ITEMGET);
            else
                st.playSound(SOUND_MIDDLE);
        }

        return null;
    }
}