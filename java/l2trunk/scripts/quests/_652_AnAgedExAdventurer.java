package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _652_AnAgedExAdventurer extends Quest {
    //NPC
    private static final int Tantan = 32012;
    private static final int Sara = 30180;
    //Item
    private static final int SoulshotCgrade = 1464;
    private static final int ScrollEnchantArmorD = 956;

    public _652_AnAgedExAdventurer() {
        super(false);

        addStartNpc(Tantan);
        addTalkId(Sara);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext;
        if (event.equalsIgnoreCase("retired_oldman_tantan_q0652_03.htm") && st.getQuestItemsCount(SoulshotCgrade) >= 100) {
            st.setCond(1);
            st.start();
            st.takeItems(SoulshotCgrade, 100);
            st.playSound(SOUND_ACCEPT);
            htmltext = "retired_oldman_tantan_q0652_04.htm";
        } else {
            htmltext = "retired_oldman_tantan_q0652_03.htm";
            st.exitCurrentQuest();
            st.playSound(SOUND_GIVEUP);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Tantan) {
            if (cond == 0)
                if (st.player.getLevel() < 46) {
                    htmltext = "retired_oldman_tantan_q0652_01a.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "retired_oldman_tantan_q0652_01.htm";
        } else if (npcId == Sara && cond == 1) {
            htmltext = "sara_q0652_01.htm";
            st.giveAdena( 10000);
            if (Rnd.chance(50))
                st.giveItems(ScrollEnchantArmorD);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }
}