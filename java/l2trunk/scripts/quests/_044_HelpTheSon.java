package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _044_HelpTheSon extends Quest {
    private static final int LUNDY = 30827;
    private static final int DRIKUS = 30505;

    private static final int WORK_HAMMER = 168;
    private static final int GEMSTONE_FRAGMENT = 7552;
    private static final int GEMSTONE = 7553;
    private static final int PET_TICKET = 7585;

    private static final int MAILLE_GUARD = 20921;
    private static final int MAILLE_SCOUT = 20920;
    private static final int MAILLE_LIZARDMAN = 20919;

    public _044_HelpTheSon() {
        addStartNpc(LUNDY);

        addTalkId(DRIKUS);

        addKillId(MAILLE_GUARD, MAILLE_SCOUT, MAILLE_LIZARDMAN);

        addQuestItem(GEMSTONE_FRAGMENT);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("1".equals(event)) {
            htmltext = "pet_manager_lundy_q0044_0104.htm";
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("3".equals(event) && st.haveQuestItem(WORK_HAMMER)) {
            htmltext = "pet_manager_lundy_q0044_0201.htm";
            st.takeItems(WORK_HAMMER);
            st.setCond(2);
        } else if ("4".equals(event) && st.haveQuestItem(GEMSTONE_FRAGMENT, 30)) {
            htmltext = "pet_manager_lundy_q0044_0301.htm";
            st.takeItems(GEMSTONE_FRAGMENT);
            st.giveItems(GEMSTONE);
            st.setCond(4);
        } else if ("5".equals(event) && st.haveQuestItem(GEMSTONE)) {
            htmltext = "high_prefect_drikus_q0044_0401.htm";
            st.takeItems(GEMSTONE);
            st.setCond(5);
        } else if ("7".equals(event)) {
            htmltext = "pet_manager_lundy_q0044_0501.htm";
            st.giveItems(PET_TICKET);
            st.finish();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        if (id == CREATED) {
            if (st.player.getLevel() >= 24)
                htmltext = "pet_manager_lundy_q0044_0101.htm";
            else {
                st.exitCurrentQuest();
                htmltext = "pet_manager_lundy_q0044_0103.htm";
            }
        } else if (id == STARTED) {
            int cond = st.getCond();
            if (npcId == LUNDY) {
                if (cond == 1) {
                    if (!st.haveQuestItem(WORK_HAMMER))
                        htmltext = "pet_manager_lundy_q0044_0106.htm";
                    else
                        htmltext = "pet_manager_lundy_q0044_0105.htm";
                } else if (cond == 2)
                    htmltext = "pet_manager_lundy_q0044_0204.htm";
                else if (cond == 3)
                    htmltext = "pet_manager_lundy_q0044_0203.htm";
                else if (cond == 4)
                    htmltext = "pet_manager_lundy_q0044_0303.htm";
                else if (cond == 5)
                    htmltext = "pet_manager_lundy_q0044_0401.htm";
            } else if (npcId == DRIKUS)
                if (cond == 4 && st.haveQuestItem(GEMSTONE))
                    htmltext = "high_prefect_drikus_q0044_0301.htm";
                else if (cond == 5)
                    htmltext = "high_prefect_drikus_q0044_0403.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 2) {
            if (st.giveItemIfNotHave(GEMSTONE_FRAGMENT, 30)) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(3);
            }
        }
    }
}