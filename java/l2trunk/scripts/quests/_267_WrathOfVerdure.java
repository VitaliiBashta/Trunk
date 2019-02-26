package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _267_WrathOfVerdure extends Quest {
    //NPCs
    private static final int Treant_Bremec = 31853;
    //Mobs
    private static final int Goblin_Raider = 20325;
    //Quest items
    private static final int Goblin_Club = 1335;
    //items
    private static final int Silvery_Leaf = 1340;
    //Chances
    private static final int Goblin_Club_Chance = 50;

    public _267_WrathOfVerdure() {
        super(false);
        addStartNpc(Treant_Bremec);
        addKillId(Goblin_Raider);
        addQuestItem(Goblin_Club);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        if ("bri_mec_tran_q0267_03.htm".equalsIgnoreCase(event) && _state == CREATED && st.player.getRace() == Race.elf && st.player.getLevel() >= 4) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("bri_mec_tran_q0267_06.htm".equalsIgnoreCase(event) && _state == STARTED) {
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != Treant_Bremec)
            return htmltext;
        int _state = st.getState();
        if (_state == CREATED) {
            if (st.player.getRace() != Race.elf) {
                htmltext = "bri_mec_tran_q0267_00.htm";
                st.exitCurrentQuest();
            } else if (st.player.getLevel() < 4) {
                htmltext = "bri_mec_tran_q0267_01.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "bri_mec_tran_q0267_02.htm";
                st.setCond(0);
            }
        } else if (_state == STARTED) {
            long Goblin_Club_Count = st.getQuestItemsCount(Goblin_Club);
            if (Goblin_Club_Count > 0) {
                htmltext = "bri_mec_tran_q0267_05.htm";
                st.takeItems(Goblin_Club);
                st.giveItems(Silvery_Leaf, Goblin_Club_Count);
                st.playSound(SOUND_MIDDLE);
            } else
                htmltext = "bri_mec_tran_q0267_04.htm";
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;

        if (Rnd.chance(Goblin_Club_Chance)) {
            qs.giveItems(Goblin_Club);
            qs.playSound(SOUND_ITEMGET);
        }
    }
}
