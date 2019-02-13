package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _691_MatrasSuspiciousRequest extends Quest {
    // NPC
    private final static int MATRAS = 32245;
    private final static int LABYRINTH_CAPTAIN = 22368;

    // Items
    private final static int RED_STONE = 10372;
    private final static int RED_STONES_COUNT = 744;
    private final static int DYNASTIC_ESSENCE_II = 10413;

    public _691_MatrasSuspiciousRequest() {
        super(true);

        addStartNpc(MATRAS);
        addQuestItem(RED_STONE);
        addKillId(22363,
                22364,
                22365,
                22366,
                22367,
                LABYRINTH_CAPTAIN,
                22369,
                22370,
                22371,
                22372);
    }

    @Override
    public String onEvent(String event, QuestState qs, NpcInstance npc) {
        if (event.equalsIgnoreCase("32245-03.htm")) {
            qs.setCond(1);
            qs.setState(STARTED);
            qs.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("32245-05.htm")) {
            qs.takeItems(RED_STONE, RED_STONES_COUNT);
            qs.giveItems(DYNASTIC_ESSENCE_II, 1, false);
            qs.playSound(SOUND_FINISH);
            qs.exitCurrentQuest(true);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() >= 76)
                htmltext = "32245-01.htm";
            else
                htmltext = "32245-00.htm";
            st.exitCurrentQuest(true);
        } else if (st.haveQuestItem(RED_STONE, RED_STONES_COUNT)) {
            htmltext = "32245-04.htm";
        } else {
            htmltext = "32245-03.htm";
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        st.rollAndGive(RED_STONE, 1, 1, RED_STONES_COUNT, npc.getNpcId() == LABYRINTH_CAPTAIN ? 50 : 30);
    }
}