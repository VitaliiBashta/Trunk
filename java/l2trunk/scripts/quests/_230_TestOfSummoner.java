package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public final class _230_TestOfSummoner extends Quest {
    private static final int MARK_OF_SUMMONER_ID = 3336;
    private static final int LETOLIZARDMAN_AMULET_ID = 3337;
    private static final int SAC_OF_REDSPORES_ID = 3338;
    private static final int KARULBUGBEAR_TOTEM_ID = 3339;
    private static final int SHARDS_OF_MANASHEN_ID = 3340;
    private static final int BREKAORC_TOTEM_ID = 3341;
    private static final int CRIMSON_BLOODSTONE_ID = 3342;
    private static final int TALONS_OF_TYRANT_ID = 3343;
    private static final int WINGS_OF_DRONEANT_ID = 3344;
    private static final int TUSK_OF_WINDSUS_ID = 3345;
    private static final int FANGS_OF_WYRM_ID = 3346;
    private static final int LARS_LIST1_ID = 3347;
    private static final int LARS_LIST2_ID = 3348;
    private static final int LARS_LIST3_ID = 3349;
    private static final int LARS_LIST4_ID = 3350;
    private static final int LARS_LIST5_ID = 3351;
    private static final int GALATEAS_LETTER_ID = 3352;
    private static final int BEGINNERS_ARCANA_ID = 3353;
    private static final int ALMORS_ARCANA_ID = 3354;
    private static final int CAMONIELL_ARCANA_ID = 3355;
    private static final int BELTHUS_ARCANA_ID = 3356;
    private static final int BASILLIA_ARCANA_ID = 3357;
    private static final int CELESTIEL_ARCANA_ID = 3358;
    private static final int BRYNTHEA_ARCANA_ID = 3359;
    private static final int CRYSTAL_OF_PROGRESS1_ID = 3360;
    private static final int CRYSTAL_OF_INPROGRESS1_ID = 3361;
    private static final int CRYSTAL_OF_FOUL1_ID = 3362;
    private static final int CRYSTAL_OF_DEFEAT1_ID = 3363;
    private static final int CRYSTAL_OF_VICTORY1_ID = 3364;
    private static final int CRYSTAL_OF_PROGRESS2_ID = 3365;
    private static final int CRYSTAL_OF_INPROGRESS2_ID = 3366;
    private static final int CRYSTAL_OF_FOUL2_ID = 3367;
    private static final int CRYSTAL_OF_DEFEAT2_ID = 3368;
    private static final int CRYSTAL_OF_VICTORY2_ID = 3369;
    private static final int CRYSTAL_OF_PROGRESS3_ID = 3370;
    private static final int CRYSTAL_OF_INPROGRESS3_ID = 3371;
    private static final int CRYSTAL_OF_FOUL3_ID = 3372;
    private static final int CRYSTAL_OF_DEFEAT3_ID = 3373;
    private static final int CRYSTAL_OF_VICTORY3_ID = 3374;
    private static final int CRYSTAL_OF_PROGRESS4_ID = 3375;
    private static final int CRYSTAL_OF_INPROGRESS4_ID = 3376;
    private static final int CRYSTAL_OF_FOUL4_ID = 3377;
    private static final int CRYSTAL_OF_DEFEAT4_ID = 3378;
    private static final int CRYSTAL_OF_VICTORY4_ID = 3379;
    private static final int CRYSTAL_OF_PROGRESS5_ID = 3380;
    private static final int CRYSTAL_OF_INPROGRESS5_ID = 3381;
    private static final int CRYSTAL_OF_FOUL5_ID = 3382;
    private static final int CRYSTAL_OF_DEFEAT5_ID = 3383;
    private static final int CRYSTAL_OF_VICTORY5_ID = 3384;
    private static final int CRYSTAL_OF_PROGRESS6_ID = 3385;
    private static final int CRYSTAL_OF_INPROGRESS6_ID = 3386;
    private static final int CRYSTAL_OF_FOUL6_ID = 3387;
    private static final int CRYSTAL_OF_DEFEAT6_ID = 3388;
    private static final int CRYSTAL_OF_VICTORY6_ID = 3389;
    private static final List<String> VARS = List.of(
            "Almors", "Camoniell", "Belthus", "Basilla", "Celestiel", "Brynthea");
    private static final List<Integer> NPCS = List.of(
            30063, 30634, 30635, 30636, 30637, 30638, 30639, 30640);
    private static final int Lara = NPCS.get(0);
    private static final int Galatea = NPCS.get(1);
    private static final int[][] SUMMONERS = {
            {
                    30635,
                    ALMORS_ARCANA_ID,
                    CRYSTAL_OF_VICTORY1_ID
            },
            // Almors
            {
                    30636,
                    CAMONIELL_ARCANA_ID,
                    CRYSTAL_OF_VICTORY2_ID
            },
            // Camoniell
            {
                    30637,
                    BELTHUS_ARCANA_ID,
                    CRYSTAL_OF_VICTORY3_ID
            },
            // Belthus
            {
                    30638,
                    BASILLIA_ARCANA_ID,
                    CRYSTAL_OF_VICTORY4_ID
            },
            // Basilla
            {
                    30639,
                    CELESTIEL_ARCANA_ID,
                    CRYSTAL_OF_VICTORY5_ID
            },
            // Celestiel
            {
                    30640,
                    BRYNTHEA_ARCANA_ID,
                    CRYSTAL_OF_VICTORY6_ID
            }
            // Brynthea
    };
    private static final Map<Integer, String> NAMES = Map.of(
            27102, "Almors",
            27103, "Camoniell",
            27104, "Belthus",
            27105, "Basilla",
            27106, "Celestiel",
            27107, "Brynthea");
    private static final Map<Integer, Integer[]> DROPLIST_LARA = new HashMap<>();
    private static final List<String> STATS = List.of(
            "cond",
            "step",
            "Lara_Part",
            "Arcanas",
            "Belthus",
            "Brynthea",
            "Celestiel",
            "Camoniell",
            "Basilla",
            "Almors");
    private static final int[][] LISTS = {
            {},
            // zero element filler
            {
                    LARS_LIST1_ID,
                    SAC_OF_REDSPORES_ID,
                    LETOLIZARDMAN_AMULET_ID
            },
            // List 1
            {
                    LARS_LIST2_ID,
                    KARULBUGBEAR_TOTEM_ID,
                    SHARDS_OF_MANASHEN_ID
            },
            // List 2
            {
                    LARS_LIST3_ID,
                    CRIMSON_BLOODSTONE_ID,
                    BREKAORC_TOTEM_ID
            },
            // List 3
            {
                    LARS_LIST4_ID,
                    TUSK_OF_WINDSUS_ID,
                    TALONS_OF_TYRANT_ID
            },
            // List 4
            {
                    LARS_LIST5_ID,
                    WINGS_OF_DRONEANT_ID,
                    FANGS_OF_WYRM_ID
            }
            // List 5
    };
    private static final Map<Integer, List<Integer>> DROPLIST_SUMMON = new HashMap<>();

    static {
        DROPLIST_LARA.put(20555, new Integer[]{
                1,
                80,
                SAC_OF_REDSPORES_ID
        });
        DROPLIST_LARA.put(20557, new Integer[]{
                1,
                25,
                LETOLIZARDMAN_AMULET_ID
        });
        DROPLIST_LARA.put(20558, new Integer[]{
                1,
                25,
                LETOLIZARDMAN_AMULET_ID
        });
        DROPLIST_LARA.put(20559, new Integer[]{
                1,
                25,
                LETOLIZARDMAN_AMULET_ID
        });
        DROPLIST_LARA.put(20580, new Integer[]{
                1,
                50,
                LETOLIZARDMAN_AMULET_ID
        });
        DROPLIST_LARA.put(20581, new Integer[]{
                1,
                75,
                LETOLIZARDMAN_AMULET_ID
        });
        DROPLIST_LARA.put(20582, new Integer[]{
                1,
                75,
                LETOLIZARDMAN_AMULET_ID
        });
        DROPLIST_LARA.put(20600, new Integer[]{
                2,
                80,
                KARULBUGBEAR_TOTEM_ID
        });
        DROPLIST_LARA.put(20563, new Integer[]{
                2,
                80,
                SHARDS_OF_MANASHEN_ID
        });
        DROPLIST_LARA.put(20552, new Integer[]{
                3,
                60,
                CRIMSON_BLOODSTONE_ID
        });
        DROPLIST_LARA.put(20267, new Integer[]{
                3,
                25,
                BREKAORC_TOTEM_ID
        });
        DROPLIST_LARA.put(20268, new Integer[]{
                3,
                25,
                BREKAORC_TOTEM_ID
        });
        DROPLIST_LARA.put(20271, new Integer[]{
                3,
                25,
                BREKAORC_TOTEM_ID
        });
        DROPLIST_LARA.put(20269, new Integer[]{
                3,
                50,
                BREKAORC_TOTEM_ID
        });
        DROPLIST_LARA.put(20270, new Integer[]{
                3,
                50,
                BREKAORC_TOTEM_ID
        });
        DROPLIST_LARA.put(20553, new Integer[]{
                4,
                70,
                TUSK_OF_WINDSUS_ID
        });
        DROPLIST_LARA.put(20192, new Integer[]{
                4,
                50,
                TALONS_OF_TYRANT_ID
        });
        DROPLIST_LARA.put(20193, new Integer[]{
                4,
                50,
                TALONS_OF_TYRANT_ID
        });
        DROPLIST_LARA.put(20089, new Integer[]{
                5,
                30,
                WINGS_OF_DRONEANT_ID
        });
        DROPLIST_LARA.put(20090, new Integer[]{
                5,
                60,
                WINGS_OF_DRONEANT_ID
        });
        DROPLIST_LARA.put(20176, new Integer[]{
                5,
                50,
                FANGS_OF_WYRM_ID
        });
    }

    static {
        DROPLIST_SUMMON.put(27102, List.of(
                CRYSTAL_OF_PROGRESS1_ID,
                CRYSTAL_OF_INPROGRESS1_ID,
                CRYSTAL_OF_FOUL1_ID,
                CRYSTAL_OF_DEFEAT1_ID,
                CRYSTAL_OF_VICTORY1_ID)); // Pako the Cat
        DROPLIST_SUMMON.put(27103, List.of(
                CRYSTAL_OF_PROGRESS2_ID,
                CRYSTAL_OF_INPROGRESS2_ID,
                CRYSTAL_OF_FOUL2_ID,
                CRYSTAL_OF_DEFEAT2_ID,
                CRYSTAL_OF_VICTORY2_ID)); // Mimi the Cat
        DROPLIST_SUMMON.put(27104, List.of(
                CRYSTAL_OF_PROGRESS3_ID,
                CRYSTAL_OF_INPROGRESS3_ID,
                CRYSTAL_OF_FOUL3_ID,
                CRYSTAL_OF_DEFEAT3_ID,
                CRYSTAL_OF_VICTORY3_ID)); // Shadow Turen
        DROPLIST_SUMMON.put(27105, List.of(
                CRYSTAL_OF_PROGRESS4_ID,
                CRYSTAL_OF_INPROGRESS4_ID,
                CRYSTAL_OF_FOUL4_ID,
                CRYSTAL_OF_DEFEAT4_ID,
                CRYSTAL_OF_VICTORY4_ID)); // Unicorn Racer
        DROPLIST_SUMMON.put(27106, List.of(
                CRYSTAL_OF_PROGRESS5_ID,
                CRYSTAL_OF_INPROGRESS5_ID,
                CRYSTAL_OF_FOUL5_ID,
                CRYSTAL_OF_DEFEAT5_ID,
                CRYSTAL_OF_VICTORY5_ID)); // Unicorn Phantasm
        DROPLIST_SUMMON.put(27107, List.of(
                CRYSTAL_OF_PROGRESS6_ID,
                CRYSTAL_OF_INPROGRESS6_ID,
                CRYSTAL_OF_FOUL6_ID,
                CRYSTAL_OF_DEFEAT6_ID,
                CRYSTAL_OF_VICTORY6_ID)); // Silhoutte Tilfo
    }


    public _230_TestOfSummoner() {
        addStartNpc(Galatea);

        addTalkId(NPCS);
        addKillId(DROPLIST_LARA.keySet());
        addKillId(DROPLIST_SUMMON.keySet());
        addAttackId(DROPLIST_SUMMON.keySet());
        addQuestItem(IntStream.rangeClosed(3337, 3389).toArray());
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("30634-08.htm".equalsIgnoreCase(event)) { // start part for Galatea
            for (String var : STATS) {
                if ("Arcanas".equalsIgnoreCase(var) || "Lara_Part".equalsIgnoreCase(var))
                    continue;
                st.set(var);
            }
            st.start();
            st.playSound(SOUND_ACCEPT);
            if (!st.player.isVarSet("dd3")) {
                st.giveItems(7562, 122);
                st.player.setVar("dd3");
            }
        } else if ("30634-07.htm".equalsIgnoreCase(event))
            st.giveItems(GALATEAS_LETTER_ID);
        else if ("30063-02.htm".equalsIgnoreCase(event)) { // Lara first time to give a list out
            int random = Rnd.get(5) + 1;
            st.giveItems(LISTS[random][0]);
            st.takeItems(GALATEAS_LETTER_ID);
            st.set("Lara_Part", random);
            st.set("step", 2);
            st.setCond(2);
        } else if ("30063-04.htm".equalsIgnoreCase(event)) { // Lara later to give a list out
            int random = Rnd.get(5) + 1;
            st.giveItems(LISTS[random][0], 1, false);
            st.set("Lara_Part", random);
        } else if ("30635-02.htm".equalsIgnoreCase(event)) { // Almors' Part, this is the same just other items below.. so just one time comments
            if (st.haveQuestItem(BEGINNERS_ARCANA_ID) ) { // if( the getPlayer has more then one beginners' arcana he can start a fight against the masters summon
                htmltext = "30635-03.htm";
                st.set("Almors", 2);
            }
        } // set state ready to fight
        else if ("30635-04.htm".equalsIgnoreCase(event)) {
            st.giveItems(CRYSTAL_OF_PROGRESS1_ID); // give Starting Crystal
            st.takeAllItems(CRYSTAL_OF_FOUL1_ID, CRYSTAL_OF_DEFEAT1_ID);
            st.takeItems(BEGINNERS_ARCANA_ID, 1);
        } // this takes one Beginner Arcana and set Beginner_Arcana stat -1
        else if ("30636-02.htm".equalsIgnoreCase(event)) { // Camoniell's Part
            if (st.haveQuestItem(BEGINNERS_ARCANA_ID)) {
                htmltext = "30636-03.htm";
                st.set("Camoniell", 2);
            }
        } else if ("30636-04.htm".equalsIgnoreCase(event)) {
            st.giveItems(CRYSTAL_OF_PROGRESS2_ID);
            st.takeItems(CRYSTAL_OF_FOUL2_ID);
            st.takeItems(CRYSTAL_OF_DEFEAT2_ID);
            st.takeItems(BEGINNERS_ARCANA_ID, 1);
        } else if ("30637-02.htm".equalsIgnoreCase(event)) { // Belthus' Part
            if (st.haveQuestItem(BEGINNERS_ARCANA_ID)) {
                htmltext = "30637-03.htm";
                st.set("Belthus", 2);
            }
        } else if ("30637-04.htm".equalsIgnoreCase(event)) {
            st.giveItems(CRYSTAL_OF_PROGRESS3_ID);
            st.takeItems(CRYSTAL_OF_FOUL3_ID);
            st.takeItems(CRYSTAL_OF_DEFEAT3_ID);
            st.takeItems(BEGINNERS_ARCANA_ID, 1);
        } else if ("30638-02.htm".equalsIgnoreCase(event)) { // Basilla's Part
            if (st.haveQuestItem(BEGINNERS_ARCANA_ID)) {
                htmltext = "30638-03.htm";
                st.set("Basilla", 2);
            }
        } else if ("30638-04.htm".equalsIgnoreCase(event)) {
            st.giveItems(CRYSTAL_OF_PROGRESS4_ID);
            st.takeItems(CRYSTAL_OF_FOUL4_ID);
            st.takeItems(CRYSTAL_OF_DEFEAT4_ID);
            st.takeItems(BEGINNERS_ARCANA_ID, 1);
        } else if ("30639-02.htm".equalsIgnoreCase(event)) { // Celestiel's Part
            if (st.haveQuestItem(BEGINNERS_ARCANA_ID)) {
                htmltext = "30639-03.htm";
                st.set("Celestiel", 2);
            }
        } else if ("30639-04.htm".equalsIgnoreCase(event)) {
            st.giveItems(CRYSTAL_OF_PROGRESS5_ID);
            st.takeItems(CRYSTAL_OF_FOUL5_ID);
            st.takeItems(CRYSTAL_OF_DEFEAT5_ID);
            st.takeItems(BEGINNERS_ARCANA_ID, 1);
        } else if ("30640-02.htm".equalsIgnoreCase(event)) { // Brynthea's Part
            if (st.getQuestItemsCount(BEGINNERS_ARCANA_ID) > 0) {
                htmltext = "30640-03.htm";
                st.set("Brynthea", 2);
            }
        } else if ("30640-04.htm".equalsIgnoreCase(event)) {
            st.giveItems(CRYSTAL_OF_PROGRESS6_ID);
            st.takeItems(CRYSTAL_OF_FOUL6_ID);
            st.takeItems(CRYSTAL_OF_DEFEAT6_ID);
            st.takeItems(BEGINNERS_ARCANA_ID, 1);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (st.haveQuestItem(MARK_OF_SUMMONER_ID)) {
            st.exitCurrentQuest();
            return "completed";
        }
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        if (id == CREATED && npcId == 30634) { // start part, Galatea
            STATS.forEach(st::unset);
            if (st.player.getClassId() == ClassId.wizard || st.player.getClassId() == ClassId.elvenWizard || st.player.getClassId() == ClassId.darkWizard)
                if (st.player.getLevel() > 38) // conditions are ok, lets start
                    htmltext = "30634-03.htm";
                else {
                    htmltext = "30634-02.htm"; // too young.. not now
                    st.exitCurrentQuest();
                }
            else { // wrong class.. never
                htmltext = "30634-01.htm";
                st.exitCurrentQuest();
            }
        } else if (id == STARTED) {
            int LaraPart = st.getInt("Lara_Part");
            int Arcanas = st.getInt("Arcanas");
            int step = st.getInt("step"); // stats as int vars if( the getPlayer has state <Progress>
            if (npcId == 30634) { // Start&&End Npc Galatea related stuff
                if (step == 1) // step 1 means just started
                    htmltext = "30634-09.htm";
                else if (step == 2) { // step 2 means already talkd with lara
                    if (Arcanas == 6) { // finished all battles... the getPlayer is able to earn the marks
                        htmltext = "30634-12.htm";
                        st.playSound(SOUND_FINISH);
                        st.takeAllItems(LARS_LIST1_ID, LARS_LIST2_ID, LARS_LIST3_ID, LARS_LIST4_ID, LARS_LIST5_ID,
                                ALMORS_ARCANA_ID, BASILLIA_ARCANA_ID, CAMONIELL_ARCANA_ID, CELESTIEL_ARCANA_ID,
                                BELTHUS_ARCANA_ID, BRYNTHEA_ARCANA_ID);
                        st.giveItems(MARK_OF_SUMMONER_ID);
                        if (!st.player.isVarSet("prof2.3")) {
                            st.addExpAndSp(832247, 57110);
                            st.giveItems(ADENA_ID, 150480);
                            st.player.setVar("prof2.3");
                        }
                        st.playSound(SOUND_FINISH);
                        st.exitCurrentQuest();
                    }
                } else
                    // he lost something ))||didnt finished
                    htmltext = "30634-10.htm";
            } else if (npcId == Lara) { // anything realated to Lara below
                if (step == 1) // first talk to lara
                    htmltext = "30063-01.htm";
                else if (LaraPart == 0) // if( you havent a part taken, give one
                    htmltext = "30063-03.htm";
                else {
                    long ItemCount1 = st.getQuestItemsCount(LISTS[LaraPart][1]);
                    long ItemCount2 = st.getQuestItemsCount(LISTS[LaraPart][2]);
                    if (ItemCount1 < 30 || ItemCount2 < 30) // if( you have not enough materials, List 1 - 5
                        htmltext = "30063-05.htm";
                    else {// if( you have enough materials, receive your Beginner Arcanas, List 1 - 5
                        htmltext = "30063-06.htm";
                        st.giveItems(BEGINNERS_ARCANA_ID, 2, false);
                        st.takeItems(LISTS[LaraPart][0], 1);
                        st.takeItems(LISTS[LaraPart][1]);
                        st.takeItems(LISTS[LaraPart][2]);
                        st.setCond(3);
                        st.unset("Lara_Part");
                    }
                }
            } else
                for (int[] i : SUMMONERS)
                    if (i[0] == npcId) {
                        List<Integer> k = DROPLIST_SUMMON.get(npcId - 30635 + 27102);
                        int summonerStat = st.getInt(NAMES.get(i[0]));
                        if (step > 1)
                            if (st.getQuestItemsCount(k.get(0)) > 0) // ready to fight... already take the mission to kill his pet
                                htmltext = npcId + "-08.htm";
                            else if (st.getQuestItemsCount(k.get(1)) > 0) { // in battle...
                                // this will add the getPlayer&&his pet to the list of notif(ied objects in onDeath Part
                                st.addNotifyOfDeath(st.player, true);
                                htmltext = npcId + "-09.htm";
                            } else if (st.getQuestItemsCount(k.get(3)) > 0) // haha... your summon lose
                                htmltext = npcId + "-05.htm";
                            else if (st.getQuestItemsCount(k.get(2)) > 0) // hey.. shit cheater.. dont help your pet
                                htmltext = npcId + "-06.htm";
                            else if (st.getQuestItemsCount(k.get(4)) > 0) { // damn.. you won the batlle.. here are the arcanas
                                htmltext = npcId + "-07.htm";
                                st.takeItems(SUMMONERS[npcId - 30635][2]); // take crystal of victory
                                st.giveItems(SUMMONERS[npcId - 30635][1]);// give arcana
                                if (st.getQuestItemsCount(ALMORS_ARCANA_ID) + st.getQuestItemsCount(3355) + st.getQuestItemsCount(3356) + st.getQuestItemsCount(3357) + st.getQuestItemsCount(3358) + st.getQuestItemsCount(3359) >= 6)
                                    st.setCond(4);
                                st.set(NAMES.get(i[0]), 7); // set 7, this mark that the players' summon won the battle
                                st.set("Arcanas", Arcanas + 1);
                            } // set arcana stat +1, if( its 6... quest is finished&&he can earn the mark
                            else if (summonerStat == 7) // you already won the battle against my summon
                                htmltext = npcId + "-10.htm";
                            else
                                htmltext = npcId + "-01.htm";
                    }
        }
        return htmltext;
    }

    @Override
    public void onDeath(Creature killer, Creature victim, QuestState st) {
        if (killer == null || victim == null)
            return; // WTF?
        // if players summon dies, the crystal of defeat is given to the getPlayer and set stat to lose
        int npcId = killer.getNpcId();
        ////      if (deadPerson == st.getPlayer() or deadPerson = st.getPlayer().getPet()) and npcId in DROPLIST_SUMMON.keys() :
        if (victim == st.player || victim == st.player.getPet())
            if (npcId >= 27102 && npcId <= 27107) {
                // var means the variable of the SummonerManager, the rest are all Crystalls wich mark the status
                String var = VARS.get(npcId - 27102);
                List<Integer> i = DROPLIST_SUMMON.get(npcId);
                int defeat = i.get(3);
                if (st.getInt(var) == 3) {
                    st.set(var, 4);
                    st.giveItems(defeat, 1, false);
                }
            }
    }

    @Override
    public void onAttack(NpcInstance npc, QuestState st) { // on the first attack, the stat is in battle... anytime gives crystal and set stat
        int npcId = npc.getNpcId();
        // var means the variable of the SummonerManager, the rest are all Crystalls wich mark the status
        if (npcId >= 27102 && npcId <= 27107) {
            String var = VARS.get(npcId - 27102);
            List<Integer> i = DROPLIST_SUMMON.get(npcId);
            int start = i.get(0);
            int progress = i.get(1);
            if (st.getInt(var) == 2) {
                st.set(var, 3);
                st.giveItems(progress);
                st.takeItems(start, 1);
                st.playSound(SOUND_ITEMGET);
            }

            if (st.getQuestItemsCount(i.get(2)) != 0)
                return;

            Summon summon = st.player.getPet();
            if (summon == null || summon instanceof PetInstance) {
                st.giveItems(i.get(2), 1, false);
            }
        }
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId(); // this part is just for laras parts
        if (DROPLIST_LARA.containsKey(npcId)) {
            Integer[] i = DROPLIST_LARA.get(npcId);
            String var = "Lara_Part";
            int value = i[0];
            int chance = i[1];
            int item = i[2];
            long count = st.getQuestItemsCount(item);
            if (st.getInt(var) == value && count < 30 && Rnd.chance(chance)) {
                st.giveItems(item, 1, true);
                if (count == 29)
                    st.playSound(SOUND_MIDDLE);
                else
                    st.playSound(SOUND_ITEMGET);
            }
        } else if (DROPLIST_SUMMON.containsKey(npcId)) { // if a summon dies
            // var means the variable of the SummonerManager, the rest are all Crystalls which mark the status
            String var = VARS.get(npcId - 27102);
            List<Integer> i = DROPLIST_SUMMON.get(npcId);
            int progress = i.get(1);
            int foul = i.get(2);
            int victory = i.get(4);
            if (st.getInt(var) == 3) {
                boolean isFoul = st.getQuestItemsCount(foul) == 0;
                int isName = 1; // first entry in the droplist is a name (string).  Skip it.
                for (Integer item : DROPLIST_SUMMON.get(npcId)) { // take all crystal of this summoner away from the getPlayer
                    if (isName != 1)
                        st.takeItems(item);
                    isName = 0;
                }

                st.takeItems(progress);
                if (isFoul) {
                    st.set(var, 6);
                    st.giveItems(victory, 1, false); // if he wons without cheating, set stat won and give victory crystal
                    st.playSound(SOUND_MIDDLE);
                } else
                    st.set(var, 5); // if the getPlayer cheats, give foul crystal and set stat to cheat
            }
        }
    }
}