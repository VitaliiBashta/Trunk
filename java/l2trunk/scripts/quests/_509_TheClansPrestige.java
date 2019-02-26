package l2trunk.scripts.quests;

import l2trunk.commons.lang.Pair;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Util;

import java.util.List;
import java.util.Map;

public final class _509_TheClansPrestige extends Quest {
    // Quest NPC
    private static final int GRAND_MAGISTER_VALDIS = 31331;

    // Quest items
    private static final int DAIMONS_EYES = 8489; // Daimon's Eyes : Eyes obtained by defeating Daimon the White-Eyed.
    private static final int HESTIAS_FAIRY_STONE = 8490; // Hestia's Fairy Stone : Obtain this Stone by defeating Hestia, Guardian Deity of the Hot Springs.
    private static final int NUCLEUS_OF_LESSER_GOLEM = 8491; // Nucleus of Lesser Golem : Nucleus obtained by defeating Plague Golem.
    private static final int FALSTONS_FANG = 8492; // Falston's Fang : Fang obtained by defeating Demon's Agent Falston.

    // Quest Raid Bosses
    private static final int DAIMON_THE_WHITE_EYED = 25290;
    private static final int HESTIA_GUARDIAN_DEITY = 25293;
    private static final int PLAGUE_GOLEM = 25523;
    private static final int DEMONS_AGENT_FALSTON = 25322;

    // id:[RaidBossNpcId,questItemId]
    private static final Map<Integer, Pair<Integer, Integer>> REWARDS_LIST = Map.of(
            DAIMON_THE_WHITE_EYED, Pair.of(DAIMONS_EYES, 1378),
            HESTIA_GUARDIAN_DEITY, Pair.of(HESTIAS_FAIRY_STONE, 1378),
            PLAGUE_GOLEM, Pair.of(NUCLEUS_OF_LESSER_GOLEM, 1070),
            DEMONS_AGENT_FALSTON, Pair.of(FALSTONS_FANG, 782));

    private static final List<Location> RADAR = List.of(
            Location.of(0, 0, 0),
            Location.of(186304, -43744, -3193),
            Location.of(134672, -115600, -1216),
            Location.of(168641, -60417, -3888),
            Location.of(93296, -75104, -1824));

    public _509_TheClansPrestige() {
        super(PARTY_ALL);

        addStartNpc(GRAND_MAGISTER_VALDIS);
        addKillId(REWARDS_LIST.keySet());
        addQuestItem(DAIMONS_EYES,HESTIAS_FAIRY_STONE,NUCLEUS_OF_LESSER_GOLEM,FALSTONS_FANG);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        String htmltext = event;
        if ("31331-0.htm".equalsIgnoreCase(event) && cond == 0) {
            st.setCond(1);
            st.start();
        } else if (Util.isNumber(event)) {
            int evt = Integer.parseInt(event);
            st.set("raid", evt);
            htmltext = "31331-" + event + ".htm";
            if (evt > 0)
                st.addRadar(RADAR.get(evt));
            st.playSound(SOUND_ACCEPT);
        } else if ("31331-6.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        Clan clan = st.player.getClan();

        if (clan == null) {
            st.exitCurrentQuest();
            htmltext = "31331-0a.htm";
        } else if (clan.getLeader().getPlayer() != st.player) {
            st.exitCurrentQuest();
            htmltext = "31331-0a.htm";
        } else if (clan.getLevel() < 6) {
            st.exitCurrentQuest();
            htmltext = "31331-0b.htm";
        } else {
            int cond = st.getCond();
            int raid = st.getInt("raid");
            int id = st.getState();
            if (id == CREATED && cond == 0)
                htmltext = "31331-0c.htm";
            else if (id == STARTED && cond == 1) {
                int item = REWARDS_LIST.get(raid).getKey();
                long count = st.getQuestItemsCount(item);
                if (count == 0)
                    htmltext = "31331-" + raid + "a.htm";
                else if (count == 1) {
                    htmltext = "31331-" + raid + "buffPrice.htm";
                    int increasedPoints = clan.incReputation(REWARDS_LIST.get(raid).getValue(), true, "_509_TheClansPrestige");
                    st.player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(increasedPoints));
                    st.takeItems(item, 1);
                }
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        QuestState id = null;
        Clan clan = st.player.getClan();
        if (clan == null)
            return;
        Player clan_leader = clan.getLeader().getPlayer();
        if (clan_leader == null)
            return;
        if (clan_leader.equals(st.player) || clan_leader.getDistance(npc) <= 1600)
            id = clan_leader.getQuestState(this);
        if (id == null)
            return;
        if (st.getCond() == 1 && st.getState() == STARTED) {
            if (REWARDS_LIST.containsKey(npc.getNpcId())) {
                st.giveItems(REWARDS_LIST.get(npc.getNpcId()).getKey());
                st.playSound(SOUND_MIDDLE);
            }
        }
    }
}