package l2trunk.scripts.quests;

import l2trunk.commons.lang.Pair;
import l2trunk.gameserver.Config;
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
import java.util.stream.Collectors;

public final class _508_TheClansReputation extends Quest {
    // Quest NPC
    private static final int SIR_ERIC_RODEMAI = 30868;

    // Quest items
    private static final int NUCLEUS_OF_FLAMESTONE_GIANT = 8494; // Nucleus of Flamestone Giant : Nucleus obtained by defeating Flamestone Giant
    private static final int THEMIS_SCALE = 8277; // Themis' Scale : Obtain this scale by defeating Palibati Queen Themis.
    private static final int Hisilromes_Heart = 14883; // Heart obtained after defeating Shilen's Priest Hisilrome.
    private static final int TIPHON_SHARD = 8280; // Tiphon Shard : Debris obtained by defeating Tiphon, Gargoyle Lord.
    private static final int GLAKIS_NECLEUS = 8281; // Glaki's Necleus : Nucleus obtained by defeating Glaki, the last lesser Giant.
    private static final int RAHHAS_FANG = 8282; // Rahha's Fang : Fangs obtained by defeating Rahha.

    // Quest Raid Bosses
    private static final int FLAMESTONE_GIANT = 25524;
    private static final int PALIBATI_QUEEN_THEMIS = 25252;
    private static final int Shilens_Priest_Hisilrome = 25478;
    private static final int GARGOYLE_LORD_TIPHON = 25255;
    private static final int LAST_LESSER_GIANT_GLAKI = 25245;
    private static final int RAHHA = 25051;

    // id:[RaidBossNpcId,questItemId]
    private static final Map<Integer, Pair<Integer, Integer>> REWARDS_LIST = Map.of(
            PALIBATI_QUEEN_THEMIS, Pair.of(THEMIS_SCALE, 85),
            Shilens_Priest_Hisilrome, Pair.of(Hisilromes_Heart, 65),
            GARGOYLE_LORD_TIPHON, Pair.of(TIPHON_SHARD, 50),
            LAST_LESSER_GIANT_GLAKI, Pair.of(GLAKIS_NECLEUS, 125),
            RAHHA, Pair.of(RAHHAS_FANG, 71),
            FLAMESTONE_GIANT, Pair.of(NUCLEUS_OF_FLAMESTONE_GIANT, 80));

    private static final List<Location> RADAR = List.of(
            Location.of(0, 0, 0),
            Location.of(192346, 21528, -3648),
            Location.of(191979, 54902, -7658),
            Location.of(170038, -26236, -3824),
            Location.of(171762, 55028, -5992),
            Location.of(117232, -9476, -3320),
            Location.of(144218, -5816, -4722));

    public _508_TheClansReputation() {
        super(PARTY_ALL);

        addStartNpc(SIR_ERIC_RODEMAI);

        addKillId(REWARDS_LIST.keySet());
        addQuestItem(REWARDS_LIST.values().stream().map(Pair::getKey).collect(Collectors.toList()));
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        String htmltext = event;
        if ("30868-0.htm".equalsIgnoreCase(event) && cond == 0) {
            st.setCond(1);
            st.start();
        } else if (Util.isNumber(event)) {
            int evt = Integer.parseInt(event);
            st.set("raid", evt);
            htmltext = "30868-" + event + ".htm";
            if (evt > 0)
                st.addRadar(RADAR.get(evt));
            st.playSound(SOUND_ACCEPT);
        } else if ("30868-7.htm".equalsIgnoreCase(event)) {
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
            htmltext = "30868-0a.htm";
        } else if (clan.getLeader().getPlayer() != st.player) {
            st.exitCurrentQuest();
            htmltext = "30868-0a.htm";
        } else if (clan.getLevel() < 5) {
            st.exitCurrentQuest();
            htmltext = "30868-0b.htm";
        } else {
            int cond = st.getCond();
            int raid = st.getInt("raid");
            int id = st.getState();
            if (id == CREATED && cond == 0)
                htmltext = "30868-0c.htm";
            else if (id == STARTED && cond == 1) {
                if (raid == 0) {
                    htmltext = "30868-0.htm";
                } else {
                    int item = REWARDS_LIST.get(raid).getKey();
                    boolean haveItem = st.haveQuestItem(item);
                    if (!haveItem)
                        htmltext = "30868-" + raid + "a.htm";
                    else {
                        htmltext = "30868-" + raid + "buffPrice.htm";
                        int increasedPoints = clan.incReputation(REWARDS_LIST.get(raid).getValue(), true, "_508_TheClansReputation");
                        st.player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(increasedPoints));
                        st.takeItems(item);
                    }
                }
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        Player clan_leader;
        try {
            clan_leader = st.player.getClan().getLeader().getPlayer();
        } catch (Exception E) {
            return;
        }
        if (clan_leader == null)
            return;
        if (!st.player.equals(clan_leader) && clan_leader.getDistance(npc) > Config.ALT_PARTY_DISTRIBUTION_RANGE)
            return;
        QuestState qs = clan_leader.getQuestState(this);
        if (qs == null || !qs.isStarted() || qs.getCond() != 1)
            return;

        int raid = REWARDS_LIST.get(st.getInt("raid")).getKey();
        int item = REWARDS_LIST.get(st.getInt("raid")).getValue();
        if (npc.getNpcId() == raid) {
            st.giveItemIfNotHave(item);
            st.playSound(SOUND_MIDDLE);
        }
    }
}