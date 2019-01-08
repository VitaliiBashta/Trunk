package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

public final class _016_TheComingDarkness extends Quest implements ScriptFile {
    //npc
    private final int HIERARCH = 31517;
    //ALTAR_LIST (MOB_ID, cond)
    private final int[][] ALTAR_LIST = {
            {
                    31512,
                    1
            },
            {
                    31513,
                    2
            },
            {
                    31514,
                    3
            },
            {
                    31515,
                    4
            },
            {
                    31516,
                    5
            }
    };
    //items
    private final int CRYSTAL_OF_SEAL = 7167;

    public _016_TheComingDarkness() {
        super(false);

        addStartNpc(HIERARCH);

        for (int[] element : ALTAR_LIST)
            addTalkId(element[0]);

        addQuestItem(CRYSTAL_OF_SEAL);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {

        if (event.equalsIgnoreCase("31517-02.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.giveItems(CRYSTAL_OF_SEAL, 5);
            st.playSound(SOUND_ACCEPT);
        }
        for (int[] element : ALTAR_LIST)
            if (event.equalsIgnoreCase(String.valueOf(element[0]) + "-02.htm")) {
                st.takeItems(CRYSTAL_OF_SEAL, 1);
                st.setCond(element[1] + 1);
                st.playSound(SOUND_MIDDLE);
            }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 31517)
            if (cond < 1) {
                if (st.getPlayer().getLevel() < 61) {
                    htmltext = "31517-00.htm";
                    st.exitCurrentQuest(true);
                } else
                    htmltext = "31517-01.htm";
            } else if (cond < 6 && st.getQuestItemsCount(CRYSTAL_OF_SEAL) > 0)
                htmltext = "31517-02r.htm";
            else if (cond < 6 && st.getQuestItemsCount(CRYSTAL_OF_SEAL) < 1) {
                htmltext = "31517-proeb.htm";
                st.exitCurrentQuest(false);
            } else if (cond > 5 && st.getQuestItemsCount(CRYSTAL_OF_SEAL) < 1) {
                htmltext = "31517-03.htm";
                st.addExpAndSp(865187, 69172);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(false);
            }
        for (int[] element : ALTAR_LIST)
            if (npcId == element[0])
                if (cond == element[1]) {
                    if (st.getQuestItemsCount(CRYSTAL_OF_SEAL) > 0)
                        htmltext = String.valueOf(element[0]) + "-01.htm";
                    else
                        htmltext = String.valueOf(element[0]) + "-03.htm";
                } else if (cond == element[1] + 1)
                    htmltext = String.valueOf(element[0]) + "-04.htm";
        return htmltext;
    }
}