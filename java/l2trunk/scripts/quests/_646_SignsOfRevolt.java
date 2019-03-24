package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.stream.IntStream;

public final class _646_SignsOfRevolt extends Quest {
    // NPCs
    private static final int TORRANT = 32016;
    // Mobs
    private static final int Guardian_of_the_Ghost_Town = 22047;
    private static final int Varangkas_Succubus = 22049;
    // items
    private static final int Steel = 1880;
    private static final int Coarse_Bone_Powder = 1881;
    private static final int Leather = 1882;
    // Quest items
    private static final int CURSED_DOLL = 8087;
    // Chances
    private static final int CURSED_DOLL_Chance = 75;

    public _646_SignsOfRevolt() {
        addStartNpc(TORRANT);
        addKillId(IntStream.rangeClosed(22029, 22044).toArray());
        addKillId(Guardian_of_the_Ghost_Town, Varangkas_Succubus);
        addQuestItem(CURSED_DOLL);
    }

    private static String doReward(QuestState st, int reward_id, int count) {
        if (st.getQuestItemsCount(CURSED_DOLL) < 180)
            return null;
        st.takeItems(CURSED_DOLL);
        st.giveItems(reward_id, count, true);
        st.playSound(SOUND_FINISH);
        st.exitCurrentQuest();
        return "torant_q0646_0202.htm";
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        if (state == CREATED)
            if ("torant_q0646_0103.htm".equalsIgnoreCase(event)) {
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
            } else if ("reward_adena".equalsIgnoreCase(event))
                return doReward(st, ADENA_ID, 21600);
            else if ("reward_cbp".equalsIgnoreCase(event))
                return doReward(st, Coarse_Bone_Powder, 12);
            else if ("reward_steel".equalsIgnoreCase(event))
                return doReward(st, Steel, 9);
            else if ("reward_leather".equalsIgnoreCase(event))
                return doReward(st, Leather, 20);

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != TORRANT)
            return htmltext;
        int state = st.getState();

        if (state == CREATED) {
            if (st.player.getLevel() < 40) {
                htmltext = "torant_q0646_0102.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "torant_q0646_0101.htm";
                st.setCond(0);
            }
        } else if (state == STARTED)
            htmltext = st.haveQuestItem(CURSED_DOLL, 180) ? "torant_q0646_0105.htm" : "torant_q0646_0106.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        Player player = qs.getRandomPartyMember(STARTED, Config.ALT_PARTY_DISTRIBUTION_RANGE);
        if (player == null)
            return;
        QuestState st = player.getQuestState(qs.quest);

        long CURSED_DOLL_COUNT = st.getQuestItemsCount(CURSED_DOLL);
        if (CURSED_DOLL_COUNT < 180 && Rnd.chance(CURSED_DOLL_Chance)) {
            st.giveItems(CURSED_DOLL);
            if (CURSED_DOLL_COUNT == 179) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }
}