package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _290_ThreatRemoval extends Quest {
    private static final int GuardPinaps = 30201;
    private static final List<Integer> SelMahumTrainers = List.of(22775, 22776, 22777, 22778);
    private static final List<Integer> SelMahumRecruits = List.of(22780, 22781, 22782, 22783, 22784, 22785);
    private static final int SelMahumIDTag = 15714;

    public _290_ThreatRemoval() {
        super(false);
        addStartNpc(GuardPinaps);
        addKillId(SelMahumTrainers);
        addKillId(SelMahumRecruits);
        addQuestItem(SelMahumIDTag);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("pinaps_q290_02.htm")) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("pinaps_q290_05.htm")) {
            st.takeItems(SelMahumIDTag, 400);
            switch (Rnd.get(1, 6)) {
                case 1:
                    st.giveItems(959, 1);
                    break;
                case 2:
                    st.giveItems(960, 1);
                    break;
                case 3:
                    st.giveItems(960, 2);
                    break;
                case 4:
                    st.giveItems(960, 3);
                    break;
                case 5:
                    st.giveItems(9552, 1);
                    break;
                case 6:
                    st.giveItems(9552, 2);
                    break;
            }
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("continue")) {
            htmltext = "pinaps_q290_06.htm";
        } else if (event.equalsIgnoreCase("quit")) {
            htmltext = "pinaps_q290_07.htm";
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npc.getNpcId() == GuardPinaps) {
            if (cond == 0) {
                if (st.player.getLevel() >= 82 && st.player.isQuestCompleted(_251_NoSecrets.class))
                    htmltext = "pinaps_q290_01.htm";
                else {
                    htmltext = "pinaps_q290_00.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1 && st.getQuestItemsCount(SelMahumIDTag) < 400)
                htmltext = "pinaps_q290_03.htm";
            else if (cond == 1 && st.haveQuestItem(SelMahumIDTag, 400))
                htmltext = "pinaps_q290_04.htm";
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 1) {
            if (SelMahumTrainers.contains(npc.getNpcId()))
                st.rollAndGive(SelMahumIDTag, 1, 53.2);
            else if (SelMahumRecruits.contains(npc.getNpcId()))
                st.rollAndGive(SelMahumIDTag, 1, 36.3);
        }
    }
}