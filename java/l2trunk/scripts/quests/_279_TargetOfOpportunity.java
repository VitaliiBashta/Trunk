package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.Map;

public final class _279_TargetOfOpportunity extends Quest {
    private static final int Jerian = 32302;
    private static final Map<Integer, Integer> rewards = Map.of(
            22373, 15517,
            22374, 15518,
            22375, 15519,
            22376, 15520);


    public _279_TargetOfOpportunity() {
        super(PARTY_ALL);
        addStartNpc(Jerian);
        addKillId(rewards.keySet());
        addQuestItem(rewards.values());
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("jerian_q279_04.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("jerian_q279_07.htm".equalsIgnoreCase(event)) {
            st.takeItems(rewards.values());
            st.giveItems(15515);
            st.giveItems(15516);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Jerian) {
            if (cond == 0) {
                if (st.player.getLevel() >= 82)
                    htmltext = "jerian_q279_01.htm";
                else {
                    htmltext = "jerian_q279_00.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "jerian_q279_05.htm";
            else if (cond == 2)
                htmltext = "jerian_q279_06.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && Rnd.chance(15)) {
            if (rewards.containsKey(npcId))
                st.giveItemIfNotHave(rewards.get(npcId));

            if (st.haveAllQuestItems(rewards.values()))
                st.setCond(2);
        }
    }
}