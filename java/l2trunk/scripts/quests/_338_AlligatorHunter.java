package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _338_AlligatorHunter extends Quest {
    private static final int Enverun = 30892;
    private static final int AlligatorLeather = 4337;
    private static final List<Integer> MOBS = List.of(
            20804, 20805, 20806, 20807, 20808, 20991);

    public _338_AlligatorHunter() {
        addStartNpc(Enverun);
        addKillId(MOBS);
        addQuestItem(AlligatorLeather);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        long adenaCount = st.getQuestItemsCount(AlligatorLeather) * 40;
        if ("30892-02.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
            st.start();
        } else if ("30892-02-afmenu.htm".equalsIgnoreCase(event)) {
            st.takeItems(AlligatorLeather);
            st.giveAdena(adenaCount);
        } else if ("quit".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(AlligatorLeather)) {
                st.takeItems(AlligatorLeather);
                st.giveAdena(adenaCount);
                htmltext = "30892-havequit.htm";
            } else
                htmltext = "30892-havent.htm";
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "<html><body>I have nothing to say you</body></html>";
        int npcId = npc.getNpcId();
        if (npcId == Enverun)
            if (st.getCond() == 0) {
                if (st.player.getLevel() >= 40)
                    htmltext = "30892-01.htm";
                else {
                    htmltext = "30892-00.htm";
                    st.exitCurrentQuest();
                }
            } else if (st.haveQuestItem(AlligatorLeather))
                htmltext = "30892-menu.htm";
            else
                htmltext = "30892-02-rep.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && MOBS.contains(npc.getNpcId())) {
            st.rollAndGive(AlligatorLeather, 1, 60);
        }
    }
}