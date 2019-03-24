package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _026_TiredOfWaiting extends Quest {
    private final static int ISAEL = 30655;
    private final static int KITZKA = 31045;

    private final static int LARGE_DRAGON_BONE = 17248;
    private final static int WILL_OF_ANTHARAS = 17266;
    private final static int SEALED_BLOOD_CRYSTAL = 17267;

    public _026_TiredOfWaiting() {
        addStartNpc(ISAEL);
        addTalkId(KITZKA);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equalsIgnoreCase(event)) {
            htmltext = "isael_q0026_05.htm";
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("LARGE_DRAGON_BONE".equalsIgnoreCase(event)) {
            htmltext = "kitzka_q0026_03.htm";
            st.giveItems(LARGE_DRAGON_BONE);
            st.playSound(SOUND_FINISH);
            st.finish();
        } else if ("WILL_OF_ANTHARAS".equalsIgnoreCase(event)) {
            htmltext = "kitzka_q0026_04.htm";
            st.giveItems(WILL_OF_ANTHARAS);
            st.playSound(SOUND_FINISH);
            st.finish();
        } else if ("SEALED_BLOOD_CRYSTAL".equalsIgnoreCase(event)) {
            htmltext = "kitzka_q0026_05.htm";
            st.giveItems(SEALED_BLOOD_CRYSTAL);
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        switch (npcId) {
            case ISAEL:
                if (cond == 0) {
                    if (st.player.getLevel() >= 80)
                        htmltext = "isael_q0026_02.htm";
                    else {
                        htmltext = "isael_q0026_01.htm";
                        st.exitCurrentQuest();
                    }
                } else if (cond == 1)
                    htmltext = "isael_q0026_03.htm";
                break;
            case KITZKA:
                if (cond == 1) {
                    htmltext = "kitzka_q0026_01.htm";
                    st.playSound(SOUND_MIDDLE);
                }
                break;
        }
        return htmltext;
    }
}