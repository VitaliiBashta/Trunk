package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _172_NewHorizons extends Quest {
    //NPC
    private static final int Zenya = 32140;
    private static final int Ragara = 32163;
    //items
    private static final int ScrollOfEscapeGiran = 7126;
    private static final int MarkOfTraveler = 7570;

    public _172_NewHorizons() {
        super(false);
        addStartNpc(Zenya);
        addTalkId(Ragara);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("subelder_zenya_q0172_04.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("gatekeeper_ragara_q0172_02.htm".equalsIgnoreCase(event)) {
            st.giveItems(ScrollOfEscapeGiran);
            st.giveItems(MarkOfTraveler);
            st.unset("cond");
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Zenya) {
            if (cond == 0) {
                if (st.player.getRace() != Race.kamael) {
                    htmltext = "subelder_zenya_q0172_03.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() >= 3)
                    htmltext = "subelder_zenya_q0172_01.htm";
                else
                    htmltext = "subelder_zenya_q0172_02.htm";
                st.exitCurrentQuest();
            }
        } else if (npcId == Ragara)
            if (cond == 1)
                htmltext = "gatekeeper_ragara_q0172_01.htm";
        return htmltext;
    }
}