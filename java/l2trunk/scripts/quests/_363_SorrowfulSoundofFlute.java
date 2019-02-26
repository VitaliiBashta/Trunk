package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _363_SorrowfulSoundofFlute extends Quest {
    //NPC
    private final int NANARIN = 30956;
    private final int BARBADO = 30959;
    private final int POITAN = 30458;
    private final int HOLVAS = 30058;
    //Mobs

    private final int EVENT_CLOTHES = 4318;
    private final int NANARINS_FLUTE = 4319;
    private final int SABRINS_BLACK_BEER = 4320;
    private static final int Musical_Score = 4420;

    //Item

    public _363_SorrowfulSoundofFlute() {
        super(false);

        addStartNpc(NANARIN);
        addTalkId(NANARIN,POITAN,HOLVAS,BARBADO);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("30956_2.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.takeItems(EVENT_CLOTHES);
            st.takeItems(NANARINS_FLUTE);
            st.takeItems(SABRINS_BLACK_BEER);
        } else if ("30956_4.htm".equalsIgnoreCase(event)) {
            st.giveItems(NANARINS_FLUTE);
            st.playSound(SOUND_MIDDLE);
            st.setCond(3);
        } else if ("answer1".equalsIgnoreCase(event)) {
            st.giveItems(EVENT_CLOTHES);
            st.playSound(SOUND_MIDDLE);
            st.setCond(3);
            htmltext = "30956_6.htm";
        } else if ("answer2".equalsIgnoreCase(event)) {
            st.giveItems(SABRINS_BLACK_BEER);
            st.playSound(SOUND_MIDDLE);
            st.setCond(3);
            htmltext = "30956_6.htm";
        } else if ("30956_7.htm".equalsIgnoreCase(event)) {
            st.giveItems(Musical_Score);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == NANARIN) {
            if (cond == 0) {
                if (st.player.getLevel() < 15) {
                    htmltext = "30956-00.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "30956_1.htm";
            } else if (cond == 1)
                htmltext = "30956_8.htm";
            else if (cond == 2)
                htmltext = "30956_3.htm";
            else if (cond == 3)
                htmltext = "30956_6.htm";
            else if (cond == 4)
                htmltext = "30956_5.htm";
        } else if (npcId == BARBADO) {
            if (cond == 3) {
                if (st.haveQuestItem(EVENT_CLOTHES) ) {
                    st.takeItems(EVENT_CLOTHES);
                    htmltext = "30959_2.htm";
                    st.exitCurrentQuest();
                } else if (st.haveQuestItem(SABRINS_BLACK_BEER) ) {
                    st.takeItems(SABRINS_BLACK_BEER);
                    htmltext = "30959_2.htm";
                    st.exitCurrentQuest();
                } else {
                    st.takeItems(NANARINS_FLUTE);
                    st.setCond(4);
                    st.playSound(SOUND_MIDDLE);
                    htmltext = "30959_1.htm";
                }
            } else if (cond == 4)
                htmltext = "30959_3.htm";
        } else if (npcId == HOLVAS && (cond == 1 || cond == 2)) {
            st.setCond(2);
            if (Rnd.chance(60))
                htmltext = "30058_2.htm";
            else
                htmltext = "30058_1.htm";
        } else if (npcId == POITAN && (cond == 1 || cond == 2)) {
            st.setCond(2);
            if (Rnd.chance(60))
                htmltext = "30458_2.htm";
            else
                htmltext = "30458_1.htm";
        }
        return htmltext;
    }

}
