package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;
import java.util.Map;

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
    private final Map<Integer, Integer> MobsTable = Map.of(
            TRACKER_SKELETON, 20,
            TRACKER_SKELETON_LEADER, 30,
            SKELETON_SCOUT, 20,
            SKELETON_BOWMAN, 20,
            RUIN_SPARTOI, 80,
            RAGING_SPARTOI, 80);

    private final List<Integer> STOLEN_ITEM = List.of(
            STOLEN_JEWELRY_ID,
            STOLEN_TOMES_ID,
            STOLEN_RING_ID,
            STOLEN_NECKLACE_ID);

    public _403_PathToRogue() {
        super(false);

        addStartNpc(BEZIQUE);

        addTalkId(NETI);

        addKillId(CATS_EYE_BANDIT);
        addAttackId(CATS_EYE_BANDIT);

        addKillId(MobsTable.keySet());
        addAttackId(MobsTable.keySet());

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
        switch (event) {
            case "30379_2":
                if (st.player.getClassId() == ClassId.fighter) {
                    if (st.player.getLevel() >= 18) {
                        if (st.haveQuestItem(BEZIQUES_RECOMMENDATION_ID))
                            htmltext = "captain_bezique_q0403_04.htm";
                        else
                            htmltext = "captain_bezique_q0403_05.htm";
                    } else
                        htmltext = "captain_bezique_q0403_03.htm";
                } else if (st.player.getClassId() == ClassId.rogue)
                    htmltext = "captain_bezique_q0403_02a.htm";
                else
                    htmltext = "captain_bezique_q0403_02.htm";
                break;
            case "1":
                st.setCond(1);
                st.start();
                st.giveItems(BEZIQUES_LETTER_ID);
                htmltext = "captain_bezique_q0403_06.htm";
                st.playSound(SOUND_ACCEPT);
                break;
            case "30425_1":
                st.takeItems(BEZIQUES_LETTER_ID, 1);
                st.giveItemIfNotHave(NETIS_BOW_ID);
                st.giveItemIfNotHave(NETIS_DAGGER_ID);
                st.setCond(2);
                htmltext = "neti_q0403_05.htm";
                break;
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
                st.takeItems(STOLEN_ITEM);
                if (st.player.getClassId().occupation() == 0) {
                    st.giveItems(BEZIQUES_RECOMMENDATION_ID);
                    if (!st.player.isVarSet("prof1")) {
                        st.player.setVar("prof1");
                        st.addExpAndSp(228064, 16455);
                        st.giveItems(ADENA_ID, 81900);
                    }
                }
                st.exitCurrentQuest();
                st.playSound(SOUND_FINISH);
            } else if (cond == 1 && st.getQuestItemsCount(HORSESHOE_OF_LIGHT_ID) < 1 && st.haveQuestItem(BEZIQUES_LETTER_ID))
                htmltext = "captain_bezique_q0403_07.htm";
            else if (cond == 4 && st.haveQuestItem(HORSESHOE_OF_LIGHT_ID)) {
                htmltext = "captain_bezique_q0403_08.htm";
                st.takeItems(HORSESHOE_OF_LIGHT_ID, 1);
                st.giveItems(WANTED_BILL_ID);
                st.setCond(5);
            } else if (cond > 1 && st.haveQuestItem(NETIS_BOW_ID) && st.haveQuestItem(NETIS_DAGGER_ID) && st.getQuestItemsCount(WANTED_BILL_ID) < 1)
                htmltext = "captain_bezique_q0403_10.htm";
            else if (cond == 5 && st.haveQuestItem(WANTED_BILL_ID))
                htmltext = "captain_bezique_q0403_11.htm";
            else
                htmltext = "captain_bezique_q0403_01.htm";
        } else if (npcId == NETI)
            if (cond == 1 && st.haveQuestItem(BEZIQUES_LETTER_ID))
                htmltext = "neti_q0403_01.htm";
            else if (cond == 2 | cond == 3 && st.getQuestItemsCount(SPATOIS_BONES_ID) < 10) {
                htmltext = "neti_q0403_06.htm";
                st.setCond(2);
            } else if (cond == 3 && st.haveQuestItem(SPATOIS_BONES_ID, 9)) {
                htmltext = "neti_q0403_07.htm";
                st.takeItems(SPATOIS_BONES_ID);
                st.giveItems(HORSESHOE_OF_LIGHT_ID);
                st.setCond(4);
            } else if (cond == 4 && st.haveQuestItem(HORSESHOE_OF_LIGHT_ID))
                htmltext = "neti_q0403_08.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (st.isSet("netis_cond") && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == NETIS_BOW_ID || st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == NETIS_DAGGER_ID) {
            Functions.npcSay(npc, "I must do something about this shameful incident...");
            switch (cond) {
                case 2:
                    MobsTable.forEach((k, v) -> {
                        if (npcId == k && Rnd.chance(v) && !st.haveQuestItem(SPATOIS_BONES_ID, 10)) {
                            st.giveItems(SPATOIS_BONES_ID);
                            if (st.haveQuestItem(SPATOIS_BONES_ID, 10)) {
                                st.playSound(SOUND_MIDDLE);
                                st.setCond(3);
                            } else
                                st.playSound(SOUND_ITEMGET);
                        }
                    });
                    break;
                case 5:
                    if (npcId == CATS_EYE_BANDIT)
                        if (st.haveQuestItem(WANTED_BILL_ID)) {
                            int n = Rnd.get(4);
                            if (!st.haveQuestItem(STOLEN_ITEM.get(n))) {
                                st.giveItems(STOLEN_ITEM.get(n));
                                if (st.haveAllQuestItems(STOLEN_ITEM)) {
                                    st.playSound(SOUND_MIDDLE);
                                    st.setCond(6);
                                } else st.playSound(SOUND_ITEMGET);
                            }
                        }
                    break;
            }
        }
    }

    @Override
    public void onAttack(NpcInstance npc, QuestState st) {
        if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) != NETIS_BOW_ID && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) != NETIS_DAGGER_ID)
            st.unset("netis_cond");
        else if (!st.isSet("netis_cond")) {
            st.set("netis_cond");
            Functions.npcSay(npc, "You childish fool, do you think you can catch me?");
        }
    }
}