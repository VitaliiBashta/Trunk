package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _126_IntheNameofEvilPart2 extends Quest {
    private final int Mushika = 32114;
    private final int Asamah = 32115;
    private final int UluKaimu = 32119;
    private final int BaluKaimu = 32120;
    private final int ChutaKaimu = 32121;
    private final int WarriorGrave = 32122;
    private final int ShilenStoneStatue = 32109;

    private final int BONEPOWDER = 8783;
    private final int EPITAPH = 8781;
    private static final int EWA = 729;

    public _126_IntheNameofEvilPart2() {
        super(false);

        addStartNpc(Asamah);
        addTalkId(Mushika);
        addTalkId(UluKaimu);
        addTalkId(BaluKaimu);
        addTalkId(ChutaKaimu);
        addTalkId(WarriorGrave);
        addTalkId(ShilenStoneStatue);
        addQuestItem(BONEPOWDER, EPITAPH);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("asamah_q126_4.htm".equalsIgnoreCase(event)) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
            st.takeItems(EPITAPH);
        } else if ("asamah_q126_7.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("ulukaimu_q126_2.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("ulukaimu_q126_8.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
        } else if ("ulukaimu_q126_10.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
        } else if ("balukaimu_q126_2.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.playSound(SOUND_MIDDLE);
        } else if ("balukaimu_q126_7.htm".equalsIgnoreCase(event)) {
            st.setCond(7);
            st.playSound(SOUND_MIDDLE);
        } else if ("balukaimu_q126_9.htm".equalsIgnoreCase(event)) {
            st.setCond(8);
            st.playSound(SOUND_MIDDLE);
        } else if ("chutakaimu_q126_2.htm".equalsIgnoreCase(event)) {
            st.setCond(9);
            st.playSound(SOUND_MIDDLE);
        } else if ("chutakaimu_q126_9.htm".equalsIgnoreCase(event)) {
            st.setCond(10);
            st.playSound(SOUND_MIDDLE);
        } else if ("chutakaimu_q126_14.htm".equalsIgnoreCase(event)) {
            st.setCond(11);
            st.playSound(SOUND_MIDDLE);
        } else if ("warriorgrave_q126_2.htm".equalsIgnoreCase(event)) {
            st.setCond(12);
            st.playSound(SOUND_MIDDLE);
        } else if ("warriorgrave_q126_10.htm".equalsIgnoreCase(event)) {
            st.setCond(13);
            st.playSound(SOUND_MIDDLE);
        } else if ("warriorgrave_q126_19.htm".equalsIgnoreCase(event)) {
            st.setCond(14);
            st.playSound(SOUND_MIDDLE);
        } else if ("warriorgrave_q126_20.htm".equalsIgnoreCase(event)) {
            st.setCond(15);
            st.playSound(SOUND_MIDDLE);
        } else if ("warriorgrave_q126_23.htm".equalsIgnoreCase(event)) {
            st.setCond(16);
            st.playSound(SOUND_MIDDLE);
        } else if ("warriorgrave_q126_25.htm".equalsIgnoreCase(event)) {
            st.setCond(17);
            st.giveItems(BONEPOWDER);
            st.playSound(SOUND_MIDDLE);
        } else if ("warriorgrave_q126_27.htm".equalsIgnoreCase(event)) {
            st.setCond(18);
            st.playSound(SOUND_MIDDLE);
        } else if ("shilenstatue_q126_2.htm".equalsIgnoreCase(event)) {
            st.setCond(19);
            st.playSound(SOUND_MIDDLE);
        } else if ("shilenstatue_q126_13.htm".equalsIgnoreCase(event)) {
            st.setCond(20);
            st.takeItems(BONEPOWDER);
            st.playSound(SOUND_MIDDLE);
        } else if ("asamah_q126_10.htm".equalsIgnoreCase(event)) {
            st.setCond(21);
            st.playSound(SOUND_MIDDLE);
        } else if ("asamah_q126_17.htm".equalsIgnoreCase(event)) {
            st.setCond(22);
            st.playSound(SOUND_MIDDLE);
        } else if ("mushika_q126_3.htm".equalsIgnoreCase(event)) {
            st.setCond(23);
            st.playSound(SOUND_MIDDLE);
        } else if ("mushika_q126_4.htm".equalsIgnoreCase(event)) {
            st.giveItems(EWA);
            st.giveItems(ADENA_ID, 460483);
            st.addExpAndSp(1015973, 102802);
            st.playSound(SOUND_FINISH);
            st.setState(COMPLETED);
            st.exitCurrentQuest(false);
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == Asamah) {
            if (cond == 0) {
                if (st.player.getLevel() >= 77 && st.player.isQuestCompleted(_125_InTheNameOfEvilPart1.class))
                    htmltext = "asamah_q126_1.htm";
                else {
                    htmltext = "asamah_q126_0.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1)
                htmltext = "asamah_q126_4.htm";
            else if (cond == 20)
                htmltext = "asamah_q126_8.htm";
            else if (cond == 21)
                htmltext = "asamah_q126_10.htm";
            else if (cond == 22)
                htmltext = "asamah_q126_17.htm";
            else
                htmltext = "asamah_q126_0a.htm";
        } else if (npcId == UluKaimu) {
            if (cond == 2)
                htmltext = "ulukaimu_q126_1.htm";
            else if (cond == 3)
                htmltext = "ulukaimu_q126_2.htm";
            else if (cond == 4)
                htmltext = "ulukaimu_q126_8.htm";
            else if (cond == 5)
                htmltext = "ulukaimu_q126_10.htm";
            else
                htmltext = "ulukaimu_q126_0.htm";
        } else if (npcId == BaluKaimu) {
            if (cond == 5)
                htmltext = "balukaimu_q126_1.htm";
            else if (cond == 6)
                htmltext = "balukaimu_q126_2.htm";
            else if (cond == 7)
                htmltext = "balukaimu_q126_7.htm";
            else if (cond == 8)
                htmltext = "balukaimu_q126_9.htm";
            else
                htmltext = "balukaimu_q126_0.htm";
        } else if (npcId == ChutaKaimu) {
            if (cond == 8)
                htmltext = "chutakaimu_q126_1.htm";
            else if (cond == 9)
                htmltext = "chutakaimu_q126_2.htm";
            else if (cond == 10)
                htmltext = "chutakaimu_q126_9.htm";
            else if (cond == 11)
                htmltext = "chutakaimu_q126_14.htm";
            else
                htmltext = "chutakaimu_q126_0.htm";
        } else if (npcId == WarriorGrave) {
            if (cond == 11)
                htmltext = "warriorgrave_q126_1.htm";
            else if (cond == 12)
                htmltext = "warriorgrave_q126_2.htm";
            else if (cond == 13)
                htmltext = "warriorgrave_q126_10.htm";
            else if (cond == 14)
                htmltext = "warriorgrave_q126_19.htm";
            else if (cond == 15)
                htmltext = "warriorgrave_q126_20.htm";
            else if (cond == 16)
                htmltext = "warriorgrave_q126_23.htm";
            else if (cond == 17)
                htmltext = "warriorgrave_q126_25.htm";
            else if (cond == 18)
                htmltext = "warriorgrave_q126_27.htm";
            else
                htmltext = "warriorgrave_q126_0.htm";
        } else if (npcId == ShilenStoneStatue) {
            if (cond == 18)
                htmltext = "shilenstatue_q126_1.htm";
            else if (cond == 19)
                htmltext = "shilenstatue_q126_2.htm";
            else if (cond == 20)
                htmltext = "shilenstatue_q126_13.htm";
            else
                htmltext = "shilenstatue_q126_0.htm";
        } else if (npcId == Mushika) {
            if (cond == 22)
                htmltext = "mushika_q126_1.htm";
            else if (cond == 23)
                htmltext = "mushika_q126_3.htm";
            else
                htmltext = "mushika_q126_0.htm";
        }

        return htmltext;
    }
}