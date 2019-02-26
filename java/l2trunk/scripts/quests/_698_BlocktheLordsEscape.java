package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.instancemanager.SoIManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _698_BlocktheLordsEscape extends Quest {
    // NPC
    private static final int TEPIOS = 32603;
    private static final int VesperNobleEnhanceStone = 14052;

    public _698_BlocktheLordsEscape() {
        super(PARTY_ALL);
        addStartNpc(TEPIOS);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        Player player = st.player;

        if (npcId == TEPIOS)
            if (st.getState() == CREATED) {
                if (player.getLevel() < 75 || player.getLevel() > 85) {
                    st.exitCurrentQuest();
                    return "tepios_q698_0.htm";
                }
                if (SoIManager.getCurrentStage() != 5) {
                    st.exitCurrentQuest();
                    return "tepios_q698_0a.htm";
                }
                return "tepios_q698_1.htm";
            } else if (st.getCond() == 1 && st.isSet("defenceDone") ) {
                htmltext = "tepios_q698_5.htm";
                st.giveItems(VesperNobleEnhanceStone, (int) Config.RATE_QUESTS_REWARD * Rnd.get(5, 8));
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            } else
                return "tepios_q698_4.htm";
        return htmltext;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {

        if (event.equalsIgnoreCase("tepios_q698_3.htm")) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

}