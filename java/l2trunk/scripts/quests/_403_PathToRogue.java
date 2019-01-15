package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;

public final class _403_PathToRogue extends Quest {
    //npc
    private final int BEZIQUE = 30379;
    private final int NETI = 30425;
    //mobs
    private final int TRACKER_SKELETON = 20035;
    private final int TRACKER_SKELETON_LEADER = 20042;
    private final int SKELETON_SCOUT = 20045;
    private final int SKELETON_BOWMAN = 20051;
    private final int RUIN_SPARTOI = 20054;
    private final int RAGING_SPARTOI = 20060;
    private final int CATS_EYE_BANDIT = 27038;
    //items
    private final int BEZIQUES_LETTER_ID = 1180;
    private final int SPATOIS_BONES_ID = 1183;
    private final int HORSESHOE_OF_LIGHT_ID = 1184;
    private final int WANTED_BILL_ID = 1185;
    private final int STOLEN_JEWELRY_ID = 1186;
    private final int STOLEN_TOMES_ID = 1187;
    private final int STOLEN_RING_ID = 1188;
    private final int STOLEN_NECKLACE_ID = 1189;
    private final int BEZIQUES_RECOMMENDATION_ID = 1190;
    private final int NETIS_BOW_ID = 1181;
    private final int NETIS_DAGGER_ID = 1182;
    //MobsTable {MOB_ID,CHANCE}
    private final int[][] MobsTable = {
            {
                    TRACKER_SKELETON,
                    2
            },
            {
                    TRACKER_SKELETON_LEADER,
                    3
            },
            {
                    SKELETON_SCOUT,
                    2
            },
            {
                    SKELETON_BOWMAN,
                    2
            },
            {
                    RUIN_SPARTOI,
                    8
            },
            {
                    RAGING_SPARTOI,
                    8
            }
    };

    private final int[] STOLEN_ITEM = {
            STOLEN_JEWELRY_ID,
            STOLEN_TOMES_ID,
            STOLEN_RING_ID,
            STOLEN_NECKLACE_ID
    };

    public _403_PathToRogue() {
        super(false);

        addStartNpc(BEZIQUE);

        addTalkId(NETI);

        addKillId(CATS_EYE_BANDIT);
        addAttackId(CATS_EYE_BANDIT);

        for (int[] element : MobsTable) {
            addKillId(element[0]);
            addAttackId(element[0]);
        }

        addQuestItem(STOLEN_ITEM);
        addQuestItem(NETIS_BOW_ID,
                NETIS_DAGGER_ID,
                WANTED_BILL_ID,
                HORSESHOE_OF_LIGHT_ID,
                BEZIQUES_LETTER_ID,
                SPATOIS_BONES_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("30379_2")) {
            if (st.getPlayer().getClassId().getId() == 0x00) {
                if (st.getPlayer().getLevel() >= 18) {
                    if (st.getQuestItemsCount(BEZIQUES_RECOMMENDATION_ID) > 0)
                        htmltext = "captain_bezique_q0403_04.htm";
                    else
                        htmltext = "captain_bezique_q0403_05.htm";
                } else
                    htmltext = "captain_bezique_q0403_03.htm";
            } else if (st.getPlayer().getClassId().getId() == 0x07)
                htmltext = "captain_bezique_q0403_02a.htm";
            else
                htmltext = "captain_bezique_q0403_02.htm";
        } else if (event.equalsIgnoreCase("1")) {
            st.setCond(1);
            st.setState(STARTED);
            st.giveItems(BEZIQUES_LETTER_ID, 1);
            htmltext = "captain_bezique_q0403_06.htm";
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("30425_1")) {
            st.takeItems(BEZIQUES_LETTER_ID, 1);
            if (st.getQuestItemsCount(NETIS_BOW_ID) < 1)
                st.giveItems(NETIS_BOW_ID, 1);
            if (st.getQuestItemsCount(NETIS_DAGGER_ID) < 1)
                st.giveItems(NETIS_DAGGER_ID, 1);
            st.setCond(2);
            htmltext = "neti_q0403_05.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == BEZIQUE) {
            if (cond == 6 && st.getQuestItemsCount(HORSESHOE_OF_LIGHT_ID) < 1 && st.getQuestItemsCount(STOLEN_JEWELRY_ID) + st.getQuestItemsCount(STOLEN_TOMES_ID) + st.getQuestItemsCount(STOLEN_RING_ID) + st.getQuestItemsCount(STOLEN_NECKLACE_ID) == 4) {
                htmltext = "captain_bezique_q0403_09.htm";
                st.takeItems(NETIS_BOW_ID, 1);
                st.takeItems(NETIS_DAGGER_ID, 1);
                st.takeItems(WANTED_BILL_ID, 1);
                for (int i : STOLEN_ITEM)
                    st.takeItems(i, -1);
                if (st.getPlayer().getClassId().getLevel() == 1) {
                    st.giveItems(BEZIQUES_RECOMMENDATION_ID, 1);
                    if (!st.getPlayer().getVarB("prof1")) {
                        st.getPlayer().setVar("prof1", "1", -1);
                        st.addExpAndSp(228064, 16455);
                        //FIXME [G1ta0] дать адены, только если первый чар на акке
                        st.giveItems(ADENA_ID, 81900);
                    }
                }
                st.exitCurrentQuest(true);
                st.playSound(SOUND_FINISH);
            } else if (cond == 1 && st.getQuestItemsCount(HORSESHOE_OF_LIGHT_ID) < 1 && st.getQuestItemsCount(BEZIQUES_LETTER_ID) > 0)
                htmltext = "captain_bezique_q0403_07.htm";
            else if (cond == 4 && st.getQuestItemsCount(HORSESHOE_OF_LIGHT_ID) > 0) {
                htmltext = "captain_bezique_q0403_08.htm";
                st.takeItems(HORSESHOE_OF_LIGHT_ID, 1);
                st.giveItems(WANTED_BILL_ID, 1);
                st.setCond(5);
            } else if (cond > 1 && st.getQuestItemsCount(NETIS_BOW_ID) > 0 && st.getQuestItemsCount(NETIS_DAGGER_ID) > 0 && st.getQuestItemsCount(WANTED_BILL_ID) < 1)
                htmltext = "captain_bezique_q0403_10.htm";
            else if (cond == 5 && st.getQuestItemsCount(WANTED_BILL_ID) > 0)
                htmltext = "captain_bezique_q0403_11.htm";
            else
                htmltext = "captain_bezique_q0403_01.htm";
        } else if (npcId == NETI)
            if (cond == 1 && st.getQuestItemsCount(BEZIQUES_LETTER_ID) > 0)
                htmltext = "neti_q0403_01.htm";
            else if (cond == 2 | cond == 3 && st.getQuestItemsCount(SPATOIS_BONES_ID) < 10) {
                htmltext = "neti_q0403_06.htm";
                st.setCond(2);
            } else if (cond == 3 && st.getQuestItemsCount(SPATOIS_BONES_ID) > 9) {
                htmltext = "neti_q0403_07.htm";
                st.takeItems(SPATOIS_BONES_ID, -1);
                st.giveItems(HORSESHOE_OF_LIGHT_ID, 1);
                st.setCond(4);
            } else if (cond == 4 && st.getQuestItemsCount(HORSESHOE_OF_LIGHT_ID) > 0)
                htmltext = "neti_q0403_08.htm";
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        int netis_cond = st.getInt("netis_cond");
        if (netis_cond == 1 && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == NETIS_BOW_ID || st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == NETIS_DAGGER_ID) {
            Functions.npcSay(npc, "I must do something about this shameful incident...");
            switch (cond) {
                case 2:
                    for (int[] element : MobsTable)
                        if (npcId == element[0] && Rnd.chance(10 * element[1]) && st.getQuestItemsCount(SPATOIS_BONES_ID) < 10) {
                            st.giveItems(SPATOIS_BONES_ID, 1);
                            if (st.getQuestItemsCount(SPATOIS_BONES_ID) == 10) {
                                st.playSound(SOUND_MIDDLE);
                                st.setCond(3);
                            } else
                                st.playSound(SOUND_ITEMGET);
                        }
                    break;
                case 5:
                    if (npcId == CATS_EYE_BANDIT)
                        if (st.getQuestItemsCount(WANTED_BILL_ID) > 0) {
                            int n = Rnd.get(4);
                            if (st.getQuestItemsCount(STOLEN_ITEM[n]) == 0) {
                                st.giveItems(STOLEN_ITEM[n], 1);
                                if (st.getQuestItemsCount(STOLEN_JEWELRY_ID) + st.getQuestItemsCount(STOLEN_TOMES_ID) + st.getQuestItemsCount(STOLEN_RING_ID) + st.getQuestItemsCount(STOLEN_NECKLACE_ID) < 4)
                                    st.playSound(SOUND_ITEMGET);
                                else {
                                    st.playSound(SOUND_MIDDLE);
                                    st.setCond(6);
                                }
                            }
                        }
                    break;
            }
        }
        return null;
    }

    @Override
    public String onAttack(NpcInstance npc, QuestState st) {
        int netis_cond = st.getInt("netis_cond");
        if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) != NETIS_BOW_ID && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) != NETIS_DAGGER_ID)
            st.set("netis_cond", "0");
        else if (netis_cond == 0) {
            st.set("netis_cond", "1");
            Functions.npcSay(npc, "You childish fool, do you think you can catch me?");
        }
        return null;
    }
}