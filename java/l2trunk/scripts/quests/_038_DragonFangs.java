package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _038_DragonFangs extends Quest {
    //MOBS
    private static final int LANGK_LIZARDMAN_LIEUTENANT = 20357;
    private static final int LANGK_LIZARDMAN_SENTINEL = 21100;
    private static final int LANGK_LIZARDMAN_LEADER = 20356;
    private static final int LANGK_LIZARDMAN_SHAMAN = 21101;
    //CHANCE FOR DROP
    private static final int CHANCE_FOR_QUEST_ITEMS = 100; // 100%???
    //REWARD
    private static final int BONE_HELMET = 45;
    private static final int ASSAULT_BOOTS = 1125;
    private static final int BLUE_BUCKSKIN_BOOTS = 1123;
    //NPC
    private final int ROHMER = 30344;
    private final int LUIS = 30386;
    private final int IRIS = 30034;
    //QUEST ITEM
    private final int FEATHER_ORNAMENT = 7173;
    private final int TOOTH_OF_TOTEM = 7174;
    private final int LETTER_OF_IRIS = 7176;
    private final int LETTER_OF_ROHMER = 7177;
    private final int TOOTH_OF_DRAGON = 7175;

    public _038_DragonFangs() {
        addStartNpc(LUIS);

        addTalkId(IRIS,ROHMER);

        addKillId(LANGK_LIZARDMAN_LEADER,LANGK_LIZARDMAN_SHAMAN,LANGK_LIZARDMAN_SENTINEL,LANGK_LIZARDMAN_LIEUTENANT);

        addQuestItem(TOOTH_OF_TOTEM, LETTER_OF_IRIS, LETTER_OF_ROHMER, TOOTH_OF_DRAGON, FEATHER_ORNAMENT);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        if (event.equals("guard_luis_q0038_0104.htm"))
            if (cond == 0) {
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
            }
        if (event.equals("guard_luis_q0038_0201.htm"))
            if (cond == 2) {
                st.setCond(3);
                st.takeItems(FEATHER_ORNAMENT, 100);
                st.giveItems(TOOTH_OF_TOTEM);
                st.playSound(SOUND_MIDDLE);
            }
        if (event.equals("iris_q0038_0301.htm"))
            if (cond == 3) {
                st.setCond(4);
                st.takeItems(TOOTH_OF_TOTEM);
                st.giveItems(LETTER_OF_IRIS);
                st.playSound(SOUND_MIDDLE);
            }
        if (event.equals("magister_roh_q0038_0401.htm"))
            if (cond == 4) {
                st.setCond(5);
                st.takeItems(LETTER_OF_IRIS);
                st.giveItems(LETTER_OF_ROHMER);
                st.playSound(SOUND_MIDDLE);
            }
        if (event.equals("iris_q0038_0501.htm"))
            if (cond == 5) {
                st.setCond(6);
                st.takeItems(LETTER_OF_ROHMER);
                st.playSound(SOUND_MIDDLE);
            }
        if (event.equals("iris_q0038_0601.htm"))
            if (cond == 7) {
                st.takeItems(TOOTH_OF_DRAGON, 50);
                int luck = Rnd.get(3);
                if (luck == 0) {
                    st.giveItems(BLUE_BUCKSKIN_BOOTS);
                    st.giveItems(ADENA_ID, 1500);
                }
                if (luck == 1) {
                    st.giveItems(BONE_HELMET);
                    st.giveItems(ADENA_ID, 5200);
                }
                if (luck == 2) {
                    st.giveItems(ASSAULT_BOOTS);
                    st.giveItems(ADENA_ID, 1500);
                }
                st.addExpAndSp(435117, 23977);
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == LUIS && cond == 0)
            if (st.player.getLevel() < 19) {
                htmltext = "guard_luis_q0038_0102.htm";
                st.exitCurrentQuest();
            } else htmltext = "guard_luis_q0038_0101.htm";
        if (npcId == LUIS && cond == 1)
            htmltext = "guard_luis_q0038_0202.htm";
        if (npcId == LUIS && cond == 2 && st.getQuestItemsCount(FEATHER_ORNAMENT) == 100)
            htmltext = "guard_luis_q0038_0105.htm";
        if (npcId == LUIS && cond == 3)
            htmltext = "guard_luis_q0038_0203.htm";
        if (npcId == IRIS && cond == 3 && st.getQuestItemsCount(TOOTH_OF_TOTEM) == 1)
            htmltext = "iris_q0038_0201.htm";
        if (npcId == IRIS && cond == 4)
            htmltext = "iris_q0038_0303.htm";
        if (npcId == IRIS && cond == 5 && st.getQuestItemsCount(LETTER_OF_ROHMER) == 1)
            htmltext = "iris_q0038_0401.htm";
        if (npcId == IRIS && cond == 6)
            htmltext = "iris_q0038_0602.htm";
        if (npcId == IRIS && cond == 7 && st.getQuestItemsCount(TOOTH_OF_DRAGON) == 50)
            htmltext = "iris_q0038_0503.htm";
        if (npcId == ROHMER && cond == 4 && st.getQuestItemsCount(LETTER_OF_IRIS) == 1)
            htmltext = "magister_roh_q0038_0301.htm";
        if (npcId == ROHMER && cond == 5)
            htmltext = "magister_roh_q0038_0403.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        boolean chance = Rnd.chance(CHANCE_FOR_QUEST_ITEMS);
        int cond = st.getCond();
        if (npcId == 20357 || npcId == 21100)
            if (cond == 1 && chance && st.getQuestItemsCount(FEATHER_ORNAMENT) < 100) {
                st.giveItems(FEATHER_ORNAMENT);
                if (st.getQuestItemsCount(FEATHER_ORNAMENT) == 100) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(2);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        if (npcId == 20356 || npcId == 21101)
            if (cond == 6 && chance && st.getQuestItemsCount(TOOTH_OF_DRAGON) < 50) {
                st.giveItems(TOOTH_OF_DRAGON, 1);
                if (st.getQuestItemsCount(TOOTH_OF_DRAGON) == 50) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(7);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
    }
}