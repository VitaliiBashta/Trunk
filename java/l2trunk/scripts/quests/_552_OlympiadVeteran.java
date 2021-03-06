package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.olympiad.OlympiadGame;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _552_OlympiadVeteran extends Quest {
    // NPCs
    private static final int OLYMPIAD_MANAGER = 31688;

    private static final int OLYMPIAD_CHEST = 17169;
    private static final int TEAM_CERTIFICATE = 17241;
    private static final int CLASS_FREE_CERTIFICATE = 17242;
    private static final int CLASS_CERTIFICATE = 17243;

    public _552_OlympiadVeteran() {
        addStartNpc(OLYMPIAD_MANAGER);
        addTalkId(OLYMPIAD_MANAGER);
        addQuestItem(TEAM_CERTIFICATE, CLASS_FREE_CERTIFICATE, CLASS_CERTIFICATE);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == OLYMPIAD_MANAGER) {
            Player player = st.player;
            if (!player.isNoble() || player.getLevel() < 75 || player.getClassId().occupation() < 3)
                return "olympiad_operator_q0552_08.htm";

            if (st.isCreated()) {
                if (st.isNowAvailable())
                    return "olympiad_operator_q0552_01.htm";
                else
                    return "olympiad_operator_q0552_06.htm";
            } else if (st.isStarted()) {
                if (st.getQuestItemsCount(TEAM_CERTIFICATE, CLASS_FREE_CERTIFICATE, CLASS_CERTIFICATE) == 0)
                    return "olympiad_operator_q0552_04.htm";
                else if (st.haveAllQuestItems(TEAM_CERTIFICATE,CLASS_FREE_CERTIFICATE,CLASS_CERTIFICATE) ) {
                    st.giveItems(OLYMPIAD_CHEST, 3);
                    st.takeAllItems(TEAM_CERTIFICATE,CLASS_FREE_CERTIFICATE,CLASS_CERTIFICATE);
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest(this);
                    return "olympiad_operator_q0552_07.htm";
                }
                return "olympiad_operator_q0552_05.htm";
            }
        }

        return null;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("olympiad_operator_q0552_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("olympiad_operator_q0552_07.htm".equalsIgnoreCase(event)) {
            int count = (int)st.getQuestItemsCount(TEAM_CERTIFICATE,CLASS_FREE_CERTIFICATE,CLASS_CERTIFICATE);
            if (count > 0) {
                st.giveItems(OLYMPIAD_CHEST, count);
                st.takeAllItems(TEAM_CERTIFICATE,CLASS_FREE_CERTIFICATE,CLASS_CERTIFICATE);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(this);
            }
        }
        return event;
    }

    @Override
    public void onOlympiadEnd(OlympiadGame og, QuestState qs) {
        if (qs.getCond() != 1)
            return;

        switch (og.getType()) {
            case TEAM:
                qs.inc("count1");
                if (qs.getInt("count1") == 5) {
                    qs.giveItems(TEAM_CERTIFICATE);
                    if (qs.getInt("count2") >= 5 && qs.getInt("count3") >= 5) {
                        qs.setCond(2);
                        qs.playSound(SOUND_MIDDLE);
                    } else
                        qs.playSound(SOUND_ITEMGET);
                }
                break;
            case CLASSED:
                qs.inc("count2");
                if (qs.getInt("count2") == 5) {
                    qs.giveItems(CLASS_CERTIFICATE);
                    if (qs.getInt("count1") >= 5 && qs.getInt("count3") >= 5) {
                        qs.setCond(2);
                        qs.playSound(SOUND_MIDDLE);
                    } else
                        qs.playSound(SOUND_ITEMGET);
                }
                break;
            case NON_CLASSED:
                qs.inc("count3");
                if (qs.getInt("count3") == 5) {
                    qs.giveItems(CLASS_FREE_CERTIFICATE);
                    if (qs.getInt("count1") >= 5 && qs.getInt("count2") >= 5) {
                        qs.setCond(2);
                        qs.playSound(SOUND_MIDDLE);
                    } else
                        qs.playSound(SOUND_ITEMGET);
                }
                break;
        }
    }
}
