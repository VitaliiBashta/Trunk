package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;

import static l2trunk.gameserver.model.base.ClassId.*;

public final class _211_TrialOfChallenger extends Quest {
    // Npcs
    private static final int Filaur = 30535;
    private static final int Kash = 30644;
    private static final int Martien = 30645;
    private static final int Raldo = 30646;
    private static final int ChestOfShyslassys = 30647;

    // Monsters
    private static final int Shyslassys = 27110;
    private static final int CaveBasilisk = 27111;
    private static final int Gorr = 27112;
    private static final int Baraham = 27113;
    private static final int SuccubusQueen = 27114;

    // items
    private static final int LETTER_OF_KASH_ID = 2628;
    private static final int SCROLL_OF_SHYSLASSY_ID = 2631;
    private static final int WATCHERS_EYE1_ID = 2629;
    private static final int BROKEN_KEY_ID = 2632;
    private static final int MITHRIL_SCALE_GAITERS_MATERIAL_ID = 2918;
    private static final int BRIGANDINE_GAUNTLET_PATTERN_ID = 2927;
    private static final int MANTICOR_SKIN_GAITERS_PATTERN_ID = 1943;
    private static final int GAUNTLET_OF_REPOSE_OF_THE_SOUL_PATTERN_ID = 1946;
    private static final int IRON_BOOTS_DESIGN_ID = 1940;
    private static final int TOME_OF_BLOOD_PAGE_ID = 2030;
    private static final int ELVEN_NECKLACE_BEADS_ID = 1904;
    private static final int WHITE_TUNIC_PATTERN_ID = 1936;
    private static final int MARK_OF_CHALLENGER_ID = 2627;
    private static final int WATCHERS_EYE2_ID = 2630;
    private static final int RewardExp = 533803;
    private static final int RewardSP = 34621;
    private static final int RewardAdena = 97278;

    private NpcInstance raldoSpawn;

    public _211_TrialOfChallenger() {
        super(false);

        addStartNpc(Kash);

        addTalkId(Filaur,Martien,Raldo,ChestOfShyslassys);

        addKillId(Shyslassys,CaveBasilisk,Gorr,Baraham,SuccubusQueen);

        addQuestItem(SCROLL_OF_SHYSLASSY_ID,
                LETTER_OF_KASH_ID,
                WATCHERS_EYE1_ID,
                BROKEN_KEY_ID,
                WATCHERS_EYE2_ID);
    }

    private void Spawn_Raldo(QuestState st) {
        if (raldoSpawn != null)
            raldoSpawn.deleteMe();
        raldoSpawn = addSpawn(Raldo, st.player.getLoc(), 100, 300000);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "1":
                htmltext = "kash_q0211_05.htm";
                st.setCond(1);
                st.start();
                if (!st.player.isVarSet("dd1")) {
                    st.giveItems(7562, 64);
                    st.player.setVar("dd1");
                }
                st.playSound(SOUND_ACCEPT);
                break;
            case "30644_1":
                htmltext = "kash_q0211_04.htm";
                break;
            case "30645_1":
                htmltext = "martian_q0211_02.htm";
                st.takeItems(LETTER_OF_KASH_ID, 1);
                st.setCond(4);
                break;
            case "30647_1":
                if (st.haveQuestItem(BROKEN_KEY_ID)) {
                    st.giveItems(SCROLL_OF_SHYSLASSY_ID);
                    if (Rnd.chance(22)) {
                        htmltext = "chest_of_shyslassys_q0211_03.htm";
                        st.takeItems(BROKEN_KEY_ID, 1);
                        st.playSound(SOUND_JACKPOT);
                        int n = Rnd.get(100);
                        if (n > 90) {
                            st.giveItems(MITHRIL_SCALE_GAITERS_MATERIAL_ID);
                            st.giveItems(BRIGANDINE_GAUNTLET_PATTERN_ID);
                            st.giveItems(MANTICOR_SKIN_GAITERS_PATTERN_ID);
                            st.giveItems(GAUNTLET_OF_REPOSE_OF_THE_SOUL_PATTERN_ID);
                            st.giveItems(IRON_BOOTS_DESIGN_ID);
                        } else if (n > 70) {
                            st.giveItems(TOME_OF_BLOOD_PAGE_ID);
                            st.giveItems(ELVEN_NECKLACE_BEADS_ID);
                        } else if (n > 40)
                            st.giveItems(WHITE_TUNIC_PATTERN_ID);
                        else
                            st.giveItems(IRON_BOOTS_DESIGN_ID);
                    } else {
                        htmltext = "chest_of_shyslassys_q0211_02.htm";
                        st.takeItems(BROKEN_KEY_ID);
                        st.giveItems(ADENA_ID, Rnd.get(1000) + 1);
                    }
                } else
                    htmltext = "chest_of_shyslassys_q0211_04.htm";
                break;
            case "30646_1":
                htmltext = "raldo_q0211_02.htm";
                break;
            case "30646_2":
                htmltext = "raldo_q0211_03.htm";
                break;
            case "30646_3":
                htmltext = "raldo_q0211_04.htm";
                st.setCond(8);
                st.takeItems(WATCHERS_EYE2_ID, 1);
                break;
            case "30646_4":
                htmltext = "raldo_q0211_06.htm";
                st.setCond(8);
                st.takeItems(WATCHERS_EYE2_ID, 1);
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (st.haveQuestItem(MARK_OF_CHALLENGER_ID)) {
            st.exitCurrentQuest();
            return "completed";
        }
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (id == CREATED) {
            st.setCond(0);
            if (npcId == Kash)
                if (st.player.getClassId() == warrior
                        || st.player.getClassId() == elvenKnight
                        || st.player.getClassId() == palusKnight
                        || st.player.getClassId() == orcRaider
                        || st.player.getClassId() == orcMonk)
                    if (st.player.getLevel() >= 35)
                        htmltext = "kash_q0211_03.htm";
                    else {
                        htmltext = "kash_q0211_01.htm";
                        st.exitCurrentQuest();
                    }
                else {
                    htmltext = "kash_q0211_02.htm";
                    st.exitCurrentQuest();
                }
        } else if (npcId == Kash && cond == 1)
            htmltext = "kash_q0211_06.htm";
        else if (npcId == Kash && cond == 2 && st.getQuestItemsCount(SCROLL_OF_SHYSLASSY_ID) == 1) {
            htmltext = "kash_q0211_07.htm";
            st.takeItems(SCROLL_OF_SHYSLASSY_ID, 1);
            st.giveItems(LETTER_OF_KASH_ID, 1);
            st.setCond(3);
        } else {
            if (npcId == Kash && cond == 1) {
                st.getQuestItemsCount(LETTER_OF_KASH_ID);
            }
            if (npcId == Kash && cond >= 7)
                htmltext = "kash_q0211_09.htm";
            else if (npcId == Martien && cond == 3 && st.getQuestItemsCount(LETTER_OF_KASH_ID) == 1)
                htmltext = "martian_q0211_01.htm";
            else if (npcId == Martien && cond == 4 && st.getQuestItemsCount(WATCHERS_EYE1_ID) == 0)
                htmltext = "martian_q0211_03.htm";
            else if (npcId == Martien && cond == 5 && st.getQuestItemsCount(WATCHERS_EYE1_ID) > 0) {
                htmltext = "martian_q0211_04.htm";
                st.takeItems(WATCHERS_EYE1_ID, 1);
                st.setCond(6);
            } else if (npcId == Martien && cond == 6)
                htmltext = "martian_q0211_05.htm";
            else if (npcId == Martien && cond >= 7)
                htmltext = "martian_q0211_06.htm";
            else if (npcId == ChestOfShyslassys && cond == 2)
                htmltext = "chest_of_shyslassys_q0211_01.htm";
            else if (npcId == Raldo && cond == 7 && st.haveQuestItem(WATCHERS_EYE2_ID))
                htmltext = "raldo_q0211_01.htm";
            else if (npcId == Raldo && cond == 8)
                htmltext = "raldo_q0211_06a.htm";
            else if (npcId == Raldo && cond == 10) {
                htmltext = "raldo_q0211_07.htm";
                st.takeItems(BROKEN_KEY_ID);
                st.giveItems(MARK_OF_CHALLENGER_ID);
                if (!st.player.isVarSet("prof2.1")) {
                    st.addExpAndSp(RewardExp, RewardSP);
                    st.giveItems(ADENA_ID, RewardAdena);
                    st.player.setVar("prof2.1");
                }
                st.playSound(SOUND_FINISH);
                st.finish();
            } else if (npcId == 30535 && cond == 8)
                if (st.player.getLevel() >= 36) {
                    htmltext = "elder_filaur_q0211_01.htm";
                    st.player.addRadar(Location.of(176560, -184969, -3729));
                    st.setCond(9);
                } else
                    htmltext = "elder_filaur_q0211_03.htm";
            else if (npcId == 30535 && cond == 9) {
                htmltext = "elder_filaur_q0211_02.htm";
                st.player.addRadar(Location.of(176560, -184969, -3729));
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Shyslassys && cond == 1 && st.getQuestItemsCount(SCROLL_OF_SHYSLASSY_ID) == 0 && st.getQuestItemsCount(BROKEN_KEY_ID) == 0) {
            st.giveItems(BROKEN_KEY_ID);
            st.addSpawn(ChestOfShyslassys);
            st.playSound(SOUND_MIDDLE);
            st.setCond(2);
        } else if (npcId == Gorr && cond == 4 && st.getQuestItemsCount(WATCHERS_EYE1_ID) == 0) {
            st.giveItems(WATCHERS_EYE1_ID);
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
        } else if (npcId == Baraham && (cond == 6 || cond == 7)) {
            st.giveItemIfNotHave(WATCHERS_EYE2_ID);
            st.playSound(SOUND_MIDDLE);
            st.setCond(7);
            Spawn_Raldo(st);
        } else if (npcId == SuccubusQueen && (cond == 9 || cond == 10)) {
            st.setCond(10);
            st.playSound(SOUND_MIDDLE);
            Spawn_Raldo(st);
        }
    }
}