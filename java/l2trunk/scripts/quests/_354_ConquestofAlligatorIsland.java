package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;
import java.util.Map;

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
            CROKIAN_LAD, DAILAON_LAD, CROKIAN_LAD_WARRIOR, FARHITE_LAD, NOS_LAD, SWAMP_TRIBE);
    //items
    private final int ALLIGATOR_TOOTH = 5863;
    private final int TORN_MAP_FRAGMENT = 5864;
    private final Map<Integer, Integer> RANDOM_REWARDS_IDS = Map.of(
            736, 15,          //SoE
            1061, 20,       //Healing Potion
            734, 10, //Haste Potion
            735, 5,           //Alacrity Potion
            1878, 25,         //Braided Hemp
            1875, 10,          //Stone of Purity
            1879, 10,         //Cokes
            1880, 10,    //Steel
            956, 1,         //Enchant Armor D
            955, 1);           //Enchant Weapon D


    public _354_ConquestofAlligatorIsland() {
        addStartNpc(30895);

        addKillId(MOBLIST);

        addQuestItem(ALLIGATOR_TOOTH, TORN_MAP_FRAGMENT);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        long amount = st.getQuestItemsCount(ALLIGATOR_TOOTH);
        if ("30895-00a.htm".equalsIgnoreCase(event))
            st.exitCurrentQuest();
        else if ("1".equals(event)) {
            st.start();
            st.setCond(1);
            htmltext = "30895-02.htm";
            st.playSound(SOUND_ACCEPT);
        } else if ("30895-06.htm".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(TORN_MAP_FRAGMENT, 10))
                htmltext = "30895-07.htm";
        } else if ("30895-05.htm".equalsIgnoreCase(event)) {
            if (amount > 0)
                if (amount > 99) {
                    st.giveAdena( amount * 300);
                    st.takeItems(ALLIGATOR_TOOTH);
                    st.playSound(SOUND_ITEMGET);
                    int random = Rnd.get(RANDOM_REWARDS_IDS.keySet());
                    st.giveItems(random, RANDOM_REWARDS_IDS.get(random));
                    htmltext = "30895-05b.htm";
                } else {
                    st.giveAdena( amount * 100);
                    st.takeItems(ALLIGATOR_TOOTH);
                    st.playSound(SOUND_ITEMGET);
                    htmltext = "30895-05a.htm";
                }
        } else if ("30895-08.htm".equalsIgnoreCase(event)) {
            st.giveItems(PIRATES_TREASURE_MAP);
            st.takeItems(TORN_MAP_FRAGMENT);
            st.playSound(SOUND_ITEMGET);
        } else if ("30895-09.htm".equalsIgnoreCase(event)) {
            st.exitCurrentQuest();
            st.playSound(SOUND_FINISH);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int cond = st.getCond();
        if (cond < 1) {
            if (st.player.getLevel() < 38)
                htmltext = "30895-00.htm";
            else
                htmltext = "30895-01.htm";
        } else
            htmltext = "30895-03.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (Rnd.chance(CHANCE)) {
            st.giveItems(ALLIGATOR_TOOTH);
            st.playSound(SOUND_ITEMGET);
        }
        if (Rnd.chance(CHANCE2) ) {
            st.giveItemIfNotHave(TORN_MAP_FRAGMENT, 10);
            if (st.getQuestItemsCount(TORN_MAP_FRAGMENT) >= 10)
                st.playSound(SOUND_MIDDLE);
        }
    }
}