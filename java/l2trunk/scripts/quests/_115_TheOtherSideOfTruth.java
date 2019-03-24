package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _115_TheOtherSideOfTruth extends Quest {
    // NPCs
    private static final int Rafforty = 32020;
    private static final int Misa = 32018;
    private static final int Kierre = 32022;
    private static final int Ice_Sculpture1 = 32021;
    private static final int Ice_Sculpture2 = 32077;
    private static final int Ice_Sculpture3 = 32078;
    private static final int Ice_Sculpture4 = 32079;
    //private static int Suspicious_Man = 32019;
    // Quest items
    private static final int Misas_Letter = 8079;
    private static final int Raffortys_Letter = 8080;
    private static final int Piece_of_Tablet = 8081;
    private static final int Report_Piece = 8082;

    public _115_TheOtherSideOfTruth() {
        addStartNpc(Rafforty);
        addTalkId(Misa,Kierre,Ice_Sculpture1,Ice_Sculpture2,Ice_Sculpture3,Ice_Sculpture4);
        addQuestItem(Misas_Letter,Raffortys_Letter,Piece_of_Tablet,Report_Piece);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        if ("32020-02.htm".equalsIgnoreCase(event) && _state == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        }
        if (_state != STARTED)
            return event;

        if ("32020-06.htm".equalsIgnoreCase(event) || "32020-08a.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        } else if ("32020-05.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.takeItems(Misas_Letter);
            st.playSound(SOUND_MIDDLE);
        } else if ("32020-08.htm".equalsIgnoreCase(event) || "32020-07a.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
        } else if ("32020-12.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
        } else if ("32018-04.htm".equalsIgnoreCase(event)) {
            st.setCond(7);
            st.takeItems(Raffortys_Letter);
            st.playSound(SOUND_MIDDLE);
        } else if ("Sculpture-04a.htm".equalsIgnoreCase(event)) {
            st.setCond(8);
            st.playSound(SOUND_MIDDLE);
            if (!st.isSet("32021")  && !st.isSet("32077"))
                st.giveItems(Piece_of_Tablet);

            //Functions.npcSay(st.addSpawn(Suspicious_Man, 117890, -126478, -2584, 0, 0, 300000), "This looks like the right place...");

            return "Sculpture-04.htm";
        } else if ("32022-02.htm".equalsIgnoreCase(event)) {
            st.setCond(9);
            st.giveItems(Report_Piece);
            st.playSound(SOUND_MIDDLE);

            //Functions.npcSay(st.addSpawn(Suspicious_Man, 104562, -107598, -3688, 0, 0, 300000), "We meet again.");
        } else if ("32020-16.htm".equalsIgnoreCase(event)) {
            st.setCond(10);
            st.takeItems(Report_Piece, 1);
            st.playSound(SOUND_MIDDLE);
        } else if ("32020-18.htm".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(Piece_of_Tablet)) {
                st.giveAdena( 60044);
                st.playSound(SOUND_FINISH);
                st.finish();
            } else {
                st.setCond(11);
                st.playSound(SOUND_MIDDLE);
                return "32020-19.htm";
            }
        } else if ("32020-19.htm".equalsIgnoreCase(event)) {
            st.setCond(11);
            st.playSound(SOUND_MIDDLE);
        } else if (event.startsWith("32021") || event.startsWith("32077")) {
            if (event.contains("-pick")) {
                st.set("talk");
                event = event.replace("-pick", "");
            }
            st.set(event, 1);
            return "Sculpture-05.htm";
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int _state = st.getState();
        if (_state == COMPLETED)
            return "completed";
        int npcId = npc.getNpcId();
        if (_state == CREATED) {
            if (npcId != Rafforty)
                return "noquest";
            if (st.player.getLevel() >= 53) {
                st.setCond(0);
                return "32020-01.htm";
            }
            st.exitCurrentQuest();
            return "32020-00.htm";
        }

        int cond = st.getCond();
        if (npcId == Rafforty && _state == STARTED) {
            if (cond == 1)
                return "32020-03.htm";
            else if (cond == 2)
                return "32020-04.htm";
            else if (cond == 3)
                return "32020-05.htm";
            else if (cond == 4)
                return "32020-11.htm";
            else if (cond == 5) {
                st.setCond(6);
                st.giveItems(Raffortys_Letter);
                st.playSound(SOUND_MIDDLE);
                return "32020-13.htm";
            } else if (cond == 6)
                return "32020-14.htm";
            else if (cond == 9)
                return "32020-15.htm";
            else if (cond == 10)
                return "32020-17.htm";
            else if (cond == 11)
                return "32020-20.htm";
            else if (cond == 12) {
                st.giveAdena( 115673);
                st.addExpAndSp(493595, 40442);
                st.playSound(SOUND_FINISH);
                st.finish();
                return "32020-18.htm";
            }
        } else if (npcId == Misa && _state == STARTED) {
            if (cond == 1) {
                st.setCond(2);
                st.giveItems(Misas_Letter);
                st.playSound(SOUND_MIDDLE);
                return "32018-01.htm";
            } else if (cond == 2)
                return "32018-02.htm";
            else if (cond == 6)
                return "32018-03.htm";
            else if (cond == 7)
                return "32018-05.htm";
        } else if (npcId == Kierre && _state == STARTED) {
            if (cond == 8)
                return "32022-01.htm";
            else if (cond == 9)
                return "32022-03.htm";
        } else if ((npcId == Ice_Sculpture1 || npcId == Ice_Sculpture2 || npcId == Ice_Sculpture3 || npcId == Ice_Sculpture4) && _state == STARTED)
            if (cond == 7) {
                boolean npcId_flag = st.isSet("" + npcId);
                if (npcId == Ice_Sculpture1 || npcId == Ice_Sculpture2) {
                    boolean talk_flag = st.isSet("talk");
                    return npcId_flag ? "Sculpture-02.htm" : talk_flag ? "Sculpture-06.htm" : "Sculpture-03-" + npcId + ".htm";
                } else if (npcId_flag)
                    return "Sculpture-02.htm";
                else {
                    st.set("" + npcId);
                    return "Sculpture-01.htm";
                }
            } else if (cond == 8)
                return "Sculpture-04.htm";
            else if (cond == 11) {
                st.setCond(12);
                st.giveItems(Piece_of_Tablet);
                st.playSound(SOUND_MIDDLE);
                return "Sculpture-07.htm";
            } else if (cond == 12)
                return "Sculpture-08.htm";

        return "noquest";
    }
}