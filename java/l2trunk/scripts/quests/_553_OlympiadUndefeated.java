package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.olympiad.OlympiadGame;
import l2trunk.gameserver.model.entity.olympiad.OlympiadTeam;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _553_OlympiadUndefeated extends Quest {
    // NPCs
    private static final int OLYMPIAD_MANAGER = 31688;

    // items
    private static final int MEDAL_OF_GLORY = 21874;
    private static final int OLYMPIAD_CHEST = 17169;
    private static final int WINS_CONFIRMATION1 = 17244;
    private static final int WINS_CONFIRMATION2 = 17245;
    private static final int WINS_CONFIRMATION3 = 17246;

    public _553_OlympiadUndefeated() {
        super(false);

        addStartNpc(OLYMPIAD_MANAGER);
        addTalkId(OLYMPIAD_MANAGER);
        addQuestItem(WINS_CONFIRMATION1, WINS_CONFIRMATION2, WINS_CONFIRMATION3);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == OLYMPIAD_MANAGER) {
            Player player = st.player;
            if (!player.isNoble() || player.getLevel() < 75 || player.getClassId().occupation() < 3)
                return "olympiad_operator_q0553_08.htm";

            if (st.isCreated()) {
                if (st.isNowAvailable())
                    return "olympiad_operator_q0553_01.htm";
                else
                    return "olympiad_operator_q0553_06.htm";
            } else if (st.isStarted()) {
                if (st.getQuestItemsCount(WINS_CONFIRMATION1, WINS_CONFIRMATION2, WINS_CONFIRMATION3) == 0)
                    return "olympiad_operator_q0553_04.htm";

                if (st.haveQuestItem(WINS_CONFIRMATION3) ) {
                    st.giveItems(OLYMPIAD_CHEST, 6);
                    st.giveItems(MEDAL_OF_GLORY, 5);
                    st.takeItems(WINS_CONFIRMATION1);
                    st.takeItems(WINS_CONFIRMATION2);
                    st.takeItems(WINS_CONFIRMATION3);
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest(this);
                    return "olympiad_operator_q0553_07.htm";
                } else
                    return "olympiad_operator_q0553_05.htm";
            }
        }

        return null;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("olympiad_operator_q0553_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        if ("olympiad_operator_q0553_07.htm".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(WINS_CONFIRMATION3) > 0) {
                st.giveItems(OLYMPIAD_CHEST, 6);
                st.giveItems(MEDAL_OF_GLORY, 5);
                st.takeItems(WINS_CONFIRMATION1);
                st.takeItems(WINS_CONFIRMATION2);
                st.takeItems(WINS_CONFIRMATION3);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(this);
            } else if (st.getQuestItemsCount(WINS_CONFIRMATION2) > 0) {
                st.giveItems(OLYMPIAD_CHEST, 3);
                st.giveItems(MEDAL_OF_GLORY, 3); // от балды
                st.takeItems(WINS_CONFIRMATION1);
                st.takeItems(WINS_CONFIRMATION2);
                st.takeItems(WINS_CONFIRMATION3);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(this);
            } else if (st.getQuestItemsCount(WINS_CONFIRMATION1) > 0) {
                st.giveItems(OLYMPIAD_CHEST);
                st.takeItems(WINS_CONFIRMATION1);
                st.takeItems(WINS_CONFIRMATION2);
                st.takeItems(WINS_CONFIRMATION3);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(this);
            }
        }
        return event;
    }

    @Override
    public void onOlympiadEnd(OlympiadGame og, QuestState qs) {
        if (qs.getCond() == 1) {
            int count = qs.getInt("count");
            OlympiadTeam winner = og.getWinnerTeam();
            if (winner != null && winner.contains(qs.player.objectId()))
                count++;
            else
                count = 0;

            qs.set("count", count);
            if (count == 2 && qs.getQuestItemsCount(WINS_CONFIRMATION1) == 0) {
                qs.giveItems(WINS_CONFIRMATION1);
                qs.playSound(SOUND_ITEMGET);
            } else if (count == 5 && qs.getQuestItemsCount(WINS_CONFIRMATION2) == 0) {
                qs.giveItems(WINS_CONFIRMATION2);
                qs.playSound(SOUND_ITEMGET);
            } else if (count == 10 && qs.getQuestItemsCount(WINS_CONFIRMATION3) == 0) {
                qs.giveItems(WINS_CONFIRMATION3, 2);
                qs.setCond(2);
                qs.playSound(SOUND_MIDDLE);
            }
            if (count < 10 && qs.getQuestItemsCount(WINS_CONFIRMATION3) > 0)
                qs.takeItems(WINS_CONFIRMATION3);
            if (count < 5 && qs.getQuestItemsCount(WINS_CONFIRMATION2) > 0)
                qs.takeItems(WINS_CONFIRMATION2);
            if (count < 2 && qs.getQuestItemsCount(WINS_CONFIRMATION1) > 0)
                qs.takeItems(WINS_CONFIRMATION1);
        }
    }
}
