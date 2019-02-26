package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _631_DeliciousTopChoiceMeat extends Quest {
    //NPC
    private static final int TUNATUN = 31537;
    //MOBS
    private static final List<Integer> MOB_LIST = List.of(
            18878, 18879, 18885, 18886, 18892, 18893, 18899, 18900);
    //CHANCE
    private static final int MEAT_DROP_CHANCE = 100;
    // Full Grown Kookabura/Cougar/Buffalo/Grendel
    //ITEMS
    private final int PRIME_MEAT = 15534;
    //REWARDS
    private final int[][] REWARDS = {
            {
                    10373,
                    1,
                    1
            },
            {
                    10374,
                    1,
                    1
            },
            {
                    10375,
                    1,
                    1
            },
            {
                    10376,
                    1,
                    1
            },
            {
                    10377,
                    1,
                    1
            },
            {
                    10378,
                    1,
                    1
            },
            {
                    10379,
                    1,
                    1
            },
            {
                    10380,
                    1,
                    1
            },
            {
                    10381,
                    1,
                    1
            },
            {
                    10397,
                    1,
                    9
            },
            {
                    10398,
                    1,
                    9
            },
            {
                    10399,
                    1,
                    9
            },
            {
                    10400,
                    1,
                    9
            },
            {
                    10401,
                    1,
                    9
            },
            {
                    10402,
                    1,
                    9
            },
            {
                    10403,
                    1,
                    9
            },
            {
                    10404,
                    1,
                    9
            },
            {
                    10405,
                    1,
                    9
            },
            {
                    15482,
                    1,
                    2
            },
            {
                    15483,
                    1,
                    2
            }
    };

    public _631_DeliciousTopChoiceMeat() {
        super(false);

        addStartNpc(TUNATUN);

        addTalkId(TUNATUN);
        addKillId(MOB_LIST);

        addQuestItem(PRIME_MEAT);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("beast_herder_tunatun_q0631_0104.htm")) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("beast_herder_tunatun_q0631_0201.htm".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(PRIME_MEAT, 120)) {
                st.takeItems(PRIME_MEAT);
                int[] reward = REWARDS[Rnd.get(0, REWARDS.length - 1)];
                int count = Rnd.get(reward[1], reward[2]);
                st.giveItems(reward[0], Math.round(count * st.getRateQuestsReward()));
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            } else {
                htmltext = "beast_herder_tunatun_q0631_0202.htm";
                st.setCond(1);
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond < 1) {
            if (st.player.getLevel() < 82) {
                htmltext = "beast_herder_tunatun_q0631_0103.htm";
                st.exitCurrentQuest();
            } else
                htmltext = "beast_herder_tunatun_q0631_0101.htm";
        } else if (cond == 1)
            htmltext = "beast_herder_tunatun_q0631_0106.htm";
        else if (cond == 2) {
            if (st.haveQuestItem(PRIME_MEAT, 120))
                htmltext = "beast_herder_tunatun_q0631_0105.htm";
            else {
                htmltext = "beast_herder_tunatun_q0631_0106.htm";
                st.setCond(1);
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && Rnd.chance(MEAT_DROP_CHANCE)) {
            st.giveItems(PRIME_MEAT, 1, true);
            if (st.haveQuestItem(PRIME_MEAT, 120)) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
            } else st.playSound(SOUND_ITEMGET);
        }
    }
}