package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _313_CollectSpores extends Quest {
    //NPC
    private final int Herbiel = 30150;
    //Mobs
    private final int SporeFungus = 20509;
    //Quest items
    private final int SporeSac = 1118;

    public _313_CollectSpores() {
        super(false);

        addStartNpc(Herbiel);
        addTalkId(Herbiel);
        addKillId(SporeFungus);
        addQuestItem(SporeSac);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("green_q0313_05.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() >= 8)
                htmltext = "green_q0313_03.htm";
            else {
                htmltext = "green_q0313_02.htm";
                st.exitCurrentQuest();
            }
        } else if (cond == 1)
            htmltext = "green_q0313_06.htm";
        else if (cond == 2)
            if (st.getQuestItemsCount(SporeSac) < 10) {
                st.setCond(1);
                htmltext = "green_q0313_06.htm";
            } else {
                st.takeItems(SporeSac);
                st.giveItems(ADENA_ID, 3500, true);
                st.playSound(SOUND_FINISH);
                htmltext = "green_q0313_07.htm";
                st.exitCurrentQuest();
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && npcId == SporeFungus && Rnd.chance(70)) {
            st.giveItems(SporeSac);
            if (st.getQuestItemsCount(SporeSac) < 10)
                st.playSound(SOUND_ITEMGET);
            else {
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
                st.start();
            }
        }
    }
}