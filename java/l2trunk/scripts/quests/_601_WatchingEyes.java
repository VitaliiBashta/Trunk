package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _601_WatchingEyes extends Quest {
    //NPC
    private static final int EYE_OF_ARGOS = 31683;
    //ITEMS
    private static final int PROOF_OF_AVENGER = 7188;
    //CHANCE
    private static final int DROP_CHANCE = 50;
    //MOBS
    private static final List<Integer> MOBS = List.of(
            21306, 21308, 21309, 21310, 21311);
    private static final int[][] REWARDS = {
            {
                    6699,
                    90000,
                    0,
                    19
            },
            {
                    6698,
                    80000,
                    20,
                    39
            },
            {
                    6700,
                    40000,
                    40,
                    49
            },
            {
                    0,
                    230000,
                    50,
                    100
            }
    };

    public _601_WatchingEyes() {
        super(true);

        addStartNpc(EYE_OF_ARGOS);

        addKillId(MOBS);

        addQuestItem(PROOF_OF_AVENGER);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("eye_of_argos_q0601_0104.htm".equalsIgnoreCase(event))
            if (st.player.getLevel() < 71) {
                htmltext = "eye_of_argos_q0601_0103.htm";
                st.exitCurrentQuest();
            } else {
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
            }
        else if ("eye_of_argos_q0601_0201.htm".equalsIgnoreCase(event)) {
            int random = Rnd.get(101);
            int i = 0;
            int item = 0;
            int adena = 0;
            while (i < REWARDS.length) {
                item = REWARDS[i][0];
                adena = REWARDS[i][1];
                if (REWARDS[i][2] <= random && random <= REWARDS[i][3])
                    break;
                i++;
            }
            st.giveAdena(adena);
            if (item != 0) {
                st.giveItems(item, 5, true);
                st.addExpAndSp(120000, 10000);
            }
            st.takeItems(PROOF_OF_AVENGER);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int cond = st.getCond();
        if (cond == 0)
            htmltext = "eye_of_argos_q0601_0101.htm";
        else if (cond == 1)
            htmltext = "eye_of_argos_q0601_0106.htm";
        else if (cond == 2 && st.haveQuestItem(PROOF_OF_AVENGER, 100))
            htmltext = "eye_of_argos_q0601_0105.htm";
        else {
            htmltext = "eye_of_argos_q0601_0202.htm";
            st.setCond(1);
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            long count = st.getQuestItemsCount(PROOF_OF_AVENGER);
            if (count < 100 && Rnd.chance(DROP_CHANCE)) {
                st.giveItems(PROOF_OF_AVENGER);
                if (count == 99) {
                    st.setCond(2);
                    st.playSound(SOUND_MIDDLE);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        }
    }
}