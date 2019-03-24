package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _063_PathToWarder extends Quest {
    private static final int GobiesOrders = 9764;
    private static final int HumansReply = 9766;
    private static final int LettertotheDarkElves = 9767;
    private static final int DarkElvesReply = 9768;
    private static final int SteelrazorEvaluation = 9772;
    private final int Sione = 32195;
    private final int Gobie = 32198;
    private final int Patrol = 20053;
    private final int Novice = 20782;
    private final int Bathis = 30332;
    private final int Tobias = 30297;
    private final int Tak = 27337;
    private final int Maille = 20919;
    private final int Maille_scout = 20920;
    private final int Maille_guard = 20921;
    private final int OlMahumOrders = 9762;
    private final int OlMahumOrganizationChart = 9763;
    private final int LettertotheHumans = 9765;
    private final int ReporttoSione = 9769;
    private final int EmptySoulCrystal = 9770; //empty
    private final int TaksCapturedSoul = 9771;

    public _063_PathToWarder() {
        addStartNpc(Sione);
        addTalkId(Gobie, Bathis, Tobias);
        addKillId(Patrol, Novice, Tak, Maille, Maille_scout, Maille_guard);
        addQuestItem(OlMahumOrganizationChart, OlMahumOrders, TaksCapturedSoul, EmptySoulCrystal);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equals("master_sione_q0063_06.htm")) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        }
        switch (event) {
            case "master_sione_q0063_08.htm":
                st.setCond(2);
                break;
            case "captain_bathia_q0063_04.htm":
                st.takeItems(LettertotheHumans, 1);
                st.giveItems(HumansReply);
                st.setCond(6);
                break;
            case "master_gobie_q0063_08.htm":
                st.takeItems(HumansReply);
                st.giveItems(LettertotheDarkElves, 1);
                st.setCond(7);
                break;
            case "master_tobias_q0063_05.htm":
                st.takeItems(LettertotheDarkElves, 1);
                st.giveItems(DarkElvesReply);
                st.setCond(8);
                break;
            case "master_gobie_q0063_11.htm":
                st.takeItems(DarkElvesReply, 1);
                st.giveItems(ReporttoSione);
                st.setCond(9);
                break;
            case "master_gobie_q0063_16.htm":
                st.takeItems(EmptySoulCrystal, 1);
                st.setCond(11);
                break;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        int state = st.getState();
        if (npcId == Sione)
            if (state == CREATED) {
                if (st.player.getClassId() != ClassId.femaleSoldier) {
                    htmltext = "master_sione_q0063_04.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() < 18) {
                    htmltext = "master_sione_q0063_02.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "master_sione_q0063_05.htm";
            } else {
                if (cond == 1)
                    htmltext = "master_sione_q0063_06.htm";
                if (cond == 3)
                    if (st.getQuestItemsCount(OlMahumOrders) < 10 && st.getQuestItemsCount(OlMahumOrganizationChart) < 5)
                        htmltext = "master_sione_q0063_09.htm";
                    else {
                        htmltext = "master_sione_q0063_10.htm";
                        st.setCond(4);
                        st.takeAllItems(OlMahumOrders,OlMahumOrganizationChart);
                        st.giveItems(GobiesOrders);
                    }
                if (cond == 9) {
                    st.takeItems(ReporttoSione, 1);
                    st.setCond(10);
                    htmltext = "master_sione_q0063_13.htm";
                }
            }
        if (npcId == Gobie) {
            if (cond == 4)
                if (st.getQuestItemsCount(GobiesOrders) < 1)
                    htmltext = "master_gobie_q0063_01.htm";
                else {
                    htmltext = "master_gobie_q0063_03.htm";
                    st.takeItems(GobiesOrders);
                    st.giveItems(LettertotheHumans);
                    st.setCond(5);
                }
            if (cond == 6)
                htmltext = "master_gobie_q0063_05.htm";
            if (cond == 8)
                htmltext = "master_gobie_q0063_10.htm";
            if (cond == 10)
                htmltext = "master_gobie_q0063_14.htm";
            if (cond == 11)
                htmltext = "master_gobie_q0063_17.htm";
            if (cond == 12)
                if (st.haveQuestItem(TaksCapturedSoul)) {
                    st.takeItems(TaksCapturedSoul);
                    if (st.player.getClassId().occupation() == 0) {
                        st.giveItems(SteelrazorEvaluation);
                        if (!st.player.isVarSet("prof1")) {
                            st.player.setVar("prof1");
                            st.addExpAndSp(160267, 11023);
                            st.giveAdena( 81900);
                        }
                    }
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest();
                    htmltext = "master_gobie_q0063_20.htm";
                } else
                    htmltext = "master_gobie_q0063_19.htm";
        }
        if (npcId == Bathis && cond == 5)
            htmltext = "captain_bathia_q0063_01.htm";
        if (npcId == Tobias && cond == 7)
            htmltext = "master_tobias_q0063_01.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 2) {
            if (npcId == Patrol) {
                st.giveItems(OlMahumOrganizationChart);
                st.playSound(SOUND_ITEMGET);
            } else if (npcId == Novice) {
                st.giveItems(OlMahumOrders);
                st.playSound(SOUND_ITEMGET);
            }
            if (st.getQuestItemsCount(OlMahumOrders) > 9 && st.getQuestItemsCount(OlMahumOrganizationChart) > 4) {
                st.setCond(3);
                st.playSound(SOUND_MIDDLE);
            }
        }
        if (cond == 11)
            if ((npcId == Maille || npcId == Maille_scout || npcId == Maille_guard) && Rnd.chance(20))
                st.addSpawn(Tak);
            else if (npcId == Tak) {
                st.takeItems(EmptySoulCrystal, 1);
                st.giveItems(TaksCapturedSoul);
                st.setCond(12);
                st.playSound(SOUND_MIDDLE);
            }
    }
}