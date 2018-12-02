package l2trunk.gameserver.model.entity.events.impl;

import l2trunk.commons.collections.MultiValueSet;
import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.commons.lang.reference.HardReference;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.dao.SiegeClanDAO;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.OnKillListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.base.RestartType;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.entity.events.objects.ZoneObject;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.ExPVPMatchCCRetire;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.RelationChanged;
import l2trunk.gameserver.network.serverpackets.ShowSiegeKillResults;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.ClanTable;
import l2trunk.gameserver.templates.DoorTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public abstract class SiegeEvent<R extends Residence, S extends SiegeClanObject> extends GlobalEvent {
    public static final String ATTACKERS = "attackers";
    public static final String DEFENDERS = "defenders";
    public static final String FLAG_ZONES = "flag_zones";
    public static final String DOORS = "doors";
    static final String OWNER = "owner";
    static final String OLD_OWNER = "old_owner";
    static final String SPECTATORS = "spectators";
    static final String SIEGE_ZONES = "siege_zones";
    private static final String DAY_OF_WEEK = "day_of_week";
    private static final String HOUR_OF_DAY = "hour_of_day";
    private static final String REGISTRATION = "registration";
    private static ScheduledFuture<?> _resultsThread = null;
    final int _dayOfWeek;
    final int _hourOfDay;
    private final List<HardReference<SummonInstance>> _siegeSummons = new ArrayList<>();
    R _residence;
    Clan _oldOwner;
    OnKillListener _killListener = new KillListener();
    OnDeathListener _doorDeathListener = new DoorDeathListener();
    private boolean _isInProgress;
    private boolean _isRegistrationOver;

    public SiegeEvent(MultiValueSet<String> set) {
        super(set);
        _dayOfWeek = set.getInteger(DAY_OF_WEEK, 0);
        _hourOfDay = set.getInteger(HOUR_OF_DAY, 0);
    }

    static void showResults() {
        if (_resultsThread != null)
            return;

        List<Clan> clans = ClanTable.INSTANCE.getClans();
        clans.sort((o1, o2) -> o2.getSiegeKills() - o1.getSiegeKills());


        ShowSiegeKillResults results =
                new ShowSiegeKillResults(clans
                        .stream()
                        .filter(a -> a.getSiegeKills() > 0)
                        .limit(25)
                        .collect(Collectors.toList()));
        broadcastToWorld(results);

        _resultsThread = ThreadPoolManager.INSTANCE.schedule(() ->
        {
            broadcastToWorld(ExPVPMatchCCRetire.STATIC);
            ClanTable.INSTANCE.getClans().forEach(cl -> cl.setSiegeKills(0));
            _resultsThread = null;
        }, 5 * 60000L);
    }

    // ========================================================================================================================================================================
    // Start / Stop Siege
    // ========================================================================================================================================================================

    @Override
    public void startEvent() {
        setInProgress(true);

        super.startEvent();
    }

    @Override
    public final void stopEvent() {
        stopEvent(false);
    }

    void stopEvent(boolean step) {
        despawnSiegeSummons();
        setInProgress(false);
        reCalcNextTime(false);

        super.stopEvent();
    }

    public void processStep(Clan clan) {
        //
    }

    @Override
    public void reCalcNextTime(boolean onInit) {
        clearActions();

        final Calendar startSiegeDate = getResidence().getSiegeDate();
        if (onInit) {
            if (startSiegeDate.getTimeInMillis() <= System.currentTimeMillis()) {
                startSiegeDate.set(Calendar.DAY_OF_WEEK, _dayOfWeek);
                startSiegeDate.set(Calendar.HOUR_OF_DAY, _hourOfDay);

                validateSiegeDate(startSiegeDate, 1);
                getResidence().setJdbcState(JdbcEntityState.UPDATED);
            }
        } else {
            startSiegeDate.add(Calendar.WEEK_OF_YEAR, 1);
            getResidence().setJdbcState(JdbcEntityState.UPDATED);
        }

        registerActions();

        getResidence().update();
    }

    void validateSiegeDate(Calendar calendar, int add) {
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        while (calendar.getTimeInMillis() < System.currentTimeMillis())
            calendar.add(Calendar.WEEK_OF_YEAR, add);
    }

    @Override
    protected long startTimeMillis() {
        return getResidence().getSiegeDate().getTimeInMillis();
    }

    // ========================================================================================================================================================================
    // Zones
    // ========================================================================================================================================================================

    @Override
    public void teleportPlayers(String t) {
        List<Player> players = new ArrayList<>();
        Clan ownerClan = getResidence().getOwner();
        if (t.equalsIgnoreCase(OWNER)) {
            if (ownerClan != null)
                for (Player player : getPlayersInZone())
                    if (player.getClan() == ownerClan)
                        players.add(player);
        } else if (t.equalsIgnoreCase(ATTACKERS)) {
            for (Player player : getPlayersInZone()) {
                S siegeClan = getSiegeClan(ATTACKERS, player.getClan());
                if (siegeClan != null && siegeClan.isParticle(player))
                    players.add(player);
            }
        } else if (t.equalsIgnoreCase(DEFENDERS)) {
            for (Player player : getPlayersInZone()) {
                if (ownerClan != null && player.getClan() != null && player.getClan() == ownerClan)
                    continue;

                S siegeClan = getSiegeClan(DEFENDERS, player.getClan());
                if (siegeClan != null && siegeClan.isParticle(player))
                    players.add(player);
            }
        } else if (t.equalsIgnoreCase(SPECTATORS)) {
            for (Player player : getPlayersInZone()) {
                if (ownerClan != null && player.getClan() != null && player.getClan() == ownerClan)
                    continue;

                if (player.getClan() == null || getSiegeClan(ATTACKERS, player.getClan()) == null && getSiegeClan(DEFENDERS, player.getClan()) == null)
                    players.add(player);
            }
        } else
            players = getPlayersInZone();

        for (Player player : players) {
            Location loc = null;
            if (t.equalsIgnoreCase(OWNER) || t.equalsIgnoreCase(DEFENDERS))
                loc = getResidence().getOwnerRestartPoint();
            else
                loc = getResidence().getNotOwnerRestartPoint(player);

            player.teleToLocation(loc, ReflectionManager.DEFAULT);
        }
    }

    List<Player> getPlayersInZone() {
        List<ZoneObject> zones = getObjects(SIEGE_ZONES);
        List<Player> result = new ArrayList<>();
        for (ZoneObject zone : zones)
            result.addAll(zone.getInsidePlayers());
        return result;
    }

    void broadcastInZone(L2GameServerPacket... packet) {
        for (Player player : getPlayersInZone())
            player.sendPacket(packet);
    }

    public void broadcastInZone(IStaticPacket... packet) {
        for (Player player : getPlayersInZone())
            player.sendPacket(packet);
    }

    boolean checkIfInZone(Creature character) {
        List<ZoneObject> zones = getObjects(SIEGE_ZONES);
        for (ZoneObject zone : zones)
            if (zone.checkIfInZone(character))
                return true;
        return false;
    }

    public void broadcastInZone2(IStaticPacket... packet) {
        for (Player player : getResidence().getZone().getInsidePlayers())
            player.sendPacket(packet);
    }

    void broadcastInZone2(L2GameServerPacket... packet) {
        for (Player player : getResidence().getZone().getInsidePlayers())
            player.sendPacket(packet);
    }

    // ========================================================================================================================================================================
    // Siege Clans
    // ========================================================================================================================================================================
    void loadSiegeClans() {
        addObjects(ATTACKERS, SiegeClanDAO.getInstance().load(getResidence(), ATTACKERS));
        addObjects(DEFENDERS, SiegeClanDAO.getInstance().load(getResidence(), DEFENDERS));
    }

    @SuppressWarnings("unchecked")
    public S newSiegeClan(String type, int clanId, long param, long date) {
        Clan clan = ClanTable.INSTANCE.getClan(clanId);
        return clan == null ? null : (S) new SiegeClanObject(type, clan, param, date);
    }

    void updateParticles(boolean start, String... arg) {
        for (String a : arg) {
            List<SiegeClanObject> siegeClans = getObjects(a);
            for (SiegeClanObject s : siegeClans)
                s.setEvent(start, this);
        }
    }

    public S getSiegeClan(String name, Clan clan) {
        if (clan == null)
            return null;
        return getSiegeClan(name, clan.getClanId());
    }

    @SuppressWarnings("unchecked")
    public S getSiegeClan(String name, int objectId) {
        List<SiegeClanObject> siegeClanList = getObjects(name);
        if (siegeClanList.isEmpty())
            return null;
        for (SiegeClanObject siegeClan : siegeClanList) {
            if (siegeClan.getObjectId() == objectId)
                return (S) siegeClan;
        }
        return null;
    }

    public void broadcastTo(IStaticPacket packet, String... types) {
        for (String type : types) {
            List<SiegeClanObject> siegeClans = getObjects(type);
            for (SiegeClanObject siegeClan : siegeClans)
                siegeClan.broadcast(packet);
        }
    }

    public void broadcastTo(L2GameServerPacket packet, String... types) {
        for (String type : types) {
            List<SiegeClanObject> siegeClans = getObjects(type);
            for (SiegeClanObject siegeClan : siegeClans)
                siegeClan.broadcast(packet);
        }
    }

    // ========================================================================================================================================================================
    // Override GlobalEvent
    // ========================================================================================================================================================================

    @Override
    @SuppressWarnings("unchecked")
    public void initEvent() {
        _residence = (R) ResidenceHolder.getInstance().getResidence(getId());

        loadSiegeClans();

        clearActions();

        super.initEvent();
    }

    @Override
    protected void printInfo() {
        final long startSiegeMillis = startTimeMillis();

        if (startSiegeMillis == 0)
            LOG.info(getName() + " time - undefined");
        else
            LOG.info(getName() + " time - " + TimeUtils.toSimpleFormat(startSiegeMillis));
    }

    @Override
    public boolean ifVar(String name) {
        if (name.equals(OWNER))
            return getResidence().getOwner() != null;
        if (name.equals(OLD_OWNER))
            return _oldOwner != null;

        return false;
    }

    @Override
    public boolean isParticle(Player player) {
        if (!isInProgress() || player.getClan() == null)
            return false;
        return getSiegeClan(ATTACKERS, player.getClan()) != null || getSiegeClan(DEFENDERS, player.getClan()) != null;
    }

    @Override
    public void checkRestartLocs(Player player, Map<RestartType, Boolean> r) {
        if (getObjects(FLAG_ZONES).isEmpty())
            return;

        S clan = getSiegeClan(ATTACKERS, player.getClan());
        if (clan != null)
            if (clan.getFlag() != null)
                r.put(RestartType.TO_FLAG, Boolean.TRUE);
    }

    @Override
    public Location getRestartLoc(Player player, RestartType type) {
        S attackerClan = getSiegeClan(ATTACKERS, player.getClan());

        Location loc = null;
        switch (type) {
            case TO_FLAG:
                if (!getObjects(FLAG_ZONES).isEmpty() && attackerClan != null && attackerClan.getFlag() != null)
                    loc = Location.findPointToStay(attackerClan.getFlag(), 50, 75);
                else
                    player.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
                break;
            case TO_VILLAGE:
                // If the Lords of Dawn's own seal (Dawn), and in the siege of the city is, then teleport in the 2nd in a row the city.
                if (SevenSigns.INSTANCE.getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN)
                    loc = _residence.getNotOwnerRestartPoint(player);
                break;
        }

        return loc;
    }

    @Override
    public int getRelation(Player thisPlayer, Player targetPlayer, int result) {
        Clan clan1 = thisPlayer.getClan();
        Clan clan2 = targetPlayer.getClan();
        if (clan1 == null || clan2 == null)
            return result;

        SiegeEvent<?, ?> siegeEvent2 = targetPlayer.getEvent(SiegeEvent.class);
        if (this == siegeEvent2) {
            result |= RelationChanged.RELATION_INSIEGE;

            SiegeClanObject siegeClan1 = getSiegeClan(SiegeEvent.ATTACKERS, clan1);
            SiegeClanObject siegeClan2 = getSiegeClan(SiegeEvent.ATTACKERS, clan2);

            if (siegeClan1 == null && siegeClan2 == null || siegeClan1 != null && siegeClan2 != null && isAttackersInAlly())
                result |= RelationChanged.RELATION_ALLY;
            else
                result |= RelationChanged.RELATION_ENEMY;
            if (siegeClan1 != null)
                result |= RelationChanged.RELATION_ATTACKER;
        }

        return result;
    }

    @Override
    public int getUserRelation(Player thisPlayer, int oldRelation) {
        SiegeClanObject siegeClan = getSiegeClan(SiegeEvent.ATTACKERS, thisPlayer.getClan());
        if (siegeClan != null)
            oldRelation |= 0x180;
        else
            oldRelation |= 0x80;
        return oldRelation;
    }

    @Override
    public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force) {
        SiegeEvent<?, ?> siegeEvent = target.getEvent(SiegeEvent.class);
        SiegeEvent<?, ?> siegeEvent2 = attacker.getEvent(SiegeEvent.class);

        if (siegeEvent2 != siegeEvent)
            return null;
        if ((!checkIfInZone(target)) || (!checkIfInZone(attacker))) {
            return null;
        }
        Player player = target.getPlayer();
        if (player == null) {
            return null;
        }
        SiegeClanObject siegeClan1 = getSiegeClan("attackers", player.getClan());
        if ((siegeClan1 == null) && (attacker.isSiegeGuard()))
            return SystemMsg.INVALID_TARGET;
        Player playerAttacker = attacker.getPlayer();
        if (playerAttacker == null) {
            return SystemMsg.INVALID_TARGET;
        }
        SiegeClanObject siegeClan2 = getSiegeClan("attackers", playerAttacker.getClan());

        if ((siegeClan1 != null) && (siegeClan2 != null) && (isAttackersInAlly())) {
            return SystemMsg.FORCE_ATTACK_IS_IMPOSSIBLE_AGAINST_A_TEMPORARY_ALLIED_MEMBER_DURING_A_SIEGE;
        }
        if ((siegeClan1 == null) && (siegeClan2 == null)) {
            return SystemMsg.INVALID_TARGET;
        }
        return null;
    }

    @Override
    public boolean isInProgress() {
        return _isInProgress;
    }

    public void setInProgress(boolean b) {
        _isInProgress = b;
    }

    @Override
    public void action(String name, boolean start) {
        if (name.equalsIgnoreCase(REGISTRATION))
            setRegistrationOver(!start);
        else
            super.action(name, start);
    }

    boolean isAttackersInAlly() {
        return false;
    }

    @Override
    public void onAddEvent(GameObject object) {
        if (_killListener == null)
            return;

        if (object.isPlayer())
            ((Player) object).addListener(_killListener);
    }

    @Override
    public void onRemoveEvent(GameObject object) {
        if (_killListener == null)
            return;

        if (object.isPlayer())
            ((Player) object).removeListener(_killListener);
    }

    @Override
    public List<Player> broadcastPlayers(int range) {
        return itemObtainPlayers();
    }

    @Override
    public List<Player> itemObtainPlayers() {
        List<Player> playersInZone = getPlayersInZone();

        List<Player> list = new ArrayList<>(playersInZone.size());
        for (Player player : getPlayersInZone()) {
            if (player.getEvent(getClass()) == this)
                list.add(player);
        }
        return list;
    }

    public Location getEnterLoc(Player player) {
        S siegeClan = getSiegeClan(ATTACKERS, player.getClan());
        if (siegeClan != null) {
            if (siegeClan.getFlag() != null)
                return Location.findAroundPosition(siegeClan.getFlag(), 50, 75);
            else
                return getResidence().getNotOwnerRestartPoint(player);
        } else
            return getResidence().getOwnerRestartPoint();
    }

    // ========================================================================================================================================================================
    // Getters & Setters
    // ========================================================================================================================================================================
    public R getResidence() {
        return _residence;
    }

    public boolean isRegistrationOver() {
        return _isRegistrationOver;
    }

    public void setRegistrationOver(boolean b) {
        _isRegistrationOver = b;
    }

    // ========================================================================================================================================================================
    public void addSiegeSummon(SummonInstance summon) {
        _siegeSummons.add(summon.getRef());
    }

    public boolean containsSiegeSummon(SummonInstance cha) {
        return _siegeSummons.contains(cha.getRef());
    }

    void despawnSiegeSummons() {
        for (HardReference<SummonInstance> ref : _siegeSummons) {
            SummonInstance summon = ref.get();
            if (summon != null)
                summon.unSummon();
        }
        _siegeSummons.clear();
    }


    public class DoorDeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature actor, Creature killer) {
            if (!isInProgress())
                return;

            DoorInstance door = (DoorInstance) actor;
            if (door.getDoorType() == DoorTemplate.DoorType.WALL)
                return;

            broadcastTo(SystemMsg.THE_CASTLE_GATE_HAS_BEEN_DESTROYED, SiegeEvent.ATTACKERS, SiegeEvent.DEFENDERS);
        }
    }

    public class KillListener implements OnKillListener {
        @Override
        public void onKill(Creature actor, Creature victim) {
            Player winner = actor.getPlayer();

            if (winner == null || !victim.isPlayer() || winner.getLevel() < 40 || winner == victim || victim.getEvent(SiegeEvent.this.getClass()) != SiegeEvent.this || !checkIfInZone(actor) || !checkIfInZone(victim))
                return;

            Player killed = victim.getPlayer();
            if (killed == null)
                return;

            if (killed.getVar("DisabledSiegeFame") != null)
                return;

            killed.setVar("DisabledSiegeFame", "true", System.currentTimeMillis() + 300000L);

            if (winner.getPlayerGroup() == killed.getPlayerGroup()) // Self, Party and Command Channel check.
                return;


            if (winner.isInSameClan(killed)) {
                return;
            }
            if (winner.getParty() == null)
                winner.setFame(winner.getFame() + Rnd.get(10, 20), SiegeEvent.this.toString());
            else {
                for (Player member : winner.getParty().getMembers())
                    member.setFame(member.getFame() + Rnd.get(10, 20), SiegeEvent.this.toString());
            }

            if (SiegeEvent.this instanceof CastleSiegeEvent)
                winner.getCounters().playersKilledInSiege++;
            if (SiegeEvent.this instanceof DominionSiegeEvent)
                winner.getCounters().playersKilledInDominion++;
        }

        @Override
        public boolean ignorePetOrSummon() {
            return true;
        }
    }
}
