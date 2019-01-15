package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

public final class _417_PathToScavenger extends Quest {
    // ITEMS
    private final int RING_OF_RAVEN = 1642;
    private final int PIPIS_LETTER = 1643;
    private final int ROUTS_TP_SCROLL = 1644;
    private final int SUCCUBUS_UNDIES = 1645;
    private final int MIONS_LETTER = 1646;
    private final int BRONKS_INGOT = 1647;
    private final int CHARIS_AXE = 1648;
    private final int ZIMENFS_POTION = 1649;
    private final int BRONKS_PAY = 1650;
    private final int CHALIS_PAY = 1651;
    private final int ZIMENFS_PAY = 1652;
    private final int BEAR_PIC = 1653;
    private final int TARANTULA_PIC = 1654;
    private final int HONEY_JAR = 1655;
    private final int BEAD = 1656;
    private final int BEAD_PARCEL = 1657;

    // NPC
    private final int Pippi = 30524;
    private final int Raut = 30316;
    private final int Shari = 30517;
    private final int Mion = 30519;
    private final int Bronk = 30525;
    private final int Zimenf = 30538;
    private final int Toma = 30556;
    private final int Torai = 30557;

    // MOBS
    private final int HunterTarantula = 20403;
    private final int HoneyBear = 27058;
    private final int PlunderTarantula = 20508;
    private final int HunterBear = 20777;

    public _417_PathToScavenger() {
        super(false);

        addStartNpc(Pippi);

        addTalkId(Raut);
        addTalkId(Shari);
        addTalkId(Mion);
        addTalkId(Bronk);
        addTalkId(Zimenf);
        addTalkId(Toma);
        addTalkId(Torai);

        addKillId(HunterTarantula);
        addKillId(HoneyBear);
        addKillId(PlunderTarantula);
        addKillId(HunterBear);

        addQuestItem(CHALIS_PAY);
        addQuestItem(ZIMENFS_PAY);
        addQuestItem(BRONKS_PAY);
        addQuestItem(PIPIS_LETTER);
        addQuestItem(CHARIS_AXE);
        addQuestItem(ZIMENFS_POTION);
        addQuestItem(BRONKS_INGOT);
        addQuestItem(MIONS_LETTER);
        addQuestItem(HONEY_JAR);
        addQuestItem(BEAR_PIC);
        addQuestItem(BEAD_PARCEL);
        addQuestItem(BEAD);
        addQuestItem(TARANTULA_PIC);
        addQuestItem(SUCCUBUS_UNDIES);
        addQuestItem(ROUTS_TP_SCROLL);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int cond = st.getCond();
        if (event.equals("1")) {
            st.set("id", "0");
            if (st.getPlayer().getLevel() >= 18 && st.getPlayer().getClassId().getId() == 0x35 && st.getQuestItemsCount(RING_OF_RAVEN) == 0) {
                st.setCond(1);
                st.setState(STARTED);
                st.playSound(SOUND_ACCEPT);
                st.giveItems(PIPIS_LETTER, 1);
                htmltext = "collector_pipi_q0417_05.htm";
            } else if (st.getPlayer().getClassId().getId() != 0x35) {
                if (st.getPlayer().getClassId().getId() == 0x36)
                    htmltext = "collector_pipi_q0417_02a.htm";
                else
                    htmltext = "collector_pipi_q0417_08.htm";
            } else if (st.getPlayer().getLevel() < 18 && st.getPlayer().getClassId().getId() == 0x35)
                htmltext = "collector_pipi_q0417_02.htm";
            else if (st.getPlayer().getLevel() >= 18 && st.getPlayer().getClassId().getId() == 0x35 && st.getQuestItemsCount(RING_OF_RAVEN) == 1)
                htmltext = "collector_pipi_q0417_04.htm";
        } else if (event.equals("30519_1")) {
            if (st.getQuestItemsCount(PIPIS_LETTER) > 0) {
                st.takeItems(PIPIS_LETTER, 1);
                st.setCond(2);
                int n = Rnd.get(3);
                if (n == 0) {
                    htmltext = "trader_mion_q0417_02.htm";
                    st.giveItems(ZIMENFS_POTION, 1);
                } else if (n == 1) {
                    htmltext = "trader_mion_q0417_03.htm";
                    st.giveItems(CHARIS_AXE, 1);
                } else if (n == 2) {
                    htmltext = "trader_mion_q0417_04.htm";
                    st.giveItems(BRONKS_INGOT, 1);
                }
            } else
                htmltext = "noquest";
        } else if (event.equals("30519_2"))
            htmltext = "trader_mion_q0417_06.htm";
        else if (event.equals("30519_3")) {
            htmltext = "trader_mion_q0417_07.htm";
            st.set("id", String.valueOf(st.getInt("id") + 1));
        } else if (event.equals("30519_4")) {
            int n = Rnd.get(2);
            if (n == 0)
                htmltext = "trader_mion_q0417_06.htm";
            else if (n == 1)
                htmltext = "trader_mion_q0417_11.htm";
        } else if (event.equals("30519_5")) {
            if (st.getQuestItemsCount(ZIMENFS_POTION, CHARIS_AXE, BRONKS_INGOT) > 0) {
                if (st.getInt("id") / 10 < 2) {
                    htmltext = "trader_mion_q0417_07.htm";
                    st.set("id", String.valueOf(st.getInt("id") + 1));
                } else if (st.getInt("id") / 10 >= 2 && cond == 0) {
                    htmltext = "trader_mion_q0417_09.htm";
                    if (st.getInt("id") / 10 < 3)
                        st.set("id", String.valueOf(st.getInt("id") + 1));
                } else if (st.getInt("id") / 10 >= 3 && cond > 0) {
                    htmltext = "trader_mion_q0417_10.htm";
                    st.giveItems(MIONS_LETTER, 1);
                    st.takeItems(CHARIS_AXE, 1);
                    st.takeItems(ZIMENFS_POTION, 1);
                    st.takeItems(BRONKS_INGOT, 1);
                }
            } else
                htmltext = "noquest";
        } else if (event.equals("30519_6")) {
            if (st.getQuestItemsCount(ZIMENFS_PAY) > 0 || st.getQuestItemsCount(CHALIS_PAY) > 0 || st.getQuestItemsCount(BRONKS_PAY) > 0) {
                int n = Rnd.get(3);
                st.takeItems(ZIMENFS_PAY, 1);
                st.takeItems(CHALIS_PAY, 1);
                st.takeItems(BRONKS_PAY, 1);
                if (n == 0) {
                    htmltext = "trader_mion_q0417_02.htm";
                    st.giveItems(ZIMENFS_POTION, 1);
                } else if (n == 1) {
                    htmltext = "trader_mion_q0417_03.htm";
                    st.giveItems(CHARIS_AXE, 1);
                } else if (n == 2) {
                    htmltext = "trader_mion_q0417_04.htm";
                    st.giveItems(BRONKS_INGOT, 1);
                }
            } else
                htmltext = "noquest";
        } else if (event.equals("30316_1")) {
            if (st.getQuestItemsCount(BEAD_PARCEL) > 0) {
                htmltext = "raut_q0417_02.htm";
                st.takeItems(BEAD_PARCEL, 1);
                st.giveItems(ROUTS_TP_SCROLL, 1);
                st.setCond(10);
            } else
                htmltext = "noquest";
        } else if (event.equals("30316_2")) {
            if (st.getQuestItemsCount(BEAD_PARCEL) > 0) {
                htmltext = "raut_q0417_03.htm";
                st.takeItems(BEAD_PARCEL, 1);
                st.giveItems(ROUTS_TP_SCROLL, 1);
                st.setCond(10);
            } else
                htmltext = "noquest";
        } else if (event.equals("30557_1"))
            htmltext = "torai_q0417_02.htm";
        else if (event.equals("30557_2"))
            if (st.getQuestItemsCount(ROUTS_TP_SCROLL) > 0) {
                htmltext = "torai_q0417_03.htm";
                st.takeItems(ROUTS_TP_SCROLL, 1);
                st.giveItems(SUCCUBUS_UNDIES, 1);
                st.setCond(11);
            } else
                htmltext = "noquest";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (id == CREATED)
            st.setState(STARTED);
        if (npcId == Pippi) {
            if (cond == 0)
                htmltext = "collector_pipi_q0417_01.htm";
            else if (st.getQuestItemsCount(PIPIS_LETTER) > 0)
                htmltext = "collector_pipi_q0417_06.htm";
            else if (st.getQuestItemsCount(PIPIS_LETTER) == 0 && id == STARTED)
                htmltext = "collector_pipi_q0417_01.htm";
            else if (st.getQuestItemsCount(PIPIS_LETTER) == 0)
                htmltext = "collector_pipi_q0417_07.htm";
        } else if (cond == 0)
            return "noquest";
        else if (npcId == Mion) {
            if (st.getQuestItemsCount(PIPIS_LETTER) > 0)
                htmltext = "trader_mion_q0417_01.htm";
            else if (st.getQuestItemsCount(CHARIS_AXE, BRONKS_INGOT, ZIMENFS_POTION) > 0 && st.getInt("id") / 10 == 0)
                htmltext = "trader_mion_q0417_05.htm";
            else if (st.getQuestItemsCount(CHARIS_AXE, BRONKS_INGOT, ZIMENFS_POTION) > 0 && st.getInt("id") / 10 > 0)
                htmltext = "trader_mion_q0417_08.htm";
            else if (st.getQuestItemsCount(CHALIS_PAY, BRONKS_PAY, ZIMENFS_PAY) > 0 && st.getInt("id") < 50)
                htmltext = "trader_mion_q0417_12.htm";
            else if (st.getQuestItemsCount(CHALIS_PAY, BRONKS_PAY, ZIMENFS_PAY) > 0 && st.getInt("id") >= 50) {
                htmltext = "trader_mion_q0417_15.htm";
                st.giveItems(MIONS_LETTER, 1);
                st.takeItems(CHALIS_PAY, -1);
                st.takeItems(ZIMENFS_PAY, -1);
                st.takeItems(BRONKS_PAY, -1);
                st.setCond(4);
            } else if (st.getQuestItemsCount(MIONS_LETTER) > 0)
                htmltext = "trader_mion_q0417_13.htm";
            else if (st.getQuestItemsCount(BEAR_PIC) > 0 || st.getQuestItemsCount(TARANTULA_PIC) > 0 || st.getQuestItemsCount(BEAD_PARCEL) > 0 || st.getQuestItemsCount(ROUTS_TP_SCROLL) > 0 || st.getQuestItemsCount(SUCCUBUS_UNDIES) > 0)
                htmltext = "trader_mion_q0417_14.htm";
        } else if (npcId == Shari) {
            if (st.getQuestItemsCount(CHARIS_AXE) > 0) {
                if (st.getInt("id") < 20)
                    htmltext = "trader_chali_q0417_01.htm";
                else
                    htmltext = "trader_chali_q0417_02.htm";
                st.takeItems(CHARIS_AXE, 1);
                st.giveItems(CHALIS_PAY, 1);
                if (st.getInt("id") >= 50)
                    st.setCond(3);
                st.set("id", st.getInt("id") + 10);
            } else if (st.getQuestItemsCount(CHALIS_PAY) == 1)
                htmltext = "trader_chali_q0417_03.htm";
        } else if (npcId == Bronk) {
            if (st.getQuestItemsCount(BRONKS_INGOT) == 1) {
                if (st.getInt("id") < 20)
                    htmltext = "head_blacksmith_bronk_q0417_01.htm";
                else
                    htmltext = "head_blacksmith_bronk_q0417_02.htm";
                st.takeItems(BRONKS_INGOT, 1);
                st.giveItems(BRONKS_PAY, 1);
                if (st.getInt("id") >= 50)
                    st.setCond(3);
                st.set("id", st.getInt("id") + 10);
            } else if (st.getQuestItemsCount(BRONKS_PAY) == 1)
                htmltext = "head_blacksmith_bronk_q0417_03.htm";
        } else if (npcId == Zimenf) {
            if (st.getQuestItemsCount(ZIMENFS_POTION) == 1) {
                if (st.getInt("id") < 20)
                    htmltext = "zimenf_priest_of_earth_q0417_01.htm";
                else
                    htmltext = "zimenf_priest_of_earth_q0417_02.htm";
                st.takeItems(ZIMENFS_POTION, 1);
                st.giveItems(ZIMENFS_PAY, 1);
                if (st.getInt("id") >= 50)
                    st.setCond(3);
                st.set("id", st.getInt("id") + 10);
            } else if (st.getQuestItemsCount(ZIMENFS_PAY) == 1)
                htmltext = "zimenf_priest_of_earth_q0417_03.htm";
        } else if (npcId == Toma) {
            if (st.getQuestItemsCount(MIONS_LETTER) == 1) {
                htmltext = "master_toma_q0417_01.htm";
                st.takeItems(MIONS_LETTER, 1);
                st.giveItems(BEAR_PIC, 1);
                st.setCond(5);
                st.set("id", String.valueOf(0));
            } else if (st.getQuestItemsCount(BEAR_PIC) == 1 && st.getQuestItemsCount(HONEY_JAR) < 5)
                htmltext = "master_toma_q0417_02.htm";
            else if (st.getQuestItemsCount(BEAR_PIC) == 1 && st.getQuestItemsCount(HONEY_JAR) >= 5) {
                htmltext = "master_toma_q0417_03.htm";
                st.takeItems(HONEY_JAR, st.getQuestItemsCount(HONEY_JAR));
                st.takeItems(BEAR_PIC, 1);
                st.giveItems(TARANTULA_PIC, 1);
                st.setCond(7);
            } else if (st.getQuestItemsCount(TARANTULA_PIC) == 1 && st.getQuestItemsCount(BEAD) < 20)
                htmltext = "master_toma_q0417_04.htm";
            else if (st.getQuestItemsCount(TARANTULA_PIC) == 1 && st.getQuestItemsCount(BEAD) >= 20) {
                htmltext = "master_toma_q0417_05.htm";
                st.takeItems(BEAD, st.getQuestItemsCount(BEAD));
                st.takeItems(TARANTULA_PIC, 1);
                st.giveItems(BEAD_PARCEL, 1);
                st.setCond(9);
            } else if (st.getQuestItemsCount(BEAD_PARCEL) > 0)
                htmltext = "master_toma_q0417_06.htm";
            else if (st.getQuestItemsCount(ROUTS_TP_SCROLL) > 0 || st.getQuestItemsCount(SUCCUBUS_UNDIES) > 0)
                htmltext = "master_toma_q0417_07.htm";
        } else if (npcId == Raut) {
            if (st.getQuestItemsCount(BEAD_PARCEL) == 1)
                htmltext = "raut_q0417_01.htm";
            else if (st.getQuestItemsCount(ROUTS_TP_SCROLL) == 1)
                htmltext = "raut_q0417_04.htm";
            else if (st.getQuestItemsCount(SUCCUBUS_UNDIES) == 1) {
                htmltext = "raut_q0417_05.htm";
                st.takeItems(SUCCUBUS_UNDIES, 1);
                if (st.getPlayer().getClassId().getLevel() == 1) {
                    st.giveItems(RING_OF_RAVEN, 1);
                    if (!st.getPlayer().getVarB("prof1")) {
                        st.getPlayer().setVar("prof1", "1", -1);
                        st.addExpAndSp(228064, 16455);
                        //FIXME [G1ta0] дать адены, только если первый чар на акке
                        st.giveItems(ADENA_ID, 81900);
                    }
                }
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(false);
            }
        } else if (npcId == Torai && st.getQuestItemsCount(ROUTS_TP_SCROLL) == 1)
            htmltext = "torai_q0417_01.htm";
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        MonsterInstance mob = (MonsterInstance) npc;
        boolean cond = st.getCond() > 0;
        if (npcId == HunterBear) {
            if (cond && st.getQuestItemsCount(BEAR_PIC) == 1 && st.getQuestItemsCount(HONEY_JAR) < 5 && Rnd.chance(20))
                st.addSpawn(HoneyBear);
        } else if (npcId == HoneyBear) {
            if (cond && st.getQuestItemsCount(BEAR_PIC) == 1 && st.getQuestItemsCount(HONEY_JAR) < 5)
                if (mob.isSpoiled()) {
                    st.giveItems(HONEY_JAR, 1);
                    if (st.getQuestItemsCount(HONEY_JAR) == 5) {
                        st.playSound(SOUND_MIDDLE);
                        st.setCond(6);
                    } else
                        st.playSound(SOUND_ITEMGET);
                }
        } else if (npcId == HunterTarantula || npcId == PlunderTarantula)
            if (cond && st.getQuestItemsCount(TARANTULA_PIC) == 1 && st.getQuestItemsCount(BEAD) < 20)
                if (mob.isSpoiled())
                    if (Rnd.chance(50)) {
                        st.giveItems(BEAD, 1);
                        if (st.getQuestItemsCount(BEAD) == 20) {
                            st.playSound(SOUND_MIDDLE);
                            st.setCond(8);
                        } else
                            st.playSound(SOUND_ITEMGET);
                    }
        return null;
    }
}