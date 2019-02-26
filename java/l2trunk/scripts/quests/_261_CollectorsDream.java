package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _261_CollectorsDream extends Quest {
    private final int GIANT_SPIDER_LEG = 1087;

    public _261_CollectorsDream() {
        super(false);

        addStartNpc(30222);

        addTalkId(30222);

        addKillId(20308,20460,20466);

        addQuestItem(GIANT_SPIDER_LEG);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("moneylender_alshupes_q0261_03.htm".equalsIgnoreCase(event.intern())) {
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
            if (st.player.getLevel() >= 15) {
                htmltext = "moneylender_alshupes_q0261_02.htm";
                return htmltext;
            }
            htmltext = "moneylender_alshupes_q0261_01.htm";
            st.exitCurrentQuest();
        } else if (cond == 1 || st.getQuestItemsCount(GIANT_SPIDER_LEG) < 8)
            htmltext = "moneylender_alshupes_q0261_04.htm";
        else if (cond == 2 && st.haveQuestItem(GIANT_SPIDER_LEG, 8)) {
            st.takeItems(GIANT_SPIDER_LEG);

            st.giveItems(ADENA_ID, 1000);
            st.addExpAndSp(2000, 0);

            if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("p1q4")) {
                st.player.setVar("p1q4");
                st.player.sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide."));
            }

            htmltext = "moneylender_alshupes_q0261_05.htm";
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && st.getQuestItemsCount(GIANT_SPIDER_LEG) < 8) {
            st.giveItems(GIANT_SPIDER_LEG);
            if (st.haveQuestItem(GIANT_SPIDER_LEG, 8)) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }
}