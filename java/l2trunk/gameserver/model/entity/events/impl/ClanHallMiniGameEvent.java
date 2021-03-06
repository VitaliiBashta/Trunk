package l2trunk.gameserver.model.entity.events.impl;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.dao.SiegeClanDAO;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.ClanTable;

import java.util.ArrayList;
import java.util.List;

public final class ClanHallMiniGameEvent extends SiegeEvent<ClanHall, CMGSiegeClanObject> {
    public static final String REFUND = "refund";
    private static final String NEXT_STEP = "next_step";
    private boolean _arenaClosed = true;

    public ClanHallMiniGameEvent(StatsSet set) {
        super(set);
    }

    @Override
    public void startEvent() {
        oldOwner = getResidence().getOwner();

        List<CMGSiegeClanObject> siegeClans = getObjects(ATTACKERS);
        if (siegeClans.size() < 2) {
            CMGSiegeClanObject siegeClan = siegeClans.get(0);
            if (siegeClan != null) {
                CMGSiegeClanObject oldSiegeClan = getSiegeClan(REFUND, siegeClan.getObjectId());
                if (oldSiegeClan != null) {
                    SiegeClanDAO.INSTANCE.delete(getResidence(), siegeClan); // удаляем с базы старое

                    oldSiegeClan.setParam(oldSiegeClan.getParam() + siegeClan.getParam());

                    SiegeClanDAO.INSTANCE.update(getResidence(), oldSiegeClan);
                } else {
                    siegeClan.setType(REFUND);
                    // удаляем с аттакеров
                    siegeClans.remove(siegeClan);
                    // добавляем к рефунд
                    addObject(REFUND, siegeClan);

                    SiegeClanDAO.INSTANCE.update(getResidence(), siegeClan);
                }
            }
            siegeClans.clear();

            broadcastTo(SystemMsg.THIS_CLAN_HALL_WAR_HAS_BEEN_CANCELLED, ATTACKERS);
            broadcastInZone2(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()));
            reCalcNextTime(false);
            return;
        }

        List<CMGSiegeClanObject> clans = new ArrayList<>(siegeClans);
        clans.sort(SiegeClanObject.SiegeClanComparatorImpl.getInstance());

        List<CMGSiegeClanObject> temp = new ArrayList<>(4);

        clans.forEach(siegeClan -> {
            SiegeClanDAO.INSTANCE.delete(getResidence(), siegeClan);

            if (temp.size() == 4) {
                siegeClans.remove(siegeClan);

                siegeClan.broadcast(SystemMsg.YOU_HAVE_FAILED_IN_YOUR_ATTEMPT_TO_REGISTER_FOR_THE_CLAN_HALL_WAR);
            } else {
                temp.add(siegeClan);

                siegeClan.broadcast(SystemMsg.YOU_HAVE_BEEN_REGISTERED_FOR_A_CLAN_HALL_WAR);
            }
        });

        _arenaClosed = false;

        super.startEvent();
    }

    @Override
    public void stopEvent(boolean step) {
        removeBanishItems();

        Clan newOwner = getResidence().getOwner();
        if (newOwner != null) {
            if (oldOwner != newOwner) {
                newOwner.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);

                newOwner.incReputation(1700, false, toString());
            }

            broadcastTo(new SystemMessage2(SystemMsg.S1_CLAN_HAS_DEFEATED_S2).addString(newOwner.getName()).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);
            broadcastTo(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);
        } else
            broadcastTo(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()), ATTACKERS);

        updateParticles(false, ATTACKERS);

        removeObjects(ATTACKERS);

        super.stopEvent(step);

        oldOwner = null;
    }

    private void nextStep() {
        List<CMGSiegeClanObject> siegeClans = getObjects(ATTACKERS);
        for (int i = 0; i < siegeClans.size(); i++)
            spawnAction("arena_" + i, true);

        _arenaClosed = true;

        updateParticles(true, ATTACKERS);

        broadcastTo(new SystemMessage2(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN).addResidenceName(getResidence()), ATTACKERS);
    }

    @Override
    public void setRegistrationOver(boolean b) {
        if (b)
            broadcastTo(SystemMsg.THE_REGISTRATION_PERIOD_FOR_A_CLAN_HALL_WAR_HAS_ENDED, ATTACKERS);

        super.setRegistrationOver(b);
    }

    @Override
    public CMGSiegeClanObject newSiegeClan(String type, int clanId, long param, long date) {
        Clan clan = ClanTable.INSTANCE.getClan(clanId);
        return clan == null ? null : new CMGSiegeClanObject(type, clan, param, date);
    }

    @Override
    public void announce(int val) {
        int seconds = val % 60;
        int min = val / 60;
        if (min > 0) {
            SystemMsg msg = min > 10 ? SystemMsg.IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_MUST_HURRY_AND_MOVE_TO_THE_LEFT_SIDE_OF_THE_CLAN_HALLS_ARENA : SystemMsg.IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_PLEASE_ENTER_THE_ARENA_NOW;

            broadcastTo(new SystemMessage2(msg).addInteger(min), ATTACKERS);
        } else
            broadcastTo(new SystemMessage2(SystemMsg.IN_S1_SECONDS_THE_GAME_WILL_BEGIN).addInteger(seconds), ATTACKERS);
    }

    @Override
    public void processStep(Clan clan) {
        if (clan != null)
            getResidence().changeOwner(clan);

        stopEvent(true);
    }

    @Override
    public void loadSiegeClans() {
        addObjects(ATTACKERS, SiegeClanDAO.INSTANCE.load(getResidence(), ATTACKERS));
        addObjects(REFUND, SiegeClanDAO.INSTANCE.load(getResidence(), REFUND));
    }

    @Override
    public void action(String name, boolean start) {
        if (name.equalsIgnoreCase(NEXT_STEP))
            nextStep();
        else
            super.action(name, start);
    }

    @Override
    public int getUserRelation(Player thisPlayer, int result) {
        return result;
    }

    @Override
    public int getRelation(Player thisPlayer, Player targetPlayer, int result) {
        return result;
    }

    public boolean isArenaClosed() {
        return _arenaClosed;
    }

    @Override
    public void onAddEvent(GameObject object) {
        if (object instanceof ItemInstance)
            addBanishItem((ItemInstance) object);
    }
}
