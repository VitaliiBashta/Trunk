package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _326_VanquishRemnants extends Quest {
    //NPC
    private static final int Leopold = 30435;
    //Quest items
    private static final int RedCrossBadge = 1359;
    private static final int BlueCrossBadge = 1360;
    private static final int BlackCrossBadge = 1361;
    //items
    private static final int BlackLionMark = 1369;

    //Drop Cond
    private final List<Integer> RedCrossBadges = List.of(30425,20058,20437);
    private final List<Integer> BlueCrossBadges = List.of(20061,20063,20436);
    private final List<Integer> BlackCrossBadges = List.of(20066,20438,20076);

    public _326_VanquishRemnants() {
        addStartNpc(Leopold);
        //mob Drop
        addKillId(RedCrossBadges);
        addKillId(BlueCrossBadges);
        addKillId(BlackCrossBadges);
        addQuestItem(RedCrossBadge,BlueCrossBadge,BlackCrossBadge);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("leopold_q0326_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("leopold_q0326_03.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Leopold)
            if (st.player.getLevel() < 21) {
                htmltext = "leopold_q0326_01.htm";
                st.exitCurrentQuest();
            } else if (cond == 0)
                htmltext = "leopold_q0326_02.htm";
            else if (cond == 1 && !st.haveAnyQuestItems(RedCrossBadge,BlueCrossBadge,BlackCrossBadge))
                htmltext = "leopold_q0326_04.htm";
            else if (cond == 1) {
                if (st.getQuestItemsCount(RedCrossBadge) + st.getQuestItemsCount(BlueCrossBadge) + st.getQuestItemsCount(BlackCrossBadge) >= 100) {
                    if (st.getQuestItemsCount(BlackLionMark) == 0) {
                        htmltext = "leopold_q0326_09.htm";
                        st.giveItems(BlackLionMark);
                    } else
                        htmltext = "leopold_q0326_06.htm";
                } else
                    htmltext = "leopold_q0326_05.htm";
                st.giveAdena(st.getQuestItemsCount(RedCrossBadge) * 89 + st.getQuestItemsCount(BlueCrossBadge) * 95 + st.getQuestItemsCount(BlackCrossBadge) * 101);
                st.takeAllItems(RedCrossBadge,BlueCrossBadge,BlackCrossBadge);
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() == STARTED) {
            if (RedCrossBadges.contains(npc.getNpcId()))
                st.giveItems(RedCrossBadge);
            if (BlueCrossBadges.contains(npc.getNpcId()))
                st.giveItems(BlackCrossBadge);
            if (BlackCrossBadges.contains(npc.getNpcId()))
                st.giveItems(BlackCrossBadge);
        }
    }
}