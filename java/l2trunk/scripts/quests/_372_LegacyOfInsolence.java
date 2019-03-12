package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class _372_LegacyOfInsolence extends Quest {
    // NPCs
    private static final int HOLLY = 30839;
    private static final int WALDERAL = 30844;
    private static final int DESMOND = 30855;
    private static final int PATRIN = 30929;
    private static final int CLAUDIA = 31001;
    // Mobs
    private static final int CORRUPT_SAGE = 20817;
    private static final int ERIN_EDIUNCE = 20821;
    private static final int HALLATE_INSP = 20825;
    private static final int PLATINUM_OVL = 20829;
    private static final int PLATINUM_PRE = 21069;
    private static final int MESSENGER_A1 = 21062;
    private static final int MESSENGER_A2 = 21063;
    // items
    private static final int Ancient_Red_Papyrus = 5966;
    private static final int Ancient_Blue_Papyrus = 5967;
    private static final int Ancient_Black_Papyrus = 5968;
    private static final int Ancient_White_Papyrus = 5969;

    private static final List<Integer> Revelation_of_the_Seals_Range = List.of(5972, 5978);
    private static final List<Integer> Ancient_Epic_Chapter_Range = List.of(5979, 5983);
    private static final List<Integer> Imperial_Genealogy_Range = List.of(5984, 5988);
    private static final List<Integer> Blueprint_Tower_of_Insolence_Range = List.of(5989, 6001);
    // Rewards
    private static final List<Integer> Reward_Dark_Crystal = List.of(5368, 5392, 5426);
    private static final List<Integer> Reward_Tallum = List.of(5370, 5394, 5428);
    private static final List<Integer> Reward_Nightmare = List.of(5380, 5404, 5430);
    private static final List<Integer> Reward_Majestic = List.of(5382, 5406, 5432);
    // Chances
    private static final int Three_Recipes_Reward_Chance = 1;
    private static final int Two_Recipes_Reward_Chance = 2;
    private static final int Adena4k_Reward_Chance = 2;

    private final Map<Integer, int[]> DROPLIST = new HashMap<>();

    public _372_LegacyOfInsolence() {
        super(true);
        addStartNpc(WALDERAL);

        addTalkId(HOLLY,DESMOND,PATRIN,CLAUDIA);

        addKillId(CORRUPT_SAGE,ERIN_EDIUNCE,HALLATE_INSP,PLATINUM_OVL,PLATINUM_PRE,MESSENGER_A1,MESSENGER_A2);

        DROPLIST.put(CORRUPT_SAGE, new int[]{
                Ancient_Red_Papyrus,
                35
        });
        DROPLIST.put(ERIN_EDIUNCE, new int[]{
                Ancient_Red_Papyrus,
                40
        });
        DROPLIST.put(HALLATE_INSP, new int[]{
                Ancient_Red_Papyrus,
                45
        });
        DROPLIST.put(PLATINUM_OVL, new int[]{
                Ancient_Blue_Papyrus,
                40
        });
        DROPLIST.put(PLATINUM_PRE, new int[]{
                Ancient_Black_Papyrus,
                25
        });
        DROPLIST.put(MESSENGER_A1, new int[]{
                Ancient_White_Papyrus,
                25
        });
        DROPLIST.put(MESSENGER_A2, new int[]{
                Ancient_White_Papyrus,
                25
        });
    }

    private static void giveRecipe(QuestState st, int recipe_id) {
        st.giveItems(recipe_id, 1);
    }

    private static boolean check_and_reward(QuestState st, List<Integer> items_range, List<Integer> reward) {
        for (int item_id = items_range.get(0); item_id <= items_range.get(1); item_id++)
            if (st.getQuestItemsCount(item_id) < 1)
                return false;

        for (int item_id = items_range.get(0); item_id <= items_range.get(1); item_id++)
            st.takeItems(item_id, 1);

        if (Rnd.chance(Three_Recipes_Reward_Chance)) {
            for (int reward_item_id : reward)
                giveRecipe(st, reward_item_id);
            st.playSound(SOUND_JACKPOT);
        } else if (Rnd.chance(Two_Recipes_Reward_Chance)) {
            int ignore_reward_id = Rnd.get(reward);
            for (int reward_item_id : reward)
                if (reward_item_id != ignore_reward_id)
                    giveRecipe(st, reward_item_id);
            st.playSound(SOUND_JACKPOT);
        } else if (Rnd.chance(Adena4k_Reward_Chance))
            st.giveAdena( 4000);
        else
            giveRecipe(st, Rnd.get(reward));

        return true;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int _state = st.getState();
        if (_state == CREATED) {
            if ("30844-6.htm".equalsIgnoreCase(event)) {
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
            } else if ("30844-9.htm".equalsIgnoreCase(event))
                st.setCond(2);
            else if ("30844-7.htm".equalsIgnoreCase(event)) {
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            }
        } else if (_state == STARTED)
            if ("30839-exchange".equalsIgnoreCase(event))
                htmltext = check_and_reward(st, Imperial_Genealogy_Range, Reward_Dark_Crystal) ? "30839-2.htm" : "30839-3.htm";
            else if ("30855-exchange".equalsIgnoreCase(event))
                htmltext = check_and_reward(st, Revelation_of_the_Seals_Range, Reward_Majestic) ? "30855-2.htm" : "30855-3.htm";
            else if ("30929-exchange".equalsIgnoreCase(event))
                htmltext = check_and_reward(st, Ancient_Epic_Chapter_Range, Reward_Tallum) ? "30839-2.htm" : "30839-3.htm";
            else if ("31001-exchange".equalsIgnoreCase(event))
                htmltext = check_and_reward(st, Revelation_of_the_Seals_Range, Reward_Nightmare) ? "30839-2.htm" : "30839-3.htm";
            else if ("30844-DarkCrystal".equalsIgnoreCase(event))
                htmltext = check_and_reward(st, Blueprint_Tower_of_Insolence_Range, Reward_Dark_Crystal) ? "30844-11.htm" : "30844-12.htm";
            else if ("30844-Tallum".equalsIgnoreCase(event))
                htmltext = check_and_reward(st, Blueprint_Tower_of_Insolence_Range, Reward_Tallum) ? "30844-11.htm" : "30844-12.htm";
            else if ("30844-Nightmare".equalsIgnoreCase(event))
                htmltext = check_and_reward(st, Blueprint_Tower_of_Insolence_Range, Reward_Nightmare) ? "30844-11.htm" : "30844-12.htm";
            else if ("30844-Majestic".equalsIgnoreCase(event))
                htmltext = check_and_reward(st, Blueprint_Tower_of_Insolence_Range, Reward_Majestic) ? "30844-11.htm" : "30844-12.htm";

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int _state = st.getState();
        int npcId = npc.getNpcId();

        if (_state == CREATED) {
            if (npcId != WALDERAL)
                return htmltext;
            if (st.player.getLevel() >= 59)
                htmltext = "30844-4.htm";
            else {
                htmltext = "30844-5.htm";
                st.exitCurrentQuest();
            }
        } else if (_state == STARTED)
            htmltext = npcId + "-1.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;

        int[] drop = DROPLIST.get(npc.getNpcId());
        if (drop == null)
            return;

        qs.rollAndGive(drop[0], 1, drop[1]);
    }
}