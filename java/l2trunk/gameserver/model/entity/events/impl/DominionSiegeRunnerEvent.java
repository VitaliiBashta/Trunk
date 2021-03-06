package l2trunk.gameserver.model.entity.events.impl;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.entity.residence.Dominion;
import l2trunk.gameserver.model.pledge.UnitMember;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.*;
import java.util.concurrent.Future;

public final class DominionSiegeRunnerEvent extends GlobalEvent {
    private static final String REGISTRATION = "registration";
    private static final String BATTLEFIELD = "battlefield";
    private final BattlefieldChatTask _battlefieldChatTask = new BattlefieldChatTask();
    private final Map<ClassId, Quest> classQuests = new HashMap<>();
    private final List<Quest> breakQuests = new ArrayList<>();
    private final List<Dominion> registeredDominions = new ArrayList<>(9);
    private boolean _battlefieldChatActive;
    private Future<?> battlefieldChatFuture;
    private Calendar startTime = Calendar.getInstance();
    private boolean _isInProgress;
    private boolean _isRegistrationOver;

    public DominionSiegeRunnerEvent(StatsSet set) {
        super(set);
        startTime.setTimeInMillis(0);
    }

    @Override
    public void startEvent() {
        if (startTime.getTimeInMillis() == 0) {
            clearActions();
            return;
        }

        super.startEvent();
        setInProgress(true);

        if (battlefieldChatFuture != null) {
            battlefieldChatFuture.cancel(false);
            battlefieldChatFuture = null;
        }

        for (Dominion d : registeredDominions) {
            List<SiegeClanObject> defenders = d.getSiegeEvent().getObjects(DominionSiegeEvent.DEFENDERS);
            for (SiegeClanObject siegeClan : defenders) {
                for (UnitMember member : siegeClan.getClan()) {
                    for (Dominion d2 : registeredDominions) {
                        DominionSiegeEvent siegeEvent2 = d2.getSiegeEvent();
                        List<Integer> defenderPlayers2 = siegeEvent2.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS);

                        defenderPlayers2.remove(member.objectId);

                        if (d != d2)
                            siegeEvent2.clearReward(member.objectId);
                    }
                }
            }

            List<Integer> defenderPlayers = d.getSiegeEvent().getObjects(DominionSiegeEvent.DEFENDER_PLAYERS);
            for (int i : defenderPlayers) {
                for (Dominion d2 : registeredDominions) {
                    DominionSiegeEvent siegeEvent2 = d2.getSiegeEvent();

                    if (d != d2)
                        siegeEvent2.clearReward(i);
                }
            }
        }

        registeredDominions.forEach(d -> {
            d.getSiegeEvent().clearActions();
            d.getSiegeEvent().registerActions();
        });

        broadcastToWorld(SystemMsg.TERRITORY_WAR_HAS_BEGUN);
    }

    @Override
    public void stopEvent() {
        setInProgress(false);

        reCalcNextTime(false);

        for (Dominion d : registeredDominions)
            d.getSiegeDate().setTimeInMillis(startTime.getTimeInMillis());

        broadcastToWorld(SystemMsg.TERRITORY_WAR_HAS_ENDED);

        battlefieldChatFuture = ThreadPoolManager.INSTANCE.schedule(_battlefieldChatTask, 600000L);

        SiegeEvent.showResults();

        super.stopEvent();
    }

    @Override
    public void announce(int val) {
        switch (val) {
            case -20:
                broadcastToWorld(SystemMsg.THE_TERRITORY_WAR_WILL_BEGIN_IN_20_MINUTES);
                break;
            case -10:
                broadcastToWorld(SystemMsg.THE_TERRITORY_WAR_BEGINS_IN_10_MINUTES);
                break;
            case -5:
                broadcastToWorld(SystemMsg.THE_TERRITORY_WAR_BEGINS_IN_5_MINUTES);
                break;
            case -1:
                broadcastToWorld(SystemMsg.THE_TERRITORY_WAR_BEGINS_IN_1_MINUTE);
                break;
            case 3600:
                broadcastToWorld(new SystemMessage2(SystemMsg.THE_TERRITORY_WAR_WILL_END_IN_S1HOURS).addInteger(val / 3600.));
                break;
            case 600:
            case 300:
            case 60:
                broadcastToWorld(new SystemMessage2(SystemMsg.THE_TERRITORY_WAR_WILL_END_IN_S1MINUTES).addInteger(val / 60.));
                break;
            case 10:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
                broadcastToWorld(new SystemMessage2(SystemMsg.S1_SECONDS_TO_THE_END_OF_TERRITORY_WAR).addInteger(val));
                break;
        }
    }

    public Calendar getSiegeDate() {
        return startTime;
    }

    @Override
    public void reCalcNextTime(boolean onInit) {
        clearActions();

        if (onInit) {
            if (startTime.getTimeInMillis() > 0)
                registerActions();
        } else {
            if (startTime.getTimeInMillis() > 0) {
                while (System.currentTimeMillis() > startTime.getTimeInMillis())
                    startTime.add(Calendar.WEEK_OF_MONTH, 1);

                registerActions();
            }
        }
    }

    @Override
    protected long startTimeMillis() {
        return startTime.getTimeInMillis();
    }

    @Override
    protected void printInfo() {
        //
    }

    public void broadcastTo(IStaticPacket packet) {
        for (Dominion dominion : registeredDominions)
            dominion.getSiegeEvent().broadcastTo(packet);
    }
    //========================================================================================================================================================================
    //                                                         Broadcast
    //========================================================================================================================================================================

    public void broadcastTo(L2GameServerPacket packet) {
        for (Dominion dominion : registeredDominions)
            dominion.getSiegeEvent().broadcastTo(packet);
    }

    //========================================================================================================================================================================
    //                                                         Getters/Setters
    //========================================================================================================================================================================
    public boolean isBattlefieldChatActive() {
        return _battlefieldChatActive;
    }

    private void setBattlefieldChatActive(boolean battlefieldChatActive) {
        _battlefieldChatActive = battlefieldChatActive;
    }

    @Override
    public boolean isInProgress() {
        return _isInProgress;
    }

    private void setInProgress(boolean inProgress) {
        _isInProgress = inProgress;
    }

    public boolean isRegistrationOver() {
        return _isRegistrationOver;
    }

    private void setRegistrationOver(boolean registrationOver) {
        _isRegistrationOver = registrationOver;
        for (Dominion d : registeredDominions)
            d.getSiegeEvent().setRegistrationOver(registrationOver);

        if (registrationOver)
            broadcastToWorld(SystemMsg.THE_TERRITORY_WAR_REQUEST_PERIOD_HAS_ENDED);
    }

    public void addClassQuest(ClassId c, Quest quest) {
        classQuests.put(c, quest);
    }

    Quest getClassQuest(ClassId c) {
        return classQuests.get(c);
    }

    public void addBreakQuest(Quest q) {
        breakQuests.add(q);
    }

    List<Quest> getBreakQuests() {
        return breakQuests;
    }

    //========================================================================================================================================================================
    //                                                         Overrides GlobalEvent
    //========================================================================================================================================================================
    @Override
    public void action(String name, boolean start) {
        if (name.equalsIgnoreCase(REGISTRATION))
            setRegistrationOver(!start);
        else if (name.equalsIgnoreCase(BATTLEFIELD))
            setBattlefieldChatActive(start);
        else
            super.action(name, start);
    }

    public synchronized void registerDominion(Dominion d) {
        if (registeredDominions.contains(d))
            return;

        if (registeredDominions.isEmpty()) {
            Castle castle = d.getCastle();
            if (castle.getOwnDate().getTimeInMillis() == 0)
                return;

            startTime = (Calendar) Config.CASTLE_VALIDATION_DATE.clone();
            startTime.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            startTime.set(Calendar.HOUR_OF_DAY, 20);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);
            startTime.set(Calendar.MILLISECOND, 0);

            while (startTime.getTimeInMillis() < System.currentTimeMillis())
                startTime.add(Calendar.WEEK_OF_YEAR, 1);

            d.getSiegeDate().setTimeInMillis(startTime.getTimeInMillis() + 100);

            reCalcNextTime(false);
        } else {
            d.getSiegeDate().setTimeInMillis(startTime.getTimeInMillis() + 100);
        }

        d.getSiegeEvent().spawnAction(DominionSiegeEvent.TERRITORY_NPC, true);
        d.rewardSkills();

        registeredDominions.add(d);
    }

    synchronized void unRegisterDominion(Dominion d) {
        if (!registeredDominions.contains(d))
            return;

        registeredDominions.remove(d);

        d.getSiegeEvent().spawnAction(DominionSiegeEvent.TERRITORY_NPC, false);
        d.getSiegeDate().setTimeInMillis(0);

        if (registeredDominions.isEmpty()) {
            clearActions();

            startTime.setTimeInMillis(0);

            reCalcNextTime(false);
        }
    }

    public List<Dominion> getRegisteredDominions() {
        return registeredDominions;
    }

    private class BattlefieldChatTask extends RunnableImpl {
        @Override
        public void runImpl() {
            setBattlefieldChatActive(false);
            setRegistrationOver(false);

            registeredDominions.forEach(d -> {
                DominionSiegeEvent siegeEvent = d.getSiegeEvent();

                siegeEvent.updateParticles(false);

                siegeEvent.broadcastTo(SystemMsg.THE_BATTLEFIELD_CHANNEL_HAS_BEEN_DEACTIVATED);

                siegeEvent.removeObjects(DominionSiegeEvent.ATTACKERS);
                siegeEvent.removeObjects(DominionSiegeEvent.DEFENDERS);
                siegeEvent.removeObjects(DominionSiegeEvent.ATTACKER_PLAYERS);
                siegeEvent.removeObjects(DominionSiegeEvent.DEFENDER_PLAYERS);
            });

            battlefieldChatFuture = null;
        }
    }
}
