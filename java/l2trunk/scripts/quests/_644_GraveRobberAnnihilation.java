package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _644_GraveRobberAnnihilation extends Quest {
    //NPC
    private static final int KARUDA = 32017;
    //QuestItem
    private static final int ORC_GOODS = 8088;

    public _644_GraveRobberAnnihilation() {
        super(true);
        addStartNpc(KARUDA);

        addKillId(22003,22004,22005,22006,22008);

        addQuestItem(ORC_GOODS);

    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("karuda_q0644_0103.htm".equalsIgnoreCase(event)) {
            st.takeItems(ORC_GOODS);
            if (st.player.getLevel() < 20) {
                htmltext = "karuda_q0644_0102.htm";
                st.exitCurrentQuest();
            } else {
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
            }
        }
        if (st.getCond() == 2 && st.getQuestItemsCount(ORC_GOODS) >= 120) {
            if ("varn".equalsIgnoreCase(event)) {
                st.takeItems(ORC_GOODS);
                st.giveItems(1865, 30, true);
                htmltext = null;
            } else if ("an_s".equalsIgnoreCase(event)) {
                st.takeItems(ORC_GOODS);
                st.giveItems(1867, 40, true);
                htmltext = null;
            } else if ("an_b".equalsIgnoreCase(event)) {
                st.takeItems(ORC_GOODS);
                st.giveItems(1872, 40, true);
                htmltext = null;
            } else if ("char".equalsIgnoreCase(event)) {
                st.takeItems(ORC_GOODS);
                st.giveItems(1871, 30, true);
                htmltext = null;
            } else if ("coal".equalsIgnoreCase(event)) {
                st.takeItems(ORC_GOODS);
                st.giveItems(1870, 30, true);
                htmltext = null;
            } else if ("i_o".equalsIgnoreCase(event)) {
                st.takeItems(ORC_GOODS);
                st.giveItems(1869, 30, true);
                htmltext = null;
            }
            if (htmltext == null) {
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0)
            htmltext = "karuda_q0644_0101.htm";
        else if (cond == 1)
            htmltext = "karuda_q0644_0106.htm";
        else if (cond == 2)
            if (st.haveQuestItem(ORC_GOODS, 120))
                htmltext = "karuda_q0644_0105.htm";
            else {
                st.setCond(1);
                htmltext = "karuda_q0644_0106.htm";
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && Rnd.chance(90)) {
            st.giveItems(ORC_GOODS);
            if (st.haveQuestItem(ORC_GOODS, 120)) {
                st.setCond(2);
                st.start();
            }
        }
    }
}