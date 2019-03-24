package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import static l2trunk.gameserver.model.base.ClassId.dwarvenFighter;
import static l2trunk.gameserver.model.base.ClassId.scavenger;

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

        addTalkId(Raut,Shari,Mion,Bronk,Zimenf,Toma,Torai);

        addKillId(HunterTarantula,HoneyBear,PlunderTarantula,HunterBear);

        addQuestItem(CHALIS_PAY,ZIMENFS_PAY,BRONKS_PAY,PIPIS_LETTER,CHARIS_AXE,ZIMENFS_POTION,BRONKS_INGOT,
                MIONS_LETTER,HONEY_JAR,BEAR_PIC,BEAD_PARCEL,BEAD,TARANTULA_PIC,SUCCUBUS_UNDIES,ROUTS_TP_SCROLL);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int cond = st.getCond();
        switch (event) {
            case "1":
                st.unset("id");
                if (st.player.getLevel() >= 18 && st.player.getClassId() == dwarvenFighter && !st.haveQuestItem(RING_OF_RAVEN)) {
                    st.setCond(1);
                    st.start();
                    st.playSound(SOUND_ACCEPT);
                    st.giveItems(PIPIS_LETTER);
                    htmltext = "collector_pipi_q0417_05.htm";
                } else if (st.player.getClassId() != dwarvenFighter) {
                    if (st.player.getClassId() == scavenger)
                        htmltext = "collector_pipi_q0417_02a.htm";
                    else
                        htmltext = "collector_pipi_q0417_08.htm";
                } else if (st.player.getLevel() < 18)
                    htmltext = "collector_pipi_q0417_02.htm";
                else if (st.haveQuestItem(RING_OF_RAVEN))
                    htmltext = "collector_pipi_q0417_04.htm";
                break;
            case "30519_1":
                if (st.haveQuestItem(PIPIS_LETTER) ) {
                    st.takeItems(PIPIS_LETTER);
                    st.setCond(2);
                    int n = Rnd.get(3);
                    if (n == 0) {
                        htmltext = "trader_mion_q0417_02.htm";
                        st.giveItems(ZIMENFS_POTION);
                    } else if (n == 1) {
                        htmltext = "trader_mion_q0417_03.htm";
                        st.giveItems(CHARIS_AXE);
                    } else if (n == 2) {
                        htmltext = "trader_mion_q0417_04.htm";
                        st.giveItems(BRONKS_INGOT);
                    }
                } else
                    htmltext = "noquest";
                break;
            case "30519_2":
                htmltext = "trader_mion_q0417_06.htm";
                break;
            case "30519_3":
                htmltext = "trader_mion_q0417_07.htm";
                st.inc("id");
                break;
            case "30519_4":
                int n = Rnd.get(2);
                if (n == 0)
                    htmltext = "trader_mion_q0417_06.htm";
                else if (n == 1)
                    htmltext = "trader_mion_q0417_11.htm";
                break;
            case "30519_5":
                if (st.haveAnyQuestItems(ZIMENFS_POTION, CHARIS_AXE, BRONKS_INGOT)) {
                    if (st.getInt("id") / 10 < 2) {
                        htmltext = "trader_mion_q0417_07.htm";
                        st.inc("id");
                    } else if (st.getInt("id") / 10 >= 2 && cond == 0) {
                        htmltext = "trader_mion_q0417_09.htm";
                        if (st.getInt("id") / 10 < 3)
                            st.inc("id");
                    } else if (st.getInt("id") / 10 >= 3 && cond > 0) {
                        htmltext = "trader_mion_q0417_10.htm";
                        st.giveItems(MIONS_LETTER);
                        st.takeItems(CHARIS_AXE, 1);
                        st.takeItems(ZIMENFS_POTION, 1);
                        st.takeItems(BRONKS_INGOT, 1);
                    }
                } else
                    htmltext = "noquest";
                break;
            case "30519_6":
                if (st.haveAnyQuestItems(ZIMENFS_PAY,CHALIS_PAY,BRONKS_PAY)) {
                    n = Rnd.get(3);
                    st.takeAllItems(ZIMENFS_PAY,CHALIS_PAY,BRONKS_PAY);
                    if (n == 0) {
                        htmltext = "trader_mion_q0417_02.htm";
                        st.giveItems(ZIMENFS_POTION);
                    } else if (n == 1) {
                        htmltext = "trader_mion_q0417_03.htm";
                        st.giveItems(CHARIS_AXE);
                    } else if (n == 2) {
                        htmltext = "trader_mion_q0417_04.htm";
                        st.giveItems(BRONKS_INGOT);
                    }
                } else
                    htmltext = "noquest";
                break;
            case "30316_1":
                if (st.haveQuestItem(BEAD_PARCEL) ) {
                    htmltext = "raut_q0417_02.htm";
                    st.takeItems(BEAD_PARCEL,1);
                    st.giveItems(ROUTS_TP_SCROLL);
                    st.setCond(10);
                } else
                    htmltext = "noquest";
                break;
            case "30316_2":
                if (st.haveQuestItem(BEAD_PARCEL) ) {
                    htmltext = "raut_q0417_03.htm";
                    st.takeItems(BEAD_PARCEL, 1);
                    st.giveItems(ROUTS_TP_SCROLL);
                    st.setCond(10);
                } else
                    htmltext = "noquest";
                break;
            case "30557_1":
                htmltext = "torai_q0417_02.htm";
                break;
            case "30557_2":
                if (st.haveQuestItem(ROUTS_TP_SCROLL)) {
                    htmltext = "torai_q0417_03.htm";
                    st.takeItems(ROUTS_TP_SCROLL, 1);
                    st.giveItems(SUCCUBUS_UNDIES);
                    st.setCond(11);
                } else
                    htmltext = "noquest";
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int state = st.getState();
        int cond = st.getCond();
        if (state == CREATED)
            st.start();
        if (npcId == Pippi) {
            if (cond == 0)
                htmltext = "collector_pipi_q0417_01.htm";
            else if (st.haveQuestItem(PIPIS_LETTER) )
                htmltext = "collector_pipi_q0417_06.htm";
            else if (!st.haveQuestItem(PIPIS_LETTER)  && state == STARTED)
                htmltext = "collector_pipi_q0417_01.htm";
            else if (!st.haveQuestItem(PIPIS_LETTER))
                htmltext = "collector_pipi_q0417_07.htm";
        } else if (cond == 0)
            return "noquest";
        else if (npcId == Mion) {
            if (st.haveQuestItem(PIPIS_LETTER))
                htmltext = "trader_mion_q0417_01.htm";
            else if (st.haveAnyQuestItems(CHARIS_AXE, BRONKS_INGOT, ZIMENFS_POTION)  && st.getInt("id") / 10 == 0)
                htmltext = "trader_mion_q0417_05.htm";
            else if (st.haveAnyQuestItems(CHARIS_AXE, BRONKS_INGOT, ZIMENFS_POTION)  && st.getInt("id") / 10 > 0)
                htmltext = "trader_mion_q0417_08.htm";
            else if (st.getQuestItemsCount(CHALIS_PAY, BRONKS_PAY, ZIMENFS_PAY) > 0 && st.getInt("id") < 50)
                htmltext = "trader_mion_q0417_12.htm";
            else if (st.getQuestItemsCount(CHALIS_PAY, BRONKS_PAY, ZIMENFS_PAY) > 0 && st.getInt("id") >= 50) {
                htmltext = "trader_mion_q0417_15.htm";
                st.giveItems(MIONS_LETTER);
                st.takeItems(CHALIS_PAY);
                st.takeItems(ZIMENFS_PAY);
                st.takeItems(BRONKS_PAY);
                st.setCond(4);
            } else if (st.haveQuestItem(MIONS_LETTER) )
                htmltext = "trader_mion_q0417_13.htm";
            else if (st.haveAnyQuestItems(BEAR_PIC,TARANTULA_PIC,BEAD_PARCEL,ROUTS_TP_SCROLL,SUCCUBUS_UNDIES) )
                htmltext = "trader_mion_q0417_14.htm";
        } else if (npcId == Shari) {
            if (st.getQuestItemsCount(CHARIS_AXE) > 0) {
                if (st.getInt("id") < 20)
                    htmltext = "trader_chali_q0417_01.htm";
                else
                    htmltext = "trader_chali_q0417_02.htm";
                st.takeItems(CHARIS_AXE, 1);
                st.giveItems(CHALIS_PAY);
                if (st.getInt("id") >= 50)
                    st.setCond(3);
                st.set("id", st.getInt("id") + 10);
            } else if (st.haveQuestItem(CHALIS_PAY))
                htmltext = "trader_chali_q0417_03.htm";
        } else if (npcId == Bronk) {
            if (st.haveQuestItem(BRONKS_INGOT)) {
                if (st.getInt("id") < 20)
                    htmltext = "head_blacksmith_bronk_q0417_01.htm";
                else
                    htmltext = "head_blacksmith_bronk_q0417_02.htm";
                st.takeItems(BRONKS_INGOT, 1);
                st.giveItems(BRONKS_PAY);
                if (st.getInt("id") >= 50)
                    st.setCond(3);
                st.set("id", st.getInt("id") + 10);
            } else if (st.haveQuestItem(BRONKS_PAY))
                htmltext = "head_blacksmith_bronk_q0417_03.htm";
        } else if (npcId == Zimenf) {
            if (st.getQuestItemsCount(ZIMENFS_POTION) == 1) {
                if (st.getInt("id") < 20)
                    htmltext = "zimenf_priest_of_earth_q0417_01.htm";
                else
                    htmltext = "zimenf_priest_of_earth_q0417_02.htm";
                st.takeItems(ZIMENFS_POTION, 1);
                st.giveItems(ZIMENFS_PAY);
                if (st.getInt("id") >= 50)
                    st.setCond(3);
                st.set("id", st.getInt("id") + 10);
            } else if (st.getQuestItemsCount(ZIMENFS_PAY) == 1)
                htmltext = "zimenf_priest_of_earth_q0417_03.htm";
        } else if (npcId == Toma) {
            if (st.getQuestItemsCount(MIONS_LETTER) == 1) {
                htmltext = "master_toma_q0417_01.htm";
                st.takeItems(MIONS_LETTER, 1);
                st.giveItems(BEAR_PIC);
                st.setCond(5);
                st.unset("id");
            } else if (st.haveQuestItem(BEAR_PIC) && st.getQuestItemsCount(HONEY_JAR) < 5)
                htmltext = "master_toma_q0417_02.htm";
            else if (st.getQuestItemsCount(BEAR_PIC) == 1 && st.getQuestItemsCount(HONEY_JAR) >= 5) {
                htmltext = "master_toma_q0417_03.htm";
                st.takeItems(HONEY_JAR, st.getQuestItemsCount(HONEY_JAR));
                st.takeItems(BEAR_PIC, 1);
                st.giveItems(TARANTULA_PIC);
                st.setCond(7);
            } else if (st.haveQuestItem(TARANTULA_PIC)  && st.getQuestItemsCount(BEAD) < 20)
                htmltext = "master_toma_q0417_04.htm";
            else if (st.haveQuestItem(TARANTULA_PIC)  && st.haveQuestItem(BEAD, 20)) {
                htmltext = "master_toma_q0417_05.htm";
                st.takeAllItems(BEAD,TARANTULA_PIC);
                st.giveItems(BEAD_PARCEL);
                st.setCond(9);
            } else if (st.haveQuestItem(BEAD_PARCEL))
                htmltext = "master_toma_q0417_06.htm";
            else if (st.getQuestItemsCount(ROUTS_TP_SCROLL) > 0 || st.getQuestItemsCount(SUCCUBUS_UNDIES) > 0)
                htmltext = "master_toma_q0417_07.htm";
        } else if (npcId == Raut) {
            if (st.getQuestItemsCount(BEAD_PARCEL) == 1)
                htmltext = "raut_q0417_01.htm";
            else if (st.getQuestItemsCount(ROUTS_TP_SCROLL) == 1)
                htmltext = "raut_q0417_04.htm";
            else if (st.haveQuestItem(SUCCUBUS_UNDIES) ) {
                htmltext = "raut_q0417_05.htm";
                st.takeItems(SUCCUBUS_UNDIES, 1);
                if (st.player.getClassId().occupation() == 0) {
                    st.giveItems(RING_OF_RAVEN);
                    if (!st.player.isVarSet("prof1")) {
                        st.player.setVar("prof1");
                        st.addExpAndSp(228064, 16455);
                        st.giveItems(ADENA_ID, 81900);
                    }
                }
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        } else if (npcId == Torai && st.haveQuestItem(ROUTS_TP_SCROLL))
            htmltext = "torai_q0417_01.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        MonsterInstance mob = (MonsterInstance) npc;
        boolean cond = st.getCond() > 0;
        if (npcId == HunterBear) {
            if (cond && st.getQuestItemsCount(BEAR_PIC) == 1 && st.getQuestItemsCount(HONEY_JAR) < 5 && Rnd.chance(20))
                st.addSpawn(HoneyBear);
        } else if (npcId == HoneyBear) {
            if (cond && st.getQuestItemsCount(BEAR_PIC) == 1 && st.getQuestItemsCount(HONEY_JAR) < 5)
                if (mob.isSpoiled()) {
                    st.giveItems(HONEY_JAR);
                    if (st.haveQuestItem(HONEY_JAR, 5)) {
                        st.playSound(SOUND_MIDDLE);
                        st.setCond(6);
                    } else
                        st.playSound(SOUND_ITEMGET);
                }
        } else if (npcId == HunterTarantula || npcId == PlunderTarantula)
            if (cond && st.haveQuestItem(TARANTULA_PIC) && st.getQuestItemsCount(BEAD) < 20)
                if (mob.isSpoiled())
                    if (Rnd.chance(50)) {
                        st.giveItems(BEAD);
                        if (st.getQuestItemsCount(BEAD) == 20) {
                            st.playSound(SOUND_MIDDLE);
                            st.setCond(8);
                        } else
                            st.playSound(SOUND_ITEMGET);
                    }
    }
}