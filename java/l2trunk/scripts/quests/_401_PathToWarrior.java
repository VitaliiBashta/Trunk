package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _401_PathToWarrior extends Quest {
    private final int AURON = 30010;
    private final int SIMPLON = 30253;

    private final int TRACKER_SKELETON = 20035;
    private final int POISON_SPIDER = 20038;
    private final int TRACKER_SKELETON_LD = 20042;
    private final int ARACHNID_TRACKER = 20043;

    private final int EINS_LETTER_ID = 1138;
    private final int WARRIOR_GUILD_MARK_ID = 1139;
    private final int RUSTED_BRONZE_SWORD1_ID = 1140;
    private final int RUSTED_BRONZE_SWORD2_ID = 1141;
    private final int SIMPLONS_LETTER_ID = 1143;
    private final int POISON_SPIDER_LEG2_ID = 1144;
    private final int MEDALLION_OF_WARRIOR_ID = 1145;
    private final int RUSTED_BRONZE_SWORD3_ID = 1142;

    public _401_PathToWarrior() {
        addStartNpc(AURON);

        addTalkId(SIMPLON);

        addKillId(TRACKER_SKELETON,POISON_SPIDER,TRACKER_SKELETON_LD,ARACHNID_TRACKER);

        addQuestItem(SIMPLONS_LETTER_ID,
                RUSTED_BRONZE_SWORD2_ID,
                EINS_LETTER_ID,
                WARRIOR_GUILD_MARK_ID,
                RUSTED_BRONZE_SWORD1_ID,
                POISON_SPIDER_LEG2_ID,
                RUSTED_BRONZE_SWORD3_ID);

    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "401_1":
                if (st.player.getClassId() == ClassId.fighter) {
                    if (st.player.getLevel() >= 18) {
                        if (st.getQuestItemsCount(MEDALLION_OF_WARRIOR_ID) > 0)
                            htmltext = "ein_q0401_04.htm";
                        else
                            htmltext = "ein_q0401_05.htm";
                    } else
                        htmltext = "ein_q0401_02.htm";
                } else if (st.player.getClassId() == ClassId.warrior)
                    htmltext = "ein_q0401_02a.htm";
                else
                    htmltext = "ein_q0401_03.htm";
                break;
            case "401_2":
                htmltext = "ein_q0401_10.htm";
                break;
            case "401_3":
                htmltext = "ein_q0401_11.htm";
                st.takeItems(SIMPLONS_LETTER_ID, 1);
                st.takeItems(RUSTED_BRONZE_SWORD2_ID, 1);
                st.giveItems(RUSTED_BRONZE_SWORD3_ID);
                st.setCond(5);
                break;
            case "1":
                if (st.getQuestItemsCount(EINS_LETTER_ID) == 0) {
                    st.setCond(1);
                    st.start();
                    st.playSound(SOUND_ACCEPT);
                    st.giveItems(EINS_LETTER_ID);
                    htmltext = "ein_q0401_06.htm";
                }
                break;
            case "30253_1":
                htmltext = "trader_simplon_q0401_02.htm";
                st.takeItems(EINS_LETTER_ID, 1);
                st.giveItems(WARRIOR_GUILD_MARK_ID);
                st.setCond(2);
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (id == CREATED) {
            st.start();
            st.setCond(0);
        }
        if (npcId == AURON && cond == 0)
            htmltext = "ein_q0401_01.htm";
        else if (npcId == AURON && st.getQuestItemsCount(EINS_LETTER_ID) > 0)
            htmltext = "ein_q0401_07.htm";
        else if (npcId == AURON && st.getQuestItemsCount(WARRIOR_GUILD_MARK_ID) == 1)
            htmltext = "ein_q0401_08.htm";
        else if (npcId == SIMPLON && st.getQuestItemsCount(EINS_LETTER_ID) > 0)
            htmltext = "trader_simplon_q0401_01.htm";
        else if (npcId == SIMPLON && st.getQuestItemsCount(WARRIOR_GUILD_MARK_ID) > 0) {
            if (st.getQuestItemsCount(RUSTED_BRONZE_SWORD1_ID) < 1)
                htmltext = "trader_simplon_q0401_03.htm";
            else if (st.getQuestItemsCount(RUSTED_BRONZE_SWORD1_ID) < 10)
                htmltext = "trader_simplon_q0401_04.htm";
            else if (st.haveQuestItem(RUSTED_BRONZE_SWORD1_ID, 10)) {
                st.takeAllItems(WARRIOR_GUILD_MARK_ID,RUSTED_BRONZE_SWORD1_ID);
                st.giveItems(RUSTED_BRONZE_SWORD2_ID);
                st.giveItems(SIMPLONS_LETTER_ID);
                st.setCond(4);
                htmltext = "trader_simplon_q0401_05.htm";
            }
        } else if (npcId == SIMPLON && st.haveQuestItem(SIMPLONS_LETTER_ID) )
            htmltext = "trader_simplon_q0401_06.htm";
        else if (npcId == AURON && st.haveQuestItem(SIMPLONS_LETTER_ID)  && st.haveQuestItem(RUSTED_BRONZE_SWORD2_ID)  && st.getQuestItemsCount(WARRIOR_GUILD_MARK_ID) == 0 && st.getQuestItemsCount(EINS_LETTER_ID) == 0)
            htmltext = "ein_q0401_09.htm";
        else if (npcId == AURON && st.getQuestItemsCount(RUSTED_BRONZE_SWORD3_ID) > 0 && st.getQuestItemsCount(WARRIOR_GUILD_MARK_ID) == 0 && st.getQuestItemsCount(EINS_LETTER_ID) == 0)
            if (st.getQuestItemsCount(POISON_SPIDER_LEG2_ID) < 20)
                htmltext = "ein_q0401_12.htm";
            else if (st.haveQuestItem(POISON_SPIDER_LEG2_ID, 19)) {
                st.takeItems(POISON_SPIDER_LEG2_ID);
                st.takeItems(RUSTED_BRONZE_SWORD3_ID);
                if (st.player.getClassId().occupation() == 0) {
                    st.giveItems(MEDALLION_OF_WARRIOR_ID);
                    if (!st.player.isVarSet("prof1")) {
                        st.player.setVar("prof1");
                        st.addExpAndSp(228064, 16455);
                        st.giveAdena( 81900);
                    }
                }
                htmltext = "ein_q0401_13.htm";
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == TRACKER_SKELETON || npcId == TRACKER_SKELETON_LD) {
            if (cond == 2 && st.getQuestItemsCount(RUSTED_BRONZE_SWORD1_ID) < 10) {
                st.giveItems(RUSTED_BRONZE_SWORD1_ID);
                if (st.getQuestItemsCount(RUSTED_BRONZE_SWORD1_ID) == 10) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(3);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == ARACHNID_TRACKER || npcId == POISON_SPIDER)
            if (st.getQuestItemsCount(POISON_SPIDER_LEG2_ID) < 20 && st.getQuestItemsCount(RUSTED_BRONZE_SWORD3_ID) == 1 && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == RUSTED_BRONZE_SWORD3_ID) {
                st.giveItems(POISON_SPIDER_LEG2_ID);
                if (st.getQuestItemsCount(POISON_SPIDER_LEG2_ID) == 20) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(6);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
    }
}