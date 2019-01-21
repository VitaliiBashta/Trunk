package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.lang.ArrayUtils;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.InstantZoneHolder;
import l2trunk.gameserver.instancemanager.OlympiadHistoryManager;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.InstantZone;
import l2trunk.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

public class OlympiadGame {
    public static final int MAX_POINTS_LOOSE = 10;
    private static final Logger _log = LoggerFactory.getLogger(OlympiadGame.class);
    private static final List<Integer> STADIUMS_INSTANCE_ID = List.of(147, 148, 149, 150);
    private final int id;
    private final Reflection reflection;
    private final CompType type;
    private final OlympiadTeam team1;
    private final OlympiadTeam team2;
    private final List<Player> spectators = new CopyOnWriteArrayList<>();
    public boolean validated = false;
    private OlympiadGameTask _task;
    private ScheduledFuture<?> _shedule;
    private int winner = 0;
    private int _state = 0;
    private long _startTime;
    private boolean _buffersSpawned = false;

    public OlympiadGame(int id, CompType type, List<Integer> opponents) {
        this.type = type;
        this.id = id;
        reflection = new Reflection();
        InstantZone instantZone = InstantZoneHolder.getInstantZone(Rnd.get(STADIUMS_INSTANCE_ID));
        reflection.init(instantZone);

        team1 = new OlympiadTeam(this, 1);
        team2 = new OlympiadTeam(this, 2);

        for (int i = 0; i < opponents.size() / 2; i++) {
            team1.addMember(opponents.get(i));
        }

        for (int i = opponents.size() / 2; i < opponents.size(); i++) {
            team2.addMember(opponents.get(i));
        }

        Log.add("Olympiad System: Game - " + id + ": " + team1.getName() + " Vs " + team2.getName(), "olympiad");
    }

    void addBuffers() {
        if (!type.hasBuffer())
            return;

        reflection.spawnByGroup("olympiad_" + reflection.getInstancedZoneId() + "_buffers");
        _buffersSpawned = true;
    }

    void deleteBuffers() {
        if (!_buffersSpawned)
            return;

        reflection.despawnByGroup("olympiad_" + reflection.getInstancedZoneId() + "_buffers");
        _buffersSpawned = false;
    }

    public void managerShout() {
        for (NpcInstance npc : Olympiad.getNpcs()) {
            NpcString npcString;
            switch (type) {
                case TEAM:
                    npcString = NpcString.OLYMPIAD_CLASSFREE_TEAM_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
                    break;
                case CLASSED:
                    npcString = NpcString.OLYMPIAD_CLASS_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
                    break;
                case NON_CLASSED:
                    npcString = NpcString.OLYMPIAD_CLASSFREE_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
                    break;
                default:
                    continue;
            }
            Functions.npcShout(npc, npcString, String.valueOf(id + 1));
        }
    }

    public void portPlayersToArena() {
        team1.portPlayersToArena();
        team2.portPlayersToArena();
    }

    public void preparePlayers() {
        setState(1);
        team1.preparePlayers();
        team2.preparePlayers();
    }

    public void startComp() {
        setState(2);
        team1.startComp();
        team2.startComp();
    }

    private void portPlayersBack() {
        team1.portPlayersBack();
        team2.portPlayersBack();
    }

    public void heal() {
        team1.heal();
        team2.heal();
    }

    public void collapse() {
        portPlayersBack();
        clearSpectators();
        deleteBuffers();
        reflection.collapse();
    }

    public void validateWinner(boolean aborted) {
        int state = _state;
        _state = 0;

        if (validated) {
            Log.add("Olympiad Result: " + team1.getName() + " vs " + team2.getName() + " ... double validate check!!!", "olympiad");
            return;
        }
        validated = true;

        // Если игра закончилась до телепортации на стадион, то забираем очки у вышедших из игры, не засчитывая никому победу
        if (state < 1 && aborted) {
            team1.takePointsForCrash();
            team2.takePointsForCrash();
            broadcastPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME, true, false);
            return;
        }

        boolean teamOneCheck = team1.checkPlayers();
        boolean teamTwoCheck = team2.checkPlayers();

        if (winner <= 0) {
            if (!teamOneCheck && !teamTwoCheck)
                winner = 0;
            else if (!teamTwoCheck)
                winner = 1; // Выиграла первая команда
            else if (!teamOneCheck)
                winner = 2; // Выиграла вторая команда
            else if (team1.getDamage() < team2.getDamage()) // Вторая команда нанесла вреда меньше, чем первая
                winner = 1; // Выиграла первая команда
            else if (team1.getDamage() > team2.getDamage()) // Вторая команда нанесла вреда больше, чем первая
                winner = 2; // Выиграла вторая команда
        }

        if (winner == 1) // Выиграла первая команда
            winGame(team1, team2);
        else if (winner == 2) // Выиграла вторая команда
            winGame(team2, team1);
        else
            tie();

        team1.saveNobleData();
        team2.saveNobleData();

        broadcastRelation();
        broadcastPacket(new SystemMessage2(SystemMsg.YOU_WILL_BE_MOVED_BACK_TO_TOWN_IN_S1_SECONDS).addInteger(20), true, true);
    }

    private void winGame(OlympiadTeam winnerTeam, OlympiadTeam looseTeam) {
        ExReceiveOlympiad.MatchResult packet = new ExReceiveOlympiad.MatchResult(false, winnerTeam.getName());

        int pointDiff = 0;

        TeamMember[] looserMembers = looseTeam.getMembers().toArray(new TeamMember[0]);
        TeamMember[] winnerMembers = winnerTeam.getMembers().toArray(new TeamMember[0]);

        for (int i = 0; i < Party.MAX_SIZE; i++) {
            TeamMember looserMember = ArrayUtils.valid(looserMembers, i);
            TeamMember winnerMember = ArrayUtils.valid(winnerMembers, i);
            if (looserMember != null && winnerMember != null) {
                winnerMember.incGameCount();
                looserMember.incGameCount();

                int gamePoints = transferPoints(looserMember.getStat(), winnerMember.getStat());

                packet.addPlayer(winnerTeam == team1 ? TeamType.BLUE : TeamType.RED, winnerMember, gamePoints);
                packet.addPlayer(looseTeam == team1 ? TeamType.BLUE : TeamType.RED, looserMember, -gamePoints);

                pointDiff += gamePoints;
                if (winnerMember.getPlayer() != null) {
                    if (winnerMember.getPlayer().getCounters().olyHiScore < winnerMember.getStat().getInteger(Olympiad.POINTS))
                        winnerMember.getPlayer().getCounters().olyHiScore = winnerMember.getStat().getInteger(Olympiad.POINTS);

                    winnerMember.getPlayer().getCounters().olyGamesWon++;
                }

                if (looserMember.getPlayer() != null)
                    looserMember.getPlayer().getCounters().olyGamesLost++;
            }
        }

        if (type != CompType.TEAM) {
            int team = team1 == winnerTeam ? 1 : 2;

            TeamMember member1 = ArrayUtils.valid(team1 == winnerTeam ? winnerMembers : looserMembers, 0);
            TeamMember member2 = ArrayUtils.valid(team2 == winnerTeam ? winnerMembers : looserMembers, 0);
            if (member1 != null && member2 != null) {
                int diff = (int) ((System.currentTimeMillis() - _startTime) / 1000L);
                OlympiadHistory h = new OlympiadHistory(member1.getObjectId(), member2.getObjectId(), member1.getClassId(), member2.getClassId(), member1.getName(), member2.getName(), _startTime, diff, team, type.ordinal());

                OlympiadHistoryManager.INSTANCE.saveHistory(h);
            }
        }

        team1.removeBuffs(false);
        team2.removeBuffs(false);

        broadcastPacket(new SystemMessage(SystemMsg.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH).addString(winnerTeam.getName()), true, true);
        winnerTeam.broadcast(new SystemMessage(SystemMsg.C1_HAS_EARNED_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(winnerTeam.getName()).addNumber(pointDiff));
        looseTeam.broadcast(new SystemMessage(SystemMsg.C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(looseTeam.getName()).addNumber(pointDiff));

        winnerTeam.getPlayers().forEach(player -> {
            player.getInventory().addItem(Config.ALT_OLY_BATTLE_REWARD_ITEM, getType().getReward(), "Olympiad Match Reward");
            player.sendPacket(SystemMessage2.obtainItems(Config.ALT_OLY_BATTLE_REWARD_ITEM, getType().getReward(), 0));
            player.sendChanges();
        });

        Stream<Player> teamsPlayers = Stream.concat(winnerTeam.getPlayers(), looseTeam.getPlayers());
        teamsPlayers.forEach(player ->
                player.getAllQuestsStates()
                        .filter(QuestState::isStarted)
                        .forEach(qs -> qs.getQuest().onOlympiadEnd(this, qs)));


        broadcastPacket(packet, true, false);

        broadcastPacket(new SystemMessage2(SystemMsg.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH).addString(winnerTeam.getName()), false, true);

        // Alexander - Announce on critical to all players in the world, who won this match
        IStaticPacket criticalAnn = new Say2(0, ChatType.CRITICAL_ANNOUNCE, "", "Olympiad: " + team1.getName() + " VS " + team2.getName() + ". Winner is: " + winnerTeam.getName() + "!");
        GameObjectsStorage.getAllPlayersStream().forEach(player -> player.sendPacket(criticalAnn));


        Log.add("Olympiad Result: " + winnerTeam.getName() + " vs " + looseTeam.getName() + " ... (" + (int) winnerTeam.getDamage() + " vs " + (int) looseTeam.getDamage() + ") " + winnerTeam.getName() + " win " + pointDiff + " points", "olympiad");
    }

    private void tie() {
        Collection<TeamMember> teamMembers1 = team1.getMembers();
        Collection<TeamMember> teamMembers2 = team2.getMembers();

        ExReceiveOlympiad.MatchResult packet = new ExReceiveOlympiad.MatchResult(true, "");
        for (TeamMember member1 : teamMembers1) {
            if (member1 != null) {
                member1.incGameCount();
                StatsSet stat1 = member1.getStat();
                packet.addPlayer(TeamType.BLUE, member1, -2);
                stat1.set(Olympiad.POINTS, stat1.getInteger(Olympiad.POINTS) - 2);
            }
        }
        for (TeamMember member2 : teamMembers2) {
            if (member2 != null) {
                member2.incGameCount();
                StatsSet stat2 = member2.getStat();
                packet.addPlayer(TeamType.RED, member2, -2);

                stat2.set(Olympiad.POINTS, stat2.getInteger(Olympiad.POINTS) - 2);
            }
        }

        if (type != CompType.TEAM) {
            TeamMember member1 = teamMembers1.stream().findFirst().orElse(null);
            TeamMember member2 = teamMembers2.stream().findFirst().orElse(null);
            if (member1 != null && member2 != null) {
                int diff = (int) ((System.currentTimeMillis() - _startTime) / 1000L);
                OlympiadHistory h = new OlympiadHistory(member1.getObjectId(), member2.getObjectId(), member1.getClassId(), member2.getClassId(), member1.getName(), member2.getName(), _startTime, diff, 0, type.ordinal());

                OlympiadHistoryManager.INSTANCE.saveHistory(h);
            }
        }

        broadcastPacket(SystemMsg.THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE, true, true);
        team1.broadcast(new SystemMessage(SystemMsg.C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(team1.getName()).addNumber(2));
        team2.broadcast(new SystemMessage(SystemMsg.C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(team2.getName()).addNumber(2));
        broadcastPacket(packet, true, false);

        team1.removeBuffs(false);
        team2.removeBuffs(false);

        Log.add("Olympiad Result: " + team1.getName() + " vs " + team2.getName() + " ... tie", "olympiad");
    }

    private int transferPoints(StatsSet from, StatsSet to) {
        int fromPoints = from.getInteger(Olympiad.POINTS);
        int fromLoose = from.getInteger(Olympiad.COMP_LOOSE);
        int fromPlayed = from.getInteger(Olympiad.COMP_DONE);

        int toPoints = to.getInteger(Olympiad.POINTS);
        int toWin = to.getInteger(Olympiad.COMP_WIN);
        int toPlayed = to.getInteger(Olympiad.COMP_DONE);

        int pointDiff = Math.max(1, (int) Math.ceil((double) Math.min(fromPoints, toPoints) / getType().getLooseMult()));
        pointDiff = pointDiff > OlympiadGame.MAX_POINTS_LOOSE ? OlympiadGame.MAX_POINTS_LOOSE : pointDiff;

        from.set(Olympiad.POINTS, fromPoints - pointDiff);
        from.set(Olympiad.COMP_LOOSE, fromLoose + 1);
        from.set(Olympiad.COMP_DONE, fromPlayed + 1);

        to.set(Olympiad.POINTS, toPoints + pointDiff);
        to.set(Olympiad.COMP_WIN, toWin + 1);
        to.set(Olympiad.COMP_DONE, toPlayed + 1);

        return pointDiff;
    }

    public void openDoors() {
        reflection.getDoors().forEach(DoorInstance::openMe);
    }

    public int getId() {
        return id;
    }

    public Reflection getReflection() {
        return reflection;
    }

    public boolean isRegistered(int objId) {
        return team1.contains(objId) || team2.contains(objId);
    }

    public List<Player> getSpectators() {
        return spectators;
    }

    public void addSpectator(Player spec) {
        spectators.add(spec);
    }

    public void removeSpectator(Player spec) {
        spectators.remove(spec);
    }

    private void clearSpectators() {
        for (Player pc : spectators)
            if (pc != null && pc.isInObserverMode())
                pc.leaveOlympiadObserverMode(false);
        spectators.clear();
    }

    public void broadcastInfo(Player sender, Player receiver, boolean onlyToSpectators) {
        // TODO заюзать пакеты:
        // ExEventMatchCreate
        // ExEventMatchFirecracker
        // ExEventMatchManage
        // ExEventMatchMessage
        // ExEventMatchObserver
        // ExEventMatchScore
        // ExEventMatchTeamInfo
        // ExEventMatchTeamUnlocked
        // ExEventMatchUserInfo

        if (sender != null) {
            if (receiver != null)
                receiver.sendPacket(new ExOlympiadUserInfo(sender, sender.getOlympiadSide()));
            else
                broadcastPacket(new ExOlympiadUserInfo(sender, sender.getOlympiadSide()), !onlyToSpectators, true);
        } else {
            // Рассылаем информацию о первой команде
            team1.getPlayers().forEach(player ->  {
                if (receiver != null)
                    receiver.sendPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()));
                else {
                    broadcastPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()), !onlyToSpectators, true);
                    player.broadcastRelationChanged();
                }
            });

            // Рассылаем информацию о второй команде
            team2.getPlayers().forEach(player -> {
                if (receiver != null)
                    receiver.sendPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()));
                else {
                    broadcastPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()), !onlyToSpectators, true);
                    player.broadcastRelationChanged();
                }
            });
        }
    }

    private void broadcastRelation() {
        team1.getPlayers().forEach(Player::broadcastRelationChanged);
        team2.getPlayers().forEach(Player::broadcastRelationChanged);
    }

    public void broadcastPacket(L2GameServerPacket packet, boolean toTeams, boolean toSpectators) {
        if (toTeams) {
            team1.broadcast(packet);
            team2.broadcast(packet);
        }

        if (toSpectators) {
            spectators.stream()
                    .filter(Objects::nonNull)
                    .forEach(spec -> spec.sendPacket(packet));
        }
    }

    public void broadcastPacket(IStaticPacket packet, boolean toTeams, boolean toSpectators) {
        if (toTeams) {
            team1.broadcast(packet);
            team2.broadcast(packet);
        }

        if (toSpectators) {
            spectators.stream()
                    .filter(Objects::nonNull)
                    .forEach(spec -> spec.sendPacket(packet));
        }
    }

    public Stream<Player> getAllPlayers() {
        return Stream.of(team1.getPlayers(), team2.getPlayers(), spectators.stream()).flatMap(s -> s);
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public OlympiadTeam getWinnerTeam() {
        if (winner == 1) // Выиграла первая команда
            return team1;
        else if (winner == 2) // Выиграла вторая команда
            return team2;
        return null;
    }

    public int getState() {
        return _state;
    }

    private void setState(int val) {
        _state = val;
        if (_state == 1)
            _startTime = System.currentTimeMillis();
    }

    public Stream<Player> getTeamMembers(Player player) {
        return player.getOlympiadSide() == 1 ? team1.getPlayers() : team2.getPlayers();
    }

    public void addDamage(Player player, double damage) {
        if (player.getOlympiadSide() == 1)
            team1.addDamage(player, damage);
        else
            team2.addDamage(player, damage);
    }

    public boolean doDie(Player player) {
        return player.getOlympiadSide() == 1 ? team1.doDie(player) : team2.doDie(player);
    }

    public boolean checkPlayersOnline() {
        return team1.checkPlayers() && team2.checkPlayers();
    }

    public boolean logoutPlayer(Player player) {
        return player != null && (player.getOlympiadSide() == 1 ? team1.logout(player) : team2.logout(player));
    }

    public synchronized void sheduleTask(OlympiadGameTask task) {
        if (_shedule != null)
            _shedule.cancel(false);
        _task = task;
        _shedule = task.shedule();
    }

    public OlympiadGameTask getTask() {
        return _task;
    }

    public BattleStatus getStatus() {
        if (_task != null)
            return _task.getStatus();
        return BattleStatus.Begining;
    }

    public void endGame(int time, boolean aborted) {
        try {
            validateWinner(aborted);
            team1.stopComp();
            team2.stopComp();
        } catch (Exception e) {
            _log.error("Error on Olympiad End Game!", e);
        }

        sheduleTask(new OlympiadGameTask(this, time > 1 ? BattleStatus.PortBack : BattleStatus.Ending, time, 100));
    }

    public CompType getType() {
        return type;
    }

    public String getTeamName1() {
        return team1.getName();
    }

    public String getTeamName2() {
        return team2.getName();
    }
}