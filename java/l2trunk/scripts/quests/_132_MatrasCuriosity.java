package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _132_MatrasCuriosity extends Quest {
    // npc
    private static final int Matras = 32245;

    // monster
    private static final int Ranku = 25542;
    private static final int Demon_Prince = 25540;

    // quest items
    private static final int Rankus_Blueprint = 9800;
    private static final int Demon_Princes_Blueprint = 9801;

    // items
    private static final int Rough_Ore_of_Fire = 10521;
    private static final int Rough_Ore_of_Water = 10522;
    private static final int Rough_Ore_of_Earth = 10523;
    private static final int Rough_Ore_of_Wind = 10524;
    private static final int Rough_Ore_of_Darkness = 10525;
    private static final int Rough_Ore_of_Divinity = 10526;

    public _132_MatrasCuriosity() {
        super(PARTY_ALL);

        addStartNpc(Matras);

        addKillId(Ranku,Demon_Prince);

        addQuestItem(Rankus_Blueprint,
                Demon_Princes_Blueprint);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("32245-02.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            if (st.player.isVarSet("q132_Rough_Ore_is_given"))
                htmltext = "32245-02a.htm";
            else {
                st.player.setVar("q132_Rough_Ore_is_given");
            }
        } else if ("32245-04.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.start();
            st.startQuestTimer("talk_timer", 10000);
        } else if ("talk_timer".equalsIgnoreCase(event))
            htmltext = "Matras wishes to talk to you.";
        else if ("get_reward".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.giveItems(Rough_Ore_of_Fire);
            st.giveItems(Rough_Ore_of_Water);
            st.giveItems(Rough_Ore_of_Earth);
            st.giveItems(Rough_Ore_of_Wind);
            st.giveItems(Rough_Ore_of_Darkness);
            st.giveItems(Rough_Ore_of_Divinity);
            st.giveItems(ADENA_ID, 31210);
            st.finish();
            return null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Matras)
            if (cond < 1 && st.player.getLevel() >= 78)
                htmltext = "32245-01.htm";
            else if (cond == 1)
                htmltext = "32245-02a.htm";
            else if (cond == 2 && st.haveAllQuestItems(Rankus_Blueprint,Demon_Princes_Blueprint) )
                htmltext = "32245-03.htm";
            else if (cond == 3)
                if (st.isRunningQuestTimer("talk_timer"))
                    htmltext = "32245-04.htm";
                else
                    htmltext = "32245-04a.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            if (npc.getNpcId() == Ranku) st.giveItemIfNotHave(Rankus_Blueprint);
            if (npc.getNpcId() == Demon_Prince ) st.giveItemIfNotHave(Demon_Princes_Blueprint);
            if (st.haveAllQuestItems(Rankus_Blueprint,Demon_Princes_Blueprint)) {
                st.setCond(2);
                st.start();
            }
        }
    }
}