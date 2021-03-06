package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.SystemMessage;

import java.util.stream.IntStream;

public final class _510_AClansReputation extends Quest {
    private static final int VALDIS = 31331;
    private static final int CLAW = 8767;
    private static final int CLAN_POINTS_REWARD = 100;

    public _510_AClansReputation() {
        super(PARTY_ALL);

        addStartNpc(VALDIS);

        addKillId(IntStream.rangeClosed(22215, 22217).toArray());
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        if (event.equals("31331-3.htm")) {
            if (cond == 0) {
                st.setCond(1);
                st.start();
            }
        } else if (event.equals("31331-6.htm")) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>";
        Player player = st.player;
        Clan clan = player.getClan();
        if (player.getClan() == null || !player.isClanLeader()) {
            st.exitCurrentQuest();
            htmltext = "31331-0.htm";
        } else if (player.getClan().getLevel() < 5) {
            st.exitCurrentQuest();
            htmltext = "31331-0.htm";
        } else {
            int cond = st.getCond();
            int id = st.getState();
            if (id == CREATED && cond == 0)
                htmltext = "31331-1.htm";
            else if (id == STARTED && cond == 1) {
                long count = st.getQuestItemsCount(CLAW);
                if (count == 0)
                    htmltext = "31331-4.htm";
                else if (count >= 1) {
                    htmltext = "31331-7.htm";// custom html
                    st.takeItems(CLAW);
                    int pointsCount = CLAN_POINTS_REWARD * (int) count;
                    if (count > 10)
                        pointsCount += count % 10 * 118;
                    int increasedPoints = clan.incReputation(pointsCount, true, "_510_AClansReputation");
                    player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(increasedPoints));
                }
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (!st.player.isClanLeader())
            st.exitCurrentQuest();
        else if (st.getState() == STARTED) {
            int npcId = npc.getNpcId();
            if (npcId >= 22215 && npcId <= 22218) {
                st.giveItems(CLAW);
                st.playSound(SOUND_ITEMGET);
            }
        }
    }
}
