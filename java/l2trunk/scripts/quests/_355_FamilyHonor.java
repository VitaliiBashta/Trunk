package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _355_FamilyHonor extends Quest {
    //NPC
    private static final int GALIBREDO = 30181;
    private static final int PATRIN = 30929;

    //CHANCES
    private static final int CHANCE_FOR_GALFREDOS_BUST = 80;
    private static final int CHANCE_FOR_GODDESS_BUST = 30;

    //ITEMS
    private static final int GALFREDOS_BUST = 4252;
    private static final int BUST_OF_ANCIENT_GODDESS = 4349;
    private static final int WORK_OF_BERONA = 4350;
    private static final int STATUE_PROTOTYPE = 4351;
    private static final int STATUE_ORIGINAL = 4352;
    private static final int STATUE_REPLICA = 4353;
    private static final int STATUE_FORGERY = 4354;

    public _355_FamilyHonor() {
        super(true);

        addStartNpc(GALIBREDO);
        addTalkId(PATRIN);

        // TIMAK ORC TROOPS
        addKillId(20767,20768,20769,20770);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "galicbredo_q0355_03.htm":
                return htmltext;
            case "galicbredo_q0355_04.htm":
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                break;
            case "galicbredo_q0355_07.htm":
                long count = st.getQuestItemsCount(BUST_OF_ANCIENT_GODDESS);
                st.takeItems(BUST_OF_ANCIENT_GODDESS, count);
                st.giveItems(WORK_OF_BERONA, count);
                break;
            case "patrin_q0355_01.htm":
                return htmltext;
            case "patrin_q0355_01a.htm":
                return htmltext;
            case "appraise":
                int appraising = Rnd.get(100);
                if (appraising < 20) {
                    htmltext = "patrin_q0355_07.htm";
                    st.takeItems(WORK_OF_BERONA, 1);
                } else if (appraising < 40) {
                    htmltext = "patrin_q0355_05.htm";
                    st.takeItems(WORK_OF_BERONA, 1);
                    st.giveItems(STATUE_REPLICA);
                } else if (appraising < 60) {
                    htmltext = "patrin_q0355_04.htm";
                    st.takeItems(WORK_OF_BERONA, 1);
                    st.giveItems(STATUE_ORIGINAL);
                } else if (appraising < 80) {
                    htmltext = "galicbredo_q0355_10.htm";
                    st.takeItems(WORK_OF_BERONA, 1);
                    st.giveItems(STATUE_FORGERY);
                } else if (appraising < 100) {
                    htmltext = "galicbredo_q0355_11.htm";
                    st.takeItems(WORK_OF_BERONA, 1);
                    st.giveItems(STATUE_PROTOTYPE);
                }
                break;
            case "galicbredo_q0355_09.htm":
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        long count = st.getQuestItemsCount(GALFREDOS_BUST);
        if (npcId == GALIBREDO) {
            if (cond == 0)
                if (st.player.getLevel() >= 36)
                    htmltext = "galicbredo_q0355_02.htm";
                else {
                    htmltext = "galicbredo_q0355_01.htm";
                    st.exitCurrentQuest();
                }
            else if (cond == 1)
                if (count > 0) {
                    long reward = count * 232;
                    if (count >= 100)
                        reward = reward + 5000; // custom - need more info
                    st.takeItems(GALFREDOS_BUST);
                    st.giveAdena(reward);
                    htmltext = "galicbredo_q0355_07a.htm";
                } else
                    htmltext = "galicbredo_q0355_08.htm";
        } else if (npcId == PATRIN)
            if (st.haveQuestItem(WORK_OF_BERONA))
                htmltext = "patrin_q0355_01.htm";
            else
                htmltext = "<html><head><body>You have nothing to appraise.</body></html>";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            if (Rnd.chance(CHANCE_FOR_GALFREDOS_BUST)) {
                st.giveItems(GALFREDOS_BUST);
                st.playSound(SOUND_ITEMGET);
            }
            if (Rnd.chance(CHANCE_FOR_GODDESS_BUST))
                st.giveItems(BUST_OF_ANCIENT_GODDESS);
        }
    }
}