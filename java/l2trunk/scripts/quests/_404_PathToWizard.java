package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import static l2trunk.gameserver.model.base.ClassId.mage;
import static l2trunk.gameserver.model.base.ClassId.wizard;

public final class _404_PathToWizard extends Quest {
    //npc
    private final int PARINA = 30391;
    private final int EARTH_SNAKE = 30409;
    private final int WASTELAND_LIZARDMAN = 30410;
    private final int FLAME_SALAMANDER = 30411;
    private final int WIND_SYLPH = 30412;
    private final int WATER_UNDINE = 30413;
    //mobs
    private final int RED_BEAR = 20021;
    private final int RATMAN_WARRIOR = 20359;
    private final int WATER_SEER = 27030;
    //items
    private final int MAP_OF_LUSTER_ID = 1280;
    private final int KEY_OF_FLAME_ID = 1281;
    private final int FLAME_EARING_ID = 1282;
    private final int BROKEN_BRONZE_MIRROR_ID = 1283;
    private final int WIND_FEATHER_ID = 1284;
    private final int WIND_BANGEL_ID = 1285;
    private final int RAMAS_DIARY_ID = 1286;
    private final int SPARKLE_PEBBLE_ID = 1287;
    private final int WATER_NECKLACE_ID = 1288;
    private final int RUST_GOLD_COIN_ID = 1289;
    private final int RED_SOIL_ID = 1290;
    private final int EARTH_RING_ID = 1291;
    private final int BEAD_OF_SEASON_ID = 1292;

    public _404_PathToWizard() {

        addStartNpc(PARINA);

        addTalkId(EARTH_SNAKE, WASTELAND_LIZARDMAN, FLAME_SALAMANDER, WIND_SYLPH, WATER_UNDINE);

        addKillId(RED_BEAR, RATMAN_WARRIOR, WATER_SEER);

        addQuestItem(KEY_OF_FLAME_ID,
                MAP_OF_LUSTER_ID,
                WIND_FEATHER_ID,
                BROKEN_BRONZE_MIRROR_ID,
                SPARKLE_PEBBLE_ID,
                RAMAS_DIARY_ID,
                RED_SOIL_ID,
                RUST_GOLD_COIN_ID,
                FLAME_EARING_ID,
                WIND_BANGEL_ID,
                WATER_NECKLACE_ID,
                EARTH_RING_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("1")) {
            if (st.player.getClassId() == mage) {
                if (st.player.getLevel() >= 18) {
                    if (st.haveQuestItem(BEAD_OF_SEASON_ID))
                        htmltext = "parina_q0404_03.htm";
                    else {
                        htmltext = "parina_q0404_08.htm";
                        st.setCond(1);
                        st.start();
                        st.playSound(SOUND_ACCEPT);
                    }
                } else
                    htmltext = "parina_q0404_02.htm";
            } else if (st.player.getClassId() == wizard)
                htmltext = "parina_q0404_02a.htm";
            else
                htmltext = "parina_q0404_01.htm";
        } else if (event.equals("30410_1"))
            if (!st.haveQuestItem(WIND_FEATHER_ID)) {
                htmltext = "lizardman_of_wasteland_q0404_03.htm";
                st.giveItems(WIND_FEATHER_ID);
                st.setCond(6);
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == PARINA) {
            if (cond == 0)
                htmltext = "parina_q0404_04.htm";
            else if (cond > 0 && !st.haveAllQuestItems(FLAME_EARING_ID, WIND_BANGEL_ID, WATER_NECKLACE_ID, EARTH_RING_ID))
                htmltext = "parina_q0404_05.htm";
            else if (cond > 0 && st.haveAllQuestItems(FLAME_EARING_ID, WIND_BANGEL_ID, WATER_NECKLACE_ID, EARTH_RING_ID)) {
                htmltext = "parina_q0404_06.htm";
                st.takeAllItems(FLAME_EARING_ID, WIND_BANGEL_ID, WATER_NECKLACE_ID, EARTH_RING_ID);
                if (st.player.getClassId().occupation() == 0) {
                    st.giveItemIfNotHave(BEAD_OF_SEASON_ID);
                    if (!st.player.isVarSet("prof1")) {
                        st.player.setVar("prof1");
                        st.addExpAndSp(295862, 18274);
                        st.giveAdena(81900);
                    }
                }
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            }
        } else if (npcId == FLAME_SALAMANDER) {
            if (cond > 0 && st.getQuestItemsCount(MAP_OF_LUSTER_ID) < 1 && st.getQuestItemsCount(FLAME_EARING_ID) < 1) {
                st.giveItems(MAP_OF_LUSTER_ID);
                htmltext = "flame_salamander_q0404_01.htm";
                st.setCond(2);
            } else if (cond > 0 && st.haveQuestItem(MAP_OF_LUSTER_ID) && !st.haveQuestItem(KEY_OF_FLAME_ID))
                htmltext = "flame_salamander_q0404_02.htm";
            else if (cond == 3 && st.haveAllQuestItems(MAP_OF_LUSTER_ID, KEY_OF_FLAME_ID)) {
                st.takeAllItems(KEY_OF_FLAME_ID, MAP_OF_LUSTER_ID);
                st.giveItemIfNotHave(FLAME_EARING_ID);
                htmltext = "flame_salamander_q0404_03.htm";
                st.setCond(4);
            } else if (cond > 0 && st.haveQuestItem(FLAME_EARING_ID))
                htmltext = "flame_salamander_q0404_04.htm";
        } else if (npcId == WIND_SYLPH) {
            if (cond == 4 && st.haveQuestItem(FLAME_EARING_ID) && !st.haveQuestItem(BROKEN_BRONZE_MIRROR_ID) && !st.haveQuestItem(WIND_BANGEL_ID)) {
                st.giveItems(BROKEN_BRONZE_MIRROR_ID);
                htmltext = "wind_sylph_q0404_01.htm";
                st.setCond(5);
            } else if (cond > 0 && st.haveQuestItem(BROKEN_BRONZE_MIRROR_ID) && !st.haveQuestItem(WIND_FEATHER_ID))
                htmltext = "wind_sylph_q0404_02.htm";
            else if (cond > 0 && st.haveAllQuestItems(BROKEN_BRONZE_MIRROR_ID, WIND_FEATHER_ID)) {
                st.takeAllItems(WIND_FEATHER_ID, BROKEN_BRONZE_MIRROR_ID);
                st.giveItemIfNotHave(WIND_BANGEL_ID);
                htmltext = "wind_sylph_q0404_03.htm";
                st.setCond(7);
            } else if (cond > 0 && st.haveQuestItem(WIND_BANGEL_ID))
                htmltext = "wind_sylph_q0404_04.htm";
        } else if (npcId == WASTELAND_LIZARDMAN) {
            if (cond > 0 && st.haveQuestItem(BROKEN_BRONZE_MIRROR_ID) && !st.haveQuestItem(WIND_FEATHER_ID))
                htmltext = "lizardman_of_wasteland_q0404_01.htm";
            else if (cond > 0 && st.haveAllQuestItems(BROKEN_BRONZE_MIRROR_ID, WIND_FEATHER_ID))
                htmltext = "lizardman_of_wasteland_q0404_04.htm";
        } else if (npcId == WATER_UNDINE) {
            if (cond == 7 && st.haveQuestItem(WIND_BANGEL_ID) && st.getQuestItemsCount(RAMAS_DIARY_ID) < 1 && st.getQuestItemsCount(WATER_NECKLACE_ID) < 1) {
                st.giveItems(RAMAS_DIARY_ID);
                htmltext = "water_undine_q0404_01.htm";
                st.setCond(8);
            } else if (cond > 0 && st.haveQuestItem(RAMAS_DIARY_ID) && st.getQuestItemsCount(SPARKLE_PEBBLE_ID) < 2)
                htmltext = "water_undine_q0404_02.htm";
            else if (cond == 9 && st.haveQuestItem(RAMAS_DIARY_ID)  && st.haveQuestItem(SPARKLE_PEBBLE_ID,2)) {
                st.takeAllItems(SPARKLE_PEBBLE_ID, RAMAS_DIARY_ID);
                st.giveItemIfNotHave(WATER_NECKLACE_ID);
                htmltext = "water_undine_q0404_03.htm";
                st.setCond(10);
            } else if (cond > 0 && st.haveQuestItem(WATER_NECKLACE_ID))
                htmltext = "water_undine_q0404_04.htm";
        } else if (npcId == EARTH_SNAKE)
            if (cond > 0 && st.haveQuestItem(WATER_NECKLACE_ID) && !st.haveQuestItem(RUST_GOLD_COIN_ID) && st.getQuestItemsCount(EARTH_RING_ID) < 1) {
                st.giveItems(RUST_GOLD_COIN_ID);
                htmltext = "earth_snake_q0404_01.htm";
                st.setCond(11);
            } else if (cond > 0 && st.haveQuestItem(RUST_GOLD_COIN_ID) && !st.haveQuestItem(RED_SOIL_ID))
                htmltext = "earth_snake_q0404_02.htm";
            else if (cond == 12 && st.haveAllQuestItems(RUST_GOLD_COIN_ID, RED_SOIL_ID)) {
                st.takeAllItems(RED_SOIL_ID,RUST_GOLD_COIN_ID);
                st.giveItemIfNotHave(EARTH_RING_ID);
                htmltext = "earth_snake_q0404_04.htm";
                st.setCond(13);
            } else if (cond > 0 && st.haveQuestItem(EARTH_RING_ID))
                htmltext = "earth_snake_q0404_04.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == RATMAN_WARRIOR) {
            if (cond == 2) {
                st.giveItems(KEY_OF_FLAME_ID);
                st.playSound(SOUND_MIDDLE);
                st.setCond(3);
            }
        } else if (npcId == WATER_SEER) {
            if (cond == 8 && st.getQuestItemsCount(SPARKLE_PEBBLE_ID) < 2) {
                st.giveItems(SPARKLE_PEBBLE_ID);
                if (st.haveQuestItem(SPARKLE_PEBBLE_ID, 2)) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(9);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == RED_BEAR)
            if (cond == 11) {
                st.giveItems(RED_SOIL_ID);
                st.playSound(SOUND_MIDDLE);
                st.setCond(12);
            }
    }
}
