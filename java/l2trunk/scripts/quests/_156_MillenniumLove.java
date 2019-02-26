package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _156_MillenniumLove extends Quest {
    private static final int GR_COMP_PACKAGE_SS = 5250;
    private static final int GR_COMP_PACKAGE_SPS = 5256;
    private final int LILITHS_LETTER = 1022;
    private final int THEONS_DIARY = 1023;

    public _156_MillenniumLove() {
        super(false);

        addStartNpc(30368);
        addTalkId(30369);
        addQuestItem(LILITHS_LETTER, THEONS_DIARY);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "30368-06.htm":
                st.giveItems(LILITHS_LETTER);
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                break;
            case "156_1":
                st.takeItems(LILITHS_LETTER);
                st.giveItemIfNotHave(THEONS_DIARY);
                st.setCond(2);
                htmltext = "30369-03.htm";
                break;
            case "156_2":
                st.takeItems(LILITHS_LETTER);
                st.playSound(SOUND_FINISH);
                htmltext = "30369-04.htm";
                st.finish();
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == 30368) {
            if (cond == 0) {
                if (st.player.getLevel() >= 15)
                    htmltext = "30368-02.htm";
                else {
                    htmltext = "30368-05.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1 && st.haveQuestItem(LILITHS_LETTER))
                htmltext = "30368-07.htm";
            else if (cond == 2 && st.haveQuestItem(THEONS_DIARY)) {
                st.takeItems(THEONS_DIARY);
                if (st.player.getClassId().isMage)
                    st.giveItems(GR_COMP_PACKAGE_SPS);
                else
                    st.giveItems(GR_COMP_PACKAGE_SS);
                st.addExpAndSp(3000, 0);
                st.playSound(SOUND_FINISH);
                htmltext = "30368-08.htm";
                st.finish();
            }
        } else if (npcId == 30369)
            if (cond == 1 && !st.haveQuestItem(LILITHS_LETTER))
                htmltext = "30369-02.htm";
            else if (cond == 2 && st.haveQuestItem(THEONS_DIARY))
                htmltext = "30369-05.htm";
        return htmltext;
    }
}