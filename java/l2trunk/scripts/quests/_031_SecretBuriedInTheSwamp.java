package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _031_SecretBuriedInTheSwamp extends Quest {
    private static final int FORGOTTEN_MONUMENT_1 = 31661;
    private static final int FORGOTTEN_MONUMENT_2 = 31662;
    private static final int FORGOTTEN_MONUMENT_3 = 31663;
    private static final int FORGOTTEN_MONUMENT_4 = 31664;
    private static final int CORPSE_OF_DWARF = 31665;
    private final int ABERCROMBIE = 31555;
    private final int KRORINS_JOURNAL = 7252;

    public _031_SecretBuriedInTheSwamp() {
        addStartNpc(ABERCROMBIE);

        addTalkId(FORGOTTEN_MONUMENT_1, FORGOTTEN_MONUMENT_2, FORGOTTEN_MONUMENT_3, FORGOTTEN_MONUMENT_4, CORPSE_OF_DWARF);

        addQuestItem(KRORINS_JOURNAL);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        if (event.equals("31555-1.htm")) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("31665-1.htm") && cond == 1) {
            st.setCond(2);
            st.playSound(SOUND_ITEMGET);
            st.giveItems(KRORINS_JOURNAL);
        } else if (event.equals("31555-4.htm") && cond == 2)
            st.setCond(3);
        else if (event.equals("31661-1.htm") && cond == 3)
            st.setCond(4);
        else if (event.equals("31662-1.htm") && cond == 4)
            st.setCond(5);
        else if (event.equals("31663-1.htm") && cond == 5)
            st.setCond(6);
        else if (event.equals("31664-1.htm") && cond == 6) {
            st.setCond(7);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equals("31555-7.htm") && cond == 7) {
            st.takeItems(KRORINS_JOURNAL);
            st.addExpAndSp(490000, 45880);
            st.giveAdena(120000);
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == ABERCROMBIE) {
            if (cond == 0) {
                if (st.player.getLevel() >= 66)
                    htmltext = "31555-0.htm";
                else {
                    htmltext = "31555-0a.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "31555-2.htm";
            else if (cond == 2)
                htmltext = "31555-3.htm";
            else if (cond == 3)
                htmltext = "31555-5.htm";
            else if (cond == 7)
                htmltext = "31555-6.htm";
        } else if (npcId == CORPSE_OF_DWARF) {
            if (cond == 1)
                htmltext = "31665-0.htm";
            else if (cond == 2)
                htmltext = "31665-2.htm";
        } else if (npcId == FORGOTTEN_MONUMENT_1) {
            if (cond == 3)
                htmltext = "31661-0.htm";
            else if (cond > 3)
                htmltext = "31661-2.htm";
        } else if (npcId == FORGOTTEN_MONUMENT_2) {
            if (cond == 4)
                htmltext = "31662-0.htm";
            else if (cond > 4)
                htmltext = "31662-2.htm";
        } else if (npcId == FORGOTTEN_MONUMENT_3) {
            if (cond == 5)
                htmltext = "31663-0.htm";
            else if (cond > 5)
                htmltext = "31663-2.htm";
        } else if (npcId == FORGOTTEN_MONUMENT_4)
            if (cond == 6)
                htmltext = "31664-0.htm";
            else if (cond > 6)
                htmltext = "31664-2.htm";
        return htmltext;
    }
}