package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _017_LightAndDarkness extends Quest {

    public _017_LightAndDarkness() {
        addStartNpc(31517);

        addTalkId(31508,31509,31510,31511);

        addQuestItem(7168);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        switch (event) {
            case "dark_presbyter_q0017_04.htm":
                st.start();
                st.setCond(1);
                st.giveItems(7168, 4);
                st.playSound(SOUND_ACCEPT);
                break;
            case "blessed_altar1_q0017_02.htm":
                st.takeItems(7168, 1);
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
                break;
            case "blessed_altar2_q0017_02.htm":
                st.takeItems(7168, 1);
                st.setCond(3);
                st.playSound(SOUND_MIDDLE);
                break;
            case "blessed_altar3_q0017_02.htm":
                st.takeItems(7168, 1);
                st.setCond(4);
                st.playSound(SOUND_MIDDLE);
                break;
            case "blessed_altar4_q0017_02.htm":
                st.takeItems(7168, 1);
                st.setCond(5);
                st.playSound(SOUND_MIDDLE);
                break;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        boolean haveQuestItem = st.haveQuestItem(7168);
        if (npcId == 31517) {
            if (cond == 0)
                if (st.player.getLevel() >= 61)
                    htmltext = "dark_presbyter_q0017_01.htm";
                else {
                    htmltext = "dark_presbyter_q0017_03.htm";
                    st.exitCurrentQuest();
                }
            else if (cond > 0 && cond < 5 && haveQuestItem)
                htmltext = "dark_presbyter_q0017_05.htm";
            else if (cond > 0 && cond < 5) {
                htmltext = "dark_presbyter_q0017_06.htm";
                st.setCond(0);
                st.finish();
            } else if (cond == 5 && !haveQuestItem) {
                htmltext = "dark_presbyter_q0017_07.htm";
                st.addExpAndSp(697040, 54887);
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        } else if (npcId == 31508) {
            if (cond == 1)
                if (haveQuestItem)
                    htmltext = "blessed_altar1_q0017_01.htm";
                else
                    htmltext = "blessed_altar1_q0017_03.htm";
            else if (cond == 2)
                htmltext = "blessed_altar1_q0017_05.htm";
        } else if (npcId == 31509) {
            if (cond == 2)
                if (haveQuestItem)
                    htmltext = "blessed_altar2_q0017_01.htm";
                else
                    htmltext = "blessed_altar2_q0017_03.htm";
            else if (cond == 3)
                htmltext = "blessed_altar2_q0017_05.htm";
        } else if (npcId == 31510) {
            if (cond == 3)
                if (haveQuestItem)
                    htmltext = "blessed_altar3_q0017_01.htm";
                else
                    htmltext = "blessed_altar3_q0017_03.htm";
            else if (cond == 4)
                htmltext = "blessed_altar3_q0017_05.htm";
        } else if (npcId == 31511)
            if (cond == 4)
                if (haveQuestItem)
                    htmltext = "blessed_altar4_q0017_01.htm";
                else
                    htmltext = "blessed_altar4_q0017_03.htm";
            else if (cond == 5)
                htmltext = "blessed_altar4_q0017_05.htm";
        return htmltext;
    }
}