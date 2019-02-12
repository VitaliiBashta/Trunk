package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _347_GoGetTheCalculator extends Quest {
    private static final int CALCULATOR = 4393;
    //npc
    private final int BRUNON = 30526;
    private final int SILVERA = 30527;
    private final int SPIRON = 30532;
    private final int BALANKI = 30533;
    //mob
    private final int GEMSTONE_BEAST = 20540;
    //quest items
    private final int GEMSTONE_BEAST_CRYSTAL = 4286;
    private final int CALCULATOR_Q = 4285;

    public _347_GoGetTheCalculator() {
        super(false);

        addStartNpc(BRUNON);

        addTalkId(SILVERA);
        addTalkId(SPIRON);
        addTalkId(BALANKI);

        addKillId(GEMSTONE_BEAST);

        addQuestItem(GEMSTONE_BEAST_CRYSTAL);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "1":
                st.setCond(1);
                st.setState(STARTED);
                st.playSound(SOUND_ACCEPT);
                htmltext = BRUNON + "-02.htm";
                break;
            case "30533_1":
                if (st.getQuestItemsCount(ADENA_ID) > 100) {
                    st.takeItems(ADENA_ID, 100);
                    if (st.getCond() == 1)
                        st.setCond(2);
                    else
                        st.setCond(4);
                    st.setState(STARTED);
                    htmltext = BALANKI + "-02.htm";
                } else
                    htmltext = BALANKI + "-03.htm";
                break;
            case "30532_1":
                htmltext = SPIRON + "-02a.htm";
                if (st.getCond() == 1)
                    st.setCond(3);
                else
                    st.setCond(4);
                st.setState(STARTED);
                break;
            case "30532_2":
                htmltext = SPIRON + "-02b.htm";
                break;
            case "30532_3":
                htmltext = SPIRON + "-02c.htm";
                break;
            case "30526_1":
                st.giveItems(CALCULATOR);
                st.takeItems(CALCULATOR_Q, 1);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
                htmltext = BRUNON + "-05.htm";
                break;
            case "30526_2":
                st.giveItems(ADENA_ID, 1000, true);
                st.takeItems(CALCULATOR_Q);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
                htmltext = BRUNON + "-06.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        String htmltext = "noquest";
        if (npcId == BRUNON && cond == 0 && st.player.getLevel() >= 12)
            htmltext = BRUNON + "-01.htm";
        else if (npcId == BRUNON && cond > 0 && st.getQuestItemsCount(CALCULATOR_Q) == 0)
            htmltext = BRUNON + "-03.htm";
        else if (npcId == BRUNON && cond == 6 && st.getQuestItemsCount(CALCULATOR_Q) >= 1)
            htmltext = BRUNON + "-04.htm";
        else if (npcId == BALANKI && (cond == 1 || cond == 3))
            htmltext = BALANKI + "-01.htm";
        else if (npcId == SPIRON && (cond == 1 || cond == 2))
            htmltext = SPIRON + "-01.htm";
        else if (npcId == SILVERA && cond == 4) {
            st.setCond(5);
            st.setState(STARTED);
            htmltext = SILVERA + "-01.htm";
        } else if (npcId == SILVERA && cond == 5 && st.getQuestItemsCount(GEMSTONE_BEAST_CRYSTAL) < 10)
            htmltext = SILVERA + "-02.htm";
        else if (npcId == SILVERA && cond == 5 && st.getQuestItemsCount(GEMSTONE_BEAST_CRYSTAL) >= 10) {
            htmltext = SILVERA + "-03.htm";
            st.takeItems(GEMSTONE_BEAST_CRYSTAL, 10);
            st.giveItems(CALCULATOR_Q);
            st.playSound(SOUND_ITEMGET);
            st.setCond(6);
            st.setState(STARTED);
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == GEMSTONE_BEAST && st.getCond() == 5 && Rnd.chance(50) && st.getQuestItemsCount(GEMSTONE_BEAST_CRYSTAL) < 10) {
            st.giveItems(GEMSTONE_BEAST_CRYSTAL);
            if (st.getQuestItemsCount(GEMSTONE_BEAST_CRYSTAL) >= 10)
                st.playSound(SOUND_MIDDLE);
            else
                st.playSound(SOUND_ITEMGET);
        }
    }
}