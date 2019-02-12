package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _409_PathToOracle extends Quest {
    //npc
    private final int MANUEL = 30293;
    private final int ALLANA = 30424;
    private final int PERRIN = 30428;
    //mobs
    private final int LIZARDMAN_WARRIOR = 27032;
    private final int LIZARDMAN_SCOUT = 27033;
    private final int LIZARDMAN = 27034;
    private final int TAMIL = 27035;
    //items
    private final int CRYSTAL_MEDALLION_ID = 1231;
    private final int MONEY_OF_SWINDLER_ID = 1232;
    private final int DAIRY_OF_ALLANA_ID = 1233;
    private final int LIZARD_CAPTAIN_ORDER_ID = 1234;
    private final int LEAF_OF_ORACLE_ID = 1235;
    private final int HALF_OF_DAIRY_ID = 1236;
    private final int TAMATOS_NECKLACE_ID = 1275;

    public _409_PathToOracle() {
        super(false);

        addStartNpc(MANUEL);

        addTalkId(ALLANA);
        addTalkId(PERRIN);

        addKillId(LIZARDMAN_WARRIOR);
        addKillId(LIZARDMAN_SCOUT);
        addKillId(LIZARDMAN);
        addKillId(TAMIL);

        addQuestItem(MONEY_OF_SWINDLER_ID,
                DAIRY_OF_ALLANA_ID,
                LIZARD_CAPTAIN_ORDER_ID,
                CRYSTAL_MEDALLION_ID,
                HALF_OF_DAIRY_ID,
                TAMATOS_NECKLACE_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        switch (event) {
            case "1":
                if (st.player.getClassId().id != 0x19) {
                    if (st.player.getClassId().id == 0x1d)
                        return  "father_manuell_q0409_02a.htm";
                    else
                        return  "father_manuell_q0409_02.htm";
                } else if (st.player.getLevel() < 18)
                   return  "father_manuell_q0409_03.htm";
                else if (st.getQuestItemsCount(LEAF_OF_ORACLE_ID) > 0)
                    return  "father_manuell_q0409_04.htm";
                else {
                    st.setCond(1);
                    st.setState(STARTED);
                    st.playSound(SOUND_ACCEPT);
                    st.giveItems(CRYSTAL_MEDALLION_ID);
                    return  "father_manuell_q0409_05.htm";
                }
            case "allana_q0409_08.htm":
                st.addSpawn(LIZARDMAN_WARRIOR);
                st.addSpawn(LIZARDMAN_SCOUT);
                st.addSpawn(LIZARDMAN);
                st.setCond(2);
                return event;
            case "30424_1":
                return  "";
            case "30428_1":
                return  "perrin_q0409_02.htm";
            case "30428_2":
                return  "perrin_q0409_03.htm";
            case "30428_3":
                st.addSpawn(TAMIL);
                return event;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == MANUEL) {
            if (cond < 1)
                htmltext = "father_manuell_q0409_01.htm";
            else if (st.haveQuestItem(CRYSTAL_MEDALLION_ID) )
                if (st.getQuestItemsCount(MONEY_OF_SWINDLER_ID) < 1 && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) < 1 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) < 1 && st.getQuestItemsCount(HALF_OF_DAIRY_ID) < 1)
                    htmltext = "father_manuell_q0409_09.htm";
                else if (st.getQuestItemsCount(MONEY_OF_SWINDLER_ID) > 0 && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) > 0 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) > 0 && st.getQuestItemsCount(HALF_OF_DAIRY_ID) < 1) {
                    htmltext = "father_manuell_q0409_08.htm";
                    st.takeItems(MONEY_OF_SWINDLER_ID, 1);
                    st.takeItems(DAIRY_OF_ALLANA_ID);
                    st.takeItems(LIZARD_CAPTAIN_ORDER_ID);
                    st.takeItems(CRYSTAL_MEDALLION_ID);
                    if (st.player.getClassId().occupation() == 0) {
                        st.giveItems(LEAF_OF_ORACLE_ID);
                        if (!st.player.isVarSet("prof1")) {
                            st.player.setVar("prof1", 1);
                            st.addExpAndSp(228064, 16455);
                            st.giveItems(ADENA_ID, 81900);
                        }
                    }
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest(true);
                } else
                    htmltext = "father_manuell_q0409_07.htm";
        } else if (npcId == ALLANA) {
            if (st.getQuestItemsCount(CRYSTAL_MEDALLION_ID) > 0)
                if (st.getQuestItemsCount(MONEY_OF_SWINDLER_ID) < 1 && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) < 1 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) < 1 && st.getQuestItemsCount(HALF_OF_DAIRY_ID) < 1) {
                    if (cond > 2)
                        htmltext = "allana_q0409_05.htm";
                    else
                        htmltext = "allana_q0409_01.htm";
                } else if (st.getQuestItemsCount(MONEY_OF_SWINDLER_ID) < 1 && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) < 1 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) > 0 && st.getQuestItemsCount(HALF_OF_DAIRY_ID) < 1) {
                    htmltext = "allana_q0409_02.htm";
                    st.giveItems(HALF_OF_DAIRY_ID);
                    st.setCond(4);
                } else if (st.getQuestItemsCount(MONEY_OF_SWINDLER_ID) < 1 && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) < 1 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) > 0 && st.getQuestItemsCount(HALF_OF_DAIRY_ID) > 0) {
                    if (st.haveQuestItem(TAMATOS_NECKLACE_ID)) {
                        htmltext = "allana_q0409_03.htm";
                    } else {
                        htmltext = "allana_q0409_06.htm";
                    }
                } else if (st.haveQuestItem(MONEY_OF_SWINDLER_ID) && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) < 1 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) > 0 && st.getQuestItemsCount(HALF_OF_DAIRY_ID) > 0) {
                    htmltext = "allana_q0409_04.htm";
                    st.takeItems(HALF_OF_DAIRY_ID);
                    st.giveItems(DAIRY_OF_ALLANA_ID);
                    st.setCond(7);
                } else if (st.haveQuestItem(MONEY_OF_SWINDLER_ID)  && st.haveQuestItem(LIZARD_CAPTAIN_ORDER_ID)  && st.getQuestItemsCount(HALF_OF_DAIRY_ID) < 1 && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) > 0)
                    htmltext = "allana_q0409_05.htm";
        } else if (npcId == PERRIN)
            if (st.haveQuestItem(CRYSTAL_MEDALLION_ID)  && st.haveQuestItem(LIZARD_CAPTAIN_ORDER_ID) )
                if (st.haveQuestItem(TAMATOS_NECKLACE_ID)) {
                    htmltext = "perrin_q0409_04.htm";
                    st.takeItems(TAMATOS_NECKLACE_ID);
                    st.giveItems(MONEY_OF_SWINDLER_ID);
                    st.setCond(6);
                } else if (st.haveQuestItem(MONEY_OF_SWINDLER_ID) )
                    htmltext = "perrin_q0409_05.htm";
                else if (cond > 4)
                    htmltext = "perrin_q0409_06.htm";
                else
                    htmltext = "perrin_q0409_01.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == LIZARDMAN_WARRIOR | npcId == LIZARDMAN_SCOUT | npcId == LIZARDMAN) {
            if (cond == 2 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) < 1) {
                st.giveItems(LIZARD_CAPTAIN_ORDER_ID);
                st.playSound(SOUND_MIDDLE);
                st.setCond(3);
            }
        } else if (npcId == TAMIL)
            if (cond == 4 && st.getQuestItemsCount(TAMATOS_NECKLACE_ID) < 1) {
                st.giveItems(TAMATOS_NECKLACE_ID);
                st.playSound(SOUND_MIDDLE);
                st.setCond(5);
            }
    }
}