package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _632_NecromancersRequest extends Quest {
    //NPC
    private static final int WIZARD = 31522;
    //ITEMS
    private static final int V_HEART = 7542;
    private static final int Z_BRAIN = 7543;
    //REWARDS
    private static final int ADENA_AMOUNT = 120000;
    //MOBS
    private static final List<Integer> VAMPIRES = List.of(
            21568, 21573, 21582, 21585, 21586, 21587, 21588, 21589, 21590, 21591, 21592, 21593, 21594, 21595);
    private static final List<Integer> UNDEADS = List.of(
            21547, 21548, 21549, 21550, 21551, 21552, 21555, 21556, 21562, 21571, 21576, 21577, 21579);

    public _632_NecromancersRequest() {
        super(true);
        addStartNpc(WIZARD);

        addKillId(VAMPIRES);
        addKillId(UNDEADS);

        addQuestItem(V_HEART,
                Z_BRAIN);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "632_4":
                st.playSound(SOUND_FINISH);
                htmltext = "shadow_hardin_q0632_0204.htm";
                st.exitCurrentQuest();
                break;
            case "632_1":
                htmltext = "shadow_hardin_q0632_0104.htm";
                break;
            case "632_3":
                if (st.getCond() == 2)
                    if (st.getQuestItemsCount(V_HEART) > 199) {
                        st.takeItems(V_HEART, 200);
                        st.giveItems(ADENA_ID, ADENA_AMOUNT, true);
                        st.playSound(SOUND_FINISH);
                        st.setCond(1);
                        htmltext = "shadow_hardin_q0632_0202.htm";
                    }
                break;
            case "quest_accept":
                if (st.player.getLevel() > 62) {
                    htmltext = "shadow_hardin_q0632_0104.htm";
                    st.setCond(1);
                    st.start();
                    st.playSound(SOUND_ACCEPT);
                } else {
                    htmltext = "shadow_hardin_q0632_0103.htm";
                    st.exitCurrentQuest();
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 0)
            if (npcId == WIZARD)
                htmltext = "shadow_hardin_q0632_0101.htm";
        if (cond == 1)
            htmltext = "shadow_hardin_q0632_0202.htm";
        if (cond == 2)
            if (st.getQuestItemsCount(V_HEART) > 199)
                htmltext = "shadow_hardin_q0632_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (VAMPIRES.contains(npcId))
            if (st.getCond() < 2 && Rnd.chance(50)) {
                st.giveItems(V_HEART);
                if (st.getQuestItemsCount(V_HEART) > 199)
                    st.setCond(2);
            }
        st.rollAndGive(Z_BRAIN, 1, 33);
    }
}