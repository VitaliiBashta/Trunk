package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class _629_CleanUpTheSwampOfScreams extends Quest {
    //NPC
    private static final int CAPTAIN = 31553;
    private static final int CLAWS = 7250;
    private static final int COIN = 7251;

    //CHANCES
    private static final Map<Integer, Integer> CHANCE = Map.of(
            21508, 50,
            21509, 43,
            21510, 52,
            21511, 57,
            21512, 74,
            21513, 53,
            21514, 53,
            21515, 54,
            21516, 55,
            21517, 56);

    public _629_CleanUpTheSwampOfScreams() {
        addStartNpc(CAPTAIN);

        addKillId(IntStream.rangeClosed(21508, 21518).toArray());

        addQuestItem(CLAWS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("merc_cap_peace_q0629_0104.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("merc_cap_peace_q0629_0202.htm".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(CLAWS, 100)) {
                st.takeItems(CLAWS, 100);
                st.giveItems(COIN, 20, false);
            } else
                htmltext = "merc_cap_peace_q0629_0203.htm";
        } else if ("merc_cap_peace_q0629_0204.htm".equalsIgnoreCase(event)) {
            st.takeItems(CLAWS);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (st.haveAnyQuestItems(7246, 7247)) {
            if (cond == 0) {
                if (st.player.getLevel() >= 66)
                    htmltext = "merc_cap_peace_q0629_0101.htm";
                else {
                    htmltext = "merc_cap_peace_q0629_0103.htm";
                    st.exitCurrentQuest();
                }
            } else if (st.getState() == STARTED)
                if (st.haveQuestItem(CLAWS, 100))
                    htmltext = "merc_cap_peace_q0629_0105.htm";
                else
                    htmltext = "merc_cap_peace_q0629_0106.htm";
        } else {
            htmltext = "merc_cap_peace_q0629_0205.htm";
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() == STARTED)
            st.rollAndGive(CLAWS, 1, CHANCE.get(npc.getNpcId()));
    }
}