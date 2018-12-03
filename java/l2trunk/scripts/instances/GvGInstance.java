package l2trunk.scripts.instances;

import l2trunk.commons.lang.reference.HardReference;
import l2trunk.commons.lang.reference.HardReferences;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2trunk.gameserver.listener.actor.player.OnTeleportListener;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.events.GvG.GvG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

public final class GvGInstance extends Reflection {
    private final static int BOX_ID = 18822;
    private final static int BOSS_ID = 25655;

    private final static int SCORE_BOX = 20;
    private final static int SCORE_BOSS = 100;
    private final static int SCORE_KILL = 5;
    private final static int SCORE_DEATH = 3;

    private final int eventTime = 1200;
    private final long bossSpawnTime = 10 * 60 * 1000L;
    private final List<HardReference<Player>> bothTeams = new CopyOnWriteArrayList<>();
    private final Map<Integer, Integer> score = new HashMap<>();
    private final DeathListener _deathListener = new DeathListener();
    private final TeleportListener _teleportListener = new TeleportListener();
    private final PlayerPartyLeaveListener _playerPartyLeaveListener = new PlayerPartyLeaveListener();
    private boolean active = false;
    private Party team1;
    private Party team2;
    private int team1Score = 0;
    private int team2Score = 0;
    private long startTime;
    private ScheduledFuture<?> _bossSpawnTask;
    private ScheduledFuture<?> _countDownTask;
    private ScheduledFuture<?> _battleEndTask;
    private Zone zonepvp;

    private Zone peace1;

    private Zone peace2;

    public GvGInstance() {
        super();
    }

    /**
     * General instance initialization and assigning global variables
     */
    public void start() {
        zonepvp = getZone("[gvg_battle_zone]");
        peace1 = getZone("[gvg_1_peace]");
        peace2 = getZone("[gvg_2_peace]");
        //Box spawns
        Location boxes[] = {
                new Location(142696, 139704, -15264, 0),
                new Location(142696, 145944, -15264, 0),
                new Location(145784, 142824, -15264, 0),
                new Location(145768, 139704, -15264, 0),
                new Location(145768, 145944, -15264, 0),
                new Location(141752, 142760, -15624, 0),
                new Location(145720, 142008, -15880, 0),
                new Location(145720, 143640, -15880, 0),
                new Location(139592, 142824, -15264, 0)
        };

        for (Location boxe : boxes) addSpawnWithoutRespawn(BOX_ID, boxe, 0);

        addSpawnWithoutRespawn(35423, new Location(139640, 139736, -15264), 0); //Red team flag
        addSpawnWithoutRespawn(35426, new Location(139672, 145896, -15264), 0); //Blue team flag

        _bossSpawnTask = ThreadPoolManager.INSTANCE.schedule(() -> {
            broadCastPacketToBothTeams(new ExShowScreenMessage("There was a guard Treasure Herald", 5000, ScreenMessageAlign.MIDDLE_CENTER, true));
            addSpawnWithoutRespawn(BOSS_ID, new Location(147304, 142824, -15864, 32768), 0);
            openDoor(24220042);
        }, bossSpawnTime); //
        _countDownTask = ThreadPoolManager.INSTANCE.schedule(new CountingDown(), (eventTime - 1) * 1000L);
        _battleEndTask = ThreadPoolManager.INSTANCE.schedule(() -> {
            broadCastPacketToBothTeams(new ExShowScreenMessage("The battle has expired. Teleportation 1 minute.", 4000, ScreenMessageAlign.BOTTOM_RIGHT, true));
            end();
        }, (eventTime - 6) * 1000L); // -6 is about to prevent built-in BlockChecker countdown task

        //Assigning players to teams
        team1.getMembers().forEach(member -> {
            bothTeams.add(member.getRef());
            member.addListener(_deathListener);
            member.addListener(_teleportListener);
            member.addListener(_playerPartyLeaveListener);
        });

        for (Player member : team2.getMembers()) {
            bothTeams.add(member.getRef());
            member.addListener(_deathListener);
            member.addListener(_teleportListener);
            member.addListener(_playerPartyLeaveListener);
        }

        startTime = System.currentTimeMillis() + eventTime * 1000L; //Used in packet broadcasting

        //Forming packets to send everybody
        final ExCubeGameChangePoints initialPoints = new ExCubeGameChangePoints(eventTime, team1Score, team2Score);
        final ExCubeGameCloseUI cui = new ExCubeGameCloseUI();
        ExCubeGameExtendedChangePoints clientSetUp;

        for (Player tm : HardReferences.unwrap(bothTeams)) {
            score.put(tm.getObjectId(), 0);

            tm.setCurrentCp(tm.getMaxCp());
            tm.setCurrentHp(tm.getMaxHp(), false);
            tm.setCurrentMp(tm.getMaxMp());
            clientSetUp = new ExCubeGameExtendedChangePoints(eventTime, team1Score, team2Score, isRedTeam(tm), tm, 0);
            tm.sendPacket(clientSetUp);
            tm.sendActionFailed(); //useless? copy&past from BlockChecker
            tm.sendPacket(initialPoints);
            tm.sendPacket(cui); //useless? copy&past from BlockChecker
            broadCastPacketToBothTeams(new ExCubeGameAddPlayer(tm, isRedTeam(tm)));
        }

        active = true;
    }

    private void broadCastPacketToBothTeams(L2GameServerPacket packet) {
        for (Player tm : HardReferences.unwrap(bothTeams))
            tm.sendPacket(packet);
    }

    /**
     * @return Whether event is active. active starts with instance dungeon and ends with team victory
     */
    private boolean isActive() {
        return active;
    }

    /**
     * @param player
     * @return Whether player belongs to Red Team (team2)
     */
    private boolean isRedTeam(Player player) {
        return team2.containsMember(player);
    }

    /**
     * Handles the end of event
     */
    private void end() {
        active = false;

        startCollapseTimer(60 * 1000L);

        paralyzePlayers();
        ThreadPoolManager.INSTANCE.schedule(() -> {
            unParalyzePlayers();
            cleanUp();
        }, 55 * 1000L);

        if (_bossSpawnTask != null) {
            _bossSpawnTask.cancel(false);
            _bossSpawnTask = null;
        }
        if (_countDownTask != null) {
            _countDownTask.cancel(false);
            _countDownTask = null;
        }
        if (_battleEndTask != null) {
            _battleEndTask.cancel(false);
            _battleEndTask = null;
        }

        boolean isRedWinner = team2Score >= team1Score;

        final ExCubeGameEnd end = new ExCubeGameEnd(isRedWinner);
        broadCastPacketToBothTeams(end);

        reward(isRedWinner ? team2 : team1);
        GvG.updateWinner(isRedWinner ? team2.getLeader() : team1.getLeader());

        //Удаление созданных зон из мира
        zonepvp.setActive(false);
        peace1.setActive(false);
        peace2.setActive(false);
    }

    private void reward(Party party) {
        for (Player member : party.getMembers()) {
            member.sendMessage("Your team won the tournament GvG, leader of the group added in the top winners.");
            member.setFame(member.getFame() + 500, "GvG"); // fame
            Functions.addItem(member, 6673, 8, "GvG"); // Fantasy Isle Coin
        }
    }

    /**
     * @param teamId
     * @param toAdd             - how much points to add
     * @param toSub             - how much points to remove
     * @param subbing           - whether change is reducing points
     * @param affectAnotherTeam - change can affect only teamId or both
     * @param player            Any score change are handled here.
     */
    private synchronized void changeScore(int teamId, int toAdd, int toSub, boolean subbing, boolean affectAnotherTeam, Player player) {
        int timeLeft = (int) ((startTime - System.currentTimeMillis()) / 1000);
        if (teamId == 1) {
            if (subbing) {
                team1Score -= toSub;
                if (team1Score < 0)
                    team1Score = 0;
                if (affectAnotherTeam) {
                    team2Score += toAdd;
                    broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, true, player, getPlayerScore(player)));
                }
                broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, false, player, getPlayerScore(player)));
            } else {
                team1Score += toAdd;
                if (affectAnotherTeam) {
                    team2Score -= toSub;
                    if (team2Score < 0)
                        team2Score = 0;
                    broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, true, player, getPlayerScore(player)));
                }
                broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, false, player, getPlayerScore(player)));
            }
        } else if (teamId == 2)
            if (subbing) {
                team2Score -= toSub;
                if (team2Score < 0)
                    team2Score = 0;
                if (affectAnotherTeam) {
                    team1Score += toAdd;
                    broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, false, player, getPlayerScore(player)));
                }
                broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, true, player, getPlayerScore(player)));
            } else {
                team2Score += toAdd;
                if (affectAnotherTeam) {
                    team1Score -= toSub;
                    if (team1Score < 0)
                        team1Score = 0;
                    broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, false, player, getPlayerScore(player)));
                }
                broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, true, player, getPlayerScore(player)));
            }
    }

    private void addPlayerScore(Player player) {
        score.put(player.getObjectId(), getPlayerScore(player) + 1);
    }

    private int getPlayerScore(Player player) {
        return score.get(player.getObjectId());
    }

    /**
     * Paralyzes everybody in instance to prevent any actions while event is !isActive
     */
    private void paralyzePlayers() {
        for (Player tm : HardReferences.unwrap(bothTeams)) {
            if (tm.isDead()) {
                tm.setCurrentHp(tm.getMaxHp(), true);
                tm.broadcastPacket(new Revive(tm));
            } else
                tm.setCurrentHp(tm.getMaxHp(), false);

            tm.setCurrentMp(tm.getMaxMp());
            tm.setCurrentCp(tm.getMaxCp());

            tm.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
            tm.block();
        }
    }

    private void unParalyzePlayers() {
        HardReferences.unwrap(bothTeams).forEach(tm -> {
            tm.unblock();
            removePlayer(tm, true);
        });
    }

    /**
     * Cleans up every list and task
     */
    private void cleanUp() {
        team1 = null;
        team2 = null;
        bothTeams.clear();
        team1Score = 0;
        team2Score = 0;
        score.clear();
    }

    private void resurrectAtBase(Player player) {
        if (player.isDead()) {
            //player.setCurrentCp(player.getMaxCp());
            player.setCurrentHp(0.7 * player.getMaxHp(), true);
            //player.setCurrentMp(player.getMaxMp());
            player.broadcastPacket(new Revive(player));
        }
        player.altOnMagicUseTimer(player, SkillTable.INSTANCE().getInfo(5660, 2)); // Battlefield Death Syndrome

    }

    /**
     * @param legalQuit - whether quit was called by event or by player escape
     *                  Removes player from every list or instance, teleports him and stops the event timer
     */
    private void removePlayer(Player player, boolean legalQuit) {
        bothTeams.remove(player.getRef());

        broadCastPacketToBothTeams(new ExCubeGameRemovePlayer(player, isRedTeam(player)));
        player.removeListener(_deathListener);
        player.removeListener(_teleportListener);
        player.removeListener(_playerPartyLeaveListener);
        player.leaveParty();
        if (!legalQuit)
            player.sendPacket(new ExCubeGameEnd(false));
    }

    private void teamWithdraw(Party party) {
        if (party == team1) {
            for (Player player : team1.getMembers())
                removePlayer(player, false);

            Player player = team2.getLeader();
            changeScore(2, 200, 0, false, false, player); //adding 200 to the team score for enemy team withdrawal. player - leader of the team who's left in the instance
        } else {
            for (Player player : team2.getMembers())
                removePlayer(player, false);

            Player player = team1.getLeader();
            changeScore(1, 200, 0, false, false, player); //adding 200 to the team score for enemy team withdrawal. player - leader of the team who's left in the instance
        }

        broadCastPacketToBothTeams(new ExShowScreenMessage("The opposing team has left the field of battle at full strength. End of battle.", 4000, ScreenMessageAlign.MIDDLE_CENTER, true));
        end();
    }

    @Override
    public NpcInstance addSpawnWithoutRespawn(int npcId, Location loc, int randomOffset) {
        NpcInstance npc = super.addSpawnWithoutRespawn(npcId, loc, randomOffset);
        npc.addListener(_deathListener);
        return npc;
    }

    private class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature self, Creature killer) {
            if (!active)
                return;

            //Убийство произошло в инстанте
            if (self.getReflection() != killer.getReflection() || self.getReflection() != GvGInstance.this)
                return;

            if (self.isPlayer() && killer.isPlayable()) //if PvP kill
            {
                if (team1.containsMember(self.getPlayer()) && team2.containsMember(killer.getPlayer())) {
                    addPlayerScore(killer.getPlayer());
                    changeScore(1, SCORE_KILL, SCORE_DEATH, true, true, killer.getPlayer());
                } else if (team2.containsMember(self.getPlayer()) && team1.containsMember(killer.getPlayer())) {
                    addPlayerScore(killer.getPlayer());
                    changeScore(2, SCORE_KILL, SCORE_DEATH, true, true, killer.getPlayer());
                }
                resurrectAtBase(self.getPlayer());
            } else if (self.isPlayer() && !killer.isPlayable()) //if not-PvP kill
                resurrectAtBase(self.getPlayer());
            else if (self.isNpc() && killer.isPlayable()) //onKill - mob death
            {
                if (self.getNpcId() == BOX_ID) {
                    if (team1.containsMember(killer.getPlayer()))
                        changeScore(1, SCORE_BOX, 0, false, false, killer.getPlayer());
                    else if (team2.containsMember(killer.getPlayer()))
                        changeScore(2, SCORE_BOX, 0, false, false, killer.getPlayer());
                } else if (self.getNpcId() == BOSS_ID) {
                    if (team1.containsMember(killer.getPlayer()))
                        changeScore(1, SCORE_BOSS, 0, false, false, killer.getPlayer());
                    else if (team2.containsMember(killer.getPlayer()))
                        changeScore(2, SCORE_BOSS, 0, false, false, killer.getPlayer());

                    broadCastPacketToBothTeams(new ExShowScreenMessage("Treasure guard Gerald died at the hands of " + killer.getName(), 5000, ScreenMessageAlign.MIDDLE_CENTER, true));
                    end();
                }
            }
        }
    }


    public class CountingDown extends RunnableImpl {
        @Override
        public void runImpl() {
            broadCastPacketToBothTeams(new ExShowScreenMessage("Until the end of the battle remained 1 minute", 4000, ScreenMessageAlign.MIDDLE_CENTER, true));
        }
    }

    public class BattleEnd extends RunnableImpl {
        @Override
        public void runImpl() {
            broadCastPacketToBothTeams(new ExShowScreenMessage("The battle has expired. Teleportation 1 minute.", 4000, ScreenMessageAlign.BOTTOM_RIGHT, true));
            end();
        }
    }

    public class Finish extends RunnableImpl {
        @Override
        public void runImpl() {
            unParalyzePlayers();
            cleanUp();
        }
    }

    /**
     * Handles any Teleport action of any player inside
     */
    private class TeleportListener implements OnTeleportListener {
        @Override
        public void onTeleport(Player player, int x, int y, int z, Reflection reflection) {
            if (zonepvp.checkIfInZone(x, y, z, reflection) || peace1.checkIfInZone(x, y, z, reflection) || peace2.checkIfInZone(x, y, z, reflection))
                return;

            removePlayer(player, false);
            player.sendMessage("You left the area ahead of the battle and were disqualified.");
        }
    }

    /**
     * Handles quit from the group
     */
    private class PlayerPartyLeaveListener implements OnPlayerPartyLeaveListener {
        @Override
        public void onPartyLeave(Player player) {
            if (!active)
                return;

            Party party = player.getParty();

            if (party.size() >= 3) {
                removePlayer(player, false);
                return;
            }

            teamWithdraw(party);
        }
    }
}