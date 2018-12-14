package Elemental.managers;

import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.InstantZoneHolder;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.RestartType;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.events.impl.DuelEvent;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.InstantZone;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.TeleportUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum GmEventManager {
    INSTANCE;
    private static final int INSTANCE_ID = 909;

    private static final int RESURRECTION_DELAY = 10000;
    private final Map<Integer, GmEventParticipant> _participants = new ConcurrentHashMap<>();
    private String _eventName;
    private StateEnum _state = StateEnum.INACTIVE;
    private int _instanceId = 0;
    private Location _location;
    private int _minLvl = 0;
    private int _maxLvl = 85;
    private int _minOnlineTime = 0;
    private int _maxOnlineTime = Integer.MAX_VALUE;
    private boolean _isPvPEvent = false;
    private boolean _isPeaceEvent = false;
    private boolean _isAutoRes = false;

    /**
     * Create a new event with particular name at position pj
     */
    public void createEvent(Player gameMaster, String eventName) {
        // Creamos la instancia y metemos al pj en la misma
        final InstantZone iz = InstantZoneHolder.getInstantZone(INSTANCE_ID);
        Reflection r = new Reflection();
        r.init(iz);

        _instanceId = r.getId();
        if (_instanceId < 1) {
            gameMaster.sendMessage("An error ocurred while creating the initial instance for the event");
            return;
        }

        gameMaster.teleToLocation(gameMaster.getLoc(), r);

        _eventName = eventName;
        _location = gameMaster.getLoc();

        _state = StateEnum.STARTING;

        gameMaster.sendMessage("Event created succesfuly");
    }

    public void changeEventParameter(EventParameter param, int value) {
        switch (param) {
            case MIN_LVL:
                _minLvl = value;
                break;
            case MAX_LVL:
                _maxLvl = value;
                break;
            case MIN_TIME:
                _minOnlineTime = value;
                break;
            case MAX_TIME:
                _maxOnlineTime = value;
                break;
            case IS_PVP_EVENT:
                _isPvPEvent = value == 1;
                break;
            case IS_PEACE_EVENT:
                _isPeaceEvent = value == 1;
                break;
            case IS_AUTO_RES:
                _isAutoRes = value == 1;
                break;
        }
    }

    /**
     * Comienza el periodo de registro del evento
     */
    public void startRegistration() {
        if (_state != StateEnum.STARTING)
            return;

        // Avisamos que el evento comenzo
        Announcements.INSTANCE.announceToAll("The registration period for the event " + _eventName + " is now opened");

        // Ponemos el evento en registro
        _state = StateEnum.REGISTERING;
    }

    public void startEvent() {
        if (_state != StateEnum.REGISTERING)
            return;

        Announcements.INSTANCE.announceToAll("The event " + _eventName + " has started");

        _state = StateEnum.ACTIVE;

        if (isPvPEvent()) {
            for (GmEventParticipant participant : _participants.values()) {
                if (participant == null)
                    continue;

                if (participant.getPlayer() != null)
                    participant.getPlayer().getEffectList().stopAllEffects();
            }
        }
    }

    public void stopEvent() {
        if (_state == StateEnum.INACTIVE)
            return;

        Announcements.INSTANCE.announceToAll("The event " + _eventName + " has finished");

        for (GmEventParticipant participant : _participants.values()) {
            if (participant == null || participant.getPlayer() == null)
                continue;

            if (participant.getPlayer().isDead())
                participant.getPlayer().doRevive(100);

            participant.getPlayer().teleToLocation(participant.getInitialLoc(), 0);
        }

        _participants.clear();
        _eventName = "";
        _state = StateEnum.INACTIVE;
        _minLvl = 0;
        _maxLvl = 85;
        _minOnlineTime = 0;
        _maxOnlineTime = Integer.MAX_VALUE;
        _isPvPEvent = false;
        _isPeaceEvent = false;
        _isAutoRes = false;


        Reflection r = ReflectionManager.INSTANCE.get(_instanceId);
        if (r != null)
            r.startCollapseTimer(5);
    }

    public void registerToEvent(Player player) {
        // Si no es momento de registro, no hacemos nada
        if (_state != StateEnum.REGISTERING)
            return;

        // Si ya esta registrado, no hacemos nada
        if (_participants.containsKey(player.getObjectId()))
            return;

        // Chequeamos que el pj cumpla con los requisitos impuestos para registrarse en el evento
        if (player.getLevel() < _minLvl) {
            player.sendMessage("You have not enough level to register to this event");
            return;
        }

        if (player.getLevel() > _maxLvl) {
            player.sendMessage("Your level is too high to be able to register to this event");
            return;
        }

        if (player.getOnlineTime() / 3600 < _minOnlineTime) {
            player.sendMessage("Your total online time is too low to be able to register to this event");
            return;
        }

        if (player.getOnlineTime() / 3600 > _maxOnlineTime) {
            player.sendMessage("Your total online time is too high to be able to register to this event");
            return;
        }

        // Chequeamos todas las demas condiciones de estado para unirse al evento
        if (player.isBlocked()) {
            player.sendMessage("Blocked players cannot join the event");
            return;
        }

        if (player.getCursedWeaponEquippedId() > 0) {
            player.sendMessage("Cursed Weapon owners may not participate in the event!");
            return;
        }

        if (Olympiad.isRegistered(player) || player.isInOlympiadMode() || player.getOlympiadGame() != null) {
            player.sendMessage("Players registered to Olympiad Match may not participate in the event!");
            return;
        }

        if (player.isInObserverMode())
            return;

        if (player.isDead() || player.isAlikeDead())
            return;

        if (!player.isInPeaceZone() && player.getPvpFlag() > 0) {
            player.sendMessage("Players in PvP Battle may not participate in the event!");
            return;
        }

        if (player.isInCombat()) {
            player.sendMessage("Players in Combat may not participate in the event!");
            return;
        }

        if (player.getEvent(DuelEvent.class) != null) {
            player.sendMessage("Players engaged in Duel may not participate in the event!");
            return;
        }

        if (player.getKarma() > 0) {
            player.sendMessage("Chaotic players may not participate in the event!");
            return;
        }

        if (player.isInStoreMode()) {
            player.sendMessage("Players in Store mode may not participate in the event!");
            return;
        }

        if (player.getReflectionId() > 0) {
            player.sendMessage("Players in instances may not participate in the event!");
            return;
        }

        // Salvamos la ubicacion del pj actual y lo registramos al evento
        _participants.put(player.getObjectId(), new GmEventParticipant(player, player.getLoc()));

        // Lo transportamos hacia la ubicacion del evento
        player.teleToLocation(_location, _instanceId);

        // Le enviamos el mensaje
        player.sendMessage("You have succesfully registered to the event");
    }

    public void unregisterOfEvent(Player player) {
        // Si el pj apreta desregistrarse, pero no esta anotado aunque esta en la zona del evento, lo enviamos a la ciudad. Esto puede pasar si se desloguea y cuando vuelve ya termino
        if (!_participants.containsKey(player.getObjectId()) && player.getReflectionId() == _instanceId) {
            player.teleToLocation(TeleportUtils.getRestartLocation(player, RestartType.TO_VILLAGE), 0);
            return;
        }

        // Si no es momento de registro, no hacemos nada
        if (_state != StateEnum.REGISTERING)
            return;

        // Si no esta registrado, no hacemos nada
        if (!_participants.containsKey(player.getObjectId()))
            return;

        // Lo transportamos nuevamente a donde el pj se registro al evento
        player.teleToLocation(_participants.get(player.getObjectId()).getInitialLoc(), 0);

        // Quitamos al pj del evento
        _participants.remove(player.getObjectId());

        // Le enviamos el mensaje
        player.sendMessage("You have succesfully unregistered from the event");
    }

    public void onPlayerKill(Player killed, Creature killer) {
        if (killed == null || killer == null)
            return;

        // Chequeamos si el pj que murio esta participando de este evento
        if (!isParticipating(killed))
            return;

        // Si el evento debe revivir a los muertos, ponemos un thread a 10 segundos para revivirlo
        if (isAutoRes())
            ThreadPoolManager.INSTANCE.schedule(new ResurrectionTask(killed), RESURRECTION_DELAY);
    }

    public boolean canResurrect(Player player) {
        // No puede revivir si esta participando del evento
        return !isParticipating(player);
    }

    public boolean isParticipating(Player player) {
        if (getEventStatus() != StateEnum.ACTIVE || player == null)
            return false;

        if (_participants.containsKey(player.getObjectId())) {
            // Si el pj esta anotado pero no esta en la instancia esta, lo desanotamos, porque seguro se salio
            if (player.getReflectionId() != _instanceId) {
                _participants.remove(player.getObjectId());
                return false;
            }

            return true;
        }

        return false;
    }

    public String getEventName() {
        return _eventName;
    }

     public int getMinLvl() {
        return _minLvl;
    }

    public int getMaxLvl() {
        return _maxLvl;
    }

    public int getMinOnlineTime() {
        return _minOnlineTime;
    }

    public int getMaxOnlineTime() {
        return _maxOnlineTime;
    }

    public boolean isPvPEvent() {
        return _isPvPEvent;
    }

    public boolean isPeaceEvent() {
        return _isPeaceEvent;
    }

    public boolean isAutoRes() {
        return _isAutoRes;
    }

    public StateEnum getEventStatus() {
        return _state;
    }

    public enum EventParameter {
        MIN_LVL,
        MAX_LVL,
        MIN_TIME,
        MAX_TIME,
        IS_PVP_EVENT,
        IS_PEACE_EVENT,
        IS_AUTO_RES
    }

    public enum StateEnum {
        ACTIVE, INACTIVE, REGISTERING, STARTING
    }

    // Clase para guardar cada participante y alguna que otra info extra necesaria de cada uno
    static class GmEventParticipant {
        private final Player _player;
        private final Location _initialLoc;

        GmEventParticipant(Player player, Location initialLoc) {
            _player = player;
            _initialLoc = initialLoc;
        }

        Player getPlayer() {
            return _player;
        }

        Location getInitialLoc() {
            return _initialLoc;
        }
    }

    private class ResurrectionTask implements Runnable {
        private final Player _player;

        ResurrectionTask(Player player) {
            _player = player;
        }

        @Override
        public void run() {
            if (_player == null || !_player.isDead() || !isParticipating(_player))
                return;

            _player.doRevive(100);
            SkillTable.INSTANCE.getInfo(5576).getEffects(_player, false, true);
        }
    }
}
