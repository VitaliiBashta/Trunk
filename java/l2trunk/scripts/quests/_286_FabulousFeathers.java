package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _286_FabulousFeathers extends Quest {
    //NPCs
    private static final int ERINU = 32164;
    //Mobs
    private static final List<Integer> Shady_Muertos = List.of(22251, 22253, 22254, 22255, 22256);
    //Quest items
    private static final int Commanders_Feather = 9746;
    //Chances
    private static final int Commanders_Feather_Chance = 66;

    public _286_FabulousFeathers() {
        super(false);
        addStartNpc(ERINU);
        addKillId(Shady_Muertos);
        addQuestItem(Commanders_Feather);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        if ("trader_erinu_q0286_0103.htm".equalsIgnoreCase(event) && _state == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("trader_erinu_q0286_0201.htm".equalsIgnoreCase(event) && _state == STARTED) {
            st.takeItems(Commanders_Feather);
            st.giveItems(ADENA_ID, 4160);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != ERINU)
            return htmltext;
        int state = st.getState();

        if (state == CREATED) {
            if (st.player.getLevel() >= 17) {
                htmltext = "trader_erinu_q0286_0101.htm";
                st.setCond(0);
            } else {
                htmltext = "trader_erinu_q0286_0102.htm";
                st.exitCurrentQuest();
            }
        } else if (state == STARTED)
            htmltext = st.haveQuestItem(Commanders_Feather, 80) ? "trader_erinu_q0286_0105.htm" : "trader_erinu_q0286_0106.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;

        long questItemsCount = qs.getQuestItemsCount(Commanders_Feather);
        if (questItemsCount < 80 && Rnd.chance(Commanders_Feather_Chance)) {
            qs.giveItems(Commanders_Feather);
            if (questItemsCount == 79) {
                qs.setCond(2);
                qs.playSound(SOUND_MIDDLE);
            } else
                qs.playSound(SOUND_ITEMGET);
        }
    }
}