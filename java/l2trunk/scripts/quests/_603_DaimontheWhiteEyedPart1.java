package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _603_DaimontheWhiteEyedPart1 extends Quest {
    //NPC
    private static final int EYE = 31683;
    private static final int TABLE1 = 31548;
    private static final int TABLE2 = 31549;
    private static final int TABLE3 = 31550;
    private static final int TABLE4 = 31551;
    private static final int TABLE5 = 31552;
    //MOBS
    private static final int BUFFALO = 21299;
    private static final int BANDERSNATCH = 21297;
    private static final int GRENDEL = 21304;
    //ITEMS
    private static final int EVIL_SPIRIT = 7190;
    private static final int BROKEN_CRYSTAL = 7191;
    private static final int U_SUMMON = 7192;

    public _603_DaimontheWhiteEyedPart1() {
        super(true);

        addStartNpc(EYE);

        addTalkId(TABLE1,TABLE2,TABLE3,TABLE4,TABLE5);

        addKillId(BUFFALO,BANDERSNATCH,GRENDEL);

        addQuestItem(EVIL_SPIRIT);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("31683-02.htm".equalsIgnoreCase(event)) {
            if (st.player.getLevel() < 73) {
                htmltext = "31683-01a.htm";
                st.exitCurrentQuest();
            } else {
                st.setCond(1);
                st.start();
                st.playSound("ItemSound.quest_accept");
            }
        } else if ("31548-02.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.start();
            st.playSound("ItemSound.quest_middle");
            st.giveItems(BROKEN_CRYSTAL);
        } else if ("31549-02.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.start();
            st.playSound("ItemSound.quest_middle");
            st.giveItems(BROKEN_CRYSTAL);
        } else if ("31550-02.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.start();
            st.playSound("ItemSound.quest_middle");
            st.giveItems(BROKEN_CRYSTAL);
        } else if ("31551-02.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.start();
            st.playSound("ItemSound.quest_middle");
            st.giveItems(BROKEN_CRYSTAL);
        } else if ("31552-02.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.start();
            st.playSound("ItemSound.quest_middle");
            st.giveItems(BROKEN_CRYSTAL);
        } else if ("31683-04.htm".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(BROKEN_CRYSTAL, 5)) {
                st.setCond(7);
                st.start();
                st.takeItems(BROKEN_CRYSTAL);
                st.playSound("ItemSound.quest_middle");
            } else {
                htmltext = "31683-08.htm";
            }
        } else if ("31683-07.htm".equalsIgnoreCase(event))
            if (st.haveQuestItem(EVIL_SPIRIT, 200)) {
                st.takeItems(EVIL_SPIRIT);
                st.giveItems(U_SUMMON);
                st.playSound("ItemSound.quest_finish");
                st.exitCurrentQuest();
            } else
                htmltext = "31683-09.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 0) {
            if (npcId == EYE)
                htmltext = "31683-01.htm";
        } else if (cond == 1) {
            if (npcId == EYE)
                htmltext = "31683-02a.htm";
            else if (npcId == TABLE1)
                htmltext = "31548-01.htm";
        } else if (cond == 2) {
            if (npcId == EYE)
                htmltext = "31683-02a.htm";
            else if (npcId == TABLE2)
                htmltext = "31549-01.htm";
            else
                htmltext = "table-no.htm";
        } else if (cond == 3) {
            if (npcId == EYE)
                htmltext = "31683-02a.htm";
            else if (npcId == TABLE3)
                htmltext = "31550-01.htm";
            else
                htmltext = "table-no.htm";
        } else if (cond == 4) {
            if (npcId == EYE)
                htmltext = "31683-02a.htm";
            else if (npcId == TABLE4)
                htmltext = "31551-01.htm";
            else
                htmltext = "table-no.htm";
        } else if (cond == 5) {
            if (npcId == EYE)
                htmltext = "31683-02a.htm";
            else if (npcId == TABLE5)
                htmltext = "31552-01.htm";
            else
                htmltext = "table-no.htm";
        } else if (cond == 6) {
            if (npcId == EYE)
                htmltext = "31683-03.htm";
            else
                htmltext = "table-no.htm";
        } else if (cond == 7) {
            if (npcId == EYE)
                htmltext = "31683-05.htm";
        } else if (cond == 8)
            if (npcId == EYE)
                htmltext = "31683-06.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        st.rollAndGive(EVIL_SPIRIT, 1, 1, 200, 100);
        if (st.haveQuestItem(EVIL_SPIRIT, 200)) {
            st.setCond(8);
            st.start();
        }
    }
}