package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _324_SweetestVenom extends Quest {
    //NPCs
    private static final int ASTARON = 30351;
    //Mobs
    private static final int Prowler = 20034;
    private static final int Venomous_Spider = 20038;
    private static final int Arachnid_Tracker = 20043;
    //items
    private static final int VENOM_SAC = 1077;
    //Chances
    private static final int VENOM_SAC_BASECHANCE = 60;

    public _324_SweetestVenom() {
        addStartNpc(ASTARON);
        addKillId(Prowler,Venomous_Spider,Arachnid_Tracker);
        addQuestItem(VENOM_SAC);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != ASTARON)
            return htmltext;
        int state = st.getState();

        if (state == CREATED) {
            if (st.player.getLevel() >= 18) {
                htmltext = "astaron_q0324_03.htm";
                st.setCond(0);
            } else {
                htmltext = "astaron_q0324_02.htm";
                st.exitCurrentQuest();
            }
        } else if (state == STARTED) {
            long count = st.getQuestItemsCount(VENOM_SAC);
            if (count >= 10) {
                htmltext = "astaron_q0324_06.htm";
                st.takeItems(VENOM_SAC);
                st.giveAdena( 5810);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            } else
                htmltext = "astaron_q0324_05.htm";
        }
        return htmltext;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("astaron_q0324_04.htm".equalsIgnoreCase(event) && st.getState() == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;

        long count = qs.getQuestItemsCount(VENOM_SAC);
        int chance = VENOM_SAC_BASECHANCE + (npc.getNpcId() - Prowler) / 4 * 12;

        if (count < 10 && Rnd.chance(chance)) {
            qs.giveItems(VENOM_SAC);
            if (count == 9) {
                qs.setCond(2);
                qs.playSound(SOUND_MIDDLE);
            } else
                qs.playSound(SOUND_ITEMGET);
        }
    }

}