package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _638_SeekersOfTheHolyGrail extends Quest {
    private static final int DROP_CHANCE = 5; // For x1 mobs
    private static final int INNOCENTIN = 31328;
    private static final int TOTEM = 8068;
    private static final int EAS = 960;
    private static final int EWS = 959;

    public _638_SeekersOfTheHolyGrail() {
        super(true);
        addStartNpc(INNOCENTIN);
        addQuestItem(TOTEM);
        for (int i = 22137; i <= 22176; i++)
            addKillId(i);
        addKillId(22194);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equals("highpriest_innocentin_q0638_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("highpriest_innocentin_q0638_09.htm")) {
            st.playSound(SOUND_GIVEUP);
            st.exitCurrentQuest(true);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int id = st.getState();

        if (id == CREATED) {
            if (st.player.getLevel() >= 73)
                htmltext = "highpriest_innocentin_q0638_01.htm";
            else
                htmltext = "highpriest_innocentin_q0638_02.htm";
        } else
            htmltext = tryRevard(st);

        return htmltext;
    }

    private String tryRevard(QuestState st) {
        boolean ok = false;
        while (st.getQuestItemsCount(TOTEM) >= 2000) {
            st.takeItems(TOTEM, 2000);
            int rnd = Rnd.get(100);
            if (rnd < 40)
                st.giveItems(ADENA_ID, 10728000, false);
            else if (rnd < 85)
                st.giveItems(EAS, 3, false);
            else
                st.giveItems(EWS, 3, false);
            ok = true;
        }
        if (ok) {
            st.playSound(SOUND_MIDDLE);
            return "highpriest_innocentin_q0638_10.htm";
        }
        return "highpriest_innocentin_q0638_05.htm";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        st.rollAndGive(TOTEM, 1, DROP_CHANCE * npc.getTemplate().rateHp);

        if ((npc.getNpcId() == 22146 || npc.getNpcId() == 22151) && Rnd.chance(10))
            npc.dropItem(st.player, 8275, 1);

        if ((npc.getNpcId() == 22140 || npc.getNpcId() == 22149) && Rnd.chance(10))
            npc.dropItem(st.player, 8273, 1);

        if ((npc.getNpcId() == 22142 || npc.getNpcId() == 22143) && Rnd.chance(10))
            npc.dropItem(st.player, 8274, 1);
    }
}