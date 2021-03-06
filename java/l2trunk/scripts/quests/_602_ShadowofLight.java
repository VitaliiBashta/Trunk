package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _602_ShadowofLight extends Quest {
    //NPC
    private static final int ARGOS = 31683;
    //Quest Item
    private static final int EYE_OF_DARKNESS = 7189;
    //Bonus
    private static final int[][] REWARDS = {
            {
                    6699,
                    40000,
                    120000,
                    20000,
                    1,
                    19
            },
            {
                    6698,
                    60000,
                    110000,
                    15000,
                    20,
                    39
            },
            {
                    6700,
                    40000,
                    150000,
                    10000,
                    40,
                    49
            },
            {
                    0,
                    100000,
                    140000,
                    11250,
                    50,
                    100
            }
    };

    public _602_ShadowofLight() {
        super(true);

        addStartNpc(ARGOS);

        addKillId(21299,21304);

        addQuestItem(EYE_OF_DARKNESS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("eye_of_argos_q0602_0104.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("eye_of_argos_q0602_0201.htm".equalsIgnoreCase(event)) {
            st.takeItems(EYE_OF_DARKNESS);
            int random = Rnd.get(100) + 1;
            for (int[] REWARD : REWARDS)
                if (REWARD[4] <= random && random <= REWARD[5]) {
                    st.giveAdena(REWARD[1]);
                    st.addExpAndSp(REWARD[2], REWARD[3]);
                    if (REWARD[0] != 0)
                        st.giveItems(REWARD[0], 3, true);
                }
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int cond = 0;
        if (id != CREATED)
            cond = st.getCond();
        if (npcId == ARGOS)
            if (cond == 0)
                if (st.player.getLevel() < 68) {
                    htmltext = "eye_of_argos_q0602_0103.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "eye_of_argos_q0602_0101.htm";
            else if (cond == 1)
                htmltext = "eye_of_argos_q0602_0106.htm";
            else if (cond == 2 && st.getQuestItemsCount(EYE_OF_DARKNESS) == 100)
                htmltext = "eye_of_argos_q0602_0105.htm";
            else {
                htmltext = "eye_of_argos_q0602_0106.htm";
                st.setCond(1);
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            long count = st.getQuestItemsCount(EYE_OF_DARKNESS);
            if (count < 100 && Rnd.chance(npc.getNpcId() == 21299 ? 35 : 40)) {
                st.giveItems(EYE_OF_DARKNESS);
                if (count == 99) {
                    st.setCond(2);
                    st.playSound(SOUND_MIDDLE);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        }
    }
}
