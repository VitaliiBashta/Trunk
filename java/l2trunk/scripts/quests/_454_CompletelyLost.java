package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _454_CompletelyLost extends Quest {
    private static final int WoundedSoldier = 32738;
    private static final int Ermian = 32736;
    private static final List<Integer> rewardIDs = List.of(
            15792, 15798, 15795, 15801, 15808, 15804, 15809, 15810, 15811, 15660, 15666, 15663, 15667,
            15669, 15668, 15769, 15770, 15771, 15805, 15796, 15793, 15799, 15802, 15809, 15810, 15811,
            15672, 15664, 15661, 15670, 15671, 15769, 15770, 15771, 15800, 15803, 15806, 15807, 15797,
            15794, 15809, 15810, 15811, 15673, 15674, 15675, 15691, 15665, 15662, 15769, 15770, 15771);
    private static final List<Integer> rewardCounts = List.of(
            1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3,
            3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1,
            3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3);

    public _454_CompletelyLost() {
        super(PARTY_ALL);
        addStartNpc(WoundedSoldier);
        addTalkId(Ermian);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("wounded_soldier_q454_02.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("wounded_soldier_q454_03.htm")) {
            if (seeSoldier(npc, st.getPlayer()) == null) {
                npc.setFollowTarget(st.getPlayer());
                npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, st.getPlayer(), Config.FOLLOW_RANGE);
            }
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() == WoundedSoldier) {
            switch (st.getState()) {
                case CREATED:
                    if (st.isNowAvailable()) {
                        if (st.getPlayer().getLevel() >= 84)
                            htmltext = "wounded_soldier_q454_01.htm";
                        else {
                            htmltext = "wounded_soldier_q454_00.htm";
                            st.exitCurrentQuest(true);
                        }
                    } else
                        htmltext = "wounded_soldier_q454_00a.htm";
                    break;
                case STARTED:
                    if (st.getCond() == 1) {
                        htmltext = "wounded_soldier_q454_04.htm";
                        if (seeSoldier(npc, st.getPlayer()) == null) {
                            npc.setFollowTarget(st.getPlayer());
                            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, st.getPlayer(), Config.FOLLOW_RANGE);
                        }
                    }
                    break;
            }
        } else if (npc.getNpcId() == Ermian) {
            if (st.getCond() == 1) {
                if (seeSoldier(npc, st.getPlayer()) != null) {
                    htmltext = "ermian_q454_01.htm";
                    NpcInstance soldier = seeSoldier(npc, st.getPlayer());
                    soldier.doDie(null);
                    soldier.endDecayTask();
                    giveReward(st);
                    st.setState(COMPLETED);
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest(this);
                } else
                    htmltext = "ermian_q454_02.htm";
            }
        }

        return htmltext;
    }

    private NpcInstance seeSoldier(NpcInstance npc, Player player) {
        return npc.getAroundNpc(Config.FOLLOW_RANGE * 2, 300)
                .filter(n -> n.getNpcId() == WoundedSoldier)
                .filter(n -> n.getFollowTarget() != null)
                .filter(n -> n.getFollowTarget().getObjectId() == player.getObjectId())
                .findFirst().orElse(null);
    }

    private void giveReward(QuestState st) {
        int row = Rnd.get(0, rewardIDs.size() - 1);
        int id = rewardIDs.get(row);
        int count = rewardCounts.get(row);
        st.giveItems(id, count);
    }

}