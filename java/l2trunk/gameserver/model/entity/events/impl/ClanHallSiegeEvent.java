package l2trunk.gameserver.model.entity.events.impl;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.dao.SiegeClanDAO;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class ClanHallSiegeEvent extends SiegeEvent<ClanHall, SiegeClanObject> {
    public static final String BOSS = "boss";

    public ClanHallSiegeEvent(StatsSet set) {
        super(set);
    }

    @Override
    public void startEvent() {
        oldOwner = getResidence().getOwner();
        if (oldOwner != null) {
            getResidence().changeOwner(null);

            addObject(ATTACKERS, new SiegeClanObject(ATTACKERS, oldOwner, 0));
        }

        if (getObjects(ATTACKERS).size() == 0) {
            broadcastInZone2(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addResidenceName(getResidence()));
            reCalcNextTime(false);
            return;
        }

        SiegeClanDAO.INSTANCE.delete(getResidence());

        updateParticles(true, ATTACKERS);

        broadcastTo(new SystemMessage2(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN).addResidenceName(getResidence()), ATTACKERS);

        super.startEvent();
    }

    @Override
    public void stopEvent(boolean step) {
        Clan newOwner = getResidence().getOwner();
        if (newOwner != null) {
            newOwner.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);

            newOwner.incReputation(1700, false, toString());

            broadcastTo(new SystemMessage2(SystemMsg.S1_CLAN_HAS_DEFEATED_S2).addString(newOwner.getName()).addResidenceName(getResidence()), ATTACKERS);
            broadcastTo(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()), ATTACKERS);
        } else {
            broadcastTo(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()), ATTACKERS);
        }

        updateParticles(false, ATTACKERS);

        removeObjects(ATTACKERS);

        super.stopEvent(step);

        oldOwner = null;
    }

    @Override
    public void setRegistrationOver(boolean b) {
        if (b) {
            broadcastTo(new SystemMessage2(SystemMsg.THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED).addResidenceName(getResidence()), ATTACKERS);
        }

        super.setRegistrationOver(b);
    }

    @Override
    public void processStep(Clan clan) {
        if (clan != null) {
            getResidence().changeOwner(clan);
        }

        stopEvent(true);
    }

    @Override
    public void loadSiegeClans() {
        addObjects(ATTACKERS, SiegeClanDAO.INSTANCE.load(getResidence(), ATTACKERS));
    }

    @Override
    public int getUserRelation(Player thisPlayer, int result) {
        return result;
    }

    @Override
    public int getRelation(Player thisPlayer, Player targetPlayer, int result) {
        return result;
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
        ClanHallSiegeEvent siegeEvent = target.getEvent(ClanHallSiegeEvent.class);
        if (siegeEvent != this) {
            if (force) {
                targetPlayer.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
            }
            resurrectPlayer.sendPacket(force ? SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE : SystemMsg.INVALID_TARGET);
            return false;
        }

        SiegeClanObject targetSiegeClan = siegeEvent.getSiegeClan(ATTACKERS, targetPlayer.getClan());
        // если нету флага - рес запрещен
        if (targetSiegeClan.getFlag() == null) {
            if (force) {
                targetPlayer.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
            }
            resurrectPlayer.sendPacket(force ? SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
            return false;
        }

        if (force) {
            return true;
        } else {
            resurrectPlayer.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }
    }
}
