package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.olympiad.OlympiadGame;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _551_OlympiadStarter extends Quest {
    // NPCs
    private static final int OLYMPIAD_MANAGER = 31688;

    // items
    private static final int MEDAL_OF_GLORY = 21874;
    private static final int OLYMPIAD_CHEST = 17169;
    private static final int OLYMPIAD_CERT1 = 17238;
    private static final int OLYMPIAD_CERT2 = 17239;
    private static final int OLYMPIAD_CERT3 = 17240;

    public _551_OlympiadStarter() {
        super(false);

        addStartNpc(OLYMPIAD_MANAGER);
        addTalkId(OLYMPIAD_MANAGER);
        addQuestItem(OLYMPIAD_CERT1, OLYMPIAD_CERT2, OLYMPIAD_CERT3);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == OLYMPIAD_MANAGER) {
            Player player = st.player;
            if (!player.isNoble() || player.getLevel() < 75 || player.getClassId().occupation() < 3)
                return "olympiad_operator_q0551_08.htm";

            if (st.isCreated()) {
                if (st.isNowAvailable())
                    return "olympiad_operator_q0551_01.htm";
                else
                    return "olympiad_operator_q0551_06.htm";
            } else if (st.isStarted()) {
                if (st.getQuestItemsCount(OLYMPIAD_CERT1, OLYMPIAD_CERT2, OLYMPIAD_CERT3) == 0)
                    return "olympiad_operator_q0551_04.htm";

                if (st.getQuestItemsCount(OLYMPIAD_CERT3) > 0) {
                    st.giveItems(OLYMPIAD_CHEST, 4);
                    st.giveItems(MEDAL_OF_GLORY, 5);
                    st.takeItems(OLYMPIAD_CERT1);
                    st.takeItems(OLYMPIAD_CERT2);
                    st.takeItems(OLYMPIAD_CERT3);
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest(this);
                    return "olympiad_operator_q0551_07.htm";
                } else
                    return "olympiad_operator_q0551_05.htm";
            }
        }

        return null;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("olympiad_operator_q0551_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("olympiad_operator_q0551_07.htm".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(OLYMPIAD_CERT3) > 0) {
                st.giveItems(OLYMPIAD_CHEST, 4);
                st.giveItems(MEDAL_OF_GLORY, 5);
                st.takeItems(OLYMPIAD_CERT1);
                st.takeItems(OLYMPIAD_CERT2);
                st.takeItems(OLYMPIAD_CERT3);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(this);
            } else if (st.getQuestItemsCount(OLYMPIAD_CERT2) > 0) {
                st.giveItems(OLYMPIAD_CHEST, 2);
                st.giveItems(MEDAL_OF_GLORY, 3); // от балды
                st.takeItems(OLYMPIAD_CERT1);
                st.takeItems(OLYMPIAD_CERT2);
                st.takeItems(OLYMPIAD_CERT3);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(this);
            } else if (st.getQuestItemsCount(OLYMPIAD_CERT1) > 0) {
                st.giveItems(OLYMPIAD_CHEST, 1);
                //st.giveItems(MEDAL_OF_GLORY, 5); ??
                st.takeItems(OLYMPIAD_CERT1);
                st.takeItems(OLYMPIAD_CERT2);
                st.takeItems(OLYMPIAD_CERT3);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(this);
            }
        }
        return event;
    }

    @Override
    public void onOlympiadEnd(OlympiadGame og, QuestState qs) {
        if (qs.getCond() == 1) {
            int count = qs.getInt("count") + 1;
            qs.set("count", count);
            if (count == 3) {
                qs.giveItems(OLYMPIAD_CERT1);
                qs.playSound(SOUND_ITEMGET);
            } else if (count == 5) {
                qs.giveItems(OLYMPIAD_CERT2);
                qs.playSound(SOUND_ITEMGET);
            } else if (count == 10) {
                qs.giveItems(OLYMPIAD_CERT3);
                qs.setCond(2);
                qs.playSound(SOUND_MIDDLE);
            }
        }
    }
}