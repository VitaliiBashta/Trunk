package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import static l2trunk.gameserver.model.base.ClassId.*;

public final class _224_TestOfSagittarius extends Quest {
    private static final int BERNARDS_INTRODUCTION_ID = 3294;
    private static final int LETTER_OF_HAMIL3_ID = 3297;
    private static final int HUNTERS_RUNE2_ID = 3299;
    private static final int MARK_OF_SAGITTARIUS_ID = 3293;
    private static final int CRESCENT_MOON_BOW_ID = 3028;
    private static final int TALISMAN_OF_KADESH_ID = 3300;
    private static final int BLOOD_OF_LIZARDMAN_ID = 3306;
    private static final int LETTER_OF_HAMIL1_ID = 3295;
    private static final int LETTER_OF_HAMIL2_ID = 3296;
    private static final int HUNTERS_RUNE1_ID = 3298;
    private static final int TALISMAN_OF_SNAKE_ID = 3301;
    private static final int MITHRIL_CLIP_ID = 3302;
    private static final int STAKATO_CHITIN_ID = 3303;
    private static final int ST_BOWSTRING_ID = 3304;
    private static final int MANASHENS_HORN_ID = 3305;
    private static final int WOODEN_ARROW_ID = 17;
    private static final int RewardExp = 447444;
    private static final int RewardSP = 30704;
    private static final int RewardAdena = 80903;

    public _224_TestOfSagittarius() {
        addStartNpc(30702);
        addTalkId(30514, 30626, 30653, 30702, 30717);

        addKillId(20230, 20232, 20233, 20234, 20269, 20270, 27090, 20551, 20563, 20577, 20578,
                20579, 20580, 20581, 20582, 20079, 20080, 20081, 20082, 20084, 20086, 20089, 20090);

        addQuestItem(HUNTERS_RUNE2_ID,
                CRESCENT_MOON_BOW_ID,
                TALISMAN_OF_KADESH_ID,
                BLOOD_OF_LIZARDMAN_ID,
                BERNARDS_INTRODUCTION_ID,
                HUNTERS_RUNE1_ID,
                LETTER_OF_HAMIL1_ID,
                TALISMAN_OF_SNAKE_ID,
                LETTER_OF_HAMIL2_ID,
                LETTER_OF_HAMIL3_ID,
                MITHRIL_CLIP_ID,
                STAKATO_CHITIN_ID,
                ST_BOWSTRING_ID,
                MANASHENS_HORN_ID);
    }

    private static void checkState(QuestState st, int item) {
        if (st.getCond() == 10 && st.getQuestItemsCount(item) == 0 && Rnd.chance(10)) {
            st.giveItems(item);
            if (st.haveAllQuestItems(MITHRIL_CLIP_ID,MANASHENS_HORN_ID,STAKATO_CHITIN_ID) ) {
                st.setCond(11);
                st.playSound(SOUND_MIDDLE);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "1":
                htmltext = "30702-04.htm";
                st.setCond(1);
                st.start();
                if (!st.player.isVarSet("dd3")) {
                    st.giveItems(7562, 96);
                    st.player.setVar("dd3");
                }
                st.playSound(SOUND_ACCEPT);
                st.giveItems(BERNARDS_INTRODUCTION_ID);
                break;
            case "30626_1":
                htmltext = "30626-02.htm";
                break;
            case "30626_2":
                htmltext = "30626-03.htm";
                st.takeItems(BERNARDS_INTRODUCTION_ID);
                st.giveItems(LETTER_OF_HAMIL1_ID);
                st.setCond(2);
                break;
            case "30626_3":
                htmltext = "30626-06.htm";
                break;
            case "30626_4":
                htmltext = "30626-07.htm";
                st.takeItems(HUNTERS_RUNE1_ID);
                st.giveItems(LETTER_OF_HAMIL2_ID);
                st.setCond(5);
                break;
            case "30653_1":
                htmltext = "30653-02.htm";
                st.takeItems(LETTER_OF_HAMIL1_ID);
                st.setCond(3);
                break;
            case "30514_1":
                htmltext = "30514-02.htm";
                st.takeItems(LETTER_OF_HAMIL2_ID);
                st.setCond(6);
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (st.haveQuestItem(MARK_OF_SAGITTARIUS_ID)) {
            st.exitCurrentQuest();
            return "completed";
        }
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        if (id == CREATED) {
            st.start();
            st.setCond(0);
            st.unset("id");
        }
        if (npcId == 30702 && st.getCond() == 0) {
            if (st.player.getClassId() == rogue
                    || st.player.getClassId() == elvenScout
                    || st.player.getClassId() == assassin) {
                if (st.player.getLevel() >= 39)
                    htmltext = "30702-03.htm";
                else {
                    htmltext = "30702-01.htm";
                    st.exitCurrentQuest();
                }
            } else {
                htmltext = "30702-02.htm";
                st.exitCurrentQuest();
            }
        } else if (npcId == 30702 && st.getCond() == 1 && st.haveQuestItem(BERNARDS_INTRODUCTION_ID))
            htmltext = "30702-05.htm";
        else if (npcId == 30626 && st.getCond() == 1 && st.haveQuestItem(BERNARDS_INTRODUCTION_ID))
            htmltext = "30626-01.htm";
        else if (npcId == 30626 && st.getCond() == 2 && st.haveQuestItem(LETTER_OF_HAMIL1_ID))
            htmltext = "30626-04.htm";
        else if (npcId == 30626 && st.getCond() == 4 && st.getQuestItemsCount(HUNTERS_RUNE1_ID) == 10)
            htmltext = "30626-05.htm";
        else if (npcId == 30626 && st.getCond() == 5 && st.haveQuestItem(LETTER_OF_HAMIL2_ID))
            htmltext = "30626-08.htm";
        else if (npcId == 30626 && st.getCond() == 8) {
            htmltext = "30626-09.htm";
            st.giveItems(LETTER_OF_HAMIL3_ID, 1);
            st.setCond(9);
        } else if (npcId == 30626 && st.getCond() == 9 && st.haveQuestItem(LETTER_OF_HAMIL3_ID))
            htmltext = "30626-10.htm";
        else if (npcId == 30626 && st.getCond() == 12 && st.haveQuestItem(CRESCENT_MOON_BOW_ID)) {
            htmltext = "30626-11.htm";
            st.setCond(13);
        } else if (npcId == 30626 && st.getCond() == 13)
            htmltext = "30626-12.htm";
        else if (npcId == 30626 && st.getCond() == 14 && st.haveQuestItem(TALISMAN_OF_KADESH_ID)) {
            htmltext = "30626-13.htm";
            st.takeAllItems(CRESCENT_MOON_BOW_ID, TALISMAN_OF_KADESH_ID, BLOOD_OF_LIZARDMAN_ID);
            st.giveItems(MARK_OF_SAGITTARIUS_ID);
            st.addExpAndSp(RewardExp, RewardSP);
            st.giveAdena(RewardAdena);
            st.playSound(SOUND_FINISH);
            st.unset("cond");
            st.finish();
        } else if (npcId == 30653 && st.getCond() == 2 && st.haveQuestItem(LETTER_OF_HAMIL1_ID))
            htmltext = "30653-01.htm";
        else if (npcId == 30653 && st.getCond() == 3)
            htmltext = "30653-03.htm";
        else if (npcId == 30514 && st.getCond() == 5 && st.haveQuestItem(LETTER_OF_HAMIL2_ID))
            htmltext = "30514-01.htm";
        else if (npcId == 30514 && st.getCond() == 6)
            htmltext = "30514-03.htm";
        else if (npcId == 30514 && st.getCond() == 7 && st.haveQuestItem(TALISMAN_OF_SNAKE_ID)) {
            htmltext = "30514-04.htm";
            st.takeItems(TALISMAN_OF_SNAKE_ID);
            st.setCond(8);
        } else if (npcId == 30514 && st.getCond() == 8)
            htmltext = "30514-05.htm";
        else if (npcId == 30717 && st.getCond() == 9 && st.haveQuestItem(LETTER_OF_HAMIL3_ID)) {
            htmltext = "30717-01.htm";
            st.takeItems(LETTER_OF_HAMIL3_ID);
            st.setCond(10);
        } else if (npcId == 30717 && st.getCond() == 10)
            htmltext = "30717-03.htm";
        else if (npcId == 30717 && st.getCond() == 12)
            htmltext = "30717-04.htm";
        else if (npcId == 30717 && st.getCond() == 11 && st.haveAllQuestItems(STAKATO_CHITIN_ID, MITHRIL_CLIP_ID, ST_BOWSTRING_ID, MANASHENS_HORN_ID)) {
            htmltext = "30717-02.htm";
            st.takeAllItems(MITHRIL_CLIP_ID, STAKATO_CHITIN_ID, ST_BOWSTRING_ID, MANASHENS_HORN_ID);
            st.giveItems(CRESCENT_MOON_BOW_ID);
            st.giveItems(WOODEN_ARROW_ID, 10);
            st.setCond(12);
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == 20079 || npcId == 20080 || npcId == 20081 || npcId == 20084 || npcId == 20086 || npcId == 20089 || npcId == 20090) {
            if (st.getCond() == 3 && st.getQuestItemsCount(HUNTERS_RUNE1_ID) < 10 && Rnd.chance(50)) {
                st.giveItems(HUNTERS_RUNE1_ID);
                if (st.getQuestItemsCount(HUNTERS_RUNE1_ID) == 10) {
                    st.setCond(4);
                    st.playSound(SOUND_MIDDLE);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == 20269 || npcId == 20270) {
            if (st.getCond() == 6 && st.getQuestItemsCount(HUNTERS_RUNE2_ID) < 10 && Rnd.chance(50)) {
                st.giveItems(HUNTERS_RUNE2_ID);
                if (st.haveQuestItem(HUNTERS_RUNE2_ID, 10)) {
                    st.takeItems(HUNTERS_RUNE2_ID);
                    st.giveItems(TALISMAN_OF_SNAKE_ID);
                    st.setCond(7);
                    st.playSound(SOUND_MIDDLE);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == 20230 || npcId == 20232 || npcId == 20234) {
            if (st.haveAllQuestItems(MITHRIL_CLIP_ID, MANASHENS_HORN_ID, STAKATO_CHITIN_ID)) {
                st.setCond(11);
            }
        } else if (npcId == 20563) {
            checkState(st, MANASHENS_HORN_ID);

        } else if (npcId == 20233) {
            checkState(st, STAKATO_CHITIN_ID);
        } else if (npcId == 20551) {
            checkState(st, MITHRIL_CLIP_ID);

        } else if (npcId == 20577 || npcId == 20578 || npcId == 20579 || npcId == 20580 || npcId == 20581 || npcId == 20582) {
            if (st.getCond() == 13)
                if (Rnd.chance((st.getQuestItemsCount(BLOOD_OF_LIZARDMAN_ID) - 120) * 5)) {
                    st.addSpawn(27090);
                    st.takeItems(BLOOD_OF_LIZARDMAN_ID);
                    st.playSound(SOUND_BEFORE_BATTLE);
                } else {
                    st.giveItems(BLOOD_OF_LIZARDMAN_ID);
                    st.playSound(SOUND_ITEMGET);
                }
        } else if (npcId == 27090)
            if (st.getCond() == 13 && st.getQuestItemsCount(TALISMAN_OF_KADESH_ID) == 0)
                if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == CRESCENT_MOON_BOW_ID) {
                    st.giveItems(TALISMAN_OF_KADESH_ID);
                    st.setCond(14);
                    st.playSound(SOUND_MIDDLE);
                } else
                    st.addSpawn(27090);
    }
}