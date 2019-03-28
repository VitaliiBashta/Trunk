package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _112_WalkOfFate extends Quest {
    //NPC
    private static final int Livina = 30572;
    private static final int Karuda = 32017;
    //items
    private static final int EnchantD = 956;

    public _112_WalkOfFate() {
        addStartNpc(Livina);
        addTalkId(Karuda);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("karuda_q0112_0201.htm".equalsIgnoreCase(event)) {
            st.addExpAndSp(112876, 5774);
            st.giveAdena( (long) (22308 + 6000 * (st.getRateQuestsReward() - 1)));
            st.giveItems(EnchantD);
            st.playSound(SOUND_FINISH);
            st.finish();
        } else if ("seer_livina_q0112_0104.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Livina) {
            if (cond == 0) {
                if (st.player.getLevel() >= 20)
                    htmltext = "seer_livina_q0112_0101.htm";
                else {
                    htmltext = "seer_livina_q0112_0103.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "seer_livina_q0112_0105.htm";
        } else if (npcId == Karuda)
            if (cond == 1)
                htmltext = "karuda_q0112_0101.htm";
        return htmltext;
    }
}
