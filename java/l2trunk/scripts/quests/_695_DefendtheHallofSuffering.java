package l2trunk.scripts.quests;

import l2trunk.gameserver.instancemanager.SoIManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _695_DefendtheHallofSuffering extends Quest {
    private static final int TEPIOS = 32603;

    public _695_DefendtheHallofSuffering() {
        super(PARTY_ALL);
        addStartNpc(TEPIOS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("tepios_q695_3.htm")) {
            st.setState(STARTED);
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
                if (player.getLevel() >= 75 && player.getLevel() <= 85) {
                    if (SoIManager.getCurrentStage() == 4)
                        htmltext = "tepios_q695_1.htm";
                    else {
                        htmltext = "tepios_q695_0a.htm";
                        st.exitCurrentQuest(true);
                    }
                } else {
                    htmltext = "tepios_q695_0.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1)
                htmltext = "tepios_q695_4.htm";
        }

        return htmltext;
    }
}