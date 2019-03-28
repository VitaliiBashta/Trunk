package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _170_DangerousSeduction extends Quest {
    //NPC
    private static final int Vellior = 30305;
    //Quest items
    private static final int NightmareCrystal = 1046;
    //MOB
    private static final int Merkenis = 27022;

    public _170_DangerousSeduction() {
        addStartNpc(Vellior);
        addKillId(Merkenis);
        addQuestItem(NightmareCrystal);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("30305-04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Vellior)
            if (cond == 0) {
                if (st.player.getRace() != Race.darkelf) {
                    htmltext = "30305-00.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() < 21) {
                    htmltext = "30305-02.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "30305-03.htm";
            } else if (cond == 1)
                htmltext = "30305-05.htm";
            else if (cond == 2) {
                st.takeItems(NightmareCrystal);
                st.giveAdena(102680);
                st.addExpAndSp(38607, 4018);
                htmltext = "30305-06.htm";
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && npcId == Merkenis) {
            st.giveItemIfNotHave(NightmareCrystal);
            st.playSound(SOUND_MIDDLE);
            st.setCond(2);
            st.start();
        }
    }
}