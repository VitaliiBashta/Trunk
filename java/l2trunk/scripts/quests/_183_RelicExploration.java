package l2trunk.scripts.quests;

import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _183_RelicExploration extends Quest {
    private static final int Kusto = 30512;
    private static final int Lorain = 30673;
    private static final int Nikola = 30621;

    public _183_RelicExploration() {
        super(false);

        addStartNpc(Kusto);
        addStartNpc(Nikola);
        addTalkId(Lorain);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        if ("30512-03.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
            st.start();
        } else if ("30673-04.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("Contract".equalsIgnoreCase(event)) {
            Quest q1 = QuestManager.getQuest(_184_NikolasCooperationContract.class);
            if (q1 != null) {
                st.giveItems(ADENA_ID, 18100);
                st.addExpAndSp(60000, 3000);
                QuestState qs1 = q1.newQuestState(player, STARTED);
                q1.notifyEvent("30621-01.htm", qs1, npc);
                st.playSound(SOUND_MIDDLE);
                st.finish();
            }
            return null;
        } else if (event.equalsIgnoreCase("Consideration")) {
            Quest q2 = QuestManager.getQuest(_185_NikolasCooperationConsideration.class);
            if (q2 != null) {
                st.giveItems(ADENA_ID, 18100);
                QuestState qs2 = q2.newQuestState(st.player, STARTED);
                q2.notifyEvent("30621-01.htm", qs2, npc);
                st.playSound(SOUND_MIDDLE);
                st.finish();
            }
            return null;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Kusto) {
            if (st.getState() == CREATED)
                if (st.player.getLevel() < 40)
                    htmltext = "30512-00.htm";
                else
                    htmltext = "30512-01.htm";
            else
                htmltext = "30512-04.htm";
        } else if (npcId == Lorain)
            if (cond == 1)
                htmltext = "30673-01.htm";
            else
                htmltext = "30673-05.htm";
        else if (npcId == Nikola && cond == 2)
            htmltext = "30621-01.htm";
        return htmltext;
    }
}