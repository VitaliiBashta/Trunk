package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.InstantZoneHolder;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.templates.InstantZone;
import l2trunk.gameserver.utils.Location;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class _511_AwlUnderFoot extends Quest {
    private final static int INSTANCE_ZONE_ID = 22; // Fortress Dungeon

    private final static int DungeonLeaderMark = 9797;
    private final static int RewardMarksCount = 1000; // цифра с потолка
    private final static int KnightsEpaulette = 9912;

    private static final Map<Integer, Prison> _prisons = new ConcurrentHashMap<>();

    private static final int HagerTheOutlaw = 25572;
    private static final int AllSeeingRango = 25575;
    private static final int Jakard = 25578;

    private static final int Helsing = 25579;
    private static final int Gillien = 25582;
    private static final int Medici = 25585;
    private static final int ImmortalMuus = 25588;

    private static final int BrandTheExile = 25589;
    private static final int CommanderKoenig = 25592;
    private static final int GergTheHunter = 25593;

    private static final List<Integer> type1 = List.of(HagerTheOutlaw, AllSeeingRango, Jakard);
    private static final List<Integer> type2 = List.of(Helsing, Gillien, Medici, ImmortalMuus);
    private static final List<Integer> type3 = List.of(BrandTheExile, CommanderKoenig, GergTheHunter);

    public _511_AwlUnderFoot() {
        super(false);

        // Detention Camp Wardens
        addStartNpc(35666, 35698, 35735, 35767, 35804, 35835, 35867, 35904, 35936, 35974, 36011, 36043, 36081, 36118, 36149, 36181, 36219, 36257, 36294, 36326, 36364);
        addQuestItem(DungeonLeaderMark);
        addKillId(HagerTheOutlaw, AllSeeingRango, Jakard, Helsing, Gillien, Medici, ImmortalMuus, BrandTheExile, CommanderKoenig, GergTheHunter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("gludio_fort_a_campkeeper_q0511_03.htm") || event.equalsIgnoreCase("gludio_fort_a_campkeeper_q0511_06.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("exit")) {
            st.exitCurrentQuest(true);
            return null;
        } else if (event.equalsIgnoreCase("enter"))
            if (st.getState() == CREATED || !check(st.getPlayer()))
                return "gludio_fort_a_campkeeper_q0511_01a.htm";
            else
                return enterPrison(st.getPlayer());
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (!check(st.getPlayer()))
            return "gludio_fort_a_campkeeper_q0511_01a.htm";
        if (st.getState() == CREATED)
            return "gludio_fort_a_campkeeper_q0511_01.htm";
        if (st.getQuestItemsCount(DungeonLeaderMark) > 0) {
            st.giveItems(KnightsEpaulette, st.getQuestItemsCount(DungeonLeaderMark));
            st.takeItems(DungeonLeaderMark);
            st.playSound(SOUND_FINISH);
            return "gludio_fort_a_campkeeper_q0511_09.htm";
        }
        return "gludio_fort_a_campkeeper_q0511_10.htm";
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        for (Prison prison : _prisons.values())
            if (prison.getReflectionId() == npc.getReflectionId()) {
                switch (npc.getNpcId()) {
                    case HagerTheOutlaw:
                    case AllSeeingRango:
                    case Jakard:
                        prison.initSpawn(Rnd.get(type2), false);
                        break;
                    case Helsing:
                    case Gillien:
                    case Medici:
                    case ImmortalMuus:
                        prison.initSpawn(Rnd.get(type3), false);
                        break;
                    case BrandTheExile:
                    case CommanderKoenig:
                    case GergTheHunter:
                        Party party = st.getPlayer().getParty();
                        if (party != null)
                            party.getMembers().stream()
                                    .map(member -> member.getQuestState(getClass()))
                                    .filter(Objects::nonNull)
                                    .filter(QuestState::isStarted)
                                    .forEach(qs -> {
                                        qs.giveItems(DungeonLeaderMark, RewardMarksCount / party.size());
                                        qs.playSound(SOUND_ITEMGET);
                                        qs.getPlayer().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
                                    });

                        else {
                            st.giveItems(DungeonLeaderMark, RewardMarksCount);
                            st.playSound(SOUND_ITEMGET);
                            st.getPlayer().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
                        }
                        Reflection r = ReflectionManager.INSTANCE.get(prison.getReflectionId());
                        if (r != null)
                            r.startCollapseTimer(300000); // Всех боссов убили, запускаем коллапс через 5 минут
                        break;
                }
                break;
            }

        return null;
    }

    private boolean check(Player player) {
        Fortress fort = ResidenceHolder.getResidenceByObject(Fortress.class, player);
        if (fort == null)
            return false;
        Clan clan = player.getClan();
        if (clan == null)
            return false;
        return clan.getClanId() == fort.getOwnerId();
    }

    private String enterPrison(Player player) {
        Fortress fort = ResidenceHolder.getResidenceByObject(Fortress.class, player);
        if (fort == null || fort.getOwner() != player.getClan())
            return "gludio_fort_a_campkeeper_q0511_01a.htm";

        // Крепость должна быть независимой
        if (fort.getContractState() != 1)
            return "gludio_fort_a_campkeeper_q0511_13.htm";
        if (!areMembersSameClan(player))
            return "gludio_fort_a_campkeeper_q0511_01a.htm";
        if (player.canEnterInstance(INSTANCE_ZONE_ID)) {
            InstantZone iz = InstantZoneHolder.getInstantZone(INSTANCE_ZONE_ID);
            Prison prison;
            if (!_prisons.isEmpty()) {
                prison = _prisons.get(fort.getId());
                if (prison != null && prison.isLocked()) {
                    // TODO правильное сообщение
                    player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
                    return null;
                }

                // Synerge - Add the player to the instance again
                if (prison != null) {
                    Reflection r = ReflectionManager.INSTANCE.get(prison.getReflectionId());
                    if (r != null) {
                        player.setReflection(r);
                        player.teleToLocation(iz.getTeleportCoord());
                        player.setVar("backCoords", r.getReturnLoc().toXYZString(), -1);
                        player.setInstanceReuse(iz.getId(), System.currentTimeMillis());
                        return null;
                    }
                }
            }
            prison = new Prison(fort.getId(), iz);
            _prisons.put(prison.getFortId(), prison);

            Reflection r = ReflectionManager.INSTANCE.get(prison.getReflectionId());

            r.setReturnLoc(player.getLoc());

            player.getParty().getMembers()
                    .forEach(member -> {
                        if (member != player)
                            newQuestState(member, STARTED);
                        member.setReflection(r);
                        member.teleToLocation(iz.getTeleportCoord());
                        member.setVar("backCoords", r.getReturnLoc().toXYZString(), -1);
                        member.setInstanceReuse(iz.getId(), System.currentTimeMillis());
                    });

            player.getParty().setReflection(r);
            r.setParty(player.getParty());
            r.startCollapseTimer(iz.getTimelimit() * 60 * 1000L);
            player.getParty().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(iz.getTimelimit()));

            prison.initSpawn(Rnd.get(type1), true);
        }
        return null;
    }

    private boolean areMembersSameClan(Player player) {
        if (player.getParty() == null)
            return true;
        return player.getParty().getMembers().stream()
                .noneMatch(p -> p.getClan() != player.getClan());
    }

    private class Prison {
        private int _fortId;
        private int _reflectionId;
        private long _lastEnter;

        Prison(int id, InstantZone iz) {
            try {
                Reflection r = new Reflection();
                r.init(iz);
                _reflectionId = r.getId();
                _fortId = id;
                _lastEnter = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void initSpawn(int npcId, boolean first) {
            ThreadPoolManager.INSTANCE.schedule(() -> addSpawnToInstance(npcId, new Location(53304, 245992, -6576, 25958), 0, _reflectionId), first ? 60000 : 180000);
        }

        int getReflectionId() {
            return _reflectionId;
        }

        int getFortId() {
            return _fortId;
        }

        boolean isLocked() {
            return System.currentTimeMillis() - _lastEnter < 4 * 60 * 60 * 1000L;
        }

    }
}