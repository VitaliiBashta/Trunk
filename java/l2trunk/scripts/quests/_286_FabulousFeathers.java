package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _286_FabulousFeathers extends Quest {
    //NPCs
    private static final int ERINU = 32164;
    //Mobs
    private static final int Shady_Muertos_Captain = 22251;
    private static final int Shady_Muertos_Warrior = 22253;
    private static final int Shady_Muertos_Archer = 22254;
    private static final int Shady_Muertos_Commander = 22255;
    private static final int Shady_Muertos_Wizard = 22256;
    //Quest Items
    private static final int Commanders_Feather = 9746;
    //Chances
    private static final int Commanders_Feather_Chance = 66;

    public _286_FabulousFeathers() {
        super(false);
        addStartNpc(ERINU);
        addKillId(Shady_Muertos_Captain);
        addKillId(Shady_Muertos_Warrior);
        addKillId(Shady_Muertos_Archer);
        addKillId(Shady_Muertos_Commander);
        addKillId(Shady_Muertos_Wizard);
        addQuestItem(Commanders_Feather);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        if ("trader_erinu_q0286_0103.htm".equalsIgnoreCase(event) && _state == CREATED) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("trader_erinu_q0286_0201.htm".equalsIgnoreCase(event) && _state == STARTED) {
            st.takeItems(Commanders_Feather);
            st.giveItems(ADENA_ID, 4160);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != ERINU)
            return htmltext;
        int _state = st.getState();

        if (_state == CREATED) {
            if (st.player.getLevel() >= 17) {
                htmltext = "trader_erinu_q0286_0101.htm";
                st.setCond(0);
            } else {
                htmltext = "trader_erinu_q0286_0102.htm";
                st.exitCurrentQuest(true);
            }
        } else if (_state == STARTED)
            htmltext = st.getQuestItemsCount(Commanders_Feather) >= 80 ? "trader_erinu_q0286_0105.htm" : "trader_erinu_q0286_0106.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;

        long Commanders_Feather_count = qs.getQuestItemsCount(Commanders_Feather);
        if (Commanders_Feather_count < 80 && Rnd.chance(Commanders_Feather_Chance)) {
            qs.giveItems(Commanders_Feather);
            if (Commanders_Feather_count == 79) {
                qs.setCond(2);
                qs.playSound(SOUND_MIDDLE);
            } else
                qs.playSound(SOUND_ITEMGET);
        }
    }
}