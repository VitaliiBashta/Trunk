package l2trunk.gameserver.model.entity.events.impl;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.time.cron.SchedulingPattern;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.listener.actor.OnKillListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerExitListener;
import l2trunk.gameserver.listener.actor.player.OnTeleportListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.RestartType;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.entity.events.objects.KrateisCubePlayerObject;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExPVPMatchCCMyRecord;
import l2trunk.gameserver.network.serverpackets.ExPVPMatchCCRecord;
import l2trunk.gameserver.network.serverpackets.ExPVPMatchCCRetire;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;

import java.util.*;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;

public final class KrateisCubeEvent extends GlobalEvent {
    public static final String REGISTERED_PLAYERS = "registered_players";
    public static final String WAIT_LOCS = "wait_locs";
    public static final String TELEPORT_LOCS = "teleport_locs";
    private static final String PARTICLE_PLAYERS = "particle_players";
    private static final String PREPARE = "prepare";
    private static final SchedulingPattern DATE_PATTERN = new SchedulingPattern("0,30 * * * *");
    private static final Location RETURN_LOC = new Location(-70381, -70937, -1428);
    private static final Map<Integer, Integer> SKILL_IDS = Map.of(
            1086, 2,
            1204, 2,
            1059, 3,
            1085, 3,
            1078, 6,
            1068, 3,
            1240, 3,
            1077, 3,
            1242, 3,
            1062, 2);

    private final int _minLevel;
    private final int _maxLevel;
    private final Calendar _calendar = Calendar.getInstance();
    private final Listeners listeners = new Listeners();
    private KrateisCubeRunnerEvent _runnerEvent;

    public KrateisCubeEvent(StatsSet set) {
        super(set);
        _minLevel = set.getInteger("min_level");
        _maxLevel = set.getInteger("max_level");
    }

    @Override
    public void initEvent() {
        _runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 2);

        super.initEvent();
    }

    private void prepare() {
        NpcInstance npc = _runnerEvent.getNpc();
        List<KrateisCubePlayerObject> registeredPlayers = removeObjects(REGISTERED_PLAYERS);
        List<Location> waitLocs = getObjects(WAIT_LOCS);
        for (KrateisCubePlayerObject k : registeredPlayers) {
            if (npc.getDistance(k.getPlayer()) > 800)
                continue;

            addObject(PARTICLE_PLAYERS, k);

            Player player = k.getPlayer();

            player.teleToLocation(Rnd.get(waitLocs), ReflectionManager.DEFAULT);
        }
    }

    @Override
    public void startEvent() {
        super.startEvent();

        List<KrateisCubePlayerObject> players = getObjects(PARTICLE_PLAYERS);
        List<Location> teleportLocs = getObjects(TELEPORT_LOCS);

        for (int i = 0; i < players.size(); i++) {
            KrateisCubePlayerObject k = players.get(i);
            Player player = k.getPlayer();

            player.getEffectList().stopAllEffects();

            giveEffects(player);

            player.teleToLocation(teleportLocs.get(i));
            player.addEvent(this);

            player.sendPacket(new ExPVPMatchCCMyRecord(k), SystemMsg.THE_MATCH_HAS_STARTED);
        }
    }

    @Override
    public void stopEvent() {
        super.stopEvent();
        reCalcNextTime(false);

        double dif = 0.05;
        int pos = 0;

        List<KrateisCubePlayerObject> players = removeObjects(PARTICLE_PLAYERS);
        for (KrateisCubePlayerObject krateisPlayer : players) {
            Player player = krateisPlayer.getPlayer();
            pos++;
            if (krateisPlayer.getPoints() >= 10) {
                int count = (int) (krateisPlayer.getPoints() * dif * (1.0 + players.size() * 0.04 / pos));
                dif -= 0.0016;
                if (count > 0) {
                    addItem(player, 13067, count);

                    int exp = count * 2880;
                    int sp = count * 288;
                    player.addExpAndSp(exp, sp);
                }
            }

            player.removeEvent(this);

            player.sendPacket(ExPVPMatchCCRetire.STATIC, SystemMsg.END_MATCH);
            player.teleToLocation(RETURN_LOC);
        }
    }

    private void giveEffects(Player player) {
        player.setFullHpMp();
        player.setFullCp();
        SKILL_IDS.forEach((k, v) -> SkillTable.INSTANCE.getInfo(k, v).getEffects(player));

    }

    @Override
    public void reCalcNextTime(boolean onInit) {
        clearActions();

        _calendar.setTimeInMillis(DATE_PATTERN.next(System.currentTimeMillis()));

        registerActions();
    }

    @Override
    protected long startTimeMillis() {
        return _calendar.getTimeInMillis();
    }

    @Override
    public boolean canRessurect(Player resurrectPlayer, Creature creature, boolean force) {
        resurrectPlayer.sendPacket(SystemMsg.INVALID_TARGET);
        return false;
    }

    public KrateisCubePlayerObject getRegisteredPlayer(Player player) {
        List<KrateisCubePlayerObject> registeredPlayers = getObjects(REGISTERED_PLAYERS);
        return registeredPlayers.stream()
                .filter(p -> p.getPlayer() == player)
                .findFirst().orElse(null);
    }

    public KrateisCubePlayerObject getParticlePlayer(Player player) {
        List<KrateisCubePlayerObject> registeredPlayers = getObjects(PARTICLE_PLAYERS);
        return registeredPlayers.stream()
                .filter(p -> p.getPlayer() == player)
                .findFirst().orElse(null);
    }

    public void showRank(Player player) {
        KrateisCubePlayerObject particlePlayer = getParticlePlayer(player);
        if (particlePlayer == null || particlePlayer.isShowRank())
            return;

        particlePlayer.setShowRank(true);

        Map<String, Integer> scores = new HashMap<>();
        for (KrateisCubePlayerObject p : getSortedPlayers())
            scores.put(p.getName(), p.getPoints());

        player.sendPacket(new ExPVPMatchCCRecord(scores));
    }

    public void closeRank(Player player) {
        KrateisCubePlayerObject particlePlayer = getParticlePlayer(player);
        if (particlePlayer == null || !particlePlayer.isShowRank())
            return;

        particlePlayer.setShowRank(false);
    }

    public void updatePoints(KrateisCubePlayerObject k) {
        k.getPlayer().sendPacket(new ExPVPMatchCCMyRecord(k));

        Map<String, Integer> scores = new HashMap<>();
        for (KrateisCubePlayerObject p : getSortedPlayers())
            scores.put(p.getName(), p.getPoints());

        final ExPVPMatchCCRecord p = new ExPVPMatchCCRecord(scores);

        List<KrateisCubePlayerObject> players = getObjects(PARTICLE_PLAYERS);
        players.stream()
                .filter(KrateisCubePlayerObject::isShowRank)
                .forEach(pl -> pl.getPlayer().sendPacket(p));
    }

    private List<KrateisCubePlayerObject> getSortedPlayers() {
        List<KrateisCubePlayerObject> players = getObjects(PARTICLE_PLAYERS);
        Collections.sort(players);
        return players;
    }

    public void exitCube(Player player, boolean teleport) {
        KrateisCubePlayerObject krateisCubePlayer = getParticlePlayer(player);
        krateisCubePlayer.stopRessurectTask();

        getObjects(PARTICLE_PLAYERS).remove(krateisCubePlayer);

        player.sendPacket(ExPVPMatchCCRetire.STATIC);
        player.removeEvent(this);

        if (teleport)
            player.teleToLocation(RETURN_LOC);
    }

    @Override
    public void announce(int a) {
        IStaticPacket p;
        if (a > 0)
            p = new SystemMessage2(SystemMsg.S1_SECONDS_TO_GAME_END).addInteger(a);
        else
            p = new SystemMessage2(SystemMsg.THE_MATCH_WILL_START_IN_S1_SECONDS).addInteger(-a);

        List<KrateisCubePlayerObject> players = getObjects(PARTICLE_PLAYERS);
        for (KrateisCubePlayerObject $player : players)
            $player.getPlayer().sendPacket(p);
    }

    @Override
    public boolean isParticle(Player player) {
        return getParticlePlayer(player) != null;
    }

    @Override
    public void onAddEvent(GameObject o) {
        if (o instanceof Player)
            ((Player) o).addListener(listeners);
    }

    @Override
    public void onRemoveEvent(GameObject o) {
        if (o instanceof Player)
            ((Player) o).removeListener(listeners);
    }

    @Override
    public void action(String name, boolean start) {
        if (name.equalsIgnoreCase(PREPARE))
            prepare();
        else
            super.action(name, start);
    }

    @Override
    public void checkRestartLocs(Player player, Map<RestartType, Boolean> r) {
        r.clear();
    }

    public int getMinLevel() {
        return _minLevel;
    }

    public int getMaxLevel() {
        return _maxLevel;
    }

    @Override
    public boolean isInProgress() {
        return _runnerEvent.isInProgress();
    }

    public boolean isRegistrationOver() {
        return _runnerEvent.isRegistrationOver();
    }

    private class Listeners implements OnKillListener, OnPlayerExitListener, OnTeleportListener {
        @Override
        public void onKill(Creature actor, Creature victim) {
            if (!(victim instanceof Player))
                return;

            KrateisCubeEvent cubeEvent2 = victim.getEvent(KrateisCubeEvent.class);
            if (cubeEvent2 != KrateisCubeEvent.this)
                return;

            KrateisCubePlayerObject winnerPlayer = getParticlePlayer((Player) actor);

            winnerPlayer.setPoints(winnerPlayer.getPoints() + 5);
            updatePoints(winnerPlayer);

            KrateisCubePlayerObject looserPlayer = getParticlePlayer((Player) victim);

            looserPlayer.startRessurectTask();
        }

        @Override
        public void onPlayerExit(Player player) {
            exitCube(player, false);
        }

        @Override
        public void onTeleport(Player player, Location loc, Reflection reflection) {
            List<Location> waitLocs = getObjects(WAIT_LOCS);
            for (Location l : waitLocs)
                if (l.x == loc.x && l.y == loc.y)
                    return;

            waitLocs = getObjects(TELEPORT_LOCS);

            for (Location l : waitLocs)
                if (l.x == loc.x && l.y == loc.y)
                    return;

            exitCube(player, false);
        }
    }
}
