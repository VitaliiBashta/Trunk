package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.instancemanager.SoIManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _697_DefendtheHallofErosion extends Quest {
    private static final int TEPIOS = 32603;
    private static final int VesperNobleEnhanceStone = 14052;

    public _697_DefendtheHallofErosion() {
        super(PARTY_ALL);
        addStartNpc(TEPIOS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("tepios_q697_3.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
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
                if (player.getLevel() < 75) {
                    st.exitCurrentQuest();
                    return "tepios_q697_0.htm";
                }
                if (SoIManager.getCurrentStage() != 4) {
                    st.exitCurrentQuest();
                    return "tepios_q697_0a.htm";
                }
                htmltext = "tepios_q697_1.htm";
            } else if (cond == 1 && !st.isSet("defenceDone") )
                htmltext = "tepios_q697_4.htm";
            else if (cond == 1 && st.isSet("defenceDone")) {
                st.giveItems(VesperNobleEnhanceStone, Rnd.get(12, 20));
                htmltext = "tepios_q697_5.htm";
                st.playSound(SOUND_FINISH);
                st.unset("defenceDone");
                st.exitCurrentQuest();
            }

        }
        return htmltext;
    }
}