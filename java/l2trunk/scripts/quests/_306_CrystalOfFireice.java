package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _306_CrystalOfFireice extends Quest {
    //NPCs
    private static final int Katerina = 30004;
    //Mobs
    private static final int Salamander = 20109;
    private static final int Undine = 20110;
    private static final int Salamander_Elder = 20112;
    private static final int Undine_Elder = 20113;
    private static final int Salamander_Noble = 20114;
    private static final int Undine_Noble = 20115;
    //Quest items
    private static final int Flame_Shard = 1020;
    private static final int Ice_Shard = 1021;
    //Chances
    private static final int Chance = 30;
    private static final int Elder_Chance = 40;
    private static final int Noble_Chance = 50;

    public _306_CrystalOfFireice() {
        addStartNpc(Katerina);
        addKillId(Salamander,Undine,Salamander_Elder,Undine_Elder,Salamander_Noble,Undine_Noble);
        addQuestItem(Flame_Shard,Ice_Shard);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        if ("katrine_q0306_04.htm".equalsIgnoreCase(event) && state == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("katrine_q0306_08.htm".equalsIgnoreCase(event) && state == STARTED) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != Katerina)
            return htmltext;
        int state = st.getState();

        if (state == CREATED) {
            if (st.player.getLevel() < 17) {
                htmltext = "katrine_q0306_02.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "katrine_q0306_03.htm";
                st.setCond(0);
            }
        } else if (state == STARTED) {
            long Shrads_count = st.getQuestItemsCount(Flame_Shard) + st.getQuestItemsCount(Ice_Shard);
            long Reward = Shrads_count * 30 + (Shrads_count >= 10 ? 5000 : 0);
            if (Reward > 0) {
                htmltext = "katrine_q0306_07.htm";
                st.takeItems(Flame_Shard);
                st.takeItems(Ice_Shard);
                st.giveItems(ADENA_ID, Reward);
            } else
                htmltext = "katrine_q0306_05.htm";
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;
        int npcId = npc.getNpcId();

        if ((npcId == Salamander || npcId == Undine) && !Rnd.chance(Chance))
            return ;
        if ((npcId == Salamander_Elder || npcId == Undine_Elder) && !Rnd.chance(Elder_Chance))
            return ;
        if ((npcId == Salamander_Noble || npcId == Undine_Noble) && !Rnd.chance(Noble_Chance))
            return ;
        qs.giveItems(npcId == Salamander || npcId == Salamander_Elder || npcId == Salamander_Noble ? Flame_Shard : Ice_Shard);
        qs.playSound(SOUND_ITEMGET);
    }

}