package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _690_JudesRequest extends Quest {
    // NPC's
    private static final int JUDE = 32356;
    // ITEM's
    private static final int EVIL_WEAPON = 10327;
    // MOB's
    private static final int Evil = 22399;
    // Chance
    private static final int EVIL_WEAPON_CHANCE = 30;
    // Reward Recipe's
    private static final List<Integer> recipes = List.of(
            10373, 10374, 10375, 10376, 10377, 10378, 10379, 10380, 10381);
    // Reward Piece's
    private static final List<Integer> pieces = List.of(
    10397,10398,10399,10400,10401,10402, 10403,10404,10405);

    public _690_JudesRequest() {
        super(true);

        addStartNpc(JUDE);
        addTalkId(JUDE);
        addKillId(Evil);
        addQuestItem(EVIL_WEAPON);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("jude_q0690_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    private void giveReward(QuestState st, int item_id) {
        st.giveItems(item_id);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() >= 78)
                htmltext = "jude_q0690_01.htm";
            else
                htmltext = "jude_q0690_02.htm";
            st.exitCurrentQuest();
        } else if (cond == 1 && st.getQuestItemsCount(EVIL_WEAPON) >= 5) {
            if (st.getQuestItemsCount(EVIL_WEAPON) >= 100) {
                st.giveItems(Rnd.get(recipes));

                st.playSound(SOUND_FINISH);
                st.takeItems(EVIL_WEAPON, 100);
                htmltext = "jude_q0690_07.htm";

            } else if (st.getQuestItemsCount(EVIL_WEAPON) > 4 && st.getQuestItemsCount(EVIL_WEAPON) < 100) {
                    st.giveItems(Rnd.get(pieces));
                st.playSound(SOUND_FINISH);
                st.takeItems(EVIL_WEAPON, 5);
                htmltext = "jude_q0690_09.htm";
            }
        } else
            htmltext = "jude_q0690_10.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        Player player = st.getRandomPartyMember(STARTED, Config.ALT_PARTY_DISTRIBUTION_RANGE);

        if (st.getState() != STARTED)
            return;

        if (player != null) {
            QuestState sts = player.getQuestState(st.quest);
            if (sts != null && Rnd.chance(EVIL_WEAPON_CHANCE)) {
                st.giveItems(EVIL_WEAPON);
                st.playSound(SOUND_ITEMGET);
            }
        }
    }
}