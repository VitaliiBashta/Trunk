package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _316_DestroyPlaguebringers extends Quest {
    //NPCs
    private static final int Ellenia = 30155;
    //Mobs
    private static final int Sukar_Wererat = 20040;
    private static final int Sukar_Wererat_Leader = 20047;
    private static final int Varool_Foulclaw = 27020;
    //Quest items
    private static final int Wererats_Fang = 1042;
    private static final int Varool_Foulclaws_Fang = 1043;
    //Chances
    private static final int Wererats_Fang_Chance = 50;
    private static final int Varool_Foulclaws_Fang_Chance = 30;

    public _316_DestroyPlaguebringers() {
        addStartNpc(Ellenia);
        addKillId(Sukar_Wererat, Sukar_Wererat_Leader, Varool_Foulclaw);
        addQuestItem(Wererats_Fang, Varool_Foulclaws_Fang);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        if ("elliasin_q0316_04.htm".equalsIgnoreCase(event) && state == CREATED && st.player.getRace() == Race.elf && st.player.getLevel() >= 18) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("elliasin_q0316_08.htm".equalsIgnoreCase(event) && state == STARTED) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != Ellenia)
            return htmltext;
        int state = st.getState();

        if (state == CREATED) {
            if (st.player.getRace() != Race.elf) {
                htmltext = "elliasin_q0316_00.htm";
                st.exitCurrentQuest();
            } else if (st.player.getLevel() < 18) {
                htmltext = "elliasin_q0316_02.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "elliasin_q0316_03.htm";
                st.setCond(0);
            }
        } else if (state == STARTED) {
            long adena = st.getQuestItemsCount(Wererats_Fang) * 60 + st.getQuestItemsCount(Varool_Foulclaws_Fang) * 10000L;
            if (adena > 0) {
                htmltext = "elliasin_q0316_07.htm";
                st.takeItems(Wererats_Fang);
                st.takeItems(Varool_Foulclaws_Fang);
                st.giveAdena(adena);
                st.playSound(SOUND_MIDDLE);
            } else
                htmltext = "elliasin_q0316_05.htm";
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;

        if (npc.getNpcId() == Varool_Foulclaw && Rnd.chance(Varool_Foulclaws_Fang_Chance)) {
            qs.giveItemIfNotHave(Varool_Foulclaws_Fang);
        } else if (Rnd.chance(Wererats_Fang_Chance)) {
            qs.giveItems(Wererats_Fang);
            qs.playSound(SOUND_ITEMGET);
        }
    }
}