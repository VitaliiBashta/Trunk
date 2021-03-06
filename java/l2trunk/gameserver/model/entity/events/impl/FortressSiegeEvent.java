package l2trunk.gameserver.model.entity.events.impl;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.dao.SiegeClanDAO;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Spawner;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.events.objects.DoorObject;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.entity.events.objects.SpawnExObject;
import l2trunk.gameserver.model.entity.events.objects.StaticObjectObject;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Log;
import l2trunk.gameserver.utils.TimeUtils;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Barracks:
 * 0 - Archer Captain
 * 1 - Guard Captain
 * 2 - Support Unit Captain
 * 3 - Control Room
 * 4 - General
 */
public final class FortressSiegeEvent extends SiegeEvent<Fortress, SiegeClanObject> {
    public static final String FLAG_POLE = "flag_pole";
    public static final String SIEGE_COMMANDERS = "siege_commanders";
    public static final String PEACE_COMMANDERS = "peace_commanders";
    public static final String UPGRADEABLE_DOORS = "upgradeable_doors";
    public static final String ENTER_DOORS = "enter_doors";
    public static final String MACHINE_DOORS = "machine_doors";
    public static final String OUT_POWER_UNITS = "out_power_units";
    public static final String IN_POWER_UNITS = "in_power_units";
    public static final String MERCENARY_POINTS = "mercenary_points";
    private static final String COMBAT_FLAGS = "combat_flags";
    private static final String COMMANDER_DOORS = "commander_doors";
    private static final String GUARDS_LIVE_WITH_C_CENTER = "guards_live_with_c_center";
    private static final String ENVOY = "envoy";
    private static final String MERCENARY = "mercenary";
    private static final String MERCHANT = "merchant";
    private static final long SIEGE_WAIT_PERIOD = 4 * 60 * 60 * 1000L;
    private static final long COMMANDER_RESPAWN = 10 * 60 * 1000L;
    private Future<?> _envoyTask;
    private Future<?> _merchantSpawnTask;
    private Future<?> _commanderSpawnTask;
    private boolean[] barrackStatus;

    public FortressSiegeEvent(StatsSet set) {
        super(set);
    }

    @Override
    public void processStep(Clan newOwnerClan) {
        if (newOwnerClan.getCastle() > 0)
            getResidence().changeOwner(null);
        else {
            getResidence().changeOwner(newOwnerClan);

            stopEvent(true);
        }
    }

    @Override
    public void initEvent() {
        super.initEvent();

        SpawnExObject exObject = getFirstObject(SIEGE_COMMANDERS);
        barrackStatus = new boolean[exObject.getSpawns().size()];

        int lvl = getResidence().getFacilityLevel(Fortress.DOOR_UPGRADE);
        List<DoorObject> doorObjects = getObjects(UPGRADEABLE_DOORS);
        for (DoorObject d : doorObjects) {
            d.setUpgradeValue(this, d.getDoor().getMaxHp() * lvl);
            d.getDoor().addListener(_doorDeathListener);
        }

        flagPoleUpdate(false);
        if (getResidence().getOwnerId() > 0)
            spawnEnvoy();

        spawnMerchant();
    }

    @Override
    public void startEvent() {
        // принудительный старт осады
        if (_merchantSpawnTask != null) {
            _merchantSpawnTask.cancel(false);
            _merchantSpawnTask = null;
        }

        stopCommanderSpawnTask();

        oldOwner = getResidence().getOwner();

        if (oldOwner != null)
            addObject(DEFENDERS, new SiegeClanObject(DEFENDERS, oldOwner, 0));

        SiegeClanDAO.INSTANCE.delete(getResidence());

        flagPoleUpdate(true);
        updateParticles(true, ATTACKERS, DEFENDERS);

        broadcastTo(new SystemMessage2(SystemMsg.THE_FORTRESS_BATTLE_S1_HAS_BEGUN).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);

        super.startEvent();
    }

    @Override
    public void stopEvent(boolean step) {
        stopCommanderSpawnTask();

        spawnAction(COMBAT_FLAGS, false);
        updateParticles(false, ATTACKERS, DEFENDERS);

        broadcastTo(new SystemMessage2(SystemMsg.THE_FORTRESS_BATTLE_OF_S1_HAS_FINISHED).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);

        Clan ownerClan = getResidence().getOwner();
        if (ownerClan != null) {
            if (oldOwner != ownerClan) {
                ownerClan.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);

                ownerClan.incReputation(1700, false, toString());
                broadcastTo(new SystemMessage2(SystemMsg.S1_IS_VICTORIOUS_IN_THE_FORTRESS_BATTLE_OF_S2).addString(ownerClan.getName()).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);

                getResidence().getOwnDate().setTimeInMillis(System.currentTimeMillis());

                getResidence().startCycleTask();
                spawnEnvoy();
            }
        } else
            getResidence().getOwnDate().setTimeInMillis(0);

        getResidence().getLastSiegeDate().setTimeInMillis(System.currentTimeMillis());

        List<SiegeClanObject> attackers = removeObjects(ATTACKERS);
        for (SiegeClanObject siegeClan : attackers)
            siegeClan.deleteFlag();

        removeObjects(DEFENDERS);

        flagPoleUpdate(false);

        super.stopEvent(step);

        spawnMerchant();
    }

    @Override
    public synchronized void reCalcNextTime(boolean onStart) {
        int attackersSize = getObjects(ATTACKERS).size();

        Calendar startSiegeDate = getResidence().getSiegeDate();
        Calendar lastSiegeDate = getResidence().getLastSiegeDate();
        final long currentTimeMillis = System.currentTimeMillis();

        if (startSiegeDate.getTimeInMillis() > currentTimeMillis)
            if (attackersSize > 0) {
                if (onStart)
                    registerActions();
                return;
            }

        clearActions();

        if (attackersSize > 0) {
            if ((currentTimeMillis - lastSiegeDate.getTimeInMillis()) > SIEGE_WAIT_PERIOD) {
                startSiegeDate.setTimeInMillis(currentTimeMillis);
                startSiegeDate.add(Calendar.HOUR_OF_DAY, 1);
            } else {
                startSiegeDate.setTimeInMillis(lastSiegeDate.getTimeInMillis());
                startSiegeDate.add(Calendar.HOUR_OF_DAY, 5);
            }

            if (startSiegeDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                if (startSiegeDate.get(Calendar.HOUR_OF_DAY) >= 19 && startSiegeDate.get(Calendar.HOUR_OF_DAY) < 22) {
                    startSiegeDate.add(Calendar.HOUR_OF_DAY, 22 - startSiegeDate.get(Calendar.HOUR_OF_DAY));
                }
            }

            registerActions();
        } else
            startSiegeDate.setTimeInMillis(0);

        getResidence().setJdbcState(JdbcEntityState.UPDATED);
        getResidence().update();
    }

    @Override
    public void announce(int val) {
        SystemMessage2 msg;
        int min = val / 60;

        if (min > 0)
            msg = new SystemMessage2(SystemMsg.S1_MINUTES_UNTIL_THE_FORTRESS_BATTLE_STARTS).addInteger(min);
        else
            msg = new SystemMessage2(SystemMsg.S1_SECONDS_UNTIL_THE_FORTRESS_BATTLE_STARTS).addInteger(val);

        broadcastTo(msg, ATTACKERS, DEFENDERS);
    }

    private void spawnEnvoy() {
        long endTime = getResidence().getOwnDate().getTimeInMillis() + 60 * 60 * 1000L;
        long diff = endTime - System.currentTimeMillis();

        if (diff > 0 && getResidence().getContractState() == Fortress.NOT_DECIDED) {
            //FIXME [VISTALL] debug
            SpawnExObject exObject = getFirstObject(ENVOY);
            if (exObject.isSpawned())
                LOG.info("Last siege: " + TimeUtils.toSimpleFormat(getResidence().getLastSiegeDate()) + ", own date: " + TimeUtils.toSimpleFormat(getResidence().getOwnDate()) + ", siege date: " + TimeUtils.toSimpleFormat(getResidence().getSiegeDate()));

            spawnAction(ENVOY, true);
            _envoyTask = ThreadPoolManager.INSTANCE.schedule(this::despawnEnvoy, diff);
        } else if (getResidence().getContractState() == Fortress.NOT_DECIDED) {
            getResidence().setFortState(Fortress.INDEPENDENT, 0);
            getResidence().setJdbcState(JdbcEntityState.UPDATED);
            getResidence().update();
        }
    }

    private void spawnMerchant() {
        // DS: если до осады осталось меньше 10 минут то не спавним мерчанта
        // функция должна вызываться после установки времени осады
        final long siegeTime = getResidence().getSiegeDate().getTimeInMillis();
        if (siegeTime > 0 && siegeTime - System.currentTimeMillis() < 600000L)
            return;

        if (_merchantSpawnTask != null) {
            _merchantSpawnTask.cancel(false);
            _merchantSpawnTask = null;
        }

        SpawnExObject object = getFirstObject(MERCHANT);
        if (object.isSpawned()) {
            Log.debug(toString() + ": merchant already spawned.", new Exception());
            return;
        }

        long needDate = getResidence().getLastSiegeDate().getTimeInMillis() + SIEGE_WAIT_PERIOD;
        long diff = needDate - System.currentTimeMillis();
        if (diff > 0)
            _merchantSpawnTask = ThreadPoolManager.INSTANCE.schedule(() -> {
                setRegistrationOver(false);
                spawnAction(MERCHANT, true);
                _merchantSpawnTask = null;
            }, diff);
        else {
            setRegistrationOver(false);
            spawnAction(MERCHANT, true);
        }
    }

    public void despawnEnvoy() {
        _envoyTask.cancel(false);
        _envoyTask = null;

        spawnAction(ENVOY, false);
        if (getResidence().getContractState() == Fortress.NOT_DECIDED) {
            getResidence().setFortState(Fortress.INDEPENDENT, 0);
            getResidence().setJdbcState(JdbcEntityState.UPDATED);
            getResidence().update();
        }
    }

    private void flagPoleUpdate(boolean dis) {
        StaticObjectObject object = getFirstObject(FLAG_POLE);
        if (object != null)
            object.setMeshIndex(dis ? 0 : (getResidence().getOwner() != null ? 1 : 0));
    }

    public synchronized void barrackAction(int id, boolean val) {
        barrackStatus[id] = val;
    }

    public synchronized void checkBarracks() {
        if (_commanderSpawnTask == null)
            startCommanderSpawnTask();

        boolean allDead = true;
        for (boolean b : getBarrackStatus())
            if (!b)
                allDead = false;

        if (allDead) {
            if (oldOwner != null) {
                SpawnExObject spawn = getFirstObject(FortressSiegeEvent.MERCENARY);
                NpcInstance npc = spawn.getFirstSpawned();
                if (npc == null || npc.isDead())
                    return;

                Functions.npcShout(npc, NpcString.THE_COMMAND_GATE_HAS_OPENED_CAPTURE_THE_FLAG_QUICKLY_AND_RAISE_IT_HIGH_TO_PROCLAIM_OUR_VICTORY);

                spawnFlags();
            } else
                spawnFlags();
        }
    }

    public void spawnFlags() {
        doorAction(FortressSiegeEvent.COMMANDER_DOORS, true);
        spawnAction(FortressSiegeEvent.SIEGE_COMMANDERS, false);
        spawnAction(FortressSiegeEvent.COMBAT_FLAGS, true);

        if (oldOwner != null)
            spawnAction(FortressSiegeEvent.MERCENARY, false);

        spawnAction(FortressSiegeEvent.GUARDS_LIVE_WITH_C_CENTER, false);

        broadcastTo(SystemMsg.ALL_BARRACKS_ARE_OCCUPIED, ATTACKERS, DEFENDERS);

        stopCommanderSpawnTask();
    }

    @Override
    public boolean ifVar(String name) {
        if (name.equals(OWNER))
            return getResidence().getOwner() != null;
        if (name.equals(OLD_OWNER))
            return oldOwner != null;
        if ("reinforce_1".equalsIgnoreCase(name))
            return getResidence().getFacilityLevel(Fortress.REINFORCE) == 1;
        if ("reinforce_2".equalsIgnoreCase(name))
            return getResidence().getFacilityLevel(Fortress.REINFORCE) == 2;
        if ("dwarvens".equalsIgnoreCase(name))
            return getResidence().getFacilityLevel(Fortress.DWARVENS) == 1;
        return false;
    }

    public boolean[] getBarrackStatus() {
        return barrackStatus;
    }

    @Override
    public boolean canRessurect(Player resurrectPlayer, Creature target, boolean force) {
        boolean playerInZone = resurrectPlayer.isInZone(Zone.ZoneType.SIEGE);
        boolean targetInZone = target.isInZone(Zone.ZoneType.SIEGE);
        // если оба вне зоны - рес разрешен
        if (!playerInZone && !targetInZone)
            return true;
        // если таргет вне осадный зоны - рес разрешен
        if (!targetInZone)
            return false;

        Player targetPlayer = target.getPlayer();
        // если таргет не с нашей осады(или вообще нету осады) - рес запрещен
        FortressSiegeEvent siegeEvent = target.getEvent(FortressSiegeEvent.class);
        if (siegeEvent != this) {
            if (force)
                targetPlayer.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
            resurrectPlayer.sendPacket(force ? SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE : SystemMsg.INVALID_TARGET);
            return false;
        }

        SiegeClanObject targetSiegeClan = siegeEvent.getSiegeClan(ATTACKERS, targetPlayer.getClan());
        // если нету флага - рес запрещен
        if (targetSiegeClan == null || targetSiegeClan.getFlag() == null) {
            if (force)
                targetPlayer.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
            resurrectPlayer.sendPacket(force ? SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
            return false;
        }

        if (force)
            return true;
        else {
            resurrectPlayer.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }
    }

    private void startCommanderSpawnTask() {
        _commanderSpawnTask = ThreadPoolManager.INSTANCE.schedule(new SpawnCommanderTask(), COMMANDER_RESPAWN);
    }

    private void stopCommanderSpawnTask() {
        if (_commanderSpawnTask != null) {
            _commanderSpawnTask.cancel(false);
            _commanderSpawnTask = null;
        }
    }

    @Override
    public void setRegistrationOver(boolean b) {
        super.setRegistrationOver(b);
        if (b) {
            getResidence().getLastSiegeDate().setTimeInMillis(getResidence().getSiegeDate().getTimeInMillis());
            getResidence().setJdbcState(JdbcEntityState.UPDATED);
            getResidence().update();

            if (getResidence().getOwner() != null)
                getResidence().getOwner().broadcastToOnlineMembers(SystemMsg.ENEMY_BLOOD_PLEDGES_HAVE_INTRUDED_INTO_THE_FORTRESS);
        }
    }

    private class SpawnCommanderTask extends RunnableImpl {
        @Override
        public void runImpl() {
            SpawnExObject spawnExObject = getFirstObject(FortressSiegeEvent.SIEGE_COMMANDERS);
            List<Spawner> spawnerList = spawnExObject.getSpawns();
            for (int i = 0; i < spawnerList.size(); i++) {
                if (i == 3) // main machine не востанавливается
                    continue;

                Spawner spawner = spawnerList.get(i);

                // убит кеп
                if (spawner.getAllSpawned().isEmpty())
                    spawner.doSpawn(true);
                else {
                    NpcInstance npc = spawner.getAllSpawned().get(0);
                    npc.setFullHpMp();
                }
            }

            broadcastTo(SystemMsg.THE_BARRACKS_FUNCTION_HAS_BEEN_RESTORED, FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);

            stopCommanderSpawnTask();
        }
    }
}
