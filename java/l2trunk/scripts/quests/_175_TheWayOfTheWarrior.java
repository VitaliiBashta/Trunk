package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _175_TheWayOfTheWarrior extends Quest {
    //NPC
    private static final int Kekropus = 32138;
    private static final int Perwan = 32133;
    //Quest items
    private static final int WolfTail = 9807;
    private static final int MuertosClaw = 9808;
    //items
    private static final int WarriorsSword = 9720;
    //MOBs
    private static final int MountainWerewolf = 22235;
    private static final int MountainWerewolfChief = 22235;
    private static final int MuertosArcher = 22236;
    private static final int MuertosGuard = 22239;
    private static final int MuertosScout = 22240;
    private static final int MuertosWarrior = 22242;
    private static final int MuertosCaptain = 22243;
    private static final int MuertosLieutenant = 22245;
    private static final int MuertosCommander = 22246;

    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    2,
                    3,
                    MountainWerewolf,
                    0,
                    WolfTail,
                    5,
                    35,
                    1
            },
            {
                    2,
                    3,
                    MountainWerewolfChief,
                    0,
                    WolfTail,
                    5,
                    40,
                    1
            },
            {
                    7,
                    8,
                    MuertosArcher,
                    0,
                    MuertosClaw,
                    10,
                    32,
                    1
            },
            {
                    7,
                    8,
                    MuertosGuard,
                    0,
                    MuertosClaw,
                    10,
                    44,
                    1
            },
            {
                    7,
                    8,
                    MuertosScout,
                    0,
                    MuertosClaw,
                    10,
                    48,
                    1
            },
            {
                    7,
                    8,
                    MuertosWarrior,
                    0,
                    MuertosClaw,
                    10,
                    56,
                    1
            },
            {
                    7,
                    8,
                    MuertosCaptain,
                    0,
                    MuertosClaw,
                    10,
                    60,
                    1
            },
            {
                    7,
                    8,
                    MuertosLieutenant,
                    0,
                    MuertosClaw,
                    10,
                    68,
                    1
            },
            {
                    7,
                    8,
                    MuertosCommander,
                    0,
                    MuertosClaw,
                    10,
                    72,
                    1
            }
    };

    public _175_TheWayOfTheWarrior() {
        super(false);

        addStartNpc(Kekropus);

        addTalkId(Perwan);

        for (int[] aDROPLIST_COND : DROPLIST_COND) addKillId(aDROPLIST_COND[2]);

        addQuestItem(WolfTail,MuertosClaw);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("32138-04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("32138-08.htm".equalsIgnoreCase(event)) {
            st.takeItems(MuertosClaw);

            st.giveItems(WarriorsSword);
            st.giveItems(ADENA_ID, 8799, false);
            st.player.addExpAndSp(20739, 1777);

            if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("p1q3")) {
                st.player.setVar("p1q3"); // flag for helper
                st.player.sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide."));
                st.giveItems(1060, 100); // healing potion
                for (int item = 4412; item <= 4417; item++)
                    st.giveItems(item, 10); // echo cry
                st.playTutorialVoice("tutorial_voice_026");
                st.giveItems(5789, 7000); // newbie ss
            }

            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Kekropus) {
            if (cond == 0) {
                if (st.player.getRace() != Race.kamael) {
                    htmltext = "32138-00.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() < 10) {
                    htmltext = "32138-01.htm";
                    st.exitCurrentQuest();

                } else
                    htmltext = "32138-02.htm";
            } else if (cond == 1)
                htmltext = "32138-04.htm";
            else if (cond == 4) {
                st.setCond(5);
                st.start();
                htmltext = "32138-05.htm";
            } else if (cond == 6) {
                st.setCond(7);
                st.start();
                htmltext = "32138-06.htm";
            } else if (cond == 8)
                htmltext = "32138-07.htm";
        } else if (npcId == Perwan)
            if (cond == 1) {
                st.setCond(2);
                st.start();
                htmltext = "32133-01.htm";
            } else if (cond == 3) {
                st.takeItems(WolfTail, -1);
                st.setCond(4);
                st.start();
                htmltext = "32133-02.htm";
            } else if (cond == 5) {
                st.setCond(6);
                st.start();
                htmltext = "32133-03.htm";
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        for (int[] aDROPLIST_COND : DROPLIST_COND)
            if (cond == aDROPLIST_COND[0] && npcId == aDROPLIST_COND[2])
                if (aDROPLIST_COND[3] == 0 || st.getQuestItemsCount(aDROPLIST_COND[3]) > 0)
                    if (aDROPLIST_COND[5] == 0)
                        st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[6]);
                    else if (st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[7], aDROPLIST_COND[5], aDROPLIST_COND[6]))
                        if (aDROPLIST_COND[1] != cond && aDROPLIST_COND[1] != 0) {
                            st.setCond(aDROPLIST_COND[1]);
                            st.start();
                        }
    }
}