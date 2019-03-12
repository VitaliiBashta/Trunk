package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Drop;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.HashMap;
import java.util.Map;

public final class _327_ReclaimTheLand extends Quest {
    // NPCs
    private static final int Piotur = 30597;
    private static final int Iris = 30034;
    private static final int Asha = 30313;
    // Quest items
    private static final int TUREK_DOGTAG = 1846;
    private static final int TUREK_MEDALLION = 1847;
    private static final int CLAY_URN_FRAGMENT = 1848;
    private static final int BRASS_TRINKET_PIECE = 1849;
    private static final int BRONZE_MIRROR_PIECE = 1850;
    private static final int JADE_NECKLACE_BEAD = 1851;
    private static final int ANCIENT_CLAY_URN = 1852;
    private static final int ANCIENT_BRASS_TIARA = 1853;
    private static final int ANCIENT_BRONZE_MIRROR = 1854;
    private static final int ANCIENT_JADE_NECKLACE = 1855;
    // Chances
    private static final int Exchange_Chance = 80;

    private static final Map<Integer, Drop> DROPLIST = new HashMap<>();
    private static final Map<Integer, Integer> EXP = new HashMap<>();

    public _327_ReclaimTheLand() {
        super(false);
        addStartNpc(Piotur);
        addTalkId(Iris, Asha);

        DROPLIST.put(20495, new Drop(1, 0xFFFF, 13).addItem(TUREK_MEDALLION));
        DROPLIST.put(20496, new Drop(1, 0xFFFF, 9).addItem(TUREK_DOGTAG));
        DROPLIST.put(20497, new Drop(1, 0xFFFF, 11).addItem(TUREK_MEDALLION));
        DROPLIST.put(20498, new Drop(1, 0xFFFF, 10).addItem(TUREK_DOGTAG));
        DROPLIST.put(20499, new Drop(1, 0xFFFF, 8).addItem(TUREK_DOGTAG));
        DROPLIST.put(20500, new Drop(1, 0xFFFF, 7).addItem(TUREK_DOGTAG));
        DROPLIST.put(20501, new Drop(1, 0xFFFF, 12).addItem(TUREK_MEDALLION));
        EXP.put(ANCIENT_CLAY_URN, 913);
        EXP.put(ANCIENT_BRASS_TIARA, 1065);
        EXP.put(ANCIENT_BRONZE_MIRROR, 1065);
        EXP.put(ANCIENT_JADE_NECKLACE, 1294);

        addKillId(DROPLIST.keySet());

        addQuestItem(TUREK_MEDALLION, TUREK_DOGTAG);
    }

    private static boolean expReward(QuestState st, int item_id) {
        Integer exp = EXP.get(item_id);
        if (exp == null)
            exp = 182;
        long exp_reward = st.getQuestItemsCount(item_id * exp);
        if (exp_reward == 0)
            return false;
        st.takeItems(item_id);
        st.addExpAndSp(exp_reward, 0);
        st.playSound(SOUND_MIDDLE);
        return true;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        if ("piotur_q0327_03.htm".equalsIgnoreCase(event) && state == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("piotur_q0327_06.htm".equalsIgnoreCase(event) && state == STARTED) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        } else if ("trader_acellopy_q0327_02.htm".equalsIgnoreCase(event) && state == STARTED && st.haveQuestItem(CLAY_URN_FRAGMENT, 5)) {
            st.takeItems(CLAY_URN_FRAGMENT, 5);
            if (!Rnd.chance(Exchange_Chance))
                return "trader_acellopy_q0327_10.htm";
            st.giveItems(ANCIENT_CLAY_URN);
            st.playSound(SOUND_MIDDLE);
            return "trader_acellopy_q0327_03.htm";
        } else if ("trader_acellopy_q0327_04.htm".equalsIgnoreCase(event) && state == STARTED && st.getQuestItemsCount(BRASS_TRINKET_PIECE) >= 5) {
            st.takeItems(BRASS_TRINKET_PIECE, 5);
            if (!Rnd.chance(Exchange_Chance))
                return "trader_acellopy_q0327_10.htm";
            st.giveItems(ANCIENT_BRASS_TIARA, 1);
            st.playSound(SOUND_MIDDLE);
            return "trader_acellopy_q0327_05.htm";
        } else if ("trader_acellopy_q0327_06.htm".equalsIgnoreCase(event) && state == STARTED && st.getQuestItemsCount(BRONZE_MIRROR_PIECE) >= 5) {
            st.takeItems(BRONZE_MIRROR_PIECE, 5);
            if (!Rnd.chance(Exchange_Chance))
                return "trader_acellopy_q0327_10.htm";
            st.giveItems(ANCIENT_BRONZE_MIRROR);
            st.playSound(SOUND_MIDDLE);
            return "trader_acellopy_q0327_07.htm";
        } else if ("trader_acellopy_q0327_08.htm".equalsIgnoreCase(event) && state == STARTED && st.getQuestItemsCount(JADE_NECKLACE_BEAD) >= 5) {
            st.takeItems(JADE_NECKLACE_BEAD, 5);
            if (!Rnd.chance(Exchange_Chance))
                return "trader_acellopy_q0327_09.htm";
            st.giveItems(ANCIENT_JADE_NECKLACE);
            st.playSound(SOUND_MIDDLE);
            return "trader_acellopy_q0327_07.htm";
        } else if ("iris_q0327_03.htm".equalsIgnoreCase(event) && state == STARTED) {
            if (!expReward(st, CLAY_URN_FRAGMENT))
                return "iris_q0327_02.htm";
        } else if ("iris_q0327_04.htm".equalsIgnoreCase(event) && state == STARTED) {
            if (!expReward(st, BRASS_TRINKET_PIECE))
                return "iris_q0327_02.htm";
        } else if ("iris_q0327_05.htm".equalsIgnoreCase(event) && state == STARTED) {
            if (!expReward(st, BRONZE_MIRROR_PIECE))
                return "iris_q0327_02.htm";
        } else if ("iris_q0327_06.htm".equalsIgnoreCase(event) && state == STARTED) {
            if (!expReward(st, JADE_NECKLACE_BEAD))
                return "iris_q0327_02.htm";
        } else if ("iris_q0327_07.htm".equalsIgnoreCase(event) && state == STARTED)
            if (!(expReward(st, ANCIENT_CLAY_URN) || expReward(st, ANCIENT_BRASS_TIARA) || expReward(st, ANCIENT_BRONZE_MIRROR) || expReward(st, ANCIENT_JADE_NECKLACE)))
                return "iris_q0327_02.htm";

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int state = st.getState();
        int npcId = npc.getNpcId();
        if (state == CREATED) {
            if (npcId != Piotur)
                return "noquest";
            if (st.player.getLevel() < 25) {
                st.exitCurrentQuest();
                return "piotur_q0327_01.htm";
            }
            st.setCond(0);
            return "piotur_q0327_02.htm";
        }

        if (state != STARTED)
            return "noquest";

        if (npcId == Piotur) {
            long reward = st.getQuestItemsCount(TUREK_DOGTAG) * 40 + st.getQuestItemsCount(TUREK_MEDALLION) * 50;
            if (reward == 0)
                return "piotur_q0327_04.htm";
            st.takeAllItems(TUREK_DOGTAG, TUREK_MEDALLION);
            st.giveAdena(reward);
            st.playSound(SOUND_MIDDLE);
            return "piotur_q0327_05.htm";
        }
        if (npcId == Iris)
            return "iris_q0327_01.htm";
        if (npcId == Asha)
            return "trader_acellopy_q0327_01.htm";

        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;
        int npcId = npc.getNpcId();

        Drop drop = DROPLIST.get(npcId);
        if (drop == null)
            return;

        if (Rnd.chance(drop.chance)) {
            int n = Rnd.get(100);
            if (n < 25)
                qs.giveItems(CLAY_URN_FRAGMENT);
            else if (n < 50)
                qs.giveItems(BRASS_TRINKET_PIECE);
            else if (n < 75)
                qs.giveItems(BRONZE_MIRROR_PIECE);
            else
                qs.giveItems(JADE_NECKLACE_BEAD);
        }
        qs.giveItems(drop.itemList.get(0));
        qs.playSound(SOUND_ITEMGET);
    }
}