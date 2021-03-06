package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class _663_SeductiveWhispers extends Quest {
    // NPCs
    private final static int Wilbert = 30846;
    // Mobs
    private final static List<Integer> mobs = List.of(
            20674, 20678, 20954, 20955, 20956, 20957, 20958, 20959, 20960, 20961,
            20962, 20974, 20975, 20976, 20996, 20997, 20998, 20999, 21001, 21002,
            21006, 21007, 21008, 21009, 21010);    // Quest items
    private final static int Spirit_Bead = 8766;
    // items
    private final static int Enchant_Weapon_D = 955;
    private final static int Enchant_Weapon_C = 951;
    private final static int Enchant_Weapon_B = 947;
    private final static int Enchant_Armor_B = 948;
    private final static int Enchant_Weapon_A = 729;
    private final static int Enchant_Armor_A = 730;
    private final static List<Integer> Recipes_Weapon_B = List.of(
            4963, 4966, 4967, 4968, 5001, 5003, 5004, 5005, 5006, 5007);
    private final static List<Integer> Ingredients_Weapon_B = List.of(
            4101, 4107, 4108, 4109, 4115, 4117, 4118, 4119, 4120, 4121);
    // Chances
    private final static int drop_chance = 15;
    private final static int WinChance = 68;

    private final static List<LevelRewards> rewards = List.of(
            new LevelRewards("%n% adena").add(ADENA_ID, 40000),
            new LevelRewards("%n% adena").add(ADENA_ID, 80000),
            new LevelRewards("%n% adena, %n% D-grade Enchant Weapon Scroll(s)").add(ADENA_ID, 110000).add(Enchant_Weapon_D, 1),
            new LevelRewards("%n% adena, %n% C-grade Enchant Weapon Scroll(s)").add(ADENA_ID, 199000).add(Enchant_Weapon_C, 1),
            new LevelRewards("%n% adena, %n% recipe(s) for a B-grade Weapon").add(ADENA_ID, 388000).add(Recipes_Weapon_B, 1),
            new LevelRewards("%n% adena, %n% essential ingredient(s) for a B-grade Weapon").add(ADENA_ID, 675000).add(Ingredients_Weapon_B, 1),
            new LevelRewards("%n% adena, %n% B-grade Enchant Weapon Scroll(s), %n% B-grade Enchat Armor Scroll(s)").add(ADENA_ID, 1284000).add(Enchant_Weapon_B, 2).add(Enchant_Armor_B, 2),
            new LevelRewards("%n% adena, %n% A-grade Enchant Weapon Scroll(s), %n% A-grade Enchat Armor Scroll(s)").add(ADENA_ID, 2384000).add(Enchant_Weapon_A, 1).add(Enchant_Armor_A, 2)
    );
    private static String Dialog_WinLevel = "<font color=\"LEVEL\">Blacksmith Wilbert:</font><br><br>";
    private static String Dialog_WinGame = "<font color=\"LEVEL\">Blacksmith Wilbert:</font><br><br>";
    private static String Dialog_Rewards = "<font color=\"LEVEL\">Blacksmith Wilbert:</font><br><br>";

    static {
        Dialog_WinLevel += "You won round %occupation%!<br>";
        Dialog_WinLevel += "You can stop game now and take your prize:<br>";
        Dialog_WinLevel += "<font color=\"LEVEL\">%prize%</font><br><br>";
        Dialog_WinLevel += "<a action=\"bypass -h Quest _663_SeductiveWhispers 30846_12.htm\">Pull next card!</a><br>";
        Dialog_WinLevel += "<a action=\"bypass -h Quest _663_SeductiveWhispers 30846_13.htm\">\"No, enough for me, end game and take my prize.\"</a>";

        Dialog_WinGame += "Congratulations! You won round %n%!<br>";
        Dialog_WinGame += "Game ends now and you get your prize:<br>";
        Dialog_WinGame += "<font color=\"LEVEL\">%prize%</font><br><br>";
        Dialog_WinGame += "<a action=\"bypass -h Quest _663_SeductiveWhispers 30846_03.htm\">Return</a>";

        Dialog_Rewards += "If you win the game, the master running it owes you the appropriate amount. The higher the round, the bigger the payout. That's why the game anly allows you to win up to 8 round in a row. If -- and that's a big if -- you manage to win 8 straight times, the game will end.<br>";
        Dialog_Rewards += "Keep in mind that <font color=\"LEVEL\">if you lose any of the rounds, you get nothing</font>. That's fair warning, my friend. Here's how the prize system works:<br>";
        for (int i = 0; i < rewards.size(); i++) {
            Dialog_Rewards += "<font color=\"LEVEL\">" + (i + 1) + " winning round";
            if (i > 0)
                Dialog_Rewards += "s";
            Dialog_Rewards += ": </font>" + rewards.get(i).toString() + "<br>";
        }
        Dialog_Rewards += "<br>My advice is to identify what you'd like to win and then to play for that prize. Any questions?<br>";
        Dialog_Rewards += "<a action=\"bypass -h Quest _663_SeductiveWhispers 30846_03.htm\">Return</a>";
    }

    public _663_SeductiveWhispers() {
        addStartNpc(Wilbert);
        addKillId(mobs);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        long spiritBeadCount = st.getQuestItemsCount(Spirit_Bead);
        if ("30846_04.htm".equalsIgnoreCase(event) && state == CREATED) {
            st.setCond(1);
            st.unset("round");
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30846_07.htm".equalsIgnoreCase(event) && state == STARTED)
            return Dialog_Rewards;
        else if ("30846_09.htm".equalsIgnoreCase(event) && state == STARTED) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        } else if ("30846_08.htm".equalsIgnoreCase(event) && state == STARTED) {
            if (spiritBeadCount < 1)
                return "30846_11.htm";
            st.takeItems(Spirit_Bead, 1);
            if (!Rnd.chance(WinChance))
                return "30846_08a.htm";
        } else if ("30846_10.htm".equalsIgnoreCase(event) && state == STARTED) {
            st.unset("round");
            if (spiritBeadCount < 50)
                return "30846_11.htm";
        } else if ("30846_12.htm".equalsIgnoreCase(event) && state == STARTED) {
            int round = st.getInt("round");
            if (round == 0) {
                if (spiritBeadCount < 50)
                    return "30846_11.htm";
                st.takeItems(Spirit_Bead, 50);
            }
            if (!Rnd.chance(WinChance)) {
                st.unset("round");
                return event;
            }
            LevelRewards current_reward = rewards.get(round);
            int next_round = round + 1;
            boolean lastLevel = next_round == rewards.size();
            String dialog = lastLevel ? Dialog_WinGame : Dialog_WinLevel;
            dialog = dialog.replaceFirst("%level%", String.valueOf(next_round));
            dialog = dialog.replaceFirst("%prize%", current_reward.toString());

            if (lastLevel) {
                next_round = 0;
                current_reward.giveRewards(st);
                st.playSound(SOUND_JACKPOT);
            }

            st.set("round", next_round);
            return dialog;
        } else if ("30846_13.htm".equalsIgnoreCase(event) && state == STARTED) {
            int round = st.getInt("round") - 1;
            st.unset("round");
            if (round < 0 || round >= rewards.size())
                return "30846_13a.htm";
            rewards.get(round).giveRewards(st);
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (npc.getNpcId() != Wilbert)
            return "noquest";
        int _state = st.getState();
        if (_state == CREATED) {
            if (st.player.getLevel() < 50) {
                st.exitCurrentQuest();
                return "30846_00.htm";
            }
            st.setCond(0);
            return "30846_01.htm";
        }
        return "30846_03.htm";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() == STARTED) {
            double rand = drop_chance * Experience.penaltyModifier(qs.calculateLevelDiffForDrop(npc.getLevel(), qs.player.getLevel()), 9) * npc.getTemplate().rateHp;
            qs.rollAndGive(Spirit_Bead, 1, rand);
        }
    }

    private static class LevelRewards {
        private final Map<List<Integer>, Integer> rewards = new HashMap<>();
        private String txt;

        LevelRewards(String txt) {
            this.txt = txt;
        }

        LevelRewards add(int item_id, int count) {
            return add(List.of(item_id), count);
        }

        LevelRewards add(List<Integer> items_id, int count) {
            int cnt = (int) (count * Config.RATE_QUESTS_REWARD);
            txt = txt.replaceFirst("%n%", String.valueOf(cnt));
            rewards.put(items_id, cnt);
            return this;
        }

        void giveRewards(QuestState qs) {
            for (List<Integer> item_ids : rewards.keySet())
                qs.giveItems(Rnd.get(item_ids), rewards.get(item_ids));
        }

        @Override
        public String toString() {
            return txt;
        }
    }
}