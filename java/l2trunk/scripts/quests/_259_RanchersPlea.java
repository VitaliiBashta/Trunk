package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _259_RanchersPlea extends Quest {
    private static final int GIANT_SPIDER_SKIN_ID = 1495;
    private static final int HEALING_POTION_ID = 1061;
    private static final int WOODEN_ARROW_ID = 17;
    private static final int SSNG_ID = 1835;
    private static final int SPSSNG_ID = 2905;

    public _259_RanchersPlea() {
        addStartNpc(30497);

        addKillId(20103, 20106, 20108);

        addQuestItem(GIANT_SPIDER_SKIN_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "1":
                st.unset("id");
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                htmltext = "edmond_q0259_03.htm";
                break;
            case "30497_1":
                htmltext = "edmond_q0259_06.htm";
                st.setCond(0);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
                break;
            case "30497_2":
                htmltext = "edmond_q0259_07.htm";
                break;
            case "30405_1":
                htmltext = "marius_q0259_03.htm";
                break;
            case "30405_2":
                htmltext = "marius_q0259_04.htm";
                st.giveItems(HEALING_POTION_ID, 2);
                st.takeItems(GIANT_SPIDER_SKIN_ID, 10);
                break;
            case "30405_3":
                htmltext = "marius_q0259_05.htm";
                st.giveItems(WOODEN_ARROW_ID, 50);
                st.takeItems(GIANT_SPIDER_SKIN_ID, 10);
                break;
            case "30405_8":
                htmltext = "marius_q0259_05a.htm";
                st.giveItems(SSNG_ID, 60);
                st.takeItems(GIANT_SPIDER_SKIN_ID, 10);
                break;
            case "30405_8a":
                htmltext = "marius_q0259_05a.htm";
                break;
            case "30405_9":
                htmltext = "marius_q0259_05c.htm";
                st.giveItems(SPSSNG_ID, 30);
                st.takeItems(GIANT_SPIDER_SKIN_ID, 10);
                break;
            case "30405_9a":
                htmltext = "marius_q0259_05d.htm";
                break;
            case "30405_4":
                if (st.haveQuestItem(GIANT_SPIDER_SKIN_ID, 10))
                    htmltext = "marius_q0259_06.htm";
                else
                    htmltext = "marius_q0259_07.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        if (npcId == 30497 && st.getCond() == 0) {
            if (st.getCond() < 15) {
                if (st.player.getLevel() >= 15) {
                    htmltext = "edmond_q0259_02.htm";
                    return htmltext;
                }
                htmltext = "edmond_q0259_01.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "edmond_q0259_01.htm";
                st.exitCurrentQuest();
            }
        } else if (npcId == 30497 && st.getCond() == 1 && st.getQuestItemsCount(GIANT_SPIDER_SKIN_ID) < 1)
            htmltext = "edmond_q0259_04.htm";
        else if (npcId == 30497 && st.getCond() == 1 && st.haveQuestItem(GIANT_SPIDER_SKIN_ID) ) {
            htmltext = "edmond_q0259_05.htm";
            st.giveAdena(st.getQuestItemsCount(GIANT_SPIDER_SKIN_ID) * 25);
            st.takeItems(GIANT_SPIDER_SKIN_ID);
        } else if (npcId == 30405 && st.getCond() == 1 && st.getQuestItemsCount(GIANT_SPIDER_SKIN_ID) < 10)
            htmltext = "marius_q0259_01.htm";
        else if (npcId == 30405 && st.getCond() == 1 && st.getQuestItemsCount(GIANT_SPIDER_SKIN_ID) >= 10)
            htmltext = "marius_q0259_02.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() > 0)
            st.rollAndGive(GIANT_SPIDER_SKIN_ID, 1, 100);
    }
}