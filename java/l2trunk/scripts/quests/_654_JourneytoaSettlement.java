package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _654_JourneytoaSettlement extends Quest {
    // NPC
    private static final int NamelessSpirit = 31453;

    // Mobs
    private static final int CanyonAntelope = 21294;
    private static final int CanyonAntelopeSlave = 21295;

    // items
    private static final int AntelopeSkin = 8072;

    // Rewards
    private static final int FrintezzasMagicForceFieldRemovalScroll = 8073;

    public _654_JourneytoaSettlement() {
        super(true);

        addStartNpc(NamelessSpirit);
        addKillId(CanyonAntelope, CanyonAntelopeSlave);
        addQuestItem(AntelopeSkin);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("printessa_spirit_q0654_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        if ("printessa_spirit_q0654_04.htm".equalsIgnoreCase(event))
            st.setCond(2);
        if ("printessa_spirit_q0654_07.htm".equalsIgnoreCase(event)) {
            st.giveItems(FrintezzasMagicForceFieldRemovalScroll, 5);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (!st.player.isQuestCompleted(_119_LastImperialPrince.class)) {
            st.exitCurrentQuest();
            return htmltext;
        }
        if (st.player.getLevel() < 74) {
            st.exitCurrentQuest();
            return "printessa_spirit_q0654_02.htm";
        }

        int cond = st.getCond();
        if (npc.getNpcId() == NamelessSpirit) {
            if (cond == 0)
                return "printessa_spirit_q0654_01.htm";
            if (cond == 1)
                return "printessa_spirit_q0654_03.htm";
            if (cond == 3)
                return "printessa_spirit_q0654_06.htm";
        } else
            htmltext = "noquest";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 2 && Rnd.chance(5)) {
            st.setCond(3);
            st.giveItems(AntelopeSkin);
            st.playSound(SOUND_MIDDLE);
        }
    }
}