package l2trunk.gameserver.model.entity.events.impl;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.dao.CastleDamageZoneDAO;
import l2trunk.gameserver.dao.CastleDoorUpgradeDAO;
import l2trunk.gameserver.dao.CastleHiredGuardDAO;
import l2trunk.gameserver.dao.SiegeClanDAO;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.base.RestartType;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.entity.HeroDiary;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.objects.*;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.UnitMember;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.support.MerchantGuard;
import l2trunk.gameserver.utils.Location;

import java.util.*;
import java.util.concurrent.Future;

public final class CastleSiegeEvent extends SiegeEvent<Castle, SiegeClanObject> {
    public static final int MAX_SIEGE_CLANS = 20;
    public static final String DEFENDERS_WAITING = "defenders_waiting";
    public static final String DEFENDERS_REFUSED = "defenders_refused";
    public static final String CONTROL_TOWERS = "control_towers";
    public static final String BOUGHT_ZONES = "bought_zones";
    private static final long DAY_IN_MILISECONDS = 86400000L;
    private static final String GUARDS = "guards";
    private static final String HIRED_GUARDS = "hired_guards";
    private Set<Integer> _nextSiegeTimes = new HashSet<>();
    private Future<?> _nextSiegeDateSetTask = null;
    private boolean _firstStep = false;

    public CastleSiegeEvent(StatsSet set) {
        super(set);
    }

    // ========================================================================================================================================================================
    // Главные методы осады
    // ========================================================================================================================================================================
    @Override
    public void initEvent() {
        super.initEvent();

        List<DoorObject> doorObjects = getObjects(DOORS);

        addObjects(BOUGHT_ZONES, CastleDamageZoneDAO.INSTANCE.load(getResidence()));

        for (DoorObject doorObject : doorObjects) {
            doorObject.setUpgradeValue(this, CastleDoorUpgradeDAO.INSTANCE.load(doorObject.getUId()));
            doorObject.getDoor().addListener(_doorDeathListener);
        }
    }

    @Override
    public void processStep(Clan newOwnerClan) {
        Clan oldOwnerClan = getResidence().getOwner();

        getResidence().changeOwner(newOwnerClan);

        // если есть овнер в резиденции, делаем его аттакером
        if (oldOwnerClan != null) {
            SiegeClanObject ownerSiegeClan = getSiegeClan(DEFENDERS, oldOwnerClan);
            removeObject(DEFENDERS, ownerSiegeClan);

            ownerSiegeClan.setType(ATTACKERS);
            addObject(ATTACKERS, ownerSiegeClan);
        } else {
            // Если атакуется замок, принадлежащий NPC, и только 1 атакующий - закончить осаду
            if (getObjects(ATTACKERS).size() == 1) {
                stopEvent();
                return;
            }

            // Если атакуется замок, принадлежащий NPC, и все атакующие в одном альянсе - закончить осаду
            int allianceObjectId = newOwnerClan.getAllyId();
            if (allianceObjectId > 0) {
                List<SiegeClanObject> attackers = getObjects(ATTACKERS);
                boolean sameAlliance = true;
                for (SiegeClanObject sc : attackers) {
                    if ((sc != null) && (sc.getClan().getAllyId() != allianceObjectId)) {
                        sameAlliance = false;
                    }
                }
                if (sameAlliance) {
                    stopEvent();
                    return;
                }
            }
        }

        // ставим нового овнера защитником
        SiegeClanObject newOwnerSiegeClan = getSiegeClan(ATTACKERS, newOwnerClan);
        newOwnerSiegeClan.deleteFlag();
        newOwnerSiegeClan.setType(DEFENDERS);

        removeObject(ATTACKERS, newOwnerSiegeClan);

        // у нас защитник ток овнер
        List<SiegeClanObject> defenders = removeObjects(DEFENDERS);
        for (SiegeClanObject siegeClan : defenders) {
            siegeClan.setType(ATTACKERS);
        }

        // новый овнер это защитник
        addObject(DEFENDERS, newOwnerSiegeClan);

        // все дефендеры, стают аттакующими
        addObjects(ATTACKERS, defenders);

        updateParticles(true, ATTACKERS, DEFENDERS);

        teleportPlayers(ATTACKERS);
        teleportPlayers(SPECTATORS);

        // ток при первом захвате обнуляем мерчант гвардов и убираем апгрейд дверей
        if (!_firstStep) {
            _firstStep = true;

            broadcastTo(SystemMsg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_HAS_BEEN_DISSOLVED, ATTACKERS, DEFENDERS);

            if (oldOwner != null) {
                spawnAction(HIRED_GUARDS, false);
                damageZoneAction(false);
                removeObjects(HIRED_GUARDS);
                removeObjects(BOUGHT_ZONES);

                CastleDamageZoneDAO.INSTANCE.delete(getResidence());
            } else {
                spawnAction(GUARDS, false);
            }

            List<DoorObject> doorObjects = getObjects(DOORS);
            for (DoorObject doorObject : doorObjects) {
                doorObject.setWeak(true);
                doorObject.setUpgradeValue(this, 0);

                CastleDoorUpgradeDAO.INSTANCE.delete(doorObject.getUId());
            }
        }

        spawnAction(DOORS, true);
        despawnSiegeSummons();
    }

    @Override
    public void startEvent() {
        oldOwner = getResidence().getOwner();
        if (oldOwner != null) {
            addObject(DEFENDERS, new SiegeClanObject(DEFENDERS, oldOwner, 0));

            if (getResidence().getSpawnMerchantTickets().size() > 0) {
                for (ItemInstance item : getResidence().getSpawnMerchantTickets()) {
                    MerchantGuard guard = getResidence().getMerchantGuard(item.getItemId());

                    addObject(HIRED_GUARDS, new SpawnSimpleObject(guard.getNpcId(), item.getLoc()));

                    item.deleteMe();
                }

                CastleHiredGuardDAO.INSTANCE.delete(getResidence());

                spawnAction(HIRED_GUARDS, true);
            }
        }

        List<SiegeClanObject> attackers = getObjects(ATTACKERS);
        if (attackers.isEmpty()) {
            if (oldOwner == null) {
                broadcastToWorld(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addResidenceName(getResidence()));
            } else {
                broadcastToWorld(new SystemMessage2(SystemMsg.S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED).addResidenceName(getResidence()));
            }

            reCalcNextTime(false);
            return;
        }

        SiegeClanDAO.INSTANCE.delete(getResidence());

        updateParticles(true, ATTACKERS, DEFENDERS);

        broadcastTo(SystemMsg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_IS_IN_EFFECT, ATTACKERS);
        broadcastTo(new SystemMessage2(SystemMsg.YOU_ARE_PARTICIPATING_IN_THE_SIEGE_OF_S1_THIS_SIEGE_IS_SCHEDULED_FOR_2_HOURS).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);

        super.startEvent();

        if (oldOwner == null) {
            initControlTowers();
        } else {
            damageZoneAction(true);
        }
    }

    @Override
    public void stopEvent(boolean step) {
        List<DoorObject> doorObjects = getObjects(DOORS);
        doorObjects.forEach(d -> d.setWeak(false));

        damageZoneAction(false);

        updateParticles(false, ATTACKERS, DEFENDERS);

        List<SiegeClanObject> attackers = removeObjects(ATTACKERS);
        attackers.forEach(SiegeClanObject::deleteFlag);


        broadcastToWorld(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()));

        removeObjects(DEFENDERS);
        removeObjects(DEFENDERS_WAITING);
        removeObjects(DEFENDERS_REFUSED);

        Clan ownerClan = getResidence().getOwner();
        if (ownerClan != null) {
            if (oldOwner == ownerClan) {
                getResidence().setRewardCount(getResidence().getRewardCount() + 1);
                ownerClan.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE).addInteger(ownerClan.incReputation(1500, false, toString())));

                // Alexander - Give the winner clan a reputation reward. Half reward if the clan keeps the castle
                if (Config.SIEGE_WINNER_REPUTATION_REWARD > 0)
                    ownerClan.incReputation(Config.SIEGE_WINNER_REPUTATION_REWARD / 2, false, "SiegeWinnerCustomReward");
            } else {
                L2GameServerPacket packet = new Say2(0, ChatType.CRITICAL_ANNOUNCE, getResidence().getName() + " Castle", "Clan " + ownerClan.getName() + " is victorious over " + getResidence().getName() + "'s castle siege!");
                GameObjectsStorage.getAllPlayersStream().forEach(player -> player.sendPacket(packet));

                ownerClan.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE).addInteger(ownerClan.incReputation(3000, false, toString())));

                if (oldOwner != null) {
                    oldOwner.incReputation(-3000, false, toString());
                    oldOwner.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.YOUR_CLAN_HAS_FAILED_TO_DEFEND_THE_CASTLE));
                }

                for (UnitMember member : ownerClan) {
                    Player player = member.getPlayer();
                    if (player != null) {
                        player.getCounters().castleSiegesWon++;
                        player.sendPacket(PlaySound.SIEGE_VICTORY);
                        if (player.isOnline() && player.isNoble()) {
                            Hero.INSTANCE.addHeroDiary(player.objectId(), HeroDiary.ACTION_CASTLE_TAKEN, getResidence().getId());
                        }
                    }
                }
            }

            getResidence().getOwnDate().setTimeInMillis(System.currentTimeMillis());
            getResidence().getLastSiegeDate().setTimeInMillis(getResidence().getSiegeDate().getTimeInMillis());

            DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
            runnerEvent.registerDominion(getResidence().getDominion());
            int id = getResidence().getId();
            if (id == 3 || id == 5 || id == 8) {
                //ownerClan.incReputation(20000);
                ownerClan.incReputation(Config.SIEGE_WINNER_REPUTATION_REWARD, false, "SiegeWinnerCustomReward");
                Player leader = ownerClan.getLeader().getPlayer();
                if (leader != null && leader.isOnline()) {
                    leader.getInventory().addItem(24003, 1, "SiegeEvent");
                }

                String msg = "20.000 Clan Reputation Points has been added to " + ownerClan.getName() + " clan for capturing " + getResidence().getName() + " of castle!";
                L2GameServerPacket packet = new Say2(0, ChatType.CRITICAL_ANNOUNCE, getResidence().getName() + " Castle", msg);
                GameObjectsStorage.getAllPlayersStream().forEach(player -> {
                    player.sendPacket(packet);
                    player.sendPacket(new ExShowScreenMessage(msg, 3000, false));
                });
            }
        } else {
            broadcastToWorld(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()));

            getResidence().getOwnDate().setTimeInMillis(0);
            getResidence().getLastSiegeDate().setTimeInMillis(0);

            DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
            runnerEvent.unRegisterDominion(getResidence().getDominion());
        }

        despawnSiegeSummons();

        if (oldOwner != null) {
            spawnAction(HIRED_GUARDS, false);
            removeObjects(HIRED_GUARDS);
        }

        showResults();

        super.stopEvent(step);
    }

    @Override
    public void reCalcNextTime(boolean onInit) {
        clearActions();

        final long currentTimeMillis = System.currentTimeMillis();
        final Calendar startSiegeDate = getResidence().getSiegeDate();
        final Calendar ownSiegeDate = getResidence().getOwnDate();
        if (onInit) {
            if (startSiegeDate.getTimeInMillis() > currentTimeMillis) {
                registerActions();
            } else if (startSiegeDate.getTimeInMillis() == 0) {
                if ((currentTimeMillis - ownSiegeDate.getTimeInMillis()) > DAY_IN_MILISECONDS) {
                    setNextSiegeTime();
                } else {
                    generateNextSiegeDates();
                }
            } else if (startSiegeDate.getTimeInMillis() <= currentTimeMillis) {
                setNextSiegeTime();
            }
        } else {
            if (getResidence().getOwner() != null) {
                getResidence().getSiegeDate().setTimeInMillis(0);
                getResidence().setJdbcState(JdbcEntityState.UPDATED);
                getResidence().update();

                generateNextSiegeDates();
            } else {
                setNextSiegeTime();
            }
        }
    }

    // ========================================================================================================================================================================

    @Override
    public void loadSiegeClans() {
        super.loadSiegeClans();

        addObjects(DEFENDERS_WAITING, SiegeClanDAO.INSTANCE.load(getResidence(), DEFENDERS_WAITING));
        addObjects(DEFENDERS_REFUSED, SiegeClanDAO.INSTANCE.load(getResidence(), DEFENDERS_REFUSED));
    }

    @Override
    public void setRegistrationOver(boolean b) {
        if (b) {
            broadcastToWorld(new SystemMessage2(SystemMsg.THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED).addResidenceName(getResidence()));
        }

        super.setRegistrationOver(b);
    }

    @Override
    public void announce(int val) {
        SystemMessage2 msg;
        int min = val / 60;
        int hour = min / 60;

        if (hour > 0) {
            msg = new SystemMessage2(SystemMsg.S1_HOURS_UNTIL_CASTLE_SIEGE_CONCLUSION).addInteger(hour);
        } else if (min > 0) {
            msg = new SystemMessage2(SystemMsg.S1_MINUTES_UNTIL_CASTLE_SIEGE_CONCLUSION).addInteger(min);
        } else {
            msg = new SystemMessage2(SystemMsg.THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECONDS).addInteger(val);
        }

        broadcastTo(msg, ATTACKERS, DEFENDERS);
    }

    // ========================================================================================================================================================================
    // Control Tower Support
    // ========================================================================================================================================================================
    private void initControlTowers() {
        List<SpawnExObject> objects = getObjects(GUARDS);
        List<Spawner> spawns = new ArrayList<>();
        for (SpawnExObject o : objects) {
            spawns.addAll(o.getSpawns());
        }

        List<SiegeToggleNpcObject> ct = getObjects(CONTROL_TOWERS);

        SiegeToggleNpcInstance closestCt;
        double distance, distanceClosest;

        for (Spawner spawn : spawns) {
            Location spawnLoc = spawn.getCurrentSpawnRange().getRandomLoc(ReflectionManager.DEFAULT.getGeoIndex());

            closestCt = null;
            distanceClosest = 0;

            for (SiegeToggleNpcObject c : ct) {
                SiegeToggleNpcInstance npcTower = c.getToggleNpc();
                distance = npcTower.getDistance(spawnLoc);

                if ((closestCt == null) || (distance < distanceClosest)) {
                    closestCt = npcTower;
                    distanceClosest = distance;
                }

                closestCt.register(spawn);
            }
        }
    }

    // ========================================================================================================================================================================
    // Damage Zone Actions
    // ========================================================================================================================================================================
    private void damageZoneAction(boolean active) {
        zoneAction(BOUGHT_ZONES, active);
    }

    /**
     * Генерирует даты для следующей осады замка, и запускает таймер на автоматическую установку даты
     */
    private void generateNextSiegeDates() {
        if (getResidence().getSiegeDate().getTimeInMillis() != 0) {
            return;
        }

        final Calendar calendar = (Calendar) Config.CASTLE_VALIDATION_DATE.clone();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        if (calendar.before(Config.CASTLE_VALIDATION_DATE)) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }
        validateSiegeDate(calendar, 1);

        _nextSiegeTimes = new TreeSet<>();

        if (getResidence().getId() == 3 || getResidence().getId() == 2 || getResidence().getId() == 1 || getResidence().getId() == 8)//Giran, Dion, Gludio, Rune
        {
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            _nextSiegeTimes.add((int) (calendar.getTimeInMillis() / 1000L));
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 21);
            _nextSiegeTimes.add((int) (calendar.getTimeInMillis() / 1000L));
        }

        long diff = (getResidence().getOwnDate().getTimeInMillis() + DAY_IN_MILISECONDS) - System.currentTimeMillis();
        _nextSiegeDateSetTask = ThreadPoolManager.INSTANCE.schedule(this::setNextSiegeTime, diff);
    }

    // ========================================================================================================================================================================
    // Суппорт Методы для установки времени осады
    // ========================================================================================================================================================================

    public void setNextSiegeTime(int id) {
        if (!_nextSiegeTimes.contains(id) || (_nextSiegeDateSetTask == null)) {
            return;
        }

        _nextSiegeTimes = new TreeSet<>();
        _nextSiegeDateSetTask.cancel(false);
        _nextSiegeDateSetTask = null;

        setNextSiegeTime(id * 1000L);
    }

    private void setNextSiegeTime() {
        final Calendar calendar = (Calendar) Config.CASTLE_VALIDATION_DATE.clone();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        if (calendar.before(Config.CASTLE_VALIDATION_DATE)) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }
        validateSiegeDate(calendar, 1);
        setNextSiegeTime(calendar.getTimeInMillis());
    }

    private void setNextSiegeTime(long g) {
        broadcastToWorld(new SystemMessage2(SystemMsg.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME).addResidenceName(getResidence()));

        clearActions();

        getResidence().getSiegeDate().setTimeInMillis(g);
        getResidence().setJdbcState(JdbcEntityState.UPDATED);
        getResidence().update();

        registerActions();
    }

    @Override
    public boolean isAttackersInAlly() {
        return !_firstStep;
    }

    public List<Integer> getNextSiegeTimes() {
        return new ArrayList<>(_nextSiegeTimes);
    }

    @Override
    public boolean canRessurect(Player resurrectPlayer, Creature target, boolean force) {
        boolean playerInZone = resurrectPlayer.isInZone(Zone.ZoneType.SIEGE);
        boolean targetInZone = target.isInZone(Zone.ZoneType.SIEGE);
        // если оба вне зоны - рес разрешен
        if (!playerInZone && !targetInZone) {
            return true;
        }
        // если таргет вне осадный зоны - рес разрешен
        if (!targetInZone) {
            return false;
        }

        Player targetPlayer = target.getPlayer();
        // если таргет не с нашей осады(или вообще нету осады) - рес запрещен
        CastleSiegeEvent activeCharSiegeEvent = resurrectPlayer.getEvent(CastleSiegeEvent.class);
        CastleSiegeEvent targetSiegeEvent = target.getEvent(CastleSiegeEvent.class);
        if (activeCharSiegeEvent != this || targetSiegeEvent != this) {
            targetPlayer.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
            resurrectPlayer.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
            return false;
        }

        SiegeClanObject targetSiegeClan = targetSiegeEvent.getSiegeClan(ATTACKERS, targetPlayer.getClan());
        if (targetSiegeClan == null) {
            targetSiegeClan = targetSiegeEvent.getSiegeClan(DEFENDERS, targetPlayer.getClan());
        }

        if (targetSiegeClan.getType() == ATTACKERS) {
            // если нету флага - рес запрещен
            if (targetSiegeClan.getFlag() == null) {
                targetPlayer.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
                resurrectPlayer.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
                return false;
            }
        } else {
            final List<SiegeToggleNpcObject> towers = getObjects(CONTROL_TOWERS);

            int deadTowers = 0;
            for (SiegeToggleNpcObject t : towers) {
                if (!t.isAlive()) {
                    deadTowers++;
                }
            }

            // Ady - If two or more of the towers have been destroyed: Neither the resurrection spell nor the scroll may be used
            if (deadTowers >= 2) {
                targetPlayer.sendPacket(SystemMsg.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE);
                resurrectPlayer.sendPacket(SystemMsg.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE);
                return false;
            }
        }

        return true;
    }

    @Override
    public Location getRestartLoc(Player player, RestartType type) {
        SiegeClanObject attackerClan = getSiegeClan(ATTACKERS, player.getClan());

        Location loc = null;
        switch (type) {
            case TO_VILLAGE:
                // Если печатью владеют лорды Рассвета (Dawn), и в данном городе идет осада, то телепортирует во 2-й по счету город.
                if (SevenSigns.INSTANCE.getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN) {
                    loc = residence.getNotOwnerRestartPoint(player);
                }
                break;
            case TO_FLAG:
                if (!getObjects(FLAG_ZONES).isEmpty() && (attackerClan != null) && (attackerClan.getFlag() != null)) {
                    loc = Location.findPointToStay(attackerClan.getFlag(), 50, 75);
                } else {
                    player.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
                }
                break;
        }
        return loc;
    }

}
