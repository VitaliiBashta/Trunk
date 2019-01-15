package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.Map;

public final class _016_TheComingDarkness extends Quest {
    //npc
    private static final int HIERARCH = 31517;
    //ALTAR_LIST (MOB_ID, cond)
    private final Map<Integer, Integer> ALTAR_LIST = Map.of(
            31512, 1,
            31513, 2,
            31514, 3,
            31515, 4,
            31516, 5);
    //items
    private final int CRYSTAL_OF_SEAL = 7167;

    public _016_TheComingDarkness() {
        super(false);
        addStartNpc(HIERARCH);
        addTalkId(ALTAR_LIST.keySet());
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
        ALTAR_LIST.forEach((k, v) -> {
            if (event.equalsIgnoreCase(k + "-02.htm")) {
                st.takeItems(CRYSTAL_OF_SEAL, 1);
                st.setCond(v + 1);
                st.playSound(SOUND_MIDDLE);
            }
        });

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
        for (Map.Entry<Integer, Integer> element : ALTAR_LIST.entrySet())
            if (npcId == element.getKey())
                if (cond == element.getValue()) {
                    if (st.getQuestItemsCount(CRYSTAL_OF_SEAL) > 0)
                        htmltext = element.getKey() + "-01.htm";
                    else
                        htmltext = element.getKey() + "-03.htm";
                } else if (cond == element.getValue() + 1)
                    htmltext = element.getKey() + "-04.htm";
        return htmltext;
    }
}