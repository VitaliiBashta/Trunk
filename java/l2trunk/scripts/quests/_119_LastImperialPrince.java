package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _119_LastImperialPrince extends Quest {
    // NPC
    private static final int SPIRIT = 31453; // Nameless Spirit
    private static final int DEVORIN = 32009; // Devorin

    // ITEM
    private static final int BROOCH = 7262; // Antique Brooch

    // REWARD
    private static final int AMOUNT = 150292;

    public _119_LastImperialPrince() {
        addStartNpc(SPIRIT);
        addTalkId(DEVORIN);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("31453-4.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("32009-2.htm".equalsIgnoreCase(event)) {
            if (!st.haveQuestItem(BROOCH) ) {
                htmltext = "noquest";
                st.exitCurrentQuest();
            }
        } else if ("32009-3.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("31453-7.htm".equalsIgnoreCase(event)) {
            st.giveAdena(AMOUNT);
            st.addExpAndSp(902439, 90067);
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        // confirm that quest can be executed.
        if (st.player.getLevel() < 74) {
            htmltext = "<html><body>Quest for characters level 74 and above.</body></html>";
            st.exitCurrentQuest();
            return htmltext;
        } else if (!st.haveQuestItem(BROOCH)) {
            htmltext = "noquest";
            st.exitCurrentQuest();
            return htmltext;
        }

        if (npcId == SPIRIT) {
            if (cond == 0)
                return "31453-1.htm";
            else if (cond == 2)
                return "31453-5.htm";
            else
                return "noquest";
        } else if (npcId == DEVORIN && cond == 1)
            htmltext = "32009-1.htm";
        return htmltext;
    }
}