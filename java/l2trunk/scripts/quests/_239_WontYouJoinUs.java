package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

public final class _239_WontYouJoinUs extends Quest {
    private static final int Athenia = 32643;

    private static final int WasteLandfillMachine = 18805;
    private static final int Suppressor = 22656;
    private static final int Exterminator = 22657;

    private static final int CertificateOfSupport = 14866;
    private static final int DestroyedMachinePiece = 14869;
    private static final int EnchantedGolemFragment = 14870;

    public _239_WontYouJoinUs() {
        super(false);
        addStartNpc(Athenia);
        addKillId(WasteLandfillMachine, Suppressor, Exterminator);
        addQuestItem(DestroyedMachinePiece, EnchantedGolemFragment);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("32643-03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
        }
        if (event.equalsIgnoreCase("32643-07.htm")) {
            st.takeItems(DestroyedMachinePiece);
            st.setCond(3);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();

        if (npcId == Athenia)
            if (id == CREATED) {
                if (st.getPlayer().getLevel() < 82 || !st.getPlayer().isQuestCompleted(_237_WindsOfChange.class))
                    return "32643-00.htm";
                if (st.getQuestItemsCount(CertificateOfSupport) == 0)
                    return "32643-12.htm";
                return "32643-01.htm";
            } else if (id == COMPLETED)
                return "32643-11.htm";
            else if (cond == 1)
                return "32643-04.htm";
            else if (cond == 2)
                return "32643-06.htm";
            else if (cond == 3)
                return "32643-08.htm";
            else if (cond == 4) {
                st.takeItems(CertificateOfSupport);
                st.takeItems(EnchantedGolemFragment);
                st.giveItems(ADENA_ID, 283346);
                st.addExpAndSp(1319736, 103553);
                st.setState(COMPLETED);
                st.exitCurrentQuest(false);
                return "32643-10.htm";
            }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 1 && npc.getNpcId() == WasteLandfillMachine) {
            st.giveItems(DestroyedMachinePiece, 1);
            if (st.getQuestItemsCount(DestroyedMachinePiece) >= 10)
                st.setCond(2);
        } else if (cond == 3 && (npc.getNpcId() == Suppressor || npc.getNpcId() == Exterminator)) {
            st.giveItems(EnchantedGolemFragment, 1);
            if (st.getQuestItemsCount(EnchantedGolemFragment) >= 20)
                st.setCond(4);
        }
        return null;
    }
}