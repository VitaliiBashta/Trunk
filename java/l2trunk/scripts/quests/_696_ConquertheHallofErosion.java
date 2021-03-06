package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _696_ConquertheHallofErosion extends Quest {
    // NPC
    private static final int TEPIOS = 32603;
    private static final int Cohemenes = 25634;

    private static final int MARK_OF_KEUCEREUS_STAGE_1 = 13691;
    private static final int MARK_OF_KEUCEREUS_STAGE_2 = 13692;

    public _696_ConquertheHallofErosion() {
        super(PARTY_ALL);
        addStartNpc(TEPIOS);
        addKillId(Cohemenes);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("tepios_q696_3.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        Player player = st.player;
        int cond = st.getCond();

        if (npcId == TEPIOS) {
            if (cond == 0) {
                if (player.getLevel() >= 75) {
                    if (st.haveAnyQuestItems(MARK_OF_KEUCEREUS_STAGE_1,MARK_OF_KEUCEREUS_STAGE_2) )
                        htmltext = "tepios_q696_1.htm";
                    else {
                        htmltext = "tepios_q696_6.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "tepios_q696_0.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1) {
                if (st.isSet("cohemenesDone")) {
                    if (!st.haveQuestItem(MARK_OF_KEUCEREUS_STAGE_2)) {
                        st.takeItems(MARK_OF_KEUCEREUS_STAGE_1);
                        st.giveItems(MARK_OF_KEUCEREUS_STAGE_2);
                    }
                    htmltext = "tepios_q696_5.htm";
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest();
                } else
                    htmltext = "tepios_q696_1a.htm";
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (npc.getNpcId() == Cohemenes)
            st.set("cohemenesDone");
    }
}