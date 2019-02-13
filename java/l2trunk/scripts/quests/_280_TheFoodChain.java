package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _280_TheFoodChain extends Quest {
    // NPCs
    private static final int BIXON = 32175;
    // Mobs
    private static final int Young_Grey_Keltir = 22229;
    private static final int Grey_Keltir = 22230;
    private static final int Dominant_Grey_Keltir = 22231;
    private static final int Black_Wolf = 22232;
    private static final int Dominant_Black_Wolf = 22233;
    // Items
    private static final List<Integer> REWARDS = List.of(28, 35, 116);
    // Quest Items
    private static final int Grey_Keltir_Tooth = 9809;
    private static final int Black_Wolf_Tooth = 9810;
    // Chances
    private static final int Grey_Keltir_Tooth_Chance = 90;
    private static final int Black_Wolf_Tooth_Chance = 70;

    public _280_TheFoodChain() {
        super(false);
        addStartNpc(BIXON);
        addKillId(Young_Grey_Keltir);
        addKillId(Grey_Keltir);
        addKillId(Dominant_Grey_Keltir);
        addKillId(Black_Wolf);
        addKillId(Dominant_Black_Wolf);
        addQuestItem(Grey_Keltir_Tooth);
        addQuestItem(Black_Wolf_Tooth);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        if ("jager_bixon_q0280_03.htm".equalsIgnoreCase(event) && _state == CREATED) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("jager_bixon_q0280_09.htm".equalsIgnoreCase(event) && _state == STARTED) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        } else if (_state == STARTED) {
            long Grey_Keltir_Tooth_count = st.getQuestItemsCount(Grey_Keltir_Tooth);
            long Black_Wolf_Tooth_count = st.getQuestItemsCount(Black_Wolf_Tooth);

            if ("ADENA".equalsIgnoreCase(event)) {
                st.takeItems(Grey_Keltir_Tooth);
                st.takeItems(Black_Wolf_Tooth);
                st.giveItems(ADENA_ID, (Grey_Keltir_Tooth_count + Black_Wolf_Tooth_count) * 2);
                st.playSound(SOUND_MIDDLE);
                return "jager_bixon_q0280_06.htm";
            } else if (event.equalsIgnoreCase("ITEM")) {
                if (Grey_Keltir_Tooth_count + Black_Wolf_Tooth_count < 25)
                    return "jager_bixon_q0280_10.htm";
                int take_Grey_Keltir_Tooth = 0;
                int take_Black_Wolf_Tooth = 0;
                while (take_Grey_Keltir_Tooth + take_Black_Wolf_Tooth < 25) {
                    if (Grey_Keltir_Tooth_count > 0) {
                        take_Grey_Keltir_Tooth++;
                        Grey_Keltir_Tooth_count--;
                    }
                    if (take_Grey_Keltir_Tooth + take_Black_Wolf_Tooth < 25 && Black_Wolf_Tooth_count > 0) {
                        take_Black_Wolf_Tooth++;
                        Black_Wolf_Tooth_count--;
                    }
                }

                if (take_Grey_Keltir_Tooth > 0)
                    st.takeItems(Grey_Keltir_Tooth, take_Grey_Keltir_Tooth);
                if (take_Black_Wolf_Tooth > 0)
                    st.takeItems(Black_Wolf_Tooth, take_Black_Wolf_Tooth);
                int rew_count = (int) st.getRateQuestsReward();
                while (rew_count > 0) {
                    rew_count--;
                    st.giveItems(Rnd.get(REWARDS));
                }
                st.playSound(SOUND_MIDDLE);
                return "jager_bixon_q0280_06.htm";
            }
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (npc.getNpcId() != BIXON)
            return "noquest";
        int _state = st.getState();
        if (_state == CREATED) {
            if (st.player.getLevel() >= 3) {
                st.setCond(0);
                return "jager_bixon_q0280_01.htm";
            }
            st.exitCurrentQuest(true);
            return "jager_bixon_q0280_02.htm";
        } else if (_state == STARTED)
            return st.getQuestItemsCount(Grey_Keltir_Tooth) > 0 || st.getQuestItemsCount(Black_Wolf_Tooth) > 0 ? "jager_bixon_q0280_05.htm" : "jager_bixon_q0280_04.htm";

        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;
        int npcId = npc.getNpcId();

        if ((npcId == Young_Grey_Keltir || npcId == Grey_Keltir || npcId == Dominant_Grey_Keltir) && Rnd.chance(Grey_Keltir_Tooth_Chance)) {
            qs.giveItems(Grey_Keltir_Tooth);
            qs.playSound(SOUND_ITEMGET);
        } else if ((npcId == Black_Wolf || npcId == Dominant_Black_Wolf) && Rnd.chance(Black_Wolf_Tooth_Chance)) {
            qs.giveItems(Black_Wolf_Tooth, 3);
            qs.playSound(SOUND_ITEMGET);
        }
    }
}