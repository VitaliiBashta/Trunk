package l2trunk.scripts.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.NpcLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public final class Frintezza extends Reflection {
    private static final Logger LOG = LoggerFactory.getLogger(Frintezza.class);
    private static final int HallAlarmDevice = 18328;
    private static final int DarkChoirPlayer = 18339;
    private static final int _weakScarletId = 29046;
    private static final int _strongScarletId = 29047;
    private static final int TeleportCube = 29061;
    private static final int _frintezzasSwordId = 7903;
    private static final int DewdropItem = 8556;
    private static final List<Integer> hallADoors = List.of(17130051, 17130052, 17130053, 17130054, 17130055, 17130056, 17130057, 17130058);
    private static final List<Integer> corridorADoors = List.of(17130042, 17130043);
    private static final List<Integer> hallBDoors = List.of(17130061, 17130062, 17130063, 17130064, 17130065, 17130066, 17130067, 17130068, 17130069, 17130070);
    private static final List<Integer> corridorBDoors = List.of(17130045, 17130046);
    private static final List<Integer> blockANpcs = List.of(18329, 18330, 18331, 18333);
    private static final List<Integer> blockBNpcs = List.of(18334, 18335, 18336, 18337, 18338);
    private static final int _intervalOfFrintezzaSongs = 30000;
    //The Boss
    private static final NpcLocation frintezzaSpawn = new NpcLocation(-87784, -155090, -9080, 16048, 29045);
    // Weak Scarlet Van Halisha.
    private static final NpcLocation scarletSpawnWeak = new NpcLocation(-87784, -153288, -9176, 16384, 29046);
    // Portrait spawns - 4 portraits = 4 spawns
    private static final List<NpcLocation> portraitSpawns = List.of(
            new NpcLocation(-86136, -153960, -9168, 35048, 29048),
            new NpcLocation(-86184, -152456, -9168, 28205, 29049),
            new NpcLocation(-89368, -152456, -9168, 64817, 29048),
            new NpcLocation(-89416, -153976, -9168, 57730, 29049));
    // Demon spawns - 4 portraits = 4 demons
    private static final List<NpcLocation> demonSpawns = List.of(
            new NpcLocation(-86136, -153960, -9168, 35048, 29050),
            new NpcLocation(-86184, -152456, -9168, 28205, 29051),
            new NpcLocation(-89368, -152456, -9168, 64817, 29051),
            new NpcLocation(-89416, -153976, -9168, 57730, 29050));
    private static final long battleStartDelay = 5 * 60000L; // 5min
    private final Skill demonMorph = SkillTable.INSTANCE.getInfo(5017);
    private final List<NpcInstance> portraits = new ArrayList<>(4);
    private final List<NpcInstance> demons = new ArrayList<>(4);
    private final DeathListener _deathListener = new DeathListener();
    private final CurrentHpListener _currentHpListener = new CurrentHpListener();
    private NpcInstance _frintezzaDummy, frintezza, weakScarlet, strongScarlet;
    private int _scarletMorph = 0;
    private ScheduledFuture<?> musicTask;

    @Override
    protected void onCreate() {
        super.onCreate();

        getNpcs().forEach(n -> n.addListener(_deathListener));

        blockUnblockNpcs(true, blockANpcs);
    }

    private NpcInstance spawn(NpcLocation loc) {
        return addSpawnWithoutRespawn(loc.npcId, loc, 0);
    }

    /**
     * Shows a movie to the players in the lair.
     *
     * @param target       - L2NpcInstance target is the center of this movie
     * @param dist         - int distance from target
     * @param yaw          - angle of movie (north = 90, south = 270, east = 0 , west = 180)
     * @param pitch        - pitch > 0 looks up / pitch < 0 looks down
     * @param time         - fast ++ or slow -- depends on the value
     * @param duration     - How long to watch the movie
     * @param socialAction - 1,2,3,4 social actions / other values do nothing
     */
    private void showSocialActionMovie(NpcInstance target, int dist, int yaw, int pitch, int time, int duration, int socialAction) {
        if (target == null)
            return;
        getPlayers().filter(pc -> pc.getDistance(target) <= 2550)
                .forEach(pc -> {
                    if (pc.getDistance(target) <= 2550) {
                        pc.enterMovieMode();
                        pc.specialCamera(target, dist, yaw, pitch, time, duration);
                    } else pc.leaveMovieMode();
                });

        if (socialAction > 0 && socialAction < 5)
            target.broadcastPacket(new SocialAction(target.objectId(), socialAction));
    }

    private void blockAll(boolean flag) {
        block(frintezza, flag);
        block(weakScarlet, flag);
        block(strongScarlet, flag);
        portraits.forEach(p -> block(p, flag));
        demons.forEach(p -> block(p, flag));
    }

    private void block(NpcInstance npc, boolean flag) {
        if (npc == null || npc.isDead())
            return;
        if (flag) {
            npc.abortAttack(true, false);
            npc.abortCast(true, true);
            npc.setTarget(null);
            if (npc.isMoving)
                npc.stopMove();
        }
        npc.setBlock(flag);
        npc.setInvul(flag);
    }

    private void cleanUp() {
        startCollapseTimer(15 * 60 * 1000L);
        getPlayers().forEach(p ->
                p.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(15)));
        getNpcs().forEach(GameObject::deleteMe);
    }

    // Hack: ToRemove when doors will operate normally in reflections
    private void blockUnblockNpcs(boolean block, List<Integer> npcs) {
        getNpcs().filter(n -> npcs.contains(n.getNpcId()))
                .forEach(n -> {
                    n.setBlock(block);
                    n.setInvul(block);
                });
    }

    @Override
    protected void onCollapse() {
        super.onCollapse();

        if (musicTask != null)
            musicTask.cancel(true);
    }

//    private static class NpcLocation extends Location {
//        int npcId;
//
//        NpcLocation(Location loc, int npcId) {
//            this.x = loc.x;
//            this.y = loc.y;
//            this.z = loc.z;
//            this.h = loc.h;
//            this.npcId = npcId;
//        }
//
//        NpcLocation(int x, int y, int z, int heading, int npcId) {
//            super(x, y, z, heading);
//            this.npcId = npcId;
//        }
//    }

    private class FrintezzaStart extends RunnableImpl {
        @Override
        public void runImpl() {
            ThreadPoolManager.INSTANCE.schedule(new Spawn(1), 1000);
        }
    }

    private class Spawn extends RunnableImpl {
        private int taskId;

        Spawn(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void runImpl() {
            try {
                switch (taskId) {
                    case 1: // spawn.
                        _frintezzaDummy = spawn(new NpcLocation(-87784, -155096, -9080, 16048, 29059));
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(2), 1000);
                        break;
                    case 2:
                        closeDoor(corridorBDoors.get(1));
                        frintezza = spawn(frintezzaSpawn);
                        showSocialActionMovie(frintezza, 500, 90, 0, 6500, 8000, 0);
                        for (int i = 0; i < 4; i++) {
                            portraits.add(spawn(portraitSpawns.get(i)));
                            demons.add(spawn(demonSpawns.get(i)));
                        }
                        portraits.forEach(Creature::startImmobilized);
                        blockAll(true);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(3), 6500);
                        break;
                    case 3:
                        showSocialActionMovie(_frintezzaDummy, 1800, 90, 8, 6500, 7000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(4), 900);
                        break;
                    case 4:
                        showSocialActionMovie(_frintezzaDummy, 140, 90, 10, 2500, 4500, 0);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(5), 4000);
                        break;
                    case 5:
                        showSocialActionMovie(frintezza, 40, 75, -10, 0, 1000, 0);
                        showSocialActionMovie(frintezza, 40, 75, -10, 0, 12000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(6), 1350);
                        break;
                    case 6:
                        frintezza.broadcastPacket(new SocialAction(frintezza.objectId(), 2));
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(7), 7000);
                        break;
                    case 7:
                        _frintezzaDummy.deleteMe();
                        _frintezzaDummy = null;
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(8), 1000);
                        break;
                    case 8:
                        showSocialActionMovie(demons.get(0), 140, 0, 3, 22000, 3000, 1);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(9), 2800);
                        break;
                    case 9:
                        showSocialActionMovie(demons.get(1), 140, 0, 3, 22000, 3000, 1);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(10), 2800);
                        break;
                    case 10:
                        showSocialActionMovie(demons.get(2), 140, 180, 3, 22000, 3000, 1);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(11), 2800);
                        break;
                    case 11:
                        showSocialActionMovie(demons.get(3), 140, 180, 3, 22000, 3000, 1);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(12), 3000);
                        break;
                    case 12:
                        showSocialActionMovie(frintezza, 240, 90, 0, 0, 1000, 0);
                        showSocialActionMovie(frintezza, 240, 90, 25, 5500, 10000, 3);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(13), 3000);
                        break;
                    case 13:
                        showSocialActionMovie(frintezza, 100, 195, 35, 0, 10000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(14), 700);
                        break;
                    case 14:
                        showSocialActionMovie(frintezza, 100, 195, 35, 0, 10000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(15), 1300);
                        break;
                    case 15:
                        showSocialActionMovie(frintezza, 120, 180, 45, 1500, 10000, 0);
                        frintezza.broadcastPacket(new MagicSkillUse(frintezza, 5006, 34000));
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(16), 1500);
                        break;
                    case 16:
                        showSocialActionMovie(frintezza, 520, 135, 45, 8000, 10000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(17), 7500);
                        break;
                    case 17:
                        showSocialActionMovie(frintezza, 1500, 110, 25, 10000, 13000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(18), 9500);
                        break;
                    case 18:
                        weakScarlet = spawn(scarletSpawnWeak);
                        block(weakScarlet, true);
                        weakScarlet.addListener(_currentHpListener);
                        weakScarlet.broadcastPacket(new MagicSkillUse(weakScarlet, 5016, 3000));
                        Earthquake eq = new Earthquake(weakScarlet.getLoc(), 50, 6);
                        getPlayers().forEach(p -> p.broadcastPacket(eq));
                        showSocialActionMovie(weakScarlet, 1000, 160, 20, 6000, 6000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(19), 5500);
                        break;
                    case 19:
                        showSocialActionMovie(weakScarlet, 800, 160, 5, 1000, 10000, 2);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(20), 2100);
                        break;
                    case 20:
                        showSocialActionMovie(weakScarlet, 300, 60, 8, 0, 10000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(21), 2000);
                        break;
                    case 21:
                        showSocialActionMovie(weakScarlet, 1000, 90, 10, 3000, 5000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(22), 3000);
                        break;
                    case 22:
                        getPlayers().forEach(Player::leaveMovieMode);
                        ThreadPoolManager.INSTANCE.schedule(new Spawn(23), 2000);
                        break;
                    case 23:
                        blockAll(false);
                        spawn(new NpcLocation(-87904, -141296, -9168, 0, TeleportCube));
                        _scarletMorph = 1;
                        musicTask = ThreadPoolManager.INSTANCE.schedule(new Music(), 5000);
                        break;
                }
            } catch (RuntimeException e) {
                LOG.error("Error on Frintezza Spawn", e);
            }
        }
    }

    private class Music extends RunnableImpl {
        @Override
        public void runImpl() {
            if (frintezza == null)
                return;
            int song = Math.max(1, Math.min(4, getSong()));
            NpcString song_name;
            switch (song) {
                case 1:
                    song_name = NpcString.REQUIEM_OF_HATRED;
                    break;
                case 2:
                    song_name = NpcString.FRENETIC_TOCCATA;
                    break;
                case 3:
                    song_name = NpcString.FUGUE_OF_JUBILATION;
                    break;
                case 4:
                    song_name = NpcString.MOURNFUL_CHORALE_PRELUDE;
                    break;
                default:
                    return;
            }
            if (!frintezza.isBlocked()) {
                frintezza.broadcastPacket(new ExShowScreenMessage(song_name, 3000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                frintezza.broadcastPacket(new MagicSkillUse(frintezza, 5007, song, _intervalOfFrintezzaSongs));
                // Launch the song's effects (they start about 10 seconds after he starts to play)
                ThreadPoolManager.INSTANCE.schedule(new SongEffectLaunched(getSongTargets(song), song, 10000), 10000);
            }
            // Schedule a new song to be played in 30-40 seconds...
            musicTask = ThreadPoolManager.INSTANCE.schedule(new Music(), _intervalOfFrintezzaSongs + Rnd.get(10000));
        }

        /**
         * Depending on the song, returns the song's targets (either mobs or players)
         */
        private List<Creature> getSongTargets(int songId) {
            if (songId < 4) { // Target is the minions
                List<Creature> targets = new ArrayList<>();
                if (weakScarlet != null && !weakScarlet.isDead())
                    targets.add(weakScarlet);
                if (strongScarlet != null && !strongScarlet.isDead())
                    targets.add(strongScarlet);
                for (int i = 0; i < 4; i++) {
                    if (portraits.get(i) != null && !portraits.get(i).isDead())
                        targets.add(portraits.get(i));
                    if (demons.get(i) != null && !demons.get(i).isDead())
                        targets.add(demons.get(i));
                }
                return targets;
            } else
                // Target is the players
                return getPlayers().filter(pc -> !pc.isDead())
                        .collect(Collectors.toList());
        }

        /**
         * returns the chosen symphony for Frintezza to play
         * If the minions are injured he has 40% to play a healing song
         * If they are all dead, he will only play harmful getPlayer symphonies
         */
        private int getSong() {
            if (minionsNeedHeal())
                return 1;
            return Rnd.get(2, 4);
        }

        /**
         * Checks if Frintezza's minions need heal (only major minions are checked) Return a "need heal" = true only 40% of the time
         */
        private boolean minionsNeedHeal() {
            if (!Rnd.chance(40))
                return false;
            if (weakScarlet != null && !weakScarlet.isAlikeDead() && weakScarlet.getCurrentHp() < weakScarlet.getMaxHp() * 2 / 3)
                return true;
            if (strongScarlet != null && !strongScarlet.isAlikeDead() && strongScarlet.getCurrentHp() < strongScarlet.getMaxHp() * 2 / 3)
                return true;
            for (int i = 0; i < portraits.size(); i++) {
                if (portraits.get(i) != null && !portraits.get(i).isDead() && portraits.get(i).getCurrentHp() < portraits.get(i).getMaxHp() / 3)
                    return true;
                if (demons.get(i) != null && !demons.get(i).isDead() && demons.get(i).getCurrentHp() < demons.get(i).getMaxHp() / 3)
                    return true;
            }
            return false;
        }
    }

    /**
     * The song was played, this class checks it's affects (if any)
     */
    private class SongEffectLaunched extends RunnableImpl {
        private final List<Creature> _targets;
        private final int _song, _currentTime;

        /**
         * @param targets           - song's targets
         * @param song              - song id 1-5
         * @param currentTimeOfSong - skills during music play are consecutive, repeating
         */
        SongEffectLaunched(List<Creature> targets, int song, int currentTimeOfSong) {
            _targets = targets;
            _song = song;
            _currentTime = currentTimeOfSong;
        }

        @Override
        public void runImpl() {
            if (frintezza == null)
                return;
            // If the song time is over stop this loop
            if (_currentTime > _intervalOfFrintezzaSongs)
                return;
            // Skills are consecutive, so call them again
            SongEffectLaunched songLaunched = new SongEffectLaunched(_targets, _song, _currentTime + _intervalOfFrintezzaSongs / 10);
            ThreadPoolManager.INSTANCE.schedule(songLaunched, _intervalOfFrintezzaSongs / 10);
            frintezza.callSkill(5008, _song, _targets, false);
        }
    }

    private class SecondMorph extends RunnableImpl {
        private int _taskId;

        SecondMorph(int taskId) {
            _taskId = taskId;
        }

        @Override
        public void runImpl() {
            try {
                switch (_taskId) {
                    case 1:
                        int angle = Math.abs((weakScarlet.getHeading() < 32768 ? 180 : 540) - (int) (weakScarlet.getHeading() / 182.044444444));
                        getPlayers().forEach(Player::enterMovieMode);
                        blockAll(true);
                        showSocialActionMovie(weakScarlet, 500, angle, 5, 500, 15000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new SecondMorph(2), 2000);
                        break;
                    case 2:
                        weakScarlet.broadcastPacket(new SocialAction(weakScarlet.objectId(), 1));
                        weakScarlet.setCurrentHp(weakScarlet.getMaxHp() * 3 / 4., false);
                        weakScarlet.setRHandId(_frintezzasSwordId);
                        weakScarlet.broadcastCharInfo();
                        ThreadPoolManager.INSTANCE.schedule(new SecondMorph(3), 5500);
                        break;
                    case 3:
                        weakScarlet.broadcastPacket(new SocialAction(weakScarlet.objectId(), 4));
                        blockAll(false);
                        demonMorph.getEffects(weakScarlet);
                        getPlayers().forEach(Player::leaveMovieMode);
                        break;
                }
            } catch (RuntimeException e) {
                LOG.error("Error on Frintezza Second Morph", e);
            }
        }
    }

    private class ThirdMorph extends RunnableImpl {
        private int _taskId;
        private int _angle = 0;

        ThirdMorph(int taskId) {
            _taskId = taskId;
        }

        @Override
        public void runImpl() {
            try {
                switch (_taskId) {
                    case 1:
                        _angle = Math.abs((weakScarlet.getHeading() < 32768 ? 180 : 540) - (int) (weakScarlet.getHeading() / 182.044444444));
                        getPlayers().forEach(Player::enterMovieMode);
                        blockAll(true);
                        frintezza.broadcastPacket(new MagicSkillCanceled(frintezza.objectId()));
                        frintezza.broadcastPacket(new SocialAction(frintezza.objectId(), 4));
                        ThreadPoolManager.INSTANCE.schedule(new ThirdMorph(2), 100);
                        break;
                    case 2:
                        showSocialActionMovie(frintezza, 250, 120, 15, 0, 1000, 0);
                        showSocialActionMovie(frintezza, 250, 120, 15, 0, 10000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new ThirdMorph(3), 6500);
                        break;
                    case 3:
                        frintezza.broadcastPacket(new MagicSkillUse(frintezza, 5006, 34000));
                        showSocialActionMovie(frintezza, 500, 70, 15, 3000, 10000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new ThirdMorph(4), 3000);
                        break;
                    case 4:
                        showSocialActionMovie(frintezza, 2500, 90, 12, 6000, 10000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new ThirdMorph(5), 3000);
                        break;
                    case 5:
                        showSocialActionMovie(weakScarlet, 250, _angle, 12, 0, 1000, 0);
                        showSocialActionMovie(weakScarlet, 250, _angle, 12, 0, 10000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new ThirdMorph(6), 500);
                        break;
                    case 6:
                        weakScarlet.doDie(weakScarlet);
                        showSocialActionMovie(weakScarlet, 450, _angle, 14, 8000, 8000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new ThirdMorph(7), 6250);
                        break;
                    case 7:
                        NpcLocation loc = new NpcLocation(weakScarlet.getLoc(), _strongScarletId);
                        loc.set(weakScarlet.getLoc());
                        loc.npcId = _strongScarletId;
                        weakScarlet.deleteMe();
                        weakScarlet = null;
                        strongScarlet = spawn(loc);
                        strongScarlet.addListener(_deathListener);
                        block(strongScarlet, true);
                        showSocialActionMovie(strongScarlet, 450, _angle, 12, 500, 14000, 2);
                        ThreadPoolManager.INSTANCE.schedule(new ThirdMorph(9), 5000);
                        break;
                    case 9:
                        blockAll(false);
                        getPlayers().forEach(Player::leaveMovieMode);

                        demonMorph.getEffects(strongScarlet);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Die extends RunnableImpl {
        private int _taskId;

        Die(int taskId) {
            _taskId = taskId;
        }

        @Override
        public void runImpl() {
            try {
                switch (_taskId) {
                    case 1:
                        blockAll(true);
                        int _angle = Math.abs((strongScarlet.getHeading() < 32768 ? 180 : 540) - (int) (strongScarlet.getHeading() / 182.044444444));
                        showSocialActionMovie(strongScarlet, 300, _angle - 180, 5, 0, 7000, 0);
                        showSocialActionMovie(strongScarlet, 200, _angle, 85, 4000, 10000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new Die(2), 7500);
                        break;
                    case 2:
                        showSocialActionMovie(frintezza, 100, 120, 5, 0, 7000, 0);
                        showSocialActionMovie(frintezza, 100, 90, 5, 5000, 15000, 0);
                        ThreadPoolManager.INSTANCE.schedule(new Die(3), 6000);
                        break;
                    case 3:
                        showSocialActionMovie(frintezza, 900, 90, 25, 7000, 10000, 0);
                        frintezza.doDie(frintezza);
                        frintezza = null;
                        ThreadPoolManager.INSTANCE.schedule(new Die(4), 7000);
                        break;
                    case 4:
                        getPlayers().forEach(Player::leaveMovieMode);
                        cleanUp();
                        break;
                }
            } catch (RuntimeException e) {
                LOG.error("Error On Frintezza Death", e);
            }
        }
    }

    public class CurrentHpListener implements OnCurrentHpDamageListener {
        @Override
        public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill) {
            if (actor.isDead() || actor != weakScarlet)
                return;
            double newHp = actor.getCurrentHp() - damage;
            double maxHp = actor.getMaxHp();
            switch (_scarletMorph) {
                case 1:
                    if (newHp < 0.75 * maxHp) {
                        _scarletMorph = 2;
                        ThreadPoolManager.INSTANCE.schedule(new SecondMorph(1), 1100);
                    }
                    break;
                case 2:
                    if (newHp < 0.1 * maxHp) {
                        _scarletMorph = 3;
                        ThreadPoolManager.INSTANCE.schedule(new ThirdMorph(1), 2000);
                    }
                    break;
            }
        }
    }

    private class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature self, Creature killer) {
            if (self instanceof NpcInstance) {
                if (self.getNpcId() == HallAlarmDevice) {
                    hallADoors.forEach(Frintezza.this::openDoor);
                    blockUnblockNpcs(false, blockANpcs);
                    getNpcs().filter(n -> (blockANpcs.contains(n.getNpcId())))
                            .forEach(n -> n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, Rnd.get(getPlayers().collect(Collectors.toList())), 200));
                } else if (blockANpcs.contains(self.getNpcId())) {
                    //ToCheck: find easier way
                    if (getNpcs().filter(n -> blockANpcs.contains(n.getNpcId()))
                            .anyMatch(n -> !n.isDead()))
                        return;
                    for (int corridorADoor : corridorADoors) openDoor(corridorADoor);
                    blockUnblockNpcs(true, blockBNpcs);
                } else if (self.getNpcId() == DarkChoirPlayer) {
                    if (getNpcs().filter(n -> n.getNpcId() == DarkChoirPlayer)
                            .anyMatch(n -> !n.isDead()))
                        return;
                    for (int hallBDoor : hallBDoors) openDoor(hallBDoor);
                    blockUnblockNpcs(false, blockBNpcs);
                } else if (blockBNpcs.contains(self.getNpcId())) {
                    if (Rnd.chance(10))
                        ((NpcInstance) self).dropItem(killer.getPlayer(), DewdropItem, 1);
                    //ToCheck: find easier way
                    if (getNpcs().anyMatch(n -> (blockBNpcs.contains(n.getNpcId()) || (blockANpcs.contains(n.getNpcId())) && !n.isDead())))
                        return;
                    corridorBDoors.forEach(Frintezza.this::openDoor);
                    ThreadPoolManager.INSTANCE.schedule(new FrintezzaStart(), battleStartDelay);
                } else if (self.getNpcId() == _weakScarletId) {
                    self.decayMe();
                } else if (self.getNpcId() == _strongScarletId) {
                    ThreadPoolManager.INSTANCE.schedule(new Die(1), 10);
                    setReenterTime(System.currentTimeMillis());
                }
            }
        }
    }

}