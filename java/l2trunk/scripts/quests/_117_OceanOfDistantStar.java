package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _117_OceanOfDistantStar extends Quest {
    //NPC
    private static final int Abey = 32053;
    private static final int GhostEngineer = 32055;
    private static final int Obi = 32052;
    private static final int GhostEngineer2 = 32054;
    private static final int Box = 32076;
    //Quest items
    private static final int BookOfGreyStar = 8495;
    private static final int EngravedHammer = 8488;
    //Mobs
    private static final int BanditWarrior = 22023;
    private static final int BanditInspector = 22024;

    public _117_OceanOfDistantStar() {
        addStartNpc(Abey);

        addTalkId(GhostEngineer, Obi, Box, GhostEngineer2);

        addKillId(BanditWarrior, BanditInspector);

        addQuestItem(BookOfGreyStar, EngravedHammer);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("railman_abu_q0117_0104.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("ghost_of_railroadman2_q0117_0201.htm".equalsIgnoreCase(event))
            st.setCond(2);
        else if ("railman_obi_q0117_0301.htm".equalsIgnoreCase(event))
            st.setCond(3);
        else if ("railman_abu_q0117_0401.htm".equalsIgnoreCase(event))
            st.setCond(4);
        else if ("q_box_of_railroad_q0117_0501.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.giveItems(EngravedHammer);
        } else if ("railman_abu_q0117_0601.htm".equalsIgnoreCase(event))
            st.setCond(6);
        else if ("railman_obi_q0117_0701.htm".equalsIgnoreCase(event))
            st.setCond(7);
        else if ("railman_obi_q0117_0801.htm".equalsIgnoreCase(event)) {
            st.takeItems(BookOfGreyStar);
            st.setCond(9);
        } else if ("ghost_of_railroadman2_q0117_0901.htm".equalsIgnoreCase(event)) {
            st.takeItems(EngravedHammer);
            st.setCond(10);
        } else if ("ghost_of_railroadman_q0117_1002.htm".equalsIgnoreCase(event)) {
            st.giveAdena(17647);
            st.addExpAndSp(107387, 7369);
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        boolean haveHammer = st.haveQuestItem(EngravedHammer);
        int cond = 0;
        if (id != CREATED)
            cond = st.getCond();
        if (npcId == Abey) {
            if (cond == 0) {
                if (st.player.getLevel() >= 39)
                    htmltext = "railman_abu_q0117_0101.htm";
                else {
                    htmltext = "railman_abu_q0117_0103.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 3)
                htmltext = "railman_abu_q0117_0301.htm";
            else if (cond == 5 && haveHammer)
                htmltext = "railman_abu_q0117_0501.htm";
            else if (cond == 6 && haveHammer)
                htmltext = "railman_abu_q0117_0601.htm";
        } else if (npcId == GhostEngineer) {
            if (cond == 1)
                htmltext = "ghost_of_railroadman2_q0117_0101.htm";
            else if (cond == 9 && haveHammer)
                htmltext = "ghost_of_railroadman2_q0117_0801.htm";
        } else if (npcId == Obi) {
            if (cond == 2)
                htmltext = "railman_obi_q0117_0201.htm";
            else if (cond == 6 && haveHammer)
                htmltext = "railman_obi_q0117_0601.htm";
            else if (cond == 7 && haveHammer)
                htmltext = "railman_obi_q0117_0701.htm";
            else if (cond == 8 && st.haveQuestItem(BookOfGreyStar))
                htmltext = "railman_obi_q0117_0704.htm";
        } else if (npcId == Box && cond == 4)
            htmltext = "q_box_of_railroad_q0117_0401.htm";
        else if (npcId == GhostEngineer2 && cond == 10)
            htmltext = "ghost_of_railroadman_q0117_0901.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 7 && Rnd.chance(30)) {
            st.giveItemIfNotHave(BookOfGreyStar);
            st.setCond(8);
            st.start();
        }
    }
}