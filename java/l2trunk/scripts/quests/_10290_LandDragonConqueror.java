package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10290_LandDragonConqueror extends Quest {
    private static final int Theodric = 30755;
    private static final int ShabbyNecklace = 15522;
    private static final int MiracleNecklace = 15523;
    private static final int UltimateAntharas = 29068;

    public _10290_LandDragonConqueror() {
        super(PARTY_ALL);
        addStartNpc(Theodric);
        addQuestItem(ShabbyNecklace, MiracleNecklace);
        addKillId(UltimateAntharas);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("theodric_q10290_04.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
            st.giveItems(ShabbyNecklace);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Theodric) {
            if (cond == 0) {
                if (st.player.getLevel() >= 83 && st.haveQuestItem(3865))
                    htmltext = "theodric_q10290_01.htm";
                else if (st.getQuestItemsCount(3865) < 1)
                    htmltext = "theodric_q10290_00a.htm";
                else
                    htmltext = "theodric_q10290_00.htm";
            } else if (cond == 1)
                htmltext = "theodric_q10290_05.htm";
            else if (cond == 2) {
                if (st.haveQuestItem(MiracleNecklace)) {
                    htmltext = "theodric_q10290_07.htm";
                    st.takeItems(MiracleNecklace);
                    st.giveItems(8568);
                    st.giveItems(ADENA_ID, 131236);
                    st.addExpAndSp(702557, 76334);
                    st.playSound(SOUND_FINISH);
                    st.complete();
                    st.finish();
                } else
                    htmltext = "theodric_q10290_06.htm";
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (cond == 1 && npcId == UltimateAntharas) {
            st.takeItems(ShabbyNecklace);
            st.giveItems(MiracleNecklace);
            st.setCond(2);
        }
    }
}